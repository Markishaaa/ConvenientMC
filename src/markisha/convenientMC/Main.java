package markisha.convenientMC;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import markisha.events.AllowEnchantments;
import markisha.events.CauldronEnhancedDispenser;
import markisha.events.CombatImprovements;
import markisha.events.EntityImprovements;
import markisha.events.StrippedLogCrafting;
import markisha.items.MoreYieldCR;
import markisha.items.NewItemsCR;
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
	    setDefaultBoolean("allow_modified_enchantments", true);
	    setDefaultBoolean("allow_combat_improvements", true);
	    setDefaultBoolean("allow_camel_villager_transport", true);
	    setDefaultBoolean("allow_cauldron_enhanced_dispensers", true);
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
		boolean allowModifiedEnchantments = features.getBoolean("allow_modified_enchantments", true);
		boolean allowCombatImprovements = features.getBoolean("features.allow_combat_improvements", true);
		boolean allowCamelVillagerTransport = features.getBoolean("features.allow_camel_villager_transport", true);
		boolean allowCauldronEnhancedDispensers = features.getBoolean("features.allow_cauldron_enhanced_dispensers", true);

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

		if (allowCombatImprovements) {
			getServer().getPluginManager().registerEvents(new CombatImprovements(this), this);
		}
		
		if (allowCamelVillagerTransport) {
			getServer().getPluginManager().registerEvents(new EntityImprovements(this), this);
		}
		
		if (allowCauldronEnhancedDispensers) {
			getServer().getPluginManager().registerEvents(new CauldronEnhancedDispenser(), this);
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
