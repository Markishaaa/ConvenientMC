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
	private final List<Material> TRAPDOORS = Arrays.asList(Material.ACACIA_TRAPDOOR, Material.BAMBOO_TRAPDOOR,
			Material.BIRCH_TRAPDOOR, Material.CHERRY_TRAPDOOR, Material.CRIMSON_TRAPDOOR, Material.DARK_OAK_TRAPDOOR,
			Material.JUNGLE_TRAPDOOR, Material.MANGROVE_TRAPDOOR, Material.OAK_TRAPDOOR, Material.SPRUCE_TRAPDOOR,
			Material.WARPED_TRAPDOOR);

	public void init() {
		for (Material stair : STAIRS) {
			updateWoodenStairsRecipe(stair, "  #", " ##", "###");
			updateWoodenStairsRecipe(stair, "#  ", "## ", "###");
		}
		
		for (Material trapdoor : TRAPDOORS) {
			updateWoodenTrapdoorRecipe(trapdoor, "1", "   ", "###", "###");
			updateWoodenTrapdoorRecipe(trapdoor, "2",  "###", "###", "   ");
		}
	}

	private void updateWoodenStairsRecipe(Material stairType, String... shape) {
		ItemStack result = new ItemStack(stairType, 6);

		String stairId = stairType.name().toLowerCase();
		Bukkit.getServer().removeRecipe(NamespacedKey.minecraft(stairId));

		ShapedRecipe sr = new ShapedRecipe(NamespacedKey.minecraft(stairId), result);

		sr.shape(shape);
		sr.setIngredient('#', getPlankType(stairType.name()));

		Bukkit.getServer().addRecipe(sr);
	}

	private Material getPlankType(String plankType) {
		int endIndex = plankType.lastIndexOf("_STAIRS");
		String plank = plankType.substring(0, endIndex);
		String plankTypeName = plank + "_PLANKS";

		try {
			return Material.valueOf(plankTypeName.toUpperCase());
		} catch (IllegalArgumentException e) {
			// because BAMBOO_MOSAIC != BAMBO_MOSAIC_PLANKS
			return Material.valueOf(plank.toUpperCase());
		}
	}

	private void updateWoodenTrapdoorRecipe(Material trapdoorType, String key, String... shape) {
		ItemStack result = new ItemStack(trapdoorType, 4);

		String trapdoorId = trapdoorType.name().toLowerCase();
		Bukkit.getServer().removeRecipe(NamespacedKey.minecraft(trapdoorId));

		ShapedRecipe sr = new ShapedRecipe(NamespacedKey.minecraft(trapdoorId + "_" + key), result);

		sr.shape(shape);
		sr.setIngredient('#', getTrapdoorType(trapdoorType.name()));

		Bukkit.getServer().addRecipe(sr);
	}
	
	private Material getTrapdoorType(String trapdoorType) {
		int endIndex = trapdoorType.lastIndexOf("_TRAPDOOR");
		String trapdoor = trapdoorType.substring(0, endIndex);
		String trapdoorTypeName = trapdoor + "_PLANKS";

		try {
			return Material.valueOf(trapdoorTypeName.toUpperCase());
		} catch (IllegalArgumentException e) {
			return Material.valueOf(trapdoor.toUpperCase());
		}
	}

}
