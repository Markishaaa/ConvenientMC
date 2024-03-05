package markisha.items;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

public class StrippedLogCR {

	private ItemStack strippedLog;
	
	public StrippedLogCR() {
		this.strippedLog = createItem();
	}
	
	public void init() {
		createRecipe();
	}
	
	private ItemStack createItem() {
		ItemStack warpTome = new ItemStack(Material.STRIPPED_SPRUCE_LOG, 1);
		
		return warpTome;
	}
	
	private void createRecipe() {
		ShapelessRecipe sr = new ShapelessRecipe(NamespacedKey.minecraft("stripped_spruce_log"), strippedLog);
		sr.addIngredient(Material.DIAMOND_AXE);
		sr.addIngredient(Material.SPRUCE_LOG);
		
		Bukkit.getServer().addRecipe(sr);
	}
	
}
