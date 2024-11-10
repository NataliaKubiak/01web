package ru.netology;

import java.util.Objects;

public class Request {
    private final String method;
    private final String path;
    private final String version;

    public Request(String method, String path, String version) {
        this.method = method;
        this.path = path;
        this.version = version;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return Objects.equals(method, request.method) &&
                Objects.equals(path, request.path) &&
                Objects.equals(version, request.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, path, version);
    }

    @Override
    public String toString() {
        return "Request{" +
                "method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
