package markisha.events;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.plugin.Plugin;

import markisha.items.MoreYieldCR;

public class MoreYieldCrafting implements Listener {

	private Plugin plugin;
	
	public MoreYieldCrafting(Plugin plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onTrapdoorCrafted(CraftItemEvent event) {
		if (event.getRecipe() == null)
			return;
		
		ItemStack result = event.getRecipe().getResult();
		Player p = (Player) event.getWhoClicked(); 
		
		RecipeChoice trapdoorChoice = new RecipeChoice.MaterialChoice(MoreYieldCR.TRAPDOORS);
		if (trapdoorChoice.test(result)) {
			NamespacedKey key = NamespacedKey.minecraft(result.getType().name().toLowerCase() + "_2");

			Bukkit.getScheduler().runTaskLater(plugin, () -> {
	            if (p.hasDiscoveredRecipe(key)) {
	                p.undiscoverRecipe(key);
	            }
	        }, 1L);
		}
	}
	
}
