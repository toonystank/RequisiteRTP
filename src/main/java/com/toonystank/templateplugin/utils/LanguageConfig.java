package com.toonystank.templateplugin.utils;

import lombok.Getter;

import java.io.IOException;

@Getter
public class LanguageConfig extends FileConfig {

    private String prefix;
    private String playerOnly;
    private String consoleOnly;
    private String playerNotFound;
    private String noPermission;

    public LanguageConfig() throws IOException {
        super("language.yml",false,false);
        init();
    }

    private void init() throws IOException {
        prefix = getString("prefix","&7[&b" + plugin.getPluginName() + "&7]");
        noPermission = getString("no-permission","&cYou do not have permission to do this.");
        playerOnly = getString("player-only","&cOnly players can use this command.");
        consoleOnly = getString("console-only","&cOnly console can use this command.");
        playerNotFound = getString("player-not-found","&cPlayer not found."); 

    
    }

    @Override
    public void reload() throws IOException {
        super.reload();
        init();
    }

}
