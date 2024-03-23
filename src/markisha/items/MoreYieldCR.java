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

	private static final List<Material> BUTTONS = Arrays.asList(Material.ACACIA_BUTTON, Material.BAMBOO_BUTTON,
			Material.BIRCH_BUTTON, Material.CHERRY_BUTTON, Material.CRIMSON_BUTTON, Material.DARK_OAK_BUTTON,
			Material.JUNGLE_BUTTON, Material.MANGROVE_BUTTON, Material.OAK_BUTTON, Material.POLISHED_BLACKSTONE_BUTTON,
			Material.SPRUCE_BUTTON, Material.STONE_BUTTON, Material.WARPED_BUTTON);

	private static final List<Material> BANNERS = Arrays.asList(Material.BLACK_BANNER, Material.BLUE_BANNER,
			Material.BROWN_BANNER, Material.CYAN_BANNER, Material.GRAY_BANNER, Material.GRAY_BANNER,
			Material.GREEN_BANNER, Material.LIGHT_BLUE_BANNER, Material.LIGHT_GRAY_BANNER, Material.LIME_BANNER,
			Material.MAGENTA_BANNER, Material.ORANGE_BANNER, Material.ORANGE_BANNER, Material.PINK_BANNER,
			Material.PURPLE_BANNER, Material.RED_BANNER, Material.WHITE_BANNER, Material.YELLOW_BANNER);

	public void init() {
		for (Material stair : STAIRS) {
			updateWoodenStairsRecipe(stair, "  #", " ##", "###");
			updateWoodenStairsRecipe(stair, "#  ", "## ", "###");
		}

		for (Material trapdoor : TRAPDOORS) {
			updateWoodenTrapdoorRecipe(trapdoor);
		}

		for (Material button : BUTTONS) {
			updateButtonRecipes(button);
		}

		for (Material banner : BANNERS) {
			updateBannerRecipes(banner);
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

	private void updateWoodenTrapdoorRecipe(Material trapdoorType) {
		ItemStack result = new ItemStack(trapdoorType, 4);

		String trapdoorId = trapdoorType.name().toLowerCase();
		Bukkit.getServer().removeRecipe(NamespacedKey.minecraft(trapdoorId));

		ShapedRecipe sr = new ShapedRecipe(NamespacedKey.minecraft(trapdoorId), result);

		sr.shape("###", "###");
		sr.setGroup("wooden_trapdoors");
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
		sr.setCategory(CraftingBookCategory.REDSTONE);

		Bukkit.getServer().addRecipe(sr);
	}

	private void updateBannerRecipes(Material bannerType) {
		ItemStack result = new ItemStack(bannerType, 4);

		String bannerId = bannerType.name().toLowerCase();
		Bukkit.getServer().removeRecipe(NamespacedKey.minecraft(bannerId));

		ShapedRecipe sr = new ShapedRecipe(NamespacedKey.minecraft(bannerId), result);

		sr.shape("###", "###", " / ");
		sr.setIngredient('#', getWoolIngredientType(bannerType.name()));
		sr.setIngredient('/', Material.STICK);
		sr.setCategory(CraftingBookCategory.MISC);

		Bukkit.getServer().addRecipe(sr);
	}

	private Material getWoolIngredientType(String type) {
		String color = type.substring(0, type.lastIndexOf('_'));

		return Material.valueOf(color.toUpperCase() + "_WOOL");
	}

	private Material getButtonIngredientType(String type) {
		int endIndex = type.lastIndexOf("_");
		type = type.substring(0, endIndex);

		// because bamboo exists and you need bamboo planks for buttons
		if (type.equalsIgnoreCase("BAMBOO")) {
			return Material.valueOf(type.toUpperCase() + "_PLANKS");
		}

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
