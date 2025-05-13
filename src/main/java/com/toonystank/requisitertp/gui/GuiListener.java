package com.toonystank.requisitertp.gui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class GuiListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof BaseGui) {
            BaseGui gui = (BaseGui) event.getInventory().getHolder();
            event.setCancelled(true);
            gui.handleClick(event);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof BaseGui) {
            BaseGui gui = (BaseGui) event.getInventory().getHolder();
            if (gui.getCloseAction() != null) {
                gui.getCloseAction().execute(event);
            }
        }
    }

}
