package com.toonystank.requisitertp.gui;

import com.toonystank.requisitertp.RequisiteRTP;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.geyser.api.GeyserApi;
import org.geysermc.cumulus.form.SimpleForm;
import lombok.Setter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

public abstract class BaseGui implements InventoryHolder {

    protected final Inventory inventory;
    protected final Map<Integer, GuiItem> guiItems = new HashMap<>();
    protected final Map<Integer, GuiAction<InventoryClickEvent>> slotActions = new HashMap<>();
    protected static final Map<UUID, BaseGui> activeGuis = new HashMap<>();
    protected final UUID owner;
    protected int currentPage = 0;
    protected final int itemsPerPage = 5;

    @Setter @Getter
    private GuiAction<InventoryCloseEvent> closeAction;

    public BaseGui(UUID owner, String title, int size) {
        this.owner = owner;
        this.inventory = Bukkit.createInventory(this, size, title);
        activeGuis.put(owner, this);
    }

    public void setItem(int slot, GuiItem item, GuiAction<InventoryClickEvent> action) {
        guiItems.put(slot, item);
        slotActions.put(slot, action);
    }
    public void setItem(int slot, GuiItem item) {
        guiItems.put(slot, item);
        slotActions.put(slot, item.getAction());
    }

    public void open() {
        if (isBedrockPlayer(owner)) {
            openBedrockGui(currentPage);
        } else {
            inventory.clear();
            populateItems();
            Bukkit.getPlayer(owner).openInventory(inventory);
        }
    }

    public void handleClick(InventoryClickEvent event) {
        GuiAction<InventoryClickEvent> action = slotActions.get(event.getSlot());
        if (action != null) action.execute(event);
    }


    public void openPage(int pageNum) {
        if (isBedrockPlayer(owner)) {
            currentPage = pageNum;
            openBedrockGui(pageNum);
        } else {
            if (pageNum < 0 || pageNum >= (guiItems.size() / itemsPerPage)) {
                return; // Invalid page number
            }
            currentPage = pageNum;
            inventory.clear();
            populateItems();
        }
    }
    public void populateItems() {
        for (int i = 0; i < itemsPerPage; i++) {
            int slot = i + (currentPage * itemsPerPage);
            GuiItem item = guiItems.get(slot);
            if (item != null) {
                inventory.setItem(i, item.getItemStack());
            }
        }
    }

    private boolean isBedrockPlayer(UUID playerId) {
        return GeyserApi.api().isBedrockPlayer(playerId);
    }

    private void openBedrockGui(int pageNum) {
        SimpleForm.Builder form = SimpleForm.builder()
                .title("Menu - Page " + (pageNum + 1));

        List<Integer> itemSlots = new ArrayList<>(guiItems.keySet());
        int start = pageNum * itemsPerPage;
        int end = Math.min(start + itemsPerPage, itemSlots.size());

        for (int i = start; i < end; i++) {
            int slot = itemSlots.get(i);
            GuiItem item = guiItems.get(slot);
            form.button(item.getItemData().getDisplayName());
        }

        if (pageNum > 0) {
            form.button("Previous Page");
        }

        if (end < itemSlots.size()) {
            form.button("Next Page");
        }
        form.validResultHandler((simpleForm, simpleFormResponse) -> {
            handleBedrockResponse(owner, simpleFormResponse);
        });
        GeyserApi.api().sendForm(owner,form.build());
    }


    public void handleBedrockResponse(UUID playerId, SimpleFormResponse response) {
        if (response == null || !GeyserApi.api().isBedrockPlayer(playerId)) return;

        Bukkit.getScheduler().runTask(RequisiteRTP.getInstance(), () -> {
            BaseGui gui = activeGuis.get(playerId);
            if (gui != null) {
                int buttonIndex = response.clickedButtonId();
                List<Integer> itemSlots = new ArrayList<>(gui.guiItems.keySet());
                int start = gui.currentPage * gui.itemsPerPage;
                int end = Math.min(start + gui.itemsPerPage, itemSlots.size());

                if (buttonIndex >= (end - start)) {
                    if (buttonIndex == (end - start)) {
                        gui.openPage(gui.currentPage - 1);
                    } else if (buttonIndex == (end - start) + 1) {
                        gui.openPage(gui.currentPage + 1);
                    }
                } else {
                    int itemSlot = itemSlots.get(start + buttonIndex);
                    GuiAction<InventoryClickEvent> action = gui.slotActions.get(itemSlot);
                    if (action != null) {
                        action.execute(null);
                    }
                }
            }
        });
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void updateItem(int slot, GuiItem newItem) {
        if (guiItems.containsKey(slot)) {
            guiItems.put(slot, newItem);
            inventory.setItem(slot, newItem.getItemStack());
        }
    }
}
