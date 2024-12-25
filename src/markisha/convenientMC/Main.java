package markisha.convenientMC;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import markisha.config.FeatureConfigManager;
import markisha.config.FeatureManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class Main extends JavaPlugin {

	private FeatureConfigManager configManager;
	private FeatureManager featureManager;

	@Override
	public void onEnable() {
		loadConfig();

		FileConfiguration config = getConfig();
		configManager = new FeatureConfigManager(config);
		configManager.setDefaults();
		
		featureManager = new FeatureManager(this, configManager);
		featureManager.initializeFeatures();
		
		getServer().getConsoleSender().sendMessage(
				Component.text("[ConvenientMC]: Plugin enabled!").color(NamedTextColor.GREEN));
	}

	@Override
	public void onDisable() {
		getServer().getConsoleSender().sendMessage(
				Component.text("[ConvenientMC]: Plugin disabled!").color(NamedTextColor.GREEN));
	}

	private void loadConfig() {
		getConfig().options().copyDefaults(false);
		saveConfig();
	}

}
