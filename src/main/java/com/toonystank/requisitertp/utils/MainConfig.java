package com.toonystank.requisitertp.utils;

import lombok.Getter;

import java.io.IOException;
import java.util.Collections;

@Getter
public class MainConfig extends FileConfig{

    private boolean utilsSmallText;
    private boolean utilsDebug;
    private LanguageConfig languageConfig;

    private int worldMinimumX;
    private int worldMaximumX;
    private int worldMinimumZ;
    private int worldMaximumZ;

    private int teleportWaitingTime;
    private String teleportLookingForASafeLocation;
    private String teleportLookingForASafeLocationTitle;
    private String teleportLookingForASafeLocationSubtitle;

    public MainConfig() throws IOException {
        super("config.yml",false,true);
        init();
    }

    private void init() throws IOException {
        utilsSmallText = getBoolean("utils.smallText",true);
        utilsDebug = getBoolean("utils.debug",false);

        worldMinimumX = getInt("world.minimumX",100);
        worldMaximumX = getInt("world.maximumX",10000);
        worldMinimumZ = getInt("world.minimumZ",100);
        worldMaximumZ = getInt("world.maximumZ",10000);

        getConfig().setComments("teleport.waitingTime", Collections.singletonList("The time in seconds the player has to wait before teleporting"));
        teleportWaitingTime = getInt("teleport.waitingTime",5);

        teleportLookingForASafeLocation = getString("teleport.lookingForASafeLocation","&6Finding safe location to teleport");
        teleportLookingForASafeLocationTitle = getString("teleport.lookingForASafeLocationTitle","&a&l[Teleportation] ");
        teleportLookingForASafeLocationSubtitle = getString("teleport.lookingForASafeLocationSubtitle","&b&l>> &cFinding safe location to teleport &b&l<<");

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
