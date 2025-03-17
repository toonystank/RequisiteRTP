package com.toonystank.requisitertp.utils;

import lombok.Getter;

import java.io.IOException;

@Getter
public class MainConfig extends FileConfig{

    private boolean smallText;
    private boolean debug;
    private LanguageConfig languageConfig;

    private int minimumX;
    private int maximumX;
    private int minimumZ;
    private int maximumZ;

    public MainConfig() throws IOException {
        super("config.yml",false,true);
        init();
    }

    private void init() throws IOException {
        smallText = getBoolean("utils.smallText",true);
        debug = getBoolean("utils.debug",false);

        minimumX = getInt("world.minimumX",1000);
        maximumX = getInt("world.maximumX",1000);
        minimumZ = getInt("world.minimumZ",1000);
        maximumZ = getInt("world.maximumZ",1000);

        try {
            if (languageConfig != null) {
                languageConfig.reload();
                return;
            }
            languageConfig = new LanguageConfig();
        } catch (Exception e) {
            MessageUtils.error("An error happend on initializing Language.yml " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void reload() throws IOException {
        super.reload();
        init();
    }
}
