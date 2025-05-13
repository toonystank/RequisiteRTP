package com.toonystank.requisitertp.gui;

import org.bukkit.event.Event;

@FunctionalInterface
public interface GuiAction<T extends Event> {

    /**
     * Executes the event passed to it
     *
     * @param event Inventory action
     */
    void execute(final T event);

}