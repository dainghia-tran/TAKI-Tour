package com.aws.takitour.notifications;

public class Token {
    private String _token;
    public Token(String token){
        this._token = token;
    }
    public Token(){};

    public String getToken() {
        return _token;
    }

    public void setToken(String token) {
        this._token = token;
    }
}
