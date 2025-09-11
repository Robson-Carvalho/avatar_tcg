import requests
import concurrent.futures
import random
import string
import time
import threading
import websocket
import json

# ================= CONFIGURAÇÃO =================
IP_HOST = "10.0.0.151"
BASE_URL = f"http://{IP_HOST}:8080"
REGISTER_ENDPOINT = f"{BASE_URL}/auth/register"
LOGIN_ENDPOINT = f"{BASE_URL}/auth/login"
DELETE_USER_ENDPOINT = f"{BASE_URL}/auth"
OPEN_CARD_ENDPOINT = f"{BASE_URL}/card/open"
DECK_ENDPOINT = f"{BASE_URL}/deck"  # endpoint para colocar cartas no deck
WS_URL = "ws://10.0.0.151:8080/game"  # endpoint WebSocket

BUSRT = False   # True = todas threads esperam para começar juntas
NUM_THREADS = 100  # Ajuste para 50, 100, 200, 500, etc.

created_users = []  # IDs para limpar depois

# ================= FUNÇÕES AUXILIARES =================
def random_email():
    """Gera email aleatório único"""
    return ''.join(random.choices(string.ascii_lowercase, k=6)) + "@gmail.com"

def ws_simulation(token, user_id, stay_in_queue=2):
    """Simula entrar na fila e depois desistir"""
    try:
        ws = websocket.WebSocket()
        ws.connect(WS_URL)

        # Entrar na fila
        join_msg = json.dumps({"type": "joinQueue", "token": token, "userID": user_id})
        ws.send(join_msg)

        # Loop de ping (simula startPingLoop)
        def ping_loop():
            while True:
                try:
                    ws.send(json.dumps({"type": "ping"}))
                    time.sleep(1)
                except:
                    break

        t_ping = threading.Thread(target=ping_loop, daemon=True)
        t_ping.start()

        # Ficar alguns segundos na fila
        time.sleep(stay_in_queue)

        # Desistir
        exit_msg = json.dumps({"type": "exit", "token": token, "matchID": None, "userID": user_id})
        ws.send(exit_msg)

        ws.close()
    except Exception as e:
        print(f"[{user_id}] Erro WebSocket: {e}")

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

        # 1 - Registro
        email = random_email()
        payload_register = {"name": f"user{i}", "nickname": f"nick{i}", "email": email, "password": "123456"}
        r = requests.post(REGISTER_ENDPOINT, json=payload_register)
        if r.status_code not in (200, 201):
            report["error"] = f"Register failed ({r.status_code})"
            return report
        report["register"] = True

        # 2 - Login
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

        # 3 - Abrir pacote
        headers = {"Authorization": f"Bearer {token}"}
        for _ in range(20):
            r = requests.get(OPEN_CARD_ENDPOINT, headers=headers)
            if r.status_code != 200:
                report["error"] = f"Open card failed ({r.status_code})"
                return report
            data_cards = r.json()
            report["open_card"] = True

        cards = data_cards.get("cards", [])

        if len(cards) > 0:
            # 4 - Colocar cartas no deck
            cards = data_cards.get("cards", [])
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

            # 5 - WebSocket: entrar na fila e desistir
            ws_simulation(token, user_id, stay_in_queue=random.randint(1, 4))
            report["ws_join"] = True
            report["ws_exit"] = True

    except Exception as e:
        report["error"] = str(e)

    return report

def cleanup_users():
    """Apaga todos os usuários criados durante o teste"""
    print("\n=== Limpando usuários criados ===")
    for uid in created_users:
        try:
            payload = {"id": uid}
            r = requests.delete(DELETE_USER_ENDPOINT, json=payload)
            if r.status_code == 200:
                print(f"Usuário {uid} deletado com sucesso.")
            else:
                print(f"Falha ao deletar {uid}: {r.status_code} - {r.text}")
        except Exception as e:
            print(f"Erro ao deletar {uid}: {e}")

def stress_test(num_threads=50, BUSRT=True):
    start = time.time()
    results = []

    barrier = threading.Barrier(num_threads) if BUSRT else None

    with concurrent.futures.ThreadPoolExecutor(max_workers=num_threads) as executor:
        futures = [executor.submit(test_user_flow, i, barrier) for i in range(num_threads)]
        for future in concurrent.futures.as_completed(futures):
            results.append(future.result())

    end = time.time()

    # Relatório
    total = len(results)
    reg_ok = sum(1 for r in results if r["register"])
    log_ok = sum(1 for r in results if r["login"])
    open_ok = sum(1 for r in results if r["open_card"])
    deck_ok = sum(1 for r in results if r.get("deck"))
    ws_ok = sum(1 for r in results if r.get("ws_join") and r.get("ws_exit"))
    errors = [r for r in results if r["error"]]

    modo = "BUSRT" if BUSRT else "INDEPENDENTE"
    print(f"\n=== RELATÓRIO DE TESTE ({num_threads} threads, {modo}) ===")
    print(f"Tempo total: {end - start:.2f}s")
    print(f"Registro OK: {reg_ok}/{total}")
    print(f"Login OK: {log_ok}/{total}")
    print(f"Abrir carta OK: {open_ok}/{total}")
    print(f"Deck OK: {deck_ok}/{total}")
    print(f"WebSocket fila/desistência OK: {ws_ok}/{total}")
    print(f"Erros: {len(errors)}")
    for e in errors[:5]:
        print(f" - Thread {e['thread']}: {e['error']}")

    return results

# ================= EXECUÇÃO =================
if __name__ == "__main__":
    try:
        stress_test(NUM_THREADS, BUSRT)
    finally:
        cleanup_users()
