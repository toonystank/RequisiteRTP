package com.toonystank.requisitertp.gui;

public final class GuiException extends RuntimeException {

    public GuiException(String message) {
        super(message);
    }

    public GuiException(String message, Exception cause) {
        super(message, cause);
    }
}
