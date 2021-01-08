package meduza.plugin;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
//import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import meduza.plugin.comandos.meducommands;
import meduza.plugin.eventos.MeduEvents;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class medubuy extends JavaPlugin {
	private FileConfiguration messages = null;
	private File messagesFile = null;
    private static Economy econ = null;
    private static Permission perms = null;
    private static Chat chat = null;
	//FileConfiguration config = getConfig();
	public void onEnable() {
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&3Medu&bbuy&8] &aEl plugin ha sido activado!"));
		Bukkit.getServer().getPluginManager().registerEvents(new MeduEvents(this), this);
		registerConfig();
		registerMessages();
		this.getCommand("medubuy").setExecutor(new meducommands(this));
		this.saveDefaultConfig();
    	if(!setupEconomy()){
    	    System.out.print("No vault plugin found");
    	    getServer().getPluginManager().disablePlugin(this);
    	    return;
    	 }
	}
	
	public void onDisable() {
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&3Medu&bbuy&8] &cEl plugin ha sido desactivado!"));
	}
	
	public void registerConfig() {
		File config = new File(this.getDataFolder(), "config.yml");
		//String rutaconfig = config.getPath();
		if (!config.exists()) {
			this.getConfig().options().copyDefaults(true);
			saveConfig();
		}
	} // this.reloadConfig(); y this.saveDefaultConfig();
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    	  
     public static Economy getEconomy() {
    	    return econ;
     }
    
    public static Permission getPermissions() {
        return perms;
    }
    
    public static Chat getChat() {
        return chat;
    }
    
	public FileConfiguration getMessages() {
	if (messages == null) {
		reloadMessages();
	}
	return messages;
	}
    
	public void reloadMessages() {
		if (messages == null) {
			messagesFile = new File(getDataFolder(), "messages.yml");
		}
		
		messages = YamlConfiguration.loadConfiguration(messagesFile);
		Reader defConfigStream;
		try {
			defConfigStream = new InputStreamReader(this.getResource("messages.yml"));
			if (defConfigStream != null) {
				YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
				messages.setDefaults(defConfig);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void saveMessages() {
		try {
			messages.save(messagesFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void registerMessages() {
		messagesFile = new File(this.getDataFolder(), "messages.yml");
		if (!messagesFile.exists()) {
			this.getMessages().options().copyDefaults(true);
			saveMessages();
		}
	}
}