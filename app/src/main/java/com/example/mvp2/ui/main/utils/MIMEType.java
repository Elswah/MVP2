package com.example.mvp2.ui.main.utils;

public enum MIMEType {
    IMAGE("image/*"), VIDEO("video/*");
    public String value;

    MIMEType(String value) {
        this.value = value;
    }
}
