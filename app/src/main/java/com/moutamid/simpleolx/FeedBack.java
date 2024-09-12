package com.moutamid.simpleolx;

public class FeedBack {
    String message;
    String email;

    public FeedBack() {
    }

    public FeedBack(String message, String email) {
        this.message = message;
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
