package com.toonystank.requisitertp.hooks.implementations;

import com.toonystank.hooks.Hook;
import com.toonystank.requisitertp.utils.MessageUtils;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class GriefPreventionHook extends Hook {

    private GriefPrevention griefPrevention;

    public GriefPreventionHook(HookData hookData) {
        super(hookData);
        if (!hookData.isEnabled()) return;
        try {
            this.griefPrevention = GriefPrevention.instance;
        }catch (Exception e) {
            if (hookData.isEnabled()) {
                hookData.setEnabled(false);
                MessageUtils.error("GriefPrevention is not installed, disabling the hook");
            }
        }

    }

    @Override
    public boolean isAllowed(Player player, Location location) {

        if (this.griefPrevention == null) return true;

        Claim claim = this.getClaim(location);
        if (claim == null) return true;
        ClaimPermission permission = this.getPermissionIn(player, claim);
        return permission != null;
    }

    private Claim getClaim(Location location) {
        return this.griefPrevention.dataStore.getClaimAt(location, true, null);
    }


    @Nullable
    public ClaimPermission getPermissionIn(Player player, Claim claim) {
        if (claim.isAdminClaim()) {
            if (claim.parent != null && claim.parent.getOwnerID() != null && claim.parent.getOwnerID().equals(player.getUniqueId())
                    || claim.getOwnerID() != null && claim.getOwnerID().equals(player.getUniqueId())) {
                return ClaimPermission.Edit;
            }
        }
        return claim.getPermission(player.getUniqueId().toString());
    }

}
