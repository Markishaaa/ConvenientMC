package markisha.config;

import org.bukkit.plugin.java.JavaPlugin;

import markisha.constants.ConfigConstants;
import markisha.events.AllowEnchantments;
import markisha.events.BreedableParrots;
import markisha.events.CamelVillagerTransport;
import markisha.events.CauldronEnhancedDispenser;
import markisha.events.CombatImprovements;
import markisha.events.DoubleDoorInteract;
import markisha.events.StrippedLogCrafting;
import markisha.events.UndyeTerracotta;
import markisha.items.MoreYieldCR;
import markisha.items.NewItemsCR;
import markisha.items.RecoloringItemsCR;
import markisha.items.StrippedLogCR;

public class FeatureManager {

	private final JavaPlugin plugin;
	private final FeatureConfigManager configManager;
	
	public FeatureManager(JavaPlugin plugin, FeatureConfigManager configManager) {
		this.plugin = plugin;
		this.configManager = configManager;
	}
	
	public void initializeFeatures() {
		if (configManager.isEnabled(ConfigConstants.STRIPPED_LOG_CRAFTING)) {
			StrippedLogCR slcr = new StrippedLogCR();
			slcr.init();
			plugin.getServer().getPluginManager().registerEvents(new StrippedLogCrafting(), plugin);
		}

		if (configManager.isEnabled(ConfigConstants.MORE_YIELD_IN_CRAFTING_RECIPES)) {
			MoreYieldCR mycr = new MoreYieldCR();
			mycr.init();
		}

		if (configManager.isEnabled(ConfigConstants.NEW_CRAFTING_RECIPES)) {
			NewItemsCR nicr = new NewItemsCR();
			nicr.init();
		}

		if (configManager.isEnabled(ConfigConstants.RECOLORING_ITEMS)) {
			RecoloringItemsCR ricr = new RecoloringItemsCR();
			ricr.init();
		}
		
		if (configManager.isEnabled(ConfigConstants.WASHING_TERRACOTTA)) {
			plugin.getServer().getPluginManager().registerEvents(new UndyeTerracotta(plugin), plugin);
		}
		
		if (configManager.isEnabled(ConfigConstants.MODIFIED_ENCHANTMENTS)) {
			plugin.getServer().getPluginManager().registerEvents(new AllowEnchantments(), plugin);
		}

		if (configManager.isEnabled(ConfigConstants.COMBAT_IMPROVEMENTS)) {
			plugin.getServer().getPluginManager().registerEvents(new CombatImprovements(plugin), plugin);
		}

		if (configManager.isEnabled(ConfigConstants.CAMEL_VILLAGER_TRANSPORT)) {
			plugin.getServer().getPluginManager().registerEvents(new CamelVillagerTransport(plugin), plugin);
		}

		if (configManager.isEnabled(ConfigConstants.CAULDRON_ENHANCED_DISPENSERS)) {
			plugin.getServer().getPluginManager().registerEvents(new CauldronEnhancedDispenser(), plugin);
		}
		
		if (configManager.isEnabled(ConfigConstants.BREEDABLE_PARROTS)) {
			plugin.getServer().getPluginManager().registerEvents(new BreedableParrots(plugin), plugin);
		}
		
		if (configManager.isEnabled(ConfigConstants.DOUBLE_DOOR_INTERACT)) {
			plugin.getServer().getPluginManager().registerEvents(new DoubleDoorInteract(), plugin);
		}
	}
	
}
