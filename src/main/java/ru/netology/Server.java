package ru.netology;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server {

    private final Map<String, Map<String, Handler>> handlers = new HashMap<>();
    private final ExecutorService executorService;
    private static final int THREAD_POOL_SIZE = 64;

    public Server() {
        this.executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    public void addHandler(String method, String path, Handler handler) {
        handlers.computeIfAbsent(method, k -> new HashMap<>()).put(path, handler);
    }

    public void listen(int port) {
        try (final var serverSocket = new ServerSocket(port)) {
            while (true) {
                try {
                    var socket = serverSocket.accept();
                    executorService.execute(() -> handleConnection(socket));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    private void handleConnection(Socket socket) {
        try (
                socket;
                final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final var out = new BufferedOutputStream(socket.getOutputStream())
        ) {
            String requestLine = in.readLine();
            Map<String, String> headers = new HashMap<>();
            String header;
            while ((header = in.readLine()) != null && !header.isEmpty()) {
                var parts = header.split(":", 2);
                if (parts.length == 2) {
                    headers.put(parts[0].trim(), parts[1].trim());
                }
            }

            byte[] body = in.ready() ? in.readLine().getBytes() : new byte[0];
            Request request = parseRequest(requestLine, headers, body);

            if (request == null) {
                return;
            }

            // Find handler based on HTTP method and path
            Handler handler = handlers.getOrDefault(request.getMethod(), Collections.emptyMap()).get(request.getPath());
            if (handler != null) {
                handler.handle(request, out);
            } else {
                sendResponse(out, createNotFoundResponse());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Request parseRequest(String requestLine, Map<String, String> headers, byte[] body) {
        if (requestLine == null) {
            return null;
        }

        final var parts = requestLine.split(" ");
        if (parts.length != 3) {
            return null;
        }

        return new Request(parts[0], parts[1], parts[2], headers, body);
    }

    private Response createNotFoundResponse() {
        return new Response(
                "404 Not Found",
                "text/plain",
                0,
                "404 Not Found".getBytes()
        );
    }

    public void sendResponse(BufferedOutputStream out, Response response) throws IOException {
        sendResponseHeaders(out, response);

        if (response.getContent() != null) {
            out.write(response.getContent());
        }

        out.flush();
    }

    private void sendResponseHeaders(BufferedOutputStream out, Response response) throws IOException {
        String headers = String.format(
                "HTTP/1.1 %s\r\n" +
                        "Content-Type: %s\r\n" +
                        "Content-Length: %d\r\n" +
                        "Connection: close\r\n" +
                        "\r\n",
                response.getStatus(),
                response.getContentType() != null ? response.getContentType() : "text/plain",
                response.getContentLength()
        );

        out.write(headers.getBytes());
    }
}