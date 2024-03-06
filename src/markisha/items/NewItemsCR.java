package markisha.items;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;

public class NewItemsCR {

	public void init() {
		createShapelessRecipe(new ItemStack(Material.CLAY_BALL, 4), NamespacedKey.minecraft("clay_balls"),
				CraftingBookCategory.BUILDING, Material.CLAY);
		
		createMangroveLogRecipe();
	}

	private void createShapelessRecipe(ItemStack result, NamespacedKey key, CraftingBookCategory category,
			Material... ingredients) {
		ShapelessRecipe sr = new ShapelessRecipe(key, result);

		for (Material ingredient : ingredients) {
			sr.addIngredient(ingredient);
		}

		sr.setCategory(CraftingBookCategory.BUILDING);

		Bukkit.getServer().addRecipe(sr);
	}
	
	private void createMangroveLogRecipe() {
		ItemStack result = new ItemStack(Material.MANGROVE_LOG, 2);
		
		ShapedRecipe sr = new ShapedRecipe(NamespacedKey.minecraft("mangrove_logs"), result);

		sr.shape("##", "##");
		sr.setIngredient('#', Material.MANGROVE_ROOTS);
		sr.setCategory(CraftingBookCategory.BUILDING);
		
		Bukkit.getServer().addRecipe(sr);
	}

}