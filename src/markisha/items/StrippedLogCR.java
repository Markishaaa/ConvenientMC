package markisha.items;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;

public class StrippedLogCR {

	public final static List<Material> AXES = Arrays.asList(Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.GOLDEN_AXE,
			Material.DIAMOND_AXE, Material.NETHERITE_AXE);
	public final static List<Material> LOGS = Arrays.asList(Material.ACACIA_LOG, Material.BIRCH_LOG, Material.CHERRY_LOG, Material.DARK_OAK_LOG,
			Material.JUNGLE_LOG, Material.MANGROVE_LOG, Material.OAK_LOG, Material.SPRUCE_LOG, Material.CRIMSON_STEM, Material.WARPED_STEM);
	
	public void init() {
		for (Material axe : AXES) {
			for (Material log : LOGS) {
				createRecipe(log, axe);
			}
		}
	}
	
	private void createRecipe(Material logType, Material axeType) {
		Material strippedLogType = getStrippedLogType(logType);
		if (strippedLogType == null) {
			return;
		}

		NamespacedKey recipeKey = NamespacedKey.minecraft(strippedLogType.name().toLowerCase() + "_" + axeType.name().toLowerCase());
		ShapelessRecipe sr = new ShapelessRecipe(recipeKey, new ItemStack(strippedLogType, 1));
		sr.addIngredient(axeType);
		sr.addIngredient(logType);
		sr.setGroup("stripped_logs");
		sr.setCategory(CraftingBookCategory.BUILDING);

		Bukkit.getServer().addRecipe(sr);
	}

	private Material getStrippedLogType(Material logType) {
		String strippedLogTypeName = "STRIPPED_" + logType.name();
		try {
			return Material.valueOf(strippedLogTypeName.toUpperCase());
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}
