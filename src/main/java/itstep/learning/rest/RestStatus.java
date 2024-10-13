package itstep.learning.rest;

public class RestStatus {
    private boolean isSuccessful;
    private int code;
    private String phrase;

    public RestStatus(int code) {
        this.code = code;
        if(code >= 200 && code < 300) {
            isSuccessful = true;
        }
        switch (code) {
            case 200: setPhrase("OK"); break;
            case 201: setPhrase("Created"); break;
            case 401: setPhrase("Unauthorized"); break;
            default: setPhrase("Default message"); break;
        }
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public String getPhrase() {
        return phrase;
    }

    public RestStatus setPhrase(String phrase) {
        this.phrase = phrase;
        return this;
    }

    public int getCode() {
        return code;
    }
}
