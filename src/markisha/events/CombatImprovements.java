package markisha.events;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

public class CombatImprovements implements Listener {

	private Plugin plugin;
	
	public CombatImprovements(Plugin plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void fixBlockingDelay(PlayerJoinEvent event) {
		HumanEntity player = event.getPlayer();

		if (!player.hasMetadata("shield_blocking_delay_set")) {
			player.setShieldBlockingDelay(0);
			player.setMetadata("shield_blocking_delay_set", new FixedMetadataValue(plugin, true));
		}
	}

}
