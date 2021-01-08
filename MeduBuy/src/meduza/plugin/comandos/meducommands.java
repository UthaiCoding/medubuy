package meduza.plugin.comandos;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import meduza.plugin.medubuy;
import net.md_5.bungee.api.ChatColor;

public class meducommands implements CommandExecutor{
	
	private medubuy plugin;
	
	public meducommands(medubuy plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// TODO Auto-generated method stub
		
		if (!(sender instanceof Player)) {
			return false;
		}
		
		if (args.length == 0) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&3Medu&bbuy&8] &aCreated by Uthai, version: 1.0"));
			return true;
		} 
		
		if (args[0].equals("reload") && sender.isOp()) {
			plugin.reloadConfig();
			plugin.reloadMessages();
			FileConfiguration messages = plugin.getMessages();
			String mensaje_reload_perms = messages.getString("messages.plugin_reload_perms");
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', mensaje_reload_perms));
			
		} else if (args[0].equals("reload")) {
			FileConfiguration messages = plugin.getMessages();
			String mensaje_reload_sin_perms = messages.getString("messages.plugin_reload_sin_perms");
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', mensaje_reload_sin_perms));
			
		}
		
		return false;
	}
	
}
