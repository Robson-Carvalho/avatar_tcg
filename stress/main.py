import requests
import concurrent.futures
import random
import string
import time
import threading
import websocket
import json

# ================= CONFIGURAÇÃO =================
IP_HOST = "10.0.0.151"  # alterar para o IPv4 do host que o servidor está rodando
BASE_URL = f"http://{IP_HOST}:8080"
REGISTER_ENDPOINT = f"{BASE_URL}/auth/register"
LOGIN_ENDPOINT = f"{BASE_URL}/auth/login"
DELETE_USER_ENDPOINT = f"{BASE_URL}/auth"
OPEN_CARD_ENDPOINT = f"{BASE_URL}/card/open"
DECK_ENDPOINT = f"{BASE_URL}/deck"
WS_URL = f"ws://{IP_HOST}:8080/game"

BURST = True            # True = todas threads esperam para começar juntas (ignorado se RAMP_UP > 0)
NUM_THREADS = 500       # Total de threads
NUM_OPEN_CARDS = 1      # Quantas cartas abrir por usuário
RAMP_UP = 4             # Intervalo em segundos entre lotes; 0 = desativado
BATCH_SIZE = 50         # Quantas threads iniciar por lote
PING_INTERVAL = 1       # Intervalo de ping do WS em segundos

created_users = []  # IDs para limpar depois

# ================= FUNÇÕES AUXILIARES =================
def random_email():
    return ''.join(random.choices(string.ascii_lowercase, k=6)) + "@gmail.com"

def ws_simulation(token, user_id, stay_in_queue, report):
    try:
        ws = websocket.WebSocket()
        ws.connect(WS_URL)

        # Entrar na fila
        join_msg = json.dumps({"type": "joinQueue", "token": token, "userID": user_id})
        ws.send(join_msg)

        # Loop de ping
        def ping_loop():
            while True:
                try:
                    ws.send(json.dumps({"type": "ping"}))
                    time.sleep(PING_INTERVAL)
                except:
                    break

        t_ping = threading.Thread(target=ping_loop, daemon=True)
        t_ping.start()

        time.sleep(stay_in_queue)

        # Desistir
        exit_msg = json.dumps({"type": "exit", "token": token, "matchID": None, "userID": user_id})
        ws.send(exit_msg)

        ws.close()
    except Exception as e:
        report["error"] = f"WebSocket error: {e}"

def test_user_flow(i, barrier=None):
    """Fluxo completo: registro -> login -> abrir cartas -> deck -> WS fila"""
    report = {
        "thread": i,
        "register": False,
        "login": False,
        "open_card": False,
        "deck": False,
        "ws_join": False,
        "ws_exit": False,
        "error": None,
        "user_id": None
    }
    try:
        if barrier:
            barrier.wait()

        # Registro
        email = random_email()
        payload_register = {"name": f"user{i}", "nickname": f"nick{i}", "email": email, "password": "123456"}
        r = requests.post(REGISTER_ENDPOINT, json=payload_register)
        if r.status_code not in (200, 201):
            report["error"] = f"Register failed ({r.status_code})"
            return report
        report["register"] = True

        # Login
        payload_login = {"email": email, "password": "123456"}
        r = requests.post(LOGIN_ENDPOINT, json=payload_login)
        if r.status_code != 200:
            report["error"] = f"Login failed ({r.status_code})"
            return report
        data = r.json()
        token = data.get("token")
        user_id = data["user"]["id"]
        report["login"] = True
        report["user_id"] = user_id
        created_users.append(user_id)

        # Abrir pacotes
        headers = {"Authorization": f"Bearer {token}"}
        for _ in range(NUM_OPEN_CARDS):
            r = requests.get(OPEN_CARD_ENDPOINT, headers=headers)
            if r.status_code != 200:
                report["error"] = f"Open card failed ({r.status_code})"
                return report
            data_cards = r.json()
            report["open_card"] = True

        cards = data_cards.get("cards", [])
        if len(cards) > 0:
            # Colocar cartas no deck
            card_ids = [card.get("id") for card in cards[:5]]
            while len(card_ids) < 5:
                card_ids.append(None)
            deck_payload = {
                "id": user_id,
                "userId": user_id,
                "card1Id": card_ids[0],
                "card2Id": card_ids[1],
                "card3Id": card_ids[2],
                "card4Id": card_ids[3],
                "card5Id": card_ids[4]
            }
            r = requests.put(DECK_ENDPOINT, json=deck_payload, headers=headers)
            if r.status_code not in (200, 201):
                report["error"] = f"Deck placement failed ({r.status_code})"
                return report
            report["deck"] = True

            # WebSocket: entrar na fila e desistir
            ws_simulation(token, user_id, 1, report)
            if not report["error"]:
                report["ws_join"] = True
                report["ws_exit"] = True

    except Exception as e:
        report["error"] = str(e)

    return report

def cleanup_users():
    print(f"\n{'='*60}")
    print("🗑️ Limpando usuários criados")
    print(f"{'='*60}")
    for uid in created_users:
        try:
            payload = {"id": uid}
            r = requests.delete(DELETE_USER_ENDPOINT, json=payload)
            if r.status_code != 200:
                print(f"Falha ao deletar {uid}: {r.status_code} - {r.text}")
        except Exception as e:
            print(f"Erro ao deletar {uid}: {e}")
    print(f"   • Usuários de teste excluídos com sucesso.")

def stress_test(num_threads=50, burst=True, ramp_up=0, batch_size=50):
    start = time.time()
    results = []

    barrier = threading.Barrier(num_threads) if burst and ramp_up == 0 else None

    with concurrent.futures.ThreadPoolExecutor(max_workers=num_threads) as executor:
        futures = []

        i = 0
        while i < num_threads:
            current_batch = min(batch_size, num_threads - i)
            for j in range(current_batch):
                futures.append(executor.submit(test_user_flow, i + j, barrier))
            i += current_batch
            if ramp_up > 0 and i < num_threads:
                time.sleep(ramp_up)

        for future in concurrent.futures.as_completed(futures):
            results.append(future.result())

    end = time.time()
    total_time = end - start

    # ================= RELATÓRIO =================
    total = len(results)
    reg_ok = sum(1 for r in results if r["register"])
    log_ok = sum(1 for r in results if r["login"])
    open_ok = sum(1 for r in results if r["open_card"])
    deck_ok = sum(1 for r in results if r.get("deck"))
    ws_ok = sum(1 for r in results if r.get("ws_join") and r.get("ws_exit"))
    errors = [r for r in results if r["error"]]

    reg_percent = (reg_ok / total) * 100 if total > 0 else 0
    log_percent = (log_ok / total) * 100 if total > 0 else 0
    open_percent = (open_ok / total) * 100 if total > 0 else 0
    deck_percent = (deck_ok / total) * 100 if total > 0 else 0
    ws_percent = (ws_ok / total) * 100 if total > 0 else 0
    error_percent = (len(errors) / total) * 100 if total > 0 else 0

    total_requests = (reg_ok * 1) + (log_ok * 1) + (open_ok * NUM_OPEN_CARDS) + (deck_ok * 1) + (ws_ok * 3)
    requests_per_second = total_requests / total_time if total_time > 0 else 0

    if ramp_up > 0:
        modo = f"RAMP-UP ⏳ (lotes {batch_size} threads, intervalo {ramp_up}s)"
    else:
        modo = "BURST 🚀" if burst else "INDEPENDENTE ⏱️"

    print(f"\n{'='*60}")
    print(f"📊 RELATÓRIO DE TESTE - {num_threads} THREADS - MODO: {modo}")
    print(f"{'='*60}")
    print(f"⏰ Tempo total: {total_time:.2f}s")
    print(f"📈 Throughput: {requests_per_second:.1f} req/s")
    print(f"🔁 Total de requisições: {total_requests}")
    print(f"🎴 Pacotes de cartas abertos: {NUM_OPEN_CARDS*num_threads}")
    print(f"🎴 Cartas criadas: {NUM_OPEN_CARDS*5*num_threads}")
    print(f"{'='*60}")
    print("✅ ETAPAS DO FLUXO:")
    print(f"   • Registro: {reg_ok}/{total} ({reg_percent:.1f}%) {'🎯' if reg_percent == 100 else '⚠️'}")
    print(f"   • Login: {log_ok}/{total} ({log_percent:.1f}%) {'🎯' if log_percent == 100 else '⚠️'}")
    print(f"   • Abrir cartas: {open_ok}/{total} ({open_percent:.1f}%) - {NUM_OPEN_CARDS} por usuário")
    print(f"   • Configurar deck: {deck_ok}/{total} ({deck_percent:.1f}%) {'🎯' if deck_percent == 100 else '⚠️'}")
    print(f"   • WebSocket (fila/desistência): {ws_ok}/{total} ({ws_percent:.1f}%) {'🎯' if ws_percent == 100 else '⚠️'}")
    print(f"{'='*60}")
    print(f"❌ Erros: {len(errors)}/{total} ({error_percent:.1f}%)")

    if errors:
        print(f"🔍 Top 5 erros:")
        for i, e in enumerate(errors[:5], 1):
            print(f"   {i}. Thread {e['thread']}: {e['error']}")
    else:
        print("🎉 Todos os fluxos completados com sucesso!")

    print(f"{'='*60}")
    print("📋 DETALHAMENTO DE REQUISIÇÕES:")
    print(f"   • Registro: {reg_ok} req")
    print(f"   • Login: {log_ok} req")
    print(f"   • Abrir cartas: {open_ok * NUM_OPEN_CARDS} req ({open_ok} usuários × {NUM_OPEN_CARDS})")
    print(f"   • Deck: {deck_ok} req")
    print(f"   • WebSocket: {ws_ok * 3} req ({ws_ok} conexões × 3 mensagens)")
    print(f"{'='*60}")

    return results

# ================= EXECUÇÃO =================
if __name__ == "__main__":
    try:
        stress_test(NUM_THREADS, BURST, RAMP_UP, BATCH_SIZE)
    finally:
        cleanup_users()
