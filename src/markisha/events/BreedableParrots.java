package markisha.events;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.destroystokyo.paper.event.entity.ThrownEggHatchEvent;

import net.kyori.adventure.text.Component;

public class BreedableParrots implements Listener {

	private final Plugin plugin;
	private final Random random;

	private final Map<UUID, Long> fedParrots;
	private final Set<UUID> pairedParrots;
	
	private final Set<UUID> playerEggThrows;
	private final Map<UUID, Integer> playerEgg;

	public BreedableParrots(Plugin plugin) {
		this.plugin = plugin;
		this.random = new Random();
		this.fedParrots = new HashMap<>();
		this.pairedParrots = new HashSet<>();
		this.playerEggThrows = new HashSet<>();
		this.playerEgg = new HashMap<>();
	}

	@EventHandler
	public void onFeedParrot(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		ItemStack mainHandItem = player.getInventory().getItemInMainHand();

		if (!(mainHandItem.getType() == Material.BEETROOT_SEEDS))
			return;
		if (!(event.getRightClicked() instanceof Parrot))
			return;

		Parrot parrot = (Parrot) event.getRightClicked();

		event.setCancelled(true);

		if (pairedParrots.contains(parrot.getUniqueId()))
			return;

		fedParrots.put(parrot.getUniqueId(), System.currentTimeMillis());
		parrot.getWorld().spawnParticle(Particle.HEART, parrot.getLocation().add(0, 1, 0), 10, 0.5, 0.5, 0.5, 0.1);

		if (player.getGameMode() == GameMode.SURVIVAL) {
			mainHandItem.setAmount(mainHandItem.getAmount() - 1);
		}

		new BukkitRunnable() {

			@Override
			public void run() {
				if (findAndBreedParrots(parrot)) {
					cancel();
				}
			}
		}.runTaskTimer(plugin, 20, 20 * 10);
	}

	@EventHandler
	public void onEggInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();

		if (item == null || item.getType() != Material.EGG)
			return;

		ItemMeta meta = item.getItemMeta();
		if (meta == null || !meta.hasDisplayName() || !meta.hasCustomModelData())
			return;
		
		int variantOrdinal = meta.getCustomModelData();

		playerEggThrows.add(player.getUniqueId());
		playerEgg.put(player.getUniqueId(), variantOrdinal);
	}

	@EventHandler
	public void onEggThrow(PlayerEggThrowEvent event) {
		Player player = event.getPlayer();
		if (!playerEggThrows.contains(player.getUniqueId()))
			return;
		if (!playerEgg.containsKey(player.getUniqueId()))
			return;
		
		Parrot.Variant variant = Parrot.Variant.values()[playerEgg.get(player.getUniqueId())];

		Egg egg = event.getEgg();
		((Parrot) egg.getWorld().spawnEntity(egg.getLocation(), EntityType.PARROT)).setVariant(variant);

		playerEggThrows.remove(player.getUniqueId());
		playerEgg.remove(player.getUniqueId());
		playerEggThrows.add(egg.getUniqueId());
	}

	@EventHandler
	public void onEggHatch(ThrownEggHatchEvent event) {
		Egg egg = event.getEgg();
		if (!playerEggThrows.contains(egg.getUniqueId()))
			return;

		event.setNumHatches((byte) 1);
		event.setHatchingType(EntityType.PARROT);
		
		event.setHatching(false);

		playerEggThrows.remove(egg.getUniqueId());
	}

	private boolean findAndBreedParrots(Parrot parrot) {
		UUID parrotId = parrot.getUniqueId();

		for (UUID fedParrotId : fedParrots.keySet()) {
			if (parrotId.equals(fedParrotId))
				continue;

			Parrot fedParrot = (Parrot) plugin.getServer().getEntity(fedParrotId);
			if (fedParrot == null)
				continue;

			Long parrotFedTime = fedParrots.get(parrotId);
			Long fedParrotFedTime = fedParrots.get(fedParrotId);

			if (parrotFedTime == null || fedParrotFedTime == null)
				continue;

			double distance = parrot.getLocation().distance(fedParrot.getLocation());
			if (distance <= 10 && System.currentTimeMillis() - parrotFedTime <= 10000
					&& System.currentTimeMillis() - fedParrotFedTime <= 10000) {
				pairParrots(parrotId, fedParrotId);

				parrot.setSitting(false);
				fedParrot.setSitting(false);
				parrot.setTarget(fedParrot);
				fedParrot.setTarget(parrot);

				dropParrotEgg(parrot.getVariant(), fedParrot.getVariant(), fedParrot);

				return true;
			}
		}

		return false;
	}

	private void pairParrots(UUID parrotId, UUID fedParrotId) {
		fedParrots.remove(parrotId);
		fedParrots.remove(fedParrotId);

		pairedParrots.add(parrotId);
		pairedParrots.add(fedParrotId);

		new BukkitRunnable() {
			@Override
			public void run() {
				pairedParrots.remove(parrotId);
				pairedParrots.remove(fedParrotId);
			}
		}.runTaskLater(plugin, 5 * 60 * 20); // 5 minutes breeding cooldown
	}

	private void dropParrotEgg(Parrot.Variant variant1, Parrot.Variant variant2, Parrot parrot) {
		int totalChance = 100;
		int randomNumber = random.nextInt(totalChance);

		Parrot.Variant selectedVariant;
		if (randomNumber < 50) {
			selectedVariant = variant1;
		} else {
			selectedVariant = variant2;
		}

		ItemStack egg = new ItemStack(Material.EGG);
		ItemMeta meta = egg.getItemMeta();
		meta.displayName(Component.text("Parrot Egg"));
		meta.setCustomModelData(selectedVariant.ordinal());
		egg.setItemMeta(meta);

		new BukkitRunnable() {
			@Override
			public void run() {
				parrot.getWorld().dropItem(parrot.getLocation(), egg);
				parrot.getWorld().playSound(parrot.getLocation(), Sound.ENTITY_ITEM_PICKUP, SoundCategory.NEUTRAL, 1f,
						1f);
			}
		}.runTaskLater(plugin, 5 * 20);

	}

}