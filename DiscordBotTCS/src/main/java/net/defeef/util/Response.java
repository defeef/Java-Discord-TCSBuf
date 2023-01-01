package net.defeef.util;

public class Response {

    private final Status status;
    private final String message;

    private Response(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    public static Response success(String message) {
        return new Response(Status.OK, "");
    }

    public static Response error(String message) {
        return new Response(Status.ERROR, message);
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public enum Status {
        OK,
        ERROR
    }

}