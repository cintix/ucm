package dk.tv2.web.mvc.http.io;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author migo
 */
public class Response {

    private final Map<String, String> headers = new LinkedHashMap<>();
    private int reponseCode = 200;
    private byte[] content;

    public Response() {
        headers.put("Content-Type", "text/html; charset=utf-8");
        headers.put("Content-Encoding", "utf-8");
    }

    public int getReponseCode() {
        return reponseCode;
    }

    public Response setReponseCode(int reponseCode) {
        this.reponseCode = reponseCode;
        return this;
    }

    public byte[] getContent() {
        return content;
    }

    public Response setContent(byte[] content) {
        this.content = content;
        return this;
    }

    public Response setContent(String content) {
        this.content = content.getBytes();
        return this;
    }

    public Response setHeader(String key, String value) {
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
