package com.toonystank.requisitertp.effect.implementations;

import com.toonystank.effect.BaseTimedEffect;
import com.toonystank.requisitertp.RequisiteRTP;
import com.toonystank.requisitertp.effect.EffectManager;
import com.toonystank.requisitertp.utils.MessageUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TitleEffect extends BaseTimedEffect {

    private String teleportDelayTitle;
    private String teleportDelaySubtitle;
    private String teleportCancelTitle;
    private String teleportCancelSubtitle;

    private final Map<UUID, Location> playerLocations = new ConcurrentHashMap<>();

    public TitleEffect(Effect effect) {
        super(effect, Type.SECONDS);

    }

    @Override
    public void load() {
        try {
            this.teleportDelayTitle = EffectManager.getStringValue("Title.message.teleport_delay_title", "&a&l[Teleportation]");
            this.teleportDelaySubtitle =  EffectManager.getStringValue("Title.message.teleport_delay_subTitle", "&b&l>> &cTeleporting in &f{time}&c seconds &b&l<<");
            this.teleportCancelTitle =  EffectManager.getStringValue("Title.message.teleport_cancel_title", "&c&l[Teleportation Canceled]");
            this.teleportCancelSubtitle =  EffectManager.getStringValue("Title.message.teleport_cancel_subTitle", "&7You moved! Try again to teleport.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clearData() {
        playerLocations.clear();
        teleportDelayTitle = null;
        teleportDelaySubtitle = null;
        teleportCancelTitle = null;
        teleportCancelSubtitle = null;
    }


    @Override
    public void applyTimedEffect(Player player, int tickCount) {
        UUID playerUUID = player.getUniqueId();
        playerLocations.putIfAbsent(playerUUID, player.getLocation());

        int secondsLeft = (RequisiteRTP.getInstance().getMainConfig().getTeleportWaitingTime() * 20 - tickCount) / 20;

        if (!hasToStop(player, false) && secondsLeft > 0) {
            MessageUtils.sendTitleMessage(player, teleportDelayTitle, teleportDelaySubtitle.replace("{time}", String.valueOf(secondsLeft)));
        } else {
            playerLocations.remove(playerUUID);
        }
    }

    @Override
    public boolean hasToStop(Player player, boolean firstCall) {
        if (firstCall) {
            playerLocations.remove(player.getUniqueId());
        }
        if (hasPlayerMoved(player)) {
            MessageUtils.sendTitleMessage(player, teleportCancelTitle, teleportCancelSubtitle);
            updateRunningResult(player, true, false);
            return true;
        }
        return false;
    }

    @Override
    public void onTeleportComplete(Player player) {
    }

    private boolean hasPlayerMoved(Player player) {
        Location initialLocation = playerLocations.get(player.getUniqueId());
        return initialLocation != null && !initialLocation.equals(player.getLocation());
    }
}
