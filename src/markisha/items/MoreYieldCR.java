package markisha.items;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;

public class MoreYieldCR {

	public static final List<Material> STAIRS = Arrays.asList(Material.ACACIA_STAIRS, Material.BAMBOO_MOSAIC_STAIRS,
			Material.BAMBOO_STAIRS, Material.BIRCH_STAIRS, Material.CHERRY_STAIRS, Material.CRIMSON_STAIRS,
			Material.DARK_OAK_STAIRS, Material.JUNGLE_STAIRS, Material.MANGROVE_STAIRS, Material.OAK_STAIRS,
			Material.SPRUCE_STAIRS, Material.WARPED_STAIRS);

	public static final List<Material> TRAPDOORS = Arrays.asList(Material.ACACIA_TRAPDOOR, Material.BAMBOO_TRAPDOOR,
			Material.BIRCH_TRAPDOOR, Material.CHERRY_TRAPDOOR, Material.CRIMSON_TRAPDOOR, Material.DARK_OAK_TRAPDOOR,
			Material.JUNGLE_TRAPDOOR, Material.MANGROVE_TRAPDOOR, Material.OAK_TRAPDOOR, Material.SPRUCE_TRAPDOOR,
			Material.WARPED_TRAPDOOR);

	public static final List<Material> BUTTONS = Arrays.asList(Material.ACACIA_BUTTON, Material.BAMBOO_BUTTON,
			Material.BIRCH_BUTTON, Material.CHERRY_BUTTON, Material.CRIMSON_BUTTON, Material.DARK_OAK_BUTTON,
			Material.JUNGLE_BUTTON, Material.MANGROVE_BUTTON, Material.OAK_BUTTON, Material.POLISHED_BLACKSTONE_BUTTON,
			Material.SPRUCE_BUTTON, Material.STONE_BUTTON, Material.WARPED_BUTTON);

	public void init() {
		for (Material stair : STAIRS) {
			updateWoodenStairsRecipe(stair, "  #", " ##", "###");
			updateWoodenStairsRecipe(stair, "#  ", "## ", "###");
		}

		for (Material trapdoor : TRAPDOORS) {
			updateWoodenTrapdoorRecipe(trapdoor, "1", "   ", "###", "###");
			updateWoodenTrapdoorRecipe(trapdoor, "2", "###", "###", "   ");
		}

		for (Material button : BUTTONS) {
			updateButtonRecipes(button);
		}
	}

	private void updateWoodenStairsRecipe(Material stairType, String... shape) {
		ItemStack result = new ItemStack(stairType, 6);

		String stairId = stairType.name().toLowerCase();
		Bukkit.getServer().removeRecipe(NamespacedKey.minecraft(stairId));

		ShapedRecipe sr = new ShapedRecipe(NamespacedKey.minecraft(stairId), result);

		sr.shape(shape);
		sr.setGroup("wooden_stairs");
		sr.setCategory(CraftingBookCategory.BUILDING);
		sr.setIngredient('#', getPlankType(stairType.name()));

		Bukkit.getServer().addRecipe(sr);
	}

	private void updateWoodenTrapdoorRecipe(Material trapdoorType, String key, String... shape) {
		ItemStack result = new ItemStack(trapdoorType, 4);

		String trapdoorId = trapdoorType.name().toLowerCase();
		Bukkit.getServer().removeRecipe(NamespacedKey.minecraft(trapdoorId));

		ShapedRecipe sr = new ShapedRecipe(NamespacedKey.minecraft(trapdoorId + "_" + key), result);

		sr.shape(shape);
		sr.setGroup("wooden_trapdoors_" + key);
		sr.setCategory(CraftingBookCategory.REDSTONE);
		sr.setIngredient('#', getPlankType(trapdoorType.name()));

		Bukkit.getServer().addRecipe(sr);
	}

	private void updateButtonRecipes(Material buttonType) {
		ItemStack result = new ItemStack(buttonType, 4);

		String buttonId = buttonType.name().toLowerCase();
		Bukkit.getServer().removeRecipe(NamespacedKey.minecraft(buttonId));

		ShapelessRecipe sr = new ShapelessRecipe(NamespacedKey.minecraft(buttonId), result);

		sr.addIngredient(getButtonIngredientType(buttonType.name()));
		sr.setGroup("REDSTONE");

		Bukkit.getServer().addRecipe(sr);
	}

	private Material getButtonIngredientType(String type) {
		int endIndex = type.lastIndexOf("_");
		type = type.substring(0, endIndex);

		try {
			return Material.valueOf(type.toUpperCase());
		} catch (IllegalArgumentException e) {
			return Material.valueOf(type.toUpperCase() + "_PLANKS");
		}
	}

	private Material getPlankType(String plankType) {
		int endIndex = plankType.lastIndexOf("_");
		String plank = plankType.substring(0, endIndex);
		String plankTypeName = plank + "_PLANKS";

		try {
			return Material.valueOf(plankTypeName.toUpperCase());
		} catch (IllegalArgumentException e) {
			// because BAMBOO_MOSAIC != BAMBO_MOSAIC_PLANKS
			return Material.valueOf(plank.toUpperCase());
		}
	}

}
