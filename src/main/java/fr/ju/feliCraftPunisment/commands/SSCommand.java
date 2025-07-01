package fr.ju.feliCraftPunisment.commands;

import fr.ju.feliCraftPunisment.FeliCraftPunisment;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SSCommand implements CommandExecutor, TabCompleter {
    
    private final FeliCraftPunisment plugin;
    private static final String PERMISSION = "felix.use";
    
    public SSCommand(FeliCraftPunisment plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Cette commande ne peut être exécutée que par un joueur!");
            return true;
        }
        
        Player moderator = (Player) sender;
        
        if (!moderator.hasPermission(PERMISSION)) {
            moderator.sendMessage(ChatColor.RED + "Vous n'avez pas la permission d'utiliser cette commande!");
            return true;
        }
        
        if (args.length != 1) {
            moderator.sendMessage(ChatColor.RED + "Usage: /ss <joueur>");
            return true;
        }
        
        // Chercher d'abord le joueur en ligne
        Player onlineTarget = Bukkit.getPlayer(args[0]);
        OfflinePlayer target;
        
        if (onlineTarget != null) {
            target = onlineTarget;
        } else {
            // Si pas en ligne, chercher dans les joueurs hors ligne
            target = Bukkit.getOfflinePlayer(args[0]);
            if (!target.hasPlayedBefore() && !target.isOnline()) {
                moderator.sendMessage(ChatColor.RED + "Le joueur " + args[0] + " n'a jamais joué sur ce serveur!");
                return true;
            }
        }
        
        if (target.getName() != null && target.getName().equals(moderator.getName())) {
            moderator.sendMessage(ChatColor.RED + "Vous ne pouvez pas vous sanctionner vous-même!");
            return true;
        }
        
        plugin.getGuiManager().openMainMenu(moderator, target);
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && sender.hasPermission(PERMISSION)) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}