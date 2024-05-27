package markisha.items;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;

public class RecoloringItemsCR {

	private static final List<Material> DYES = Arrays.asList(Material.WHITE_DYE, Material.ORANGE_DYE,
			Material.MAGENTA_DYE, Material.LIGHT_BLUE_DYE, Material.YELLOW_DYE, Material.LIME_DYE, Material.PINK_DYE,
			Material.GRAY_DYE, Material.LIGHT_GRAY_DYE, Material.CYAN_DYE, Material.PURPLE_DYE, Material.BLUE_DYE,
			Material.BROWN_DYE, Material.GREEN_DYE, Material.RED_DYE, Material.BLACK_DYE);

	private static final List<Material> CONCRETE_POWDER_LIST = Arrays.asList(Material.WHITE_CONCRETE_POWDER,
			Material.ORANGE_CONCRETE_POWDER, Material.MAGENTA_CONCRETE_POWDER, Material.LIGHT_BLUE_CONCRETE_POWDER,
			Material.YELLOW_CONCRETE_POWDER, Material.LIME_CONCRETE_POWDER, Material.PINK_CONCRETE_POWDER,
			Material.GRAY_CONCRETE_POWDER, Material.LIGHT_GRAY_CONCRETE_POWDER, Material.CYAN_CONCRETE_POWDER,
			Material.PURPLE_CONCRETE_POWDER, Material.BLUE_CONCRETE_POWDER, Material.BROWN_CONCRETE_POWDER,
			Material.GREEN_CONCRETE_POWDER, Material.RED_CONCRETE_POWDER, Material.BLACK_CONCRETE_POWDER);

	private static final List<Material> CONCRETE_LIST = Arrays.asList(Material.WHITE_CONCRETE, Material.ORANGE_CONCRETE,
			Material.MAGENTA_CONCRETE, Material.LIGHT_BLUE_CONCRETE, Material.YELLOW_CONCRETE, Material.LIME_CONCRETE,
			Material.PINK_CONCRETE, Material.GRAY_CONCRETE, Material.LIGHT_GRAY_CONCRETE, Material.CYAN_CONCRETE,
			Material.PURPLE_CONCRETE, Material.BLUE_CONCRETE, Material.BROWN_CONCRETE, Material.GREEN_CONCRETE,
			Material.RED_CONCRETE, Material.BLACK_CONCRETE);

	private static final List<Material> CANDLES = Arrays.asList(Material.WHITE_CANDLE, Material.ORANGE_CANDLE,
			Material.MAGENTA_CANDLE, Material.LIGHT_BLUE_CANDLE, Material.YELLOW_CANDLE, Material.LIME_CANDLE,
			Material.PINK_CANDLE, Material.GRAY_CANDLE, Material.LIGHT_GRAY_CANDLE, Material.CYAN_CANDLE,
			Material.PURPLE_CANDLE, Material.BLUE_CANDLE, Material.BROWN_CANDLE, Material.GREEN_CANDLE,
			Material.RED_CANDLE, Material.BLACK_CANDLE);

	private static final List<Material> GLASS_LIST = Arrays.asList(Material.WHITE_STAINED_GLASS,
			Material.ORANGE_STAINED_GLASS, Material.MAGENTA_STAINED_GLASS, Material.LIGHT_BLUE_STAINED_GLASS,
			Material.YELLOW_STAINED_GLASS, Material.LIME_STAINED_GLASS, Material.PINK_STAINED_GLASS,
			Material.GRAY_STAINED_GLASS, Material.LIGHT_GRAY_STAINED_GLASS, Material.CYAN_STAINED_GLASS,
			Material.PURPLE_STAINED_GLASS, Material.BLUE_STAINED_GLASS, Material.BROWN_STAINED_GLASS,
			Material.GREEN_STAINED_GLASS, Material.RED_STAINED_GLASS, Material.BLACK_STAINED_GLASS);

	private static final List<Material> GLASS_PANES = Arrays.asList(Material.WHITE_STAINED_GLASS_PANE,
			Material.ORANGE_STAINED_GLASS_PANE, Material.MAGENTA_STAINED_GLASS_PANE,
			Material.LIGHT_BLUE_STAINED_GLASS_PANE, Material.YELLOW_STAINED_GLASS_PANE,
			Material.LIME_STAINED_GLASS_PANE, Material.PINK_STAINED_GLASS_PANE, Material.GRAY_STAINED_GLASS_PANE,
			Material.LIGHT_GRAY_STAINED_GLASS_PANE, Material.CYAN_STAINED_GLASS_PANE,
			Material.PURPLE_STAINED_GLASS_PANE, Material.BLUE_STAINED_GLASS_PANE, Material.BROWN_STAINED_GLASS_PANE,
			Material.GREEN_STAINED_GLASS_PANE, Material.RED_STAINED_GLASS_PANE, Material.BLACK_STAINED_GLASS_PANE);

	public void init() {
		String concretePowder = findBlockTypeInString(CONCRETE_POWDER_LIST.get(0));
		String concrete = findBlockTypeInString(CONCRETE_LIST.get(0));
		String glass = findBlockTypeInString(GLASS_LIST.get(0));
		String candle = findBlockTypeInString(CANDLES.get(0));
		String glassPane = findBlockTypeInString(GLASS_PANES.get(0));

		for (Material color : DYES) {
			String colorType = findColorInString(color);

			createShapedRecipe(concretePowder, CONCRETE_POWDER_LIST, colorType, color);
			createShapedRecipe(concrete, CONCRETE_LIST, colorType, color);
			createShapedRecipe(glass, GLASS_LIST, colorType, color);

			createShapelessRecipe(glassPane, GLASS_PANES, colorType, color);
			createShapelessRecipe(candle, CANDLES, colorType, color);
		}
	}

	private void createShapedRecipe(String blockType, List<Material> blockList, String colorType, Material color) {
		String newColoredBlock = colorType + "_" + blockType;
		ItemStack result = new ItemStack(Material.getMaterial(newColoredBlock), 8);

		String recipeId = blockType.toLowerCase() + "_" + color.name().toLowerCase();
		ShapedRecipe sr = new ShapedRecipe(NamespacedKey.minecraft(recipeId), result);

		sr.shape("###", "#.#", "###");
		RecipeChoice itemChoice = new RecipeChoice.MaterialChoice(blockList);
		sr.setIngredient('#', itemChoice);
		sr.setIngredient('.', color);
		sr.setGroup(newColoredBlock.substring(newColoredBlock.indexOf("_") + 1) + "s");
		sr.setCategory(CraftingBookCategory.BUILDING);

		Bukkit.getServer().addRecipe(sr);
	}

	private void createShapelessRecipe(String blockType, List<Material> blockList, String colorType, Material color) {
		String newColoredBlock = colorType + "_" + blockType;
		ItemStack result = new ItemStack(Material.getMaterial(newColoredBlock));

		String recipeId = blockType.toLowerCase() + "_" + color.name().toLowerCase();
		ShapelessRecipe sr = new ShapelessRecipe(NamespacedKey.minecraft(recipeId), result);

		RecipeChoice itemChoice = new RecipeChoice.MaterialChoice(blockList);
		sr.addIngredient(itemChoice);
		sr.addIngredient(color);
		sr.setGroup(newColoredBlock.substring(newColoredBlock.indexOf("_") + 1) + "s");
		sr.setCategory(CraftingBookCategory.BUILDING);

		Bukkit.getServer().addRecipe(sr);
	}

	private String findBlockTypeInString(Material material) {
		return material.name().replace(findColorInString(material) + "_", "");
	}

	private String findColorInString(Material material) {
		String[] tokens = material.name().split("_");
		String oldColor;
		if (tokens[0].toLowerCase().equals("light")) {
			oldColor = tokens[0] + "_" + tokens[1];
		} else {
			oldColor = tokens[0];
		}

		return oldColor;
	}

}
