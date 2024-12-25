package markisha.events;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;

public class CombatImprovements implements Listener {

	private Plugin plugin;
	private Map<Entity, Projectile> projectileMap;

	public CombatImprovements(Plugin plugin) {
		this.plugin = plugin;
		this.projectileMap = new HashMap<Entity, Projectile>();
	}

	@EventHandler
	public void fixBlockingDelay(PlayerJoinEvent event) {
		HumanEntity player = event.getPlayer();

		if (!player.hasMetadata("shield_blocking_delay_set")) {
			player.setShieldBlockingDelay(0);
			player.setMetadata("shield_blocking_delay_set", new FixedMetadataValue(plugin, true));
		}
	}
	
	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		Projectile projectile = event.getEntity();
		ProjectileSource source = projectile.getShooter();

		if (!(source instanceof Entity))
			return;
		
		Entity shooter = (Entity) source;
		
		projectileMap.put(shooter, projectile);

		new BukkitRunnable() {
			@Override
			public void run() {
				projectileMap.remove(shooter);
			}
		}.runTaskLater(plugin, 20 * 10);
	}

	@EventHandler
	public void onPlayerKnockback(EntityKnockbackByEntityEvent event) {
		if (event.getEntityType() != EntityType.PLAYER)
			return;
		
		Player player = (Player) event.getEntity();
		int protectionLevel = getProjectileProtectionLevel(player);
		
		if (protectionLevel == 0)
			return;

		if (event.getPushedBy() instanceof Projectile || projectileMap.containsKey(event.getHitBy())) {
			Vector vector = event.getKnockback();
			double lengthSquared = vector.lengthSquared();
			double adjustedLengthSquared = adjustLengthSquared(lengthSquared, protectionLevel);
			
			event.setKnockback(setAdjustedLengthSquared(vector, adjustedLengthSquared));
		}
	}
	
	private int getProjectileProtectionLevel(Player player) {
		ItemStack[] armorContents = player.getInventory().getArmorContents();
		
		int maxLevel = 0;
		
		for (ItemStack armorPiece : armorContents) {
			if (armorPiece == null) continue;
			
			int level = armorPiece.getEnchantmentLevel(Enchantment.PROJECTILE_PROTECTION);
			
			if (level > maxLevel) {
				maxLevel = level;
			}
		}
		
		return maxLevel;
	}
	
	private double adjustLengthSquared(double lengthSquared, int protectionLevel) {
		double reductionFraction = protectionLevel / 4.0; // 0.25 for lvl1, 0.50 for lvl2 etc.
		double reductionAmount = lengthSquared * reductionFraction;
		double adjustedLengthSquared = lengthSquared - reductionAmount;
		
		return Math.max(adjustedLengthSquared, 0);
	}
	
	public static Vector setAdjustedLengthSquared(Vector vector, double adjustedLengthSquared) {
		double newMagnitude = Math.sqrt(adjustedLengthSquared);
		Vector unitVector = vector.clone().normalize();
		Vector adjustedVector = unitVector.multiply(newMagnitude);
		
		return adjustedVector;
	}

}
