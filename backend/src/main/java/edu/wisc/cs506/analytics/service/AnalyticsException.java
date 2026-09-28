package edu.wisc.cs506.analytics.service;

public class AnalyticsException extends RuntimeException {
    private final int status;

    public AnalyticsException(int status, String safeMessage) {
        super(safeMessage);
        this.status = status;
    }

    public int status() { return status; }
}
