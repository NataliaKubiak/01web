package ru.netology;

public class Response {

    private final String status;
    private final String contentType;
    private final long contentLength;
    private final byte[] content;

    public Response(String status, String contentType, long contentLength, byte[] content) {
        this.status = status;
        this.contentType = contentType;
        this.contentLength = contentLength;
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public String getContentType() {
        return contentType;
    }

    public long getContentLength() {
        return contentLength;
    }

    public byte[] getContent() {
        return content;
    }
}
