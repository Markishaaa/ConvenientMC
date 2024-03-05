package markisha.items;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class WoodenItemCR {

	private final List<Material> STAIRS = Arrays.asList(Material.ACACIA_STAIRS, Material.BAMBOO_MOSAIC_STAIRS,
			Material.BAMBOO_STAIRS, Material.BIRCH_STAIRS, Material.CHERRY_STAIRS, Material.CRIMSON_STAIRS,
			Material.DARK_OAK_STAIRS, Material.JUNGLE_STAIRS, Material.MANGROVE_STAIRS, Material.OAK_STAIRS,
			Material.SPRUCE_STAIRS, Material.WARPED_STAIRS);

	public void init() {
		for (Material stair : STAIRS)
			updateWoodenStairsRecipe(stair);
	}

	private void updateWoodenStairsRecipe(Material stairType) {
		ItemStack result = new ItemStack(stairType, 6);

		String stairId = stairType.name().toLowerCase();
		Bukkit.getServer().removeRecipe(NamespacedKey.minecraft(stairId));

		ShapedRecipe sr = new ShapedRecipe(NamespacedKey.minecraft(stairId), result);

		sr.shape("  #", " ##", "###");
		sr.shape("#  ", "## ", "###");
		sr.setIngredient('#', getPlankType(stairType.name()));
		
		Bukkit.getServer().addRecipe(sr);
	}
	
	private Material getPlankType(String plankType) {
		int endIndex = plankType.lastIndexOf("_STAIRS");
		String plank = plankType.substring(0, endIndex);
		String plankTypeName = plank + "_PLANKS";
		
		if (plank.equals("BAMBOO_MOSAIC"))
			plankTypeName = plank;
		
		try {
			return Material.valueOf(plankTypeName.toUpperCase());
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

}
