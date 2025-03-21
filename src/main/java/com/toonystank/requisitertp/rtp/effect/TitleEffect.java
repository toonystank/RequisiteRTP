package com.toonystank.requisitertp.rtp.effect;

import com.toonystank.requisitertp.RequisiteRTP;
import com.toonystank.requisitertp.rtp.BaseTimedEffect;
import com.toonystank.requisitertp.rtp.EffectManager;
import com.toonystank.requisitertp.utils.MessageUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TitleEffect extends BaseTimedEffect {

    private String teleport_delay_title;
    private String teleport_delay_subTitle;
    private String teleport_cancel_title;
    private String teleport_cancel_subTitle;

    private final Map<UUID, Location> playerLocations = new HashMap<>();

    public TitleEffect(Effect effect) {
        super(effect,Type.SECONDS);
        try {
            this.teleport_delay_title = EffectManager.getEffectManager().getString("message.teleport_delay_title", "&a&l[Teleportation]");
            this.teleport_delay_subTitle = EffectManager.getEffectManager().getString("message.teleport_delay_subTitle", "&b&l>> &cTeleporting in &f{time}&c seconds &b&l<<");

            this.teleport_cancel_title = EffectManager.getEffectManager().getString("message.teleport_cancel_title", "&c&l[Teleportation Canceled]");
            this.teleport_cancel_subTitle = EffectManager.getEffectManager().getString("message.teleport_cancel_subTitle", "&7You moved! Try again to teleport.");
        } catch (IOException e) {
            MessageUtils.error("An error happened when loading TitleEffect class: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @Override
    public void applyTimedEffect(Player player, int tickCount) {
        UUID playerUUID = player.getUniqueId();

        if (!playerLocations.containsKey(playerUUID)) {
            playerLocations.put(playerUUID, player.getLocation().clone());
        }

        int secondsLeft = (RequisiteRTP.getInstance().getMainConfig().getTeleportWaitingTime() * 20 - tickCount) / 20;

        if (hasToStop(player)) return;

        if (secondsLeft > 0) {
            String subTitle = teleport_delay_subTitle.replace("{time}", String.valueOf(secondsLeft));
            MessageUtils.sendTitleMessage(player, teleport_delay_title, subTitle);
        } else {
            playerLocations.remove(playerUUID);
        }
    }

    @Override
    public boolean hasToStopTimed(Player player) {
        if (hasPlayerMoved(player)) {
            MessageUtils.sendTitleMessage(player, teleport_cancel_title, teleport_cancel_subTitle);
            playerLocations.remove(player.getUniqueId());
            return true;
        }
        return false;
    }

    private boolean hasPlayerMoved(Player player) {
        UUID playerUUID = player.getUniqueId();
        Location initialLocation = playerLocations.get(playerUUID);

        if (initialLocation == null) return false;
        Location currentLocation = player.getLocation();
        return initialLocation.getBlockX() != currentLocation.getBlockX() ||
                initialLocation.getBlockY() != currentLocation.getBlockY() ||
                initialLocation.getBlockZ() != currentLocation.getBlockZ();
    }

}
