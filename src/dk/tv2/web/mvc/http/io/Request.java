package dk.tv2.web.mvc.http.io;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import dk.tv2.web.mvc.http.utils.HTTPRequestHelper;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author migo
 */
public class Request {

    private final Map<String, String> headers = new LinkedHashMap<>();
    private final Map<String, String> queryString = new LinkedHashMap<>();
    private final Map<String, String> post = new LinkedHashMap<>();

    private final InputStream inputStream;
    private final OutputStream outputStream;

    private int reponseCode = 200;

    private byte[] content;

    public Request(HttpExchange exchange) {
        inputStream = exchange.getRequestBody();
        outputStream = exchange.getResponseBody();
        queryString.putAll(HTTPRequestHelper.requestQueryStrings(exchange));
        post.putAll(HTTPRequestHelper.postFields(exchange));
        Headers requestHeaders = exchange.getRequestHeaders();
        for (String key : requestHeaders.keySet()) {
            String value = "";
            for (String datapart : requestHeaders.get(key)) {
                value += "; " + datapart;
            }
            value = value.substring(2);
            headers.put(key, value);
        }
    }

    public Map<String, String> getQueryString() {
        return queryString;
    }

    public Map<String, String> getPost() {
        return post;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public String getQueryString(String name) {
        return queryString.get(name);
    }

    public String getPost(String name) {
        return post.get(name);
    }

    public int getReponseCode() {
        return reponseCode;
    }

    public Request setReponseCode(int reponseCode) {
        this.reponseCode = reponseCode;
        return this;
    }

    public byte[] getContent() {
        return content;
    }

    public Request setContent(byte[] content) {
        this.content = content;
        return this;
    }

    public Request setContent(String content) {
        this.content = content.getBytes();
        return this;
    }

    public Request setHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public String toString() {
        return "Response{" + "headers=" + headers + ", reponseCode=" + reponseCode + ", content=" + content + '}';
    }

}
