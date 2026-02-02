package com.stackdarker.platform.notification.exception;

public class UnsupportedChannelException extends RuntimeException {

    public UnsupportedChannelException(String channel) {
        super("Unsupported notification channel: " + channel);
    }
}
