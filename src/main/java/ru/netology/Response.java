package ru.netology;

import java.util.Arrays;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Response response = (Response) o;
        return contentLength == response.contentLength &&
                Objects.equals(status, response.status) &&
                Objects.equals(contentType, response.contentType) &&
                Arrays.equals(content, response.content);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(status, contentType, contentLength);
        result = 31 * result + Arrays.hashCode(content);
        return result;
    }

    @Override
    public String toString() {
        return "Response{" +
                "status='" + status + '\'' +
                ", contentType='" + contentType + '\'' +
                ", contentLength=" + contentLength +
                ", content=" + Arrays.toString(content) +
                '}';
    }
}
