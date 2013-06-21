package nl.itsjustbjarn.MenuBook;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;


import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
 
 public class MenuBookListener
   implements Listener
 {
   private MenuBook plugin;
 
   public MenuBookListener(MenuBook instance)
   {
	this.plugin = instance;
   }
 
   @EventHandler
   public void onInventoryClick(InventoryClickEvent event)
   {
     if (event.getInventory().getTitle().equals(this.plugin.bookInv.getTitle())) {
       fireGUI(event, this.plugin.itemsAndCommands);
     }
     else if (event.getInventory().getTitle().equals(this.plugin.bookInvAdmin.getTitle()))
       fireGUI(event, this.plugin.itemsAndCommandsAdmin);
   }
 
   private void fireGUI(InventoryClickEvent event, HashMap<String, String> map)
   {
     Player player = (Player)event.getWhoClicked();
     if (event.getCurrentItem() == null) {
       event.setCancelled(true);
       return;
     }
     String item = event.getCurrentItem().getType().name();
 
     if (map.containsKey(item)) {
       String command = (String)map.get(item);
 
       event.setCancelled(true);
       player.closeInventory();
       player.performCommand(command);
     }
   }
 
   @EventHandler
   public void onPlayerJoin(PlayerJoinEvent event)
   {
     Player player = event.getPlayer();
     if (!player.hasPlayedBefore())
         player.getInventory().addItem(new ItemStack[] { this.plugin.bookItem });
     else if (player.hasPermission("menubook.admin"))
         player.getInventory().addItem(new ItemStack[] { this.plugin.bookItem });
         player.getInventory().addItem(new ItemStack[] { this.plugin.bookItemAdmin });
     if (!player.getInventory().contains(this.plugin.bookItem))
       event.getPlayer().getInventory().addItem(new ItemStack[] { this.plugin.bookItem });
   }
 
   @EventHandler
   public void onItemDrop(PlayerDropItemEvent event)
   {
     if ((event.getItemDrop().getItemStack().equals(this.plugin.bookItem)) || (event.getItemDrop().getItemStack().equals(this.plugin.bookItemAdmin)))
      event.setCancelled(true);
   }
 
   @EventHandler
   public void onPlayerInteract(PlayerInteractEvent event)
   {
    if ((event.hasItem()) && ((event.getAction() == Action.LEFT_CLICK_AIR) || (event.getAction() == Action.RIGHT_CLICK_BLOCK) || (event.getAction() == Action.LEFT_CLICK_BLOCK) || (event.getAction() == Action.RIGHT_CLICK_AIR))) {
       Player player = event.getPlayer();
       if (event.getItem().equals(this.plugin.bookItem))
         player.openInventory(this.plugin.bookInv);
       else if ((event.getItem().equals(this.plugin.bookItemAdmin)) && (player.hasPermission("menubook.admin")))
         player.openInventory(this.plugin.bookInvAdmin);
     }
	}
}