package ru.netology;

public class Main {
  public static void main(String[] args) {
    final var server = new Server();

    // Добавляем обработчики для различных методов и путей
    server.addHandler("GET", "/messages", (request, responseStream) -> {
      String responseContent = "List of messages";
      Response response = new Response("200 OK", "text/plain", responseContent.length(), responseContent.getBytes());
      server.sendResponse(responseStream, response);
    });

    server.addHandler("POST", "/messages", (request, responseStream) -> {
      String responseContent = "Message received";
      Response response = new Response("200 OK", "text/plain", responseContent.length(), responseContent.getBytes());
      server.sendResponse(responseStream, response);
    });

    server.listen(9999);
  }
}


