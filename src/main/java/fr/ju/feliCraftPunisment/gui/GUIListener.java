package fr.ju.feliCraftPunisment.gui;

import fr.ju.feliCraftPunisment.sanctions.SanctionData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class GUIListener implements Listener {
    
    private final GUIManager guiManager;
    private final Map<Player, Long> lastClickTime = new HashMap<>();
    
    public GUIListener(GUIManager guiManager) {
        this.guiManager = guiManager;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player moderator = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        
        if (!title.startsWith(ChatColor.DARK_RED + "Sanctions") && 
            !title.startsWith(ChatColor.YELLOW + "Mutes") && 
            !title.startsWith(ChatColor.RED + "Exclusions")) {
            return;
        }
        
        event.setCancelled(true);
        
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;
        
        
        // Protection contre les doubles clics SAUF pour le bouton retour
        if (clicked.getType() != Material.ARROW) {
            long currentTime = System.currentTimeMillis();
            Long lastClick = lastClickTime.get(moderator);
            if (lastClick != null && currentTime - lastClick < 500) {
                return;
            }
            lastClickTime.put(moderator, currentTime);
        }
        
        if (title.startsWith(ChatColor.DARK_RED + "Sanctions")) {
            handleMainMenu(moderator, clicked);
        } else if (title.startsWith(ChatColor.YELLOW + "Mutes")) {
            handleMuteMenu(moderator, clicked);
        } else if (title.startsWith(ChatColor.RED + "Exclusions")) {
            handleBanMenu(moderator, clicked, event);
        }
    }
    
    private void handleMainMenu(Player moderator, ItemStack clicked) {
        if (clicked.getType() == Material.WRITABLE_BOOK) {
            if (!moderator.hasPermission("felix.mute")) {
                moderator.sendMessage(ChatColor.RED + "Vous n'avez pas la permission d'accéder aux mutes!");
                return;
            }
            guiManager.openMuteMenu(moderator);
        } else if (clicked.getType() == Material.BARRIER) {
            if (!moderator.hasPermission("felix.ban")) {
                moderator.sendMessage(ChatColor.RED + "Vous n'avez pas la permission d'accéder aux bans!");
                return;
            }
            guiManager.openBanMenu(moderator);
        }
    }
    
    private void handleMuteMenu(Player moderator, ItemStack clicked) {
        if (clicked.getType() == Material.ARROW) {
            OfflinePlayer target = guiManager.getTarget(moderator);
            if (target != null) {
                // Fermer l'inventaire actuel et ouvrir le nouveau après un court délai
                moderator.closeInventory();
                Bukkit.getScheduler().runTaskLater(guiManager.getPlugin(), () -> {
                    guiManager.openMainMenu(moderator, target);
                }, 1L);
            }
            return;
        }
        
        if (clicked.hasItemMeta() && clicked.getItemMeta().hasDisplayName()) {
            String displayName = clicked.getItemMeta().getDisplayName();
            // Ignorer si c'est le bouton retour
            if (displayName.contains("Retour")) {
                return;
            }
            
            String reason = ChatColor.stripColor(displayName);
            SanctionData sanction = guiManager.getPlugin().getSanctionManager()
                    .getMuteSanctions().stream()
                    .filter(s -> s.getReason().equals(reason))
                    .findFirst()
                    .orElse(null);
            
            if (sanction != null) {
                OfflinePlayer target = guiManager.getTarget(moderator);
                if (target != null) {
                    guiManager.getPlugin().getSanctionManager().applySanction(moderator, target, sanction);
                    moderator.closeInventory();
                }
            }
        }
    }
    
    private void handleBanMenu(Player moderator, ItemStack clicked, InventoryClickEvent event) {
        
        // Vérifier d'abord si c'est le bouton retour (flèche)
        if (clicked.getType() == Material.ARROW) {
            OfflinePlayer target = guiManager.getTarget(moderator);
            if (target != null) {
                // Utiliser un délai légèrement plus long pour s'assurer que tout est bien fermé
                moderator.closeInventory();
                Bukkit.getScheduler().runTaskLater(guiManager.getPlugin(), () -> {
                    guiManager.openMainMenu(moderator, target);
                }, 2L);
            } else {
                moderator.sendMessage(ChatColor.RED + "Erreur: Aucun joueur ciblé. Utilisez /ss <joueur> pour cibler un joueur.");
                moderator.closeInventory();
            }
            return;
        }
        
        // Ne traiter que les items qui ne sont PAS des flèches et qui ont des métadonnées
        if (!clicked.getType().equals(Material.ARROW) && clicked.hasItemMeta() && clicked.getItemMeta().hasDisplayName()) {
            String displayName = clicked.getItemMeta().getDisplayName();
            // Ignorer si le nom contient "Retour"
            if (displayName.contains("Retour")) {
                return;
            }
            
            String reason = ChatColor.stripColor(displayName);
            
            // Chercher la sanction correspondante
            SanctionData sanction = guiManager.getPlugin().getSanctionManager()
                    .getBanSanctions().stream()
                    .filter(s -> s.getReason().equals(reason))
                    .findFirst()
                    .orElse(null);
            
            // N'appliquer la sanction que si on l'a trouvée
            if (sanction != null) {
                OfflinePlayer target = guiManager.getTarget(moderator);
                if (target != null) {
                    guiManager.getPlugin().getSanctionManager().applySanction(moderator, target, sanction);
                    moderator.closeInventory();
                } else {
                    moderator.sendMessage(ChatColor.RED + "Erreur: Aucun joueur ciblé pour appliquer le ban.");
                }
            } else {
            }
        } else {
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            String title = event.getView().getTitle();
            
            if (title.startsWith(ChatColor.DARK_RED + "Sanctions") || 
                title.startsWith(ChatColor.YELLOW + "Mutes") || 
                title.startsWith(ChatColor.RED + "Exclusions")) {
                
                
                // Ne supprimer la cible que si on ne réouvre pas un autre menu
                Bukkit.getScheduler().runTaskLater(guiManager.getPlugin(), () -> {
                    if (player.getOpenInventory() != null && player.getOpenInventory().getTopInventory() != null) {
                        String currentTitle = player.getOpenInventory().getTitle();
                        if (!currentTitle.contains("Sanctions") &&
                            !currentTitle.contains("Mutes") &&
                            !currentTitle.contains("Exclusions")) {
                            guiManager.removeTarget(player);
                        } else {
                        }
                    } else {
                        guiManager.removeTarget(player);
                    }
                }, 3L); // Augmenter le délai à 3 ticks
            }
        }
    }
}