package com.toonystank.requisitertp.gui;


import java.util.*;

public class PaginatedGui extends BaseGui {

    private final List<GuiItem> items = new ArrayList<>();
    private int page = 0;
    private final Map<Integer, GuiItem> currentPage;

    public PaginatedGui(UUID owner, String title, int size, Map<Integer, GuiItem> currentPage) {
        super(owner, title, size);
        this.currentPage = currentPage;
    }
    public PaginatedGui(UUID owner, String title, int size) {
        this(owner, title, size, new HashMap<>());
    }

    public void addItem(GuiItem item) {
        items.add(item);
    }

    public void open() {

    }
}
