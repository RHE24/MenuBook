package nl.itsjustbjarn.MenuBook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
 
public class MenuBook extends JavaPlugin
	implements CommandExecutor, Listener {
		public Inventory bookInv;
		public Inventory bookInvAdmin;
		public ItemStack bookItem;
		public ItemStack bookItemAdmin;
		public HashMap<String, String> itemsAndCommands;
		public HashMap<String, String> itemsAndCommandsAdmin = new HashMap();
		public FileConfiguration config;
		public FileConfiguration admin;
		private File adminFile;
		private File configFile;
 
public void onEnable(){
	loadConfig();
 
	this.config = getConfig();
	saveConfig();
 
	this.admin = YamlConfiguration.loadConfiguration(this.adminFile);
	try {
		this.admin.save(this.adminFile);
	} catch (IOException e) {
		getLogger().warning("Failed to save the admin.yml");
		e.printStackTrace();
     }
	
	getServer().getPluginManager().registerEvents(new MenuBookListener(this), this);
  
	this.bookInv = makeInventory(this.config);
	this.bookInvAdmin = makeInventory(this.admin);
 
	this.bookItem = makeItem(this.config);
	this.bookItemAdmin = makeItem(this.admin);
 
	this.itemsAndCommands = makeCommandList(this.config, this.bookInv);
	this.itemsAndCommandsAdmin = makeCommandList(this.admin, this.bookInvAdmin);
   }
 
private void loadConfig(){
	this.configFile = new File(getDataFolder(), "config.yml");
 
	if ((!this.configFile.exists()) && (!getDataFolder().exists())){
		if (!getDataFolder().mkdirs()) {
			getLogger().severe("The config folder could NOT be created, make sure it's writable!");
			getLogger().severe("Disabling now!");
			setEnabled(false);
			return;
		}
	}
 
	if (!this.configFile.exists()) {
		copy(getResource("config.yml"), this.configFile);
	}
 
	this.adminFile = new File(getDataFolder(), "admin.yml");
	if (!this.adminFile.exists())
		copy(getResource("admin.yml"), this.adminFile);
	}
 
private Inventory makeInventory(FileConfiguration fileConf){
		int slots = 54;
	String title = ChatColor.translateAlternateColorCodes('&', fileConf.getString("global.invTitle"));
 
	return getServer().createInventory(null, slots, title);
	}
 
private ItemStack makeItem(FileConfiguration fileConf){
	ItemStack item = new ItemStack(Material.BOOK, 1);
 
	Material mat = Material.matchMaterial(fileConf.getString("item.type"));
	if (mat != null) {
		item.setType(mat);
	}
 
	ItemMeta meta = item.getItemMeta();
	meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', fileConf.getString("item.title")));
	meta.setLore(fileConf.getStringList("item.lore"));
	item.setItemMeta(meta);
	return item;
	}
 
private HashMap<String, String> makeCommandList(FileConfiguration fileConf, Inventory inv) {
	HashMap map = new HashMap();
 
	for (String key : fileConf.getConfigurationSection("commands").getKeys(false)) {
		Material mat = Material.matchMaterial(key.toUpperCase());
 
		if (mat == null) {
			getLogger().info("The item " + key + " is unkown and was skipped!");
		}
		else
		{
			map.put(mat.name(), fileConf.getString("commands." + key + ".command"));
 
			ItemStack item = new ItemStack(mat, 1);
 
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', fileConf.getString("commands." + key + ".title")));
			meta.setLore(fileConf.getStringList("commands." + key + ".lore"));
			item.setItemMeta(meta);
			
			if (inv.firstEmpty() != -1)
				inv.addItem(new ItemStack[] { item });
			}
		}
	return map;
	}
 
	private void copy(InputStream in, File file){
	OutputStream out = null;
	try {
		out = new FileOutputStream(file);
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0)
		{
			out.write(buf, 0, len);
		}
	} catch (IOException e) {
		getLogger().warning("Failed to copy the default config! (I/O)");
		e.printStackTrace();
		try
		{
			if (out != null) {
				out.flush();
				out.close();
			}
		} catch (IOException e1) {
			getLogger().warning("Failed to close the streams! (I/O -> Output)");
			e1.printStackTrace();
		}
		try {
			if (in != null)
        	 in.close();
		}
		catch (IOException e1) {
    	   getLogger().warning("Failed to close the streams! (I/O -> Input)");
    	   e1.printStackTrace();
		}
	}
  	  finally
  	  {
  		  try
  		  {
  			  if (out != null) {
  				  out.flush();
  				  out.close();
  			  }
  		  } catch (IOException e) {
  			  getLogger().warning("Failed to close the streams! (I/O -> Output)");
  			  e.printStackTrace();
  		  }
  		  try {
  			  if (in != null)
	           in.close();
	       }
  		  catch (IOException e) {
  			  getLogger().warning("Failed to close the streams! (I/O -> Input)");
  			  e.printStackTrace();
  		  }
  	  }
	}
 
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if ((sender instanceof Player)) {
			Player p = (Player)sender;
			if ((args.length == 1) && (args[0].equalsIgnoreCase("admin"))) {
				if (p.hasPermission("menubook.admin")) {
					if (p.getInventory().firstEmpty() != -1) {
						p.getInventory().addItem(new ItemStack[] { this.bookItemAdmin });
					}
					sender.sendMessage(ChatColor.GREEN + "Item added!");
				} else {
					sender.sendMessage(ChatColor.RED + "You do not have the permission to do this!");
				}
 
			}
			else if (p.hasPermission("menubook.get")) {
				if (p.getInventory().firstEmpty() != -1) {
					p.getInventory().addItem(new ItemStack[] { this.bookItem });
				}
				sender.sendMessage(ChatColor.GREEN + "Item added!");
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have the permission to do this!");
			}
		}
		else {
			sender.sendMessage("This command can only be used ingame");
		}
		return true;
	}
}	
