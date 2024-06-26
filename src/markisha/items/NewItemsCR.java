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
		
		createShapelessRecipe(new ItemStack(Material.SNOWBALL, 4), NamespacedKey.minecraft("snowballs"),
				CraftingBookCategory.BUILDING, Material.SNOW_BLOCK);
		
		createShapelessRecipe(new ItemStack(Material.STRING, 4), NamespacedKey.minecraft("strings"),
				CraftingBookCategory.BUILDING, Material.WHITE_WOOL);
		
		createMangroveLogRecipe();
		
		createBoneBlockRecipe();
	}

	private void createShapelessRecipe(ItemStack result, NamespacedKey key, CraftingBookCategory category,
			Material... ingredients) {
		ShapelessRecipe sr = new ShapelessRecipe(key, result);

		for (Material ingredient : ingredients) {
			sr.addIngredient(ingredient);
		}

		sr.setCategory(category);

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
	
	private void createBoneBlockRecipe() {
		ItemStack result = new ItemStack(Material.BONE_BLOCK, 3);
		
		ShapedRecipe sr = new ShapedRecipe(NamespacedKey.minecraft("bone_blocks"), result);
		
		sr.shape("###", "###", "###");
		sr.setIngredient('#', Material.BONE);
		sr.setCategory(CraftingBookCategory.BUILDING);
		
		Bukkit.getServer().addRecipe(sr);
	}

}