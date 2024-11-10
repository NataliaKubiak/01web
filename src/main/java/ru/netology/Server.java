package ru.netology;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public class Server {

    private final int port;
    private final List<String> validPaths;

    public Server(int port, List<String> validPaths) {
        this.port = port;
        this.validPaths = validPaths;
    }

    public void start() {
        try (final var serverSocket = new ServerSocket(port)) {
            while (true) {
                try {
                    var socket = serverSocket.accept();
                    handleConnection(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleConnection(Socket socket) {
        try (
                socket;
                final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final var out = new BufferedOutputStream(socket.getOutputStream());
        ) {
            String requestLine = in.readLine();
            Request request = parseRequest(requestLine);

            if (request == null) {
                return;
            }

            if (!validPaths.contains(request.getPath())) {
                sendResponse(out, createNotFoundResponse());
                return;
            }

            handleValidRequest(out, request.getPath());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Request parseRequest(String requestLine) {
        if (requestLine == null) {
            return null;
        }

        final var parts = requestLine.split(" ");
        if (parts.length != 3) {
            return null;
        }

        return new Request(parts[0], parts[1], parts[2]);
    }

    private void handleValidRequest(BufferedOutputStream out, String path) throws IOException {
        final var filePath = Path.of(".", "public", path);
        final var mimeType = Files.probeContentType(filePath);

        if (path.equals("/classic.html")) {
            handleClassicHtmlRequest(out, filePath, mimeType);
        } else {
            handleStaticFileRequest(out, filePath, mimeType);
        }
    }

    private void handleClassicHtmlRequest(BufferedOutputStream out, Path filePath, String mimeType) throws IOException {
        final var template = Files.readString(filePath);
        final var content = template.replace(
                "{time}",
                LocalDateTime.now().toString()
        ).getBytes();

        Response response = new Response(
                "200 OK",
                mimeType,
                content.length,
                content
        );

        sendResponse(out, response);
    }

    private void handleStaticFileRequest(BufferedOutputStream out, Path filePath, String mimeType) throws IOException {
        final var length = Files.size(filePath);

        sendResponseHeaders(out, new Response(
                "200 OK",
                mimeType,
                length,
                null
        ));

        Files.copy(filePath, out);
        out.flush();
    }

    private Response createNotFoundResponse() {
        return new Response(
                "404 Not Found",
                null,
                0,
                null
        );
    }

    private void sendResponse(BufferedOutputStream out, Response response) throws IOException {
        sendResponseHeaders(out, response);

        if (response.getContent() != null) {
            out.write(response.getContent());
        }

        out.flush();
    }

    private void sendResponseHeaders(BufferedOutputStream out, Response response) throws IOException {
        String headers = String.format(
                "HTTP/1.1 %s\r\n" +
                        "%s" +
                        "Content-Length: %d\r\n" +
                        "Connection: close\r\n" +
                        "\r\n",
                response.getStatus(),
                response.getContentType() != null ? "Content-Type: " + response.getContentType() + "\r\n" : "",
                response.getContentLength()
        );

        out.write(headers.getBytes());
    }
}
