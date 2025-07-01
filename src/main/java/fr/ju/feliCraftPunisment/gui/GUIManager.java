package fr.ju.feliCraftPunisment.gui;

import fr.ju.feliCraftPunisment.FeliCraftPunisment;
import fr.ju.feliCraftPunisment.sanctions.SanctionType;
import fr.ju.feliCraftPunisment.sanctions.SanctionData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GUIManager {
    
    private final FeliCraftPunisment plugin;
    private final Map<Player, OfflinePlayer> targetMap = new HashMap<>();
    private final Set<Player> navigatingPlayers = new HashSet<>();
    
    public GUIManager(FeliCraftPunisment plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(new GUIListener(this), plugin);
    }
    
    public void openMainMenu(Player moderator, OfflinePlayer target) {
        targetMap.put(moderator, target);
        
        String targetName = target.getName() != null ? target.getName() : "Joueur inconnu";
        Inventory gui = Bukkit.createInventory(null, 45, ChatColor.DARK_RED + "Sanctions - " + targetName);
        
        // Bordure décorative
        ItemStack borderItem = createItem(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 9; i++) {
            gui.setItem(i, borderItem);
            gui.setItem(36 + i, borderItem);
        }
        for (int i = 9; i < 36; i += 9) {
            gui.setItem(i, borderItem);
            gui.setItem(i + 8, borderItem);
        }
        
        // Infos du joueur ciblé
        ItemStack targetHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta targetMeta = (SkullMeta) targetHead.getItemMeta();
        targetMeta.setOwningPlayer(target);
        targetMeta.setDisplayName(ChatColor.GOLD + "Joueur ciblé: " + ChatColor.WHITE + targetName);
        targetMeta.setLore(Arrays.asList(ChatColor.GRAY + "Sanctions disponibles pour ce joueur"));
        targetHead.setItemMeta(targetMeta);
        gui.setItem(13, targetHead);
        
        // Mute section
        ItemStack muteItem = createItem(Material.WRITABLE_BOOK, ChatColor.YELLOW + "" + ChatColor.BOLD + "MUTES", 
            ChatColor.GRAY + "Sanctions de chat",
            "",
            ChatColor.YELLOW + "➤ " + ChatColor.GRAY + "Spam, flood, insultes",
            ChatColor.YELLOW + "➤ " + ChatColor.GRAY + "Provocations, publicité",
            ChatColor.YELLOW + "➤ " + ChatColor.GRAY + "Langage inapproprié",
            "",
            ChatColor.GREEN + "▸ Cliquez pour accéder");
        
        // Ban section
        ItemStack banItem = createItem(Material.BARRIER, ChatColor.RED + "" + ChatColor.BOLD + "EXCLUSIONS", 
            ChatColor.GRAY + "Sanctions d'exclusion",
            "",
            ChatColor.RED + "➤ " + ChatColor.GRAY + "Triche, exploitation de bugs",
            ChatColor.RED + "➤ " + ChatColor.GRAY + "Vol, arnaque, trahison",
            ChatColor.RED + "➤ " + ChatColor.GRAY + "Comportement inapproprié",
            "",
            ChatColor.GREEN + "▸ Cliquez pour accéder");
        
        gui.setItem(20, muteItem);
        gui.setItem(24, banItem);
        
        // Décoration
        ItemStack decoMute = createItem(Material.YELLOW_STAINED_GLASS_PANE, ChatColor.YELLOW + "Mutes");
        ItemStack decoBan = createItem(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "Exclusions");
        
        gui.setItem(19, decoMute);
        gui.setItem(21, decoMute);
        gui.setItem(23, decoBan);
        gui.setItem(25, decoBan);
        
        // Crédit auteur
        ItemStack authorHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta authorMeta = (SkullMeta) authorHead.getItemMeta();
        authorMeta.setOwningPlayer(Bukkit.getOfflinePlayer("Julien_1800"));
        authorMeta.setDisplayName(ChatColor.AQUA + "Auteur: " + ChatColor.WHITE + "Julien_1800");
        authorMeta.setLore(Arrays.asList(
            ChatColor.GRAY + "Pour FeliCraft",
            "",
            ChatColor.DARK_GRAY + "Plugin de sanctions v1.0"
        ));
        authorHead.setItemMeta(authorMeta);
        gui.setItem(40, authorHead);
        
        moderator.openInventory(gui);
    }
    
    public void openMuteMenu(Player moderator) {
        OfflinePlayer target = targetMap.get(moderator);
        if (target == null) return;
        
        String targetName = target.getName() != null ? target.getName() : "Joueur inconnu";
        Inventory gui = Bukkit.createInventory(null, 54, ChatColor.YELLOW + "Mutes - " + targetName);
        
        // Bordure supérieure décorative
        ItemStack borderItem = createItem(Material.YELLOW_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 9; i++) {
            gui.setItem(i, borderItem);
        }
        
        // Info header
        ItemStack headerItem = createItem(Material.WRITABLE_BOOK, ChatColor.YELLOW + "" + ChatColor.BOLD + "SANCTIONS DE MUTE",
            ChatColor.GRAY + "Cible: " + ChatColor.WHITE + targetName,
            "",
            ChatColor.GRAY + "Sélectionnez une sanction à appliquer");
        gui.setItem(4, headerItem);
        
        // Organisation des sanctions par gravité
        int[] slots = {10, 11, 12, 13, 14, 15, 16,  // Ligne 1 - Sanctions légères
                      19, 20, 21, 22, 23, 24, 25,   // Ligne 2 - Sanctions moyennes
                      28, 29, 30, 31, 32, 33, 34};  // Ligne 3 - Sanctions graves
        
        int slotIndex = 0;
        for (SanctionData sanction : plugin.getSanctionManager().getMuteSanctions()) {
            if (slotIndex < slots.length) {
                ItemStack item = createSanctionItem(sanction, Material.PAPER);
                gui.setItem(slots[slotIndex], item);
                slotIndex++;
            }
        }
        
        // Bordure inférieure avec bouton retour
        for (int i = 45; i < 54; i++) {
            gui.setItem(i, borderItem);
        }
        
        ItemStack backItem = createItem(Material.ARROW, ChatColor.GREEN + "" + ChatColor.BOLD + "◀ Retour", 
            ChatColor.GRAY + "Retourner au menu principal");
        gui.setItem(49, backItem);
        
        moderator.openInventory(gui);
    }
    
    public void openBanMenu(Player moderator) {
        OfflinePlayer target = targetMap.get(moderator);
        if (target == null) return;
        
        String targetName = target.getName() != null ? target.getName() : "Joueur inconnu";
        Inventory gui = Bukkit.createInventory(null, 54, ChatColor.RED + "Exclusions - " + targetName);
        
        // Bordure supérieure décorative
        ItemStack borderItem = createItem(Material.RED_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 9; i++) {
            gui.setItem(i, borderItem);
        }
        
        // Info header
        ItemStack headerItem = createItem(Material.BARRIER, ChatColor.RED + "" + ChatColor.BOLD + "SANCTIONS D'EXCLUSION",
            ChatColor.GRAY + "Cible: " + ChatColor.WHITE + targetName,
            "",
            ChatColor.DARK_RED + "⚠ " + ChatColor.GRAY + "Sanctions graves uniquement");
        gui.setItem(4, headerItem);
        
        // Organisation des sanctions par gravité
        int[] slots = {10, 11, 12, 13, 14, 15, 16,  // Ligne 1 - Bans temporaires courts
                      19, 20, 21, 22, 23, 24, 25,   // Ligne 2 - Bans temporaires moyens
                      28, 29, 30, 31, 32, 33, 34,   // Ligne 3 - Bans longs
                      37, 38, 39, 40, 41, 42, 43};  // Ligne 4 - Bans permanents
        
        int slotIndex = 0;
        for (SanctionData sanction : plugin.getSanctionManager().getBanSanctions()) {
            if (slotIndex < slots.length) {
                ItemStack item = createSanctionItem(sanction, Material.BARRIER);
                gui.setItem(slots[slotIndex], item);
                slotIndex++;
            }
        }
        
        // Bordure inférieure avec bouton retour
        for (int i = 45; i < 54; i++) {
            gui.setItem(i, borderItem);
        }
        
        ItemStack backItem = createItem(Material.ARROW, ChatColor.GREEN + "" + ChatColor.BOLD + "◀ Retour", 
            ChatColor.GRAY + "Retourner au menu principal");
        gui.setItem(49, backItem);
        
        // Avertissement
        ItemStack warningItem = createItem(Material.REDSTONE_BLOCK, ChatColor.DARK_RED + "" + ChatColor.BOLD + "ATTENTION",
            ChatColor.RED + "Ces sanctions sont définitives",
            ChatColor.RED + "Vérifiez bien avant d'appliquer");
        gui.setItem(53, warningItem);
        
        moderator.openInventory(gui);
    }
    
    private ItemStack createSanctionItem(SanctionData sanction, Material material) {
        ItemStack item = new ItemStack(sanction.getIcon());
        ItemMeta meta = item.getItemMeta();
        
        // Couleur du nom selon la gravité
        ChatColor nameColor = ChatColor.RED;
        if (sanction.getDuration() != -1) {
            long hours = sanction.getDuration() / (1000 * 60 * 60);
            if (hours <= 1) {
                nameColor = ChatColor.GOLD;
            } else if (hours <= 24) {
                nameColor = ChatColor.RED;
            } else {
                nameColor = ChatColor.DARK_RED;
            }
        } else {
            nameColor = ChatColor.DARK_PURPLE; // Permanent
        }
        
        meta.setDisplayName(nameColor + "" + ChatColor.BOLD + sanction.getReason());
        
        String duration = sanction.getDuration() == -1 ? "Permanent" : formatDuration(sanction.getDuration());
        String type = sanction.getType() == SanctionType.MUTE ? "Mute" : "Exclusion";
        
        // Icône selon la durée
        String icon = "⏱";
        if (sanction.getDuration() == -1) {
            icon = "∞";
        } else if (sanction.getDuration() >= 30L * 24 * 60 * 60 * 1000) {
            icon = "📅";
        } else if (sanction.getDuration() >= 24L * 60 * 60 * 1000) {
            icon = "📆";
        }
        
        meta.setLore(Arrays.asList(
            ChatColor.DARK_GRAY + "▪ " + ChatColor.GRAY + "Type: " + ChatColor.WHITE + type,
            ChatColor.DARK_GRAY + "▪ " + ChatColor.GRAY + "Durée: " + ChatColor.WHITE + icon + " " + duration,
            "",
            sanction.isIpBan() ? ChatColor.DARK_RED + "⚠ Sanction par IP" : "",
            sanction.isIpBan() ? "" : "",
            ChatColor.YELLOW + "➤ " + ChatColor.GREEN + "Cliquez pour appliquer"
        ));
        
        item.setItemMeta(meta);
        return item;
    }
    
    private ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if (lore.length > 0) {
            meta.setLore(Arrays.asList(lore));
        }
        item.setItemMeta(meta);
        return item;
    }
    
    private String formatDuration(long millis) {
        if (millis == -1) return "Permanent";
        
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (days > 0) {
            if (days >= 30) return (days / 30) + " mois";
            return days + " jour" + (days > 1 ? "s" : "");
        } else if (hours > 0) {
            return hours + " heure" + (hours > 1 ? "s" : "");
        } else if (minutes > 0) {
            return minutes + " minute" + (minutes > 1 ? "s" : "");
        }
        return seconds + " secondes";
    }
    
    public OfflinePlayer getTarget(Player moderator) {
        return targetMap.get(moderator);
    }
    
    public void removeTarget(Player moderator) {
        targetMap.remove(moderator);
    }
    
    public FeliCraftPunisment getPlugin() {
        return plugin;
    }
}