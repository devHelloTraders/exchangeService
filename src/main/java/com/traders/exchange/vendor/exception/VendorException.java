package com.traders.exchange.vendor.exception;

public class VendorException extends Throwable {
    public String message;
    public int code;

    public VendorException(String message) {
        this.message = message;
    }

    public VendorException(String message, int code) {
        this.message = message;
        this.code = code;
    }
}
