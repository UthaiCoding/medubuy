package meduza.plugin.eventos;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import meduza.plugin.medubuy;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;

public class MeduEvents implements Listener{
	private medubuy plugin;
	public MeduEvents(medubuy plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void crearCarteles(SignChangeEvent event) { // SE EJECUTARÁ CUANDO ALGUIEN PONGA UN CARTEL
		Player jugador = event.getPlayer();
		if (jugador.isOp()) {
			if (event.getLine(0).strip().equals("[Medubuy]")) {
				String cantidad = event.getLine(1); // OBTENEMOS LA CANTIDAD QUE SE LE DARÁ AL COMPRAR
				String precio = event.getLine(2); // PRECIO QUE COSTARÁ 
				String material_id = event.getLine(3); // ID DEL MATERIAL
				
				List<String> lista = new ArrayList<String>();
				
				FileConfiguration messages = plugin.getMessages();
				String linea1 = messages.getString("cartel.line1");
				lista.add(linea1);
				String linea2 = messages.getString("cartel.line2").replaceAll("%cantidad%", cantidad);
				lista.add(linea2);
				String linea3 = messages.getString("cartel.line3").replaceAll("%precio%", precio);
				lista.add(linea3);
				String linea4 = messages.getString("cartel.line4").replaceAll("%material_id%", material_id);
				lista.add(linea4);
				
				for (int i = 0; i<lista.size(); i++) {
					event.setLine(i, ChatColor.translateAlternateColorCodes('&', lista.get(i)));
				}
				
				Location localizacion_cartel = event.getBlock().getLocation();
				
				// x y z deben ser double y el yaw y pitch float
				
				double x = localizacion_cartel.getX();
				
				double y = localizacion_cartel.getY();
				
				double z = localizacion_cartel.getZ();
				
				FileConfiguration path = plugin.getConfig();
					
				String ruta = "Signs.";
				
				String result2 = (String.valueOf(x) + "|" + String.valueOf(y) + "|" + String.valueOf(z) + "|" + String.valueOf(event.getBlock().getWorld().getName())).replace(".", ";");
				
				path.set(ruta + result2 + ".cantidad", cantidad);
				
				path.set(ruta + result2 + ".precio", precio);
				
				path.set(ruta + result2 + ".material_id", material_id);
				
				plugin.saveConfig();
				
				String mensaje_crear_cartel = messages.getString("messages.crear_cartel");
				
				jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', mensaje_crear_cartel));
				
			}
		}
	}
	
	@EventHandler
	public void eliminarCarteles(BlockBreakEvent event) {
		Player jugador = event.getPlayer();
		Block block = event.getBlock();
		if (jugador.isOp() && block.getType().name().contains("SIGN") && !(event.getPlayer().getItemInHand().getType().toString().toUpperCase().contains("SWORD"))) {
			FileConfiguration config = plugin.getConfig();
			if (config.contains("Signs")) {
				//String name_world = event.getBlock().getWorld().getName();
				Location location = event.getBlock().getLocation();
				
				double x = location.getX();
				double y = location.getY();
				double z = location.getZ();
				for (String elemento: config.getConfigurationSection("Signs").getKeys(false)) {
					World world = Bukkit.getWorld(event.getBlock().getWorld().getName());
					if (world != null) {
						String location_real = (String.valueOf(x) + "|" + String.valueOf(y) + "|" + String.valueOf(z) + "|" + String.valueOf(event.getBlock().getWorld().getName())).replace(".", ";");
						if (elemento.equals(location_real)) {
							config.set("Signs."+elemento, null);
							plugin.saveConfig();
							FileConfiguration messages = plugin.getMessages();
							jugador.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("messages.eliminar_cartel")));
							return;
						} 					
					  }
			
					}

				}
			}
		} 
	
	@SuppressWarnings("deprecation")
	@EventHandler 
	public void clickCartel(PlayerInteractEvent event){ // SE EJECUTARÁ CUANDO INTERACTUE UN JUGADOR CON LO QUE SEA
		//Player jugador = event.getPlayer();
		Economy economy = medubuy.getEconomy();
		Block block = event.getClickedBlock(); // CON ESTO OBTENEMOS EL BLOQUE QUE CLICKEO EL JUGADOR
		// HAY QUE COMPROBAR SI NO ES NULO EL BLOQUE YA QUE PUEDE ESTAR CLICKEANDO AIRE
		if (block != null && event.getAction() == Action.RIGHT_CLICK_BLOCK && block.getType().name().contains("SIGN"))  { // CON ESTO COMPROBAMOS SI LE DIO CLICK DERECHO AL BLOQUE
			Location location = event.getClickedBlock().getLocation();
			FileConfiguration config = plugin.getConfig();
			
			double x = location.getX();
			double y = location.getY();
			double z = location.getZ();
			
			try {
				for (String elemento: config.getConfigurationSection("Signs").getKeys(false)) {
					World world = Bukkit.getWorld(event.getClickedBlock().getWorld().getName());
					if (world != null) { // SI EL MUNDO EXISTE
						String location_real = (String.valueOf(x) + "|" + String.valueOf(y) + "|" + String.valueOf(z) + "|" + String.valueOf(event.getClickedBlock().getWorld().getName())).replace(".", ";");
						if (elemento.equals(location_real)) {
							FileConfiguration path = plugin.getConfig();
							String cantidad = path.getString("Signs."+location_real+".cantidad");
							String precio = path.getString("Signs."+location_real+".precio");
							String material_id = path.getString("Signs."+location_real+".material_id");
							double dinero_del_jugador = economy.getBalance(event.getPlayer().getName());
							double precio_del_item = Double.valueOf(precio);
							FileConfiguration messages = plugin.getMessages();
							if (precio_del_item > dinero_del_jugador) {
								event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("messages.jugador_no_tiene_suficiente_dinero")));
								return;
							} else {
								if (event.getPlayer().getInventory().firstEmpty() == -1) {
									event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("messages.inventario_lleno")));
									
									return;
								}
								economy.withdrawPlayer(event.getPlayer().getName(), Double.valueOf(precio));
								event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("messages.compra_exitosa").replaceAll("%precio%", precio)));
								if (material_id.contains(":")) {
									String material_id2 = path.getString("Signs."+location_real+".material_id");
									String[] split = material_id2.split(":");
									ItemStack stack = new ItemStack(Integer.parseInt(split[0]), Integer.parseInt(cantidad), (short) Integer.parseInt(split[1]));
									event.getPlayer().getInventory().addItem(stack);
									return;
								} else {
									ItemStack stack = new ItemStack(Integer.parseInt(material_id), Integer.parseInt(cantidad));
									event.getPlayer().getInventory().addItem(stack);
									return;
								}
							}
						
						}
					}
		
				} 
			 } catch (Exception e) {
					return;
		   }
		}
	}
	
}