package markisha.convenientMC;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import markisha.events.AllowEnchantments;
import markisha.events.BreedableParrots;
import markisha.events.CamelVillagerTransport;
import markisha.events.CauldronEnhancedDispenser;
import markisha.events.CombatImprovements;
import markisha.events.StrippedLogCrafting;
import markisha.events.UndyeTerracotta;
import markisha.items.MoreYieldCR;
import markisha.items.NewItemsCR;
import markisha.items.RecoloringItemsCR;
import markisha.items.StrippedLogCR;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class Main extends JavaPlugin {

	private ConfigurationSection features;
	
	public Main() {
		if (getConfig().getConfigurationSection("features") == null) {
			getConfig().createSection("features");
			saveConfig();
		}
		
		this.features = getConfig().getConfigurationSection("features");
		
		setDefaultBoolean("enable_stripped_log_crafting", true);
		setDefaultBoolean("enable_more_yield_in_crafting_recipes", true);
		setDefaultBoolean("enable_new_crafting_recipes", true);
		setDefaultBoolean("enable_modified_enchantments", true);
		setDefaultBoolean("enable_combat_improvements", true);
		setDefaultBoolean("enable_camel_villager_transport", true);
		setDefaultBoolean("enable_cauldron_enhanced_dispensers", true);
		setDefaultBoolean("enable_washing_terracotta", true);
		setDefaultBoolean("enable_recoloring_items", true);
		setDefaultBoolean("enable_breedable_parrots", true);
	}

	private void setDefaultBoolean(String key, boolean defaultValue) {
		if (!features.contains(key)) {
			features.set(key, defaultValue);
		}
	}

	@Override
	public void onEnable() {
		loadConfig();

		boolean enableStrippedLogCrafting = features.getBoolean("enable_stripped_log_crafting", true);
		boolean enableMoreYieldInCraftingRecipes = features.getBoolean("enable_more_yield_in_crafting_recipes", true);
		boolean enableNewCraftingRecipes = features.getBoolean("enable_new_crafting_recipes", true);
		boolean enableModifiedEnchantments = features.getBoolean("enable_modified_enchantments", true);
		boolean enableCombatImprovements = features.getBoolean("enable_combat_improvements", true);
		boolean enableCamelVillagerTransport = features.getBoolean("enable_camel_villager_transport", true);
		boolean enableCauldronEnhancedDispensers = features.getBoolean("enable_cauldron_enhanced_dispensers", true);
		boolean enableWashingTerracotta = features.getBoolean("enable_washing_terracotta", true);
		boolean enableRecoloringItems = features.getBoolean("enable_recoloring_items", true);
		boolean enableBreedableParrots = features.getBoolean("enable_breedable_parrots", true);

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

		if (enableRecoloringItems) {
			RecoloringItemsCR ricr = new RecoloringItemsCR();
			ricr.init();
		}
		
		if (enableWashingTerracotta) {
			getServer().getPluginManager().registerEvents(new UndyeTerracotta(this), this);
		}
		
		if (enableModifiedEnchantments) {
			getServer().getPluginManager().registerEvents(new AllowEnchantments(), this);
		}

		if (enableCombatImprovements) {
			getServer().getPluginManager().registerEvents(new CombatImprovements(this), this);
		}

		if (enableCamelVillagerTransport) {
			getServer().getPluginManager().registerEvents(new CamelVillagerTransport(this), this);
		}

		if (enableCauldronEnhancedDispensers) {
			getServer().getPluginManager().registerEvents(new CauldronEnhancedDispenser(), this);
		}
		
		if (enableBreedableParrots) {
			getServer().getPluginManager().registerEvents(new BreedableParrots(this), this);
		}
		
		getServer().getConsoleSender().sendMessage(
				Component.text("[ConvenientMC]: Plugin enabled!").color(TextColor.fromHexString("#FFFF00")));
	}

	@Override
	public void onDisable() {
		getServer().getConsoleSender().sendMessage(
				Component.text("[ConvenientMC]: Plugin disabled!").color(TextColor.fromHexString("#FFFF00")));
	}

	private void loadConfig() {
		getConfig().options().copyDefaults(false);
		saveConfig();
	}

}
