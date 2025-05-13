package com.toonystank.requisitertp.gui;

import com.toonystank.requisitertp.utils.NameSpace;
import lombok.*;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * Represents an item used in a GUI with optional actions when clicked.
 */
@Getter
@SuppressWarnings("unused")
public class GuiItem {

    private final UUID uuid = UUID.randomUUID();
    private GuiAction<InventoryClickEvent> action;
    private ItemStack itemStack;
    private ItemData itemData;

    /**
     * Creates a GuiItem with a specified ItemStack and action.
     *
     * @param itemStack The item stack to use.
     * @param action The action to execute on click.
     */
    public GuiItem(final ItemStack itemStack, final GuiAction<InventoryClickEvent> action) {
        this.action = action;
        setItemStack(itemStack);
        setItemData(itemStack);
    }

    public GuiItem(final ItemStack itemStack) {
        this(itemStack, null);
    }

    public GuiItem(final Material material) {
        this(new ItemStack(material), null);
    }

    public GuiItem(final Material material, final GuiAction<InventoryClickEvent> action) {
        this(new ItemStack(material), action);
    }

    /**
     * Sets the item stack while applying a unique identifier to its metadata.
     *
     * @param itemStack The item stack to set.
     */
    public void setItemStack(final ItemStack itemStack) {
        this.itemStack = new ItemBuilder(itemStack)
                .pdc((s) -> s.set(NameSpace.KEYS.GuiItem.getKey(), PersistentDataType.STRING, uuid.toString()))
                .build();
    }

    private void setItemData(final ItemStack itemStack) {
        this.itemData = new ItemData();
        if (!itemStack.hasItemMeta()) {
            this.itemData.setMaterial(itemStack.getType());
        }
        else {
            this.itemData.setDisplayName(itemStack.getItemMeta().getDisplayName());
            this.itemData.setLore(itemStack.getItemMeta().getLore());
            this.itemData.setMaterial(itemStack.getType());
            this.itemData.setAmount(itemStack.getAmount());
            this.itemData.setModelData(itemStack.getItemMeta().getCustomModelData());
            this.itemData.setGlowing(itemStack.getItemMeta().hasEnchants());
        }

    }

    /**
     * Sets a new action for the GUI item.
     *
     * @param action The action to set.
     */
    public void setAction(@Nullable final GuiAction<@NotNull InventoryClickEvent> action) {
        this.action = action;
    }

    /**
     * Builder class for creating customized GUI items.
     */
    public class ItemBuilder extends BaseItemBuilder<ItemBuilder> {

        /**
         * Constructor of the item builder
         *
         * @param itemStack The {@link ItemStack} of the item
         */
        ItemBuilder(@NotNull final ItemStack itemStack) {
            super(itemStack);
        }

        /**
         * Main method to create {@link ItemBuilder}
         *
         * @param itemStack The {@link ItemStack} you want to edit
         * @return A new {@link ItemBuilder}
         */
        @NotNull
        @Contract("_ -> new")
        public ItemBuilder from(@NotNull final ItemStack itemStack) {
            return new ItemBuilder(itemStack);
        }


        /**
         * Alternative method to create {@link ItemBuilder}
         *
         * @param material The {@link Material} you want to create an item from
         * @return A new {@link ItemBuilder}
         */
        @NotNull
        @Contract("_ -> new")
        public ItemBuilder from(@NotNull final Material material) {
            return new ItemBuilder(new ItemStack(material));
        }

    }
    public static GuiItem ofText(String displayName, GuiAction<InventoryClickEvent> action) {
        ItemStack stack = new ItemStack(Material.PAPER);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            stack.setItemMeta(meta);
        }
        return new GuiItem(stack, action);
    }

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor
    public static class ItemData {

        private String displayName;
        private List<String> lore;
        private Material material;
        private int amount;
        private int modelData;
        private boolean glowing;
        private boolean hideEnchants;
        private boolean hideAttributes;

    }
}
