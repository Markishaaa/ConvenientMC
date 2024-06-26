package markisha.events;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Camel;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class CamelVillagerTransport implements Listener {

	private final Plugin plugin;
	private final Set<Villager> processingVillagers;
	private final Map<Player, Villager> interactingVillagers;
	
	
	public CamelVillagerTransport(Plugin plugin) {
		this.plugin = plugin;
		this.processingVillagers = new HashSet<>();
		this.interactingVillagers = new HashMap<>();
	}

	@EventHandler
	public void onVillagerInteract(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		Entity rightClicked = event.getRightClicked();

		if (rightClicked instanceof Villager && rightClicked.getVehicle() instanceof Camel) {
			event.setCancelled(true);

			Villager villager = (Villager) event.getRightClicked();
			Camel camel = (Camel) villager.getVehicle();

			camel.removePassenger(villager);
			interactingVillagers.remove(player);
			villager.getWorld().playSound(villager.getLocation(), Sound.ENTITY_VILLAGER_YES, SoundCategory.NEUTRAL, 1f,
					1f);

			return;
		}

		if (!(player.getVehicle() instanceof Camel))
			return;
		if (!(rightClicked instanceof Villager))
			return;

		Villager villager = (Villager) event.getRightClicked();

		if (processingVillagers.contains(villager) || interactingVillagers.containsKey(player))
			return;

		Camel camel = (Camel) player.getVehicle();

		if (camel.getPassengers().size() == 2)
			return;

		event.setCancelled(true);

		villager.getWorld().playSound(villager.getLocation(), Sound.ENTITY_VILLAGER_CELEBRATE, SoundCategory.NEUTRAL,
				1f, 1f);

		interactingVillagers.put(player, villager);
		processingVillagers.add(villager);
		pathFindToCamel(villager, camel, player);
	}

	private void pathFindToCamel(Villager villager, Camel camel, Player player) {
		villager.setTarget(camel);

		new BukkitRunnable() {
			private int ticks = 0;
			private final int MAX_TICKS = 20;

			@Override
			public void run() {
				if (!villager.isValid() || !camel.isValid()
						|| villager.getLocation().distanceSquared(camel.getLocation()) > 10 * 10
						|| ticks >= MAX_TICKS) {
					villager.setTarget(null);

					processingVillagers.remove(villager);
					interactingVillagers.remove(player);
					cancel();
					return;
				}

				ticks++;

				if (villager.getLocation().distanceSquared(camel.getLocation()) <= 3) {
					camel.addPassenger(villager);
					villager.setTarget(null);
					villager.getWorld().playSound(villager.getLocation(), Sound.ENTITY_VILLAGER_CELEBRATE,
							SoundCategory.NEUTRAL, 1f, 1f);

					processingVillagers.remove(villager);
					interactingVillagers.remove(player);
					cancel();
				}
			}
		}.runTaskTimer(plugin, 0, 20);
	}

}
