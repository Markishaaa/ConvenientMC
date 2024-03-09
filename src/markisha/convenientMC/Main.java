package markisha.convenientMC;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import markisha.events.AllowEnchantments;
import markisha.events.StrippedLogCrafting;
import markisha.items.MoreYieldCR;
import markisha.items.NewItemsCR;
import markisha.items.StrippedLogCR;

public class Main extends JavaPlugin {

	private ConfigurationSection features;

	public Main() {
		if (getConfig().getConfigurationSection("features") == null) {
			getConfig().set("features.enable_stripped_log_crafting", true);
			getConfig().set("features.enable_more_yield_in_crafting_recipes", true);
			getConfig().set("features.enable_new_crafting_recipes", true);
			getConfig().set("features.allow_modified_enchantments", true);
			saveConfig();
		}

		this.features = getConfig().getConfigurationSection("features");
	}

	@Override
	public void onEnable() {
		loadConfig();

		boolean enableStrippedLogCrafting = features.getBoolean("enable_stripped_log_crafting", true);
		boolean enableMoreYieldInCraftingRecipes = features.getBoolean("enable_more_yield_in_crafting_recipes", true);
		boolean enableNewCraftingRecipes = features.getBoolean("enable_new_crafting_recipes", true);
		boolean allowModifiedEnchantments = features.getBoolean("allow_modified_enchantments", true);

		if (enableStrippedLogCrafting) {
			StrippedLogCR slcr = new StrippedLogCR();
			slcr.init();
			getServer().getPluginManager().registerEvents(new StrippedLogCrafting(), this);
		}

		if (enableMoreYieldInCraftingRecipes) {
			MoreYieldCR mycr = new MoreYieldCR();
			mycr.init();
		}

		if (enableNewCraftingRecipes) {
			NewItemsCR nicr = new NewItemsCR();
			nicr.init();
		}

		if (allowModifiedEnchantments) {
			getServer().getPluginManager().registerEvents(new AllowEnchantments(), this);
		}

		getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[ConvenientMC]: Plugin enabled!");
	}

	@Override
	public void onDisable() {
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[ConvenientMC]: Plugin disabled!");
	}

	private void loadConfig() {
		getConfig().options().copyDefaults(false);
		saveConfig();
	}

}
