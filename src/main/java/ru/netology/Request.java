package ru.netology;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Request {
    private final String method;
    private final String path;
    private final String version;
    private final Map<String, String> headers;
    private final byte[] body;
    private final List<NameValuePair> queryParams;

    public Request(String method, String path, String version, Map<String, String> headers, byte[] body) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.headers = headers;
        this.body = body;
        this.queryParams = parseQueryParams(path);
    }

    private List<NameValuePair> parseQueryParams(String path) {
        try {
            URI uri = new URI(path);
            String query = uri.getQuery();
            if (query != null) {
                return URLEncodedUtils.parse(query, StandardCharsets.UTF_8);
            }
        } catch (URISyntaxException e) {
            // Ignore and return empty list
        }
        return Collections.emptyList();
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

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    public NameValuePair getQueryParam(String name) {
        return queryParams.stream()
                .filter(param -> param.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public List<NameValuePair> getQueryParams() {
        return queryParams;
    }

    public String getStringQueryParams() {
        StringBuilder result = new StringBuilder();
        for (NameValuePair queryPair : queryParams) {
            result.append(queryPair.getName()).append(" = ").append(queryPair.getValue() + "\n");
        }
        return result.toString();
    }
}
