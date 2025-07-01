package fr.ju.feliCraftPunisment;

import fr.ju.feliCraftPunisment.commands.SSCommand;
import fr.ju.feliCraftPunisment.gui.GUIManager;
import fr.ju.feliCraftPunisment.sanctions.SanctionManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class FeliCraftPunisment extends JavaPlugin {

    private static FeliCraftPunisment instance;
    private GUIManager guiManager;
    private SanctionManager sanctionManager;

    @Override
    public void onEnable() {
        instance = this;
        
        guiManager = new GUIManager(this);
        sanctionManager = new SanctionManager();
        
        getCommand("ss").setExecutor(new SSCommand(this));
        
        getLogger().info("FeliCraft Punishment plugin activé avec succès!");
    }

    @Override
    public void onDisable() {
        getLogger().info("FeliCraft Punishment plugin désactivé!");
    }
    
    public static FeliCraftPunisment getInstance() {
        return instance;
    }
    
    public GUIManager getGuiManager() {
        return guiManager;
    }
    
    public SanctionManager getSanctionManager() {
        return sanctionManager;
    }
}