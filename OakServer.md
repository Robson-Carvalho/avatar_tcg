# 🌳 OAK Server - Java

Uma biblioteca simples e poderosa para criação de servidores HTTP em Java 🚀

## ✨ Funcionalidades
- ✅ Criação de rotas HTTP (`GET`, `POST`, `PUT`, `DELETE`)
- ✅ Manipulação de `Request` e `Response`
- ✅ Suporte a **WebSockets**
- ✅ Servidor embutido com API simples
- ✅ Estrutura leve e fácil de usar

---

## 📦 Utilização
Basta incluir a pasta http dentro da pasta java no seu projeto.

---

## 🚀 Exemplo de Uso

```java
import com.oak.http.OakServer;

public class Main {
    public static void main(String[] args) {
        OAKServer server = new OakServer(8080);

        // Definindo rota GET
        server.get("/hello", (request, response) -> {
            response.send("Hello World! 🌍");
        });

        // Rota POST
        server.post("/echo", (request, response) -> {
            response.json(Map.of("body", request.getBody()));
        });

        // WebSocket
        server.websocket("/chat", socket -> {
            socket.onMessage(msg -> {
                socket.send("Você disse: " + msg);
            });
        });

        server.listen();
    }
}
```