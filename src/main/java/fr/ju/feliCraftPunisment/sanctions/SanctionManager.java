package fr.ju.feliCraftPunisment.sanctions;

import me.leoko.advancedban.manager.PunishmentManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SanctionManager {
    
    private final List<SanctionData> muteSanctions = new ArrayList<>();
    private final List<SanctionData> banSanctions = new ArrayList<>();
    
    public SanctionManager() {
        initializeMuteSanctions();
        initializeBanSanctions();
    }
    
    private void initializeMuteSanctions() {
        muteSanctions.add(new SanctionData("Spam / Flood Maj", minutesToMillis(30), SanctionType.MUTE, Material.REPEATER));
        muteSanctions.add(new SanctionData("Mendicité exagérée", minutesToMillis(30), SanctionType.MUTE, Material.GOLD_NUGGET));
        muteSanctions.add(new SanctionData("Jeux de mots abusif", minutesToMillis(30), SanctionType.MUTE, Material.BOOK));
        muteSanctions.add(new SanctionData("Langage", hoursToMillis(1), SanctionType.MUTE, Material.COAL));
        muteSanctions.add(new SanctionData("Provocation", hoursToMillis(3), SanctionType.MUTE, Material.FLINT_AND_STEEL));
        muteSanctions.add(new SanctionData("Insulte légère", hoursToMillis(6), SanctionType.MUTE, Material.WOODEN_SWORD));
        muteSanctions.add(new SanctionData("Pub youtube/stream", hoursToMillis(12), SanctionType.MUTE, Material.REDSTONE));
        muteSanctions.add(new SanctionData("Insulte lourde", hoursToMillis(12), SanctionType.MUTE, Material.IRON_SWORD));
        muteSanctions.add(new SanctionData("Pub minecraft sans IP", daysToMillis(1), SanctionType.MUTE, Material.GRASS_BLOCK));
        muteSanctions.add(new SanctionData("Troll", daysToMillis(1), SanctionType.MUTE, Material.TNT));
        muteSanctions.add(new SanctionData("Discrimination", daysToMillis(3), SanctionType.MUTE, Material.WITHER_SKELETON_SKULL));
        muteSanctions.add(new SanctionData("Contournement de mute", daysToMillis(7), SanctionType.MUTE, Material.SHEARS));
        muteSanctions.add(new SanctionData("Diffamation serveur", daysToMillis(30), SanctionType.MUTE, Material.POISONOUS_POTATO));
        muteSanctions.add(new SanctionData("Pub minecraft avec IP", daysToMillis(30), SanctionType.MUTE, Material.NETHER_STAR));
    }
    
    private void initializeBanSanctions() {
        banSanctions.add(new SanctionData("Skin inapproprié", hoursToMillis(1), SanctionType.BAN, Material.LEATHER_CHESTPLATE));
        banSanctions.add(new SanctionData("Abus support mod", hoursToMillis(6), SanctionType.BAN, Material.WRITABLE_BOOK));
        banSanctions.add(new SanctionData("Anti-afk", hoursToMillis(12), SanctionType.BAN, Material.CLOCK));
        banSanctions.add(new SanctionData("Exploitation de bug mineur", daysToMillis(5), SanctionType.BAN, Material.SPIDER_EYE));
        banSanctions.add(new SanctionData("Arnaque", daysToMillis(5), SanctionType.BAN, Material.EMERALD));
        banSanctions.add(new SanctionData("Mod interdit", daysToMillis(7), SanctionType.BAN, Material.COMMAND_BLOCK));
        banSanctions.add(new SanctionData("Fly", daysToMillis(10), SanctionType.BAN, Material.FEATHER));
        banSanctions.add(new SanctionData("Vol / Trahison", daysToMillis(10), SanctionType.BAN, Material.CHEST));
        banSanctions.add(new SanctionData("Cheat", daysToMillis(20), SanctionType.BAN, Material.BEDROCK));
        banSanctions.add(new SanctionData("Pseudo inapproprié", daysToMillis(30), SanctionType.BAN, Material.NAME_TAG));
        banSanctions.add(new SanctionData("Exploitation de bug majeur", daysToMillis(180), SanctionType.BAN, Material.DRAGON_EGG)); // 6 mois
        banSanctions.add(new SanctionData("Contournement de ban", -1, SanctionType.BAN, true, Material.BARRIER)); // Permanent IP
    }
    
    public void applySanction(Player moderator, OfflinePlayer target, SanctionData sanctionData) {
        // Vérifier que tous les paramètres sont valides
        if (moderator == null || target == null || sanctionData == null) {
            if (moderator != null) {
                moderator.sendMessage(ChatColor.RED + "Erreur: Paramètres invalides pour la sanction!");
            }
            return;
        }
        
        
        try {
            String command;
            String duration = "";
            
            if (sanctionData.getDuration() != -1) {
                duration = formatDurationForCommand(sanctionData.getDuration());
            }
            
            // Utiliser les commandes temporaires pour les sanctions avec durée
            if (sanctionData.getType() == SanctionType.MUTE) {
                if (!duration.isEmpty()) {
                    command = "tempmute";
                } else {
                    command = "mute";
                }
            } else if (sanctionData.isIpBan()) {
                if (!duration.isEmpty()) {
                    command = "tempipban";
                } else {
                    command = "ipban";
                }
            } else {
                if (!duration.isEmpty()) {
                    command = "tempban";
                } else {
                    command = "ban";
                }
            }
            
            // Création de la commande complète avec le nom du modérateur dans la raison
            String reasonWithModerator = sanctionData.getReason() + " (par " + moderator.getName() + ")";
            final String finalCommand;
            if (!duration.isEmpty()) {
                // Pour les commandes temporaires, la durée vient avant la raison
                finalCommand = command + " " + target.getName() + " " + duration + " " + reasonWithModerator;
            } else {
                // Pour les commandes permanentes, pas de durée
                finalCommand = command + " " + target.getName() + " " + reasonWithModerator;
            }
            
            // Vérifier que la commande est correctement formée
            if (target.getName() == null || target.getName().isEmpty()) {
                moderator.sendMessage(ChatColor.RED + "Erreur: Nom du joueur cible invalide!");
                return;
            }
            
            // Exécution via la console du serveur pour éviter les problèmes de permissions
            Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("FeliCraft-Punishment"), () -> {
                try {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
                } catch (Exception cmdEx) {
                    cmdEx.printStackTrace();
                }
            });
            
            moderator.sendMessage(ChatColor.GREEN + "La sanction a été appliquée avec succès!");
            moderator.sendMessage(ChatColor.GRAY + "Joueur: " + ChatColor.WHITE + target.getName());
            moderator.sendMessage(ChatColor.GRAY + "Type: " + ChatColor.WHITE + 
                (sanctionData.getType() == SanctionType.MUTE ? "Mute" : "Ban"));
            moderator.sendMessage(ChatColor.GRAY + "Raison: " + ChatColor.WHITE + sanctionData.getReason());
            moderator.sendMessage(ChatColor.GRAY + "Durée: " + ChatColor.WHITE + 
                (sanctionData.getDuration() == -1 ? "Permanent" : formatDuration(sanctionData.getDuration())));
            
        } catch (Exception e) {
            moderator.sendMessage(ChatColor.RED + "Une erreur s'est produite lors de l'application de la sanction!");
            e.printStackTrace();
        }
    }
    
    private String formatDurationForCommand(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (days > 0) {
            if (days >= 30) return (days / 30) + "mo";
            return days + "d";
        } else if (hours > 0) {
            return hours + "h";
        } else if (minutes > 0) {
            return minutes + "m";
        }
        return seconds + "s";
    }
    
    private long minutesToMillis(int minutes) {
        return minutes * 60L * 1000L;
    }
    
    private long hoursToMillis(int hours) {
        return hours * 60L * 60L * 1000L;
    }
    
    private long daysToMillis(int days) {
        return days * 24L * 60L * 60L * 1000L;
    }
    
    private String formatDuration(long millis) {
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
    
    public List<SanctionData> getMuteSanctions() {
        return muteSanctions;
    }
    
    public List<SanctionData> getBanSanctions() {
        return banSanctions;
    }
}