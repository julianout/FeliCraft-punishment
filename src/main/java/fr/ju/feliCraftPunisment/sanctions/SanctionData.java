package fr.ju.feliCraftPunisment.sanctions;

import org.bukkit.Material;

public class SanctionData {
    
    private final String reason;
    private final long duration; // en millisecondes, -1 pour permanent
    private final SanctionType type;
    private final boolean isIpBan;
    private final Material icon;
    
    public SanctionData(String reason, long duration, SanctionType type, Material icon) {
        this(reason, duration, type, false, icon);
    }
    
    public SanctionData(String reason, long duration, SanctionType type, boolean isIpBan, Material icon) {
        this.reason = reason;
        this.duration = duration;
        this.type = type;
        this.isIpBan = isIpBan;
        this.icon = icon;
    }
    
    public String getReason() {
        return reason;
    }
    
    public long getDuration() {
        return duration;
    }
    
    public SanctionType getType() {
        return type;
    }
    
    public boolean isIpBan() {
        return isIpBan;
    }
    
    public Material getIcon() {
        return icon;
    }
}