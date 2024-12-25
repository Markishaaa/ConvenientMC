package markisha.config;

import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import markisha.constants.ConfigConstants;

public class FeatureConfigManager {

	private final ConfigurationSection features;

	private static final Map<String, Boolean> DEFAULTS = Map.ofEntries(
	    Map.entry(ConfigConstants.STRIPPED_LOG_CRAFTING, true),
	    Map.entry(ConfigConstants.MORE_YIELD_IN_CRAFTING_RECIPES, true),
	    Map.entry(ConfigConstants.NEW_CRAFTING_RECIPES, true),
	    Map.entry(ConfigConstants.MODIFIED_ENCHANTMENTS, true),
	    Map.entry(ConfigConstants.COMBAT_IMPROVEMENTS, true),
	    Map.entry(ConfigConstants.CAMEL_VILLAGER_TRANSPORT, true),
	    Map.entry(ConfigConstants.CAULDRON_ENHANCED_DISPENSERS, true),
	    Map.entry(ConfigConstants.WASHING_TERRACOTTA, true),
	    Map.entry(ConfigConstants.RECOLORING_ITEMS, true),
	    Map.entry(ConfigConstants.BREEDABLE_PARROTS, true),
	    Map.entry(ConfigConstants.DOUBLE_DOOR_INTERACT, true)
	);

	public FeatureConfigManager(FileConfiguration config) {
		if (config.getConfigurationSection("features") == null) {
			config.createSection("features");
		}

		features = config.getConfigurationSection("features");
	}

	public void setDefaults() {
		DEFAULTS.forEach(this::setDefault);
	}
	
	private void setDefault(String key, boolean value) {
		if (!features.contains(key)) {
			features.set(key, value);
		}
	}

	public boolean isEnabled(String key) {
		return features.getBoolean(key, true);
	}

}
