package markisha.convenientMC;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import markisha.events.StrippedLogCrafting;
import markisha.items.StrippedLogCR;

public class Main extends JavaPlugin {

	@Override
	public void onEnable() {
		StrippedLogCR slcr = new StrippedLogCR();
		slcr.init();
		getServer().getPluginManager().registerEvents(new StrippedLogCrafting(), this);
		
		
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[ConvenientMC]: Plugin enabled!");
	}

	@Override
	public void onDisable() {
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[ConvenientMC]: Plugin disabled!");
	}
	
}
