package net.navibot.twitter;

public class TwitterException extends Exception {
    public TwitterException(String message, Exception e) {
        super(message, e);
    }

    public TwitterException(String message) {
        super(message);
    }
}
