package markisha.events;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Husk;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Trident;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class AllowEnchantments implements Listener {

	private Map<Player, ItemStack> tridentMap;
	private Map<Player, Integer> tridentProjLootingLvl;

	private final List<Material> RARE_DROPS = Arrays.asList(Material.GOLD_INGOT, Material.IRON_INGOT, Material.CARROT,
			Material.POTATO, Material.WITHER_SKELETON_SKULL);

	private Random random;

	public AllowEnchantments() {
		this.tridentMap = new HashMap<Player, ItemStack>();
		this.tridentProjLootingLvl = new HashMap<Player, Integer>();
		this.random = new Random();
	}

	@EventHandler
	public void onTridentLootingEnchant(PrepareAnvilEvent event) throws IllegalArgumentException {
		Inventory inventory = event.getInventory();
		Player player = (Player) event.getViewers().get(0);

		RecipeChoice tridentChoice = new RecipeChoice.MaterialChoice(Material.TRIDENT);
		RecipeChoice bookChoice = new RecipeChoice.MaterialChoice(Material.ENCHANTED_BOOK);

		if (inventory != null && inventory.getType().equals(InventoryType.ANVIL)) {
			ItemStack item1 = inventory.getItem(0);
			if (item1 != null && tridentChoice.test(item1)) {
				ItemStack item2 = inventory.getItem(1);
				if (item2 != null && bookChoice.test(item2)) {
					EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item2.getItemMeta();

					Map<Enchantment, Integer> enchantments = meta.getStoredEnchants();

					int level = enchantments.entrySet().stream()
							.filter(entry -> entry.getKey().equals(Enchantment.LOOT_BONUS_MOBS)).findFirst()
							.map(Map.Entry::getValue).orElse(0);

					if (level == 0)
						return;

					tridentMap.put(player, item1.clone());
					int tridentLootingLvl = tridentMap.get(player).getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);

					if (tridentLootingLvl == level && level != 3) {
						player.setGameMode(GameMode.CREATIVE);
						tridentMap.get(player).addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, level + 1);
					} else if (tridentLootingLvl < level) {
						player.setGameMode(GameMode.CREATIVE);
						tridentMap.get(player).addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, level);
					}

					event.setResult(tridentMap.get(player));
				}
			}
		}
	}
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		if (!(event.getHitEntity() instanceof LivingEntity))
			return;

		Projectile projectile = event.getEntity();
		LivingEntity entity = (LivingEntity) event.getHitEntity();

		if (projectile instanceof Trident) {
			Trident trident = (Trident) projectile;

			if (entity.getHealth() - trident.getDamage() <= 0) {

				ItemStack tridentItem = trident.getItem();

				tridentProjLootingLvl.put((Player) trident.getShooter(), tridentItem.getItemMeta().getEnchantLevel(Enchantment.LOOT_BONUS_MOBS));
			}
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		LivingEntity entity = event.getEntity();
		Player killer = entity.getKiller();
		
		if (tridentProjLootingLvl.get(killer) == null || tridentProjLootingLvl.get(killer) == 0)
			return;

		List<ItemStack> drops = event.getDrops();
		int additionalDrops = calculateAdditionalDrops(tridentProjLootingLvl.get(killer));

		for (int i = 0; i < drops.size(); i++) {
			ItemStack drop = drops.get(i);
			if (RARE_DROPS.contains(drop.getType())) {
				drops.remove(i);
			} else if (drop.getMaxStackSize() > 1) {
				drop.setAmount(drop.getAmount() + additionalDrops);
			}
		}

		Material rareDropMaterial = getRareDropMaterial(entity);

		if (rareDropMaterial != null) {
			int rareDropAmount = calculateRareDrops(tridentProjLootingLvl.get(killer));
			if (rareDropAmount > 0) {
				drops.add(new ItemStack(rareDropMaterial, rareDropAmount));
			}
		}

		tridentProjLootingLvl.put(killer, 0);
	}

	private int calculateAdditionalDrops(int lootingLevel) {
		if (lootingLevel == 1) {
			// 1/2 chance for 1 additional item
			return (Math.random() < 0.5) ? 1 : 0;
		} else if (lootingLevel == 2) {
			// 1/4 chance for 1 additional item, 1/2 for 2 additional items
			double random = Math.random();
			return (random < 0.25) ? 1 : (random < 0.75) ? 2 : 0;
		} else if (lootingLevel == 3) {
			// 1/6 chance for 1 additional item, 1/3 for 2 additional items, 1/3 for 3
			// additional items, 1/6 for 4 additional items
			double random = Math.random();
			return (random < 1.0 / 6.0) ? 1 : (random < 1.0 / 3.0) ? 2 : (random < 2.0 / 3.0) ? 3 : 4;
		} else {
			// No Looting or unrecognized level
			return 0;
		}
	}

	private int calculateRareDrops(int lootingLevel) {
		double baseChance = 0.025; // 2.5% base chance
		double additionalChancePerLevel = 0.01; // 1 percentage point per level

		if (lootingLevel > 0 && lootingLevel <= 3) {
			double random = Math.random();
			return (random < (baseChance + additionalChancePerLevel * lootingLevel)) ? 1 : 0;
		} else {
			return 0;
		}
	}

	private Material getRareDropMaterial(LivingEntity entity) {
		if (entity instanceof PigZombie) {
			return RARE_DROPS.get(0);
		} else if (entity instanceof Zombie || entity instanceof ZombieVillager || entity instanceof Husk) {
			int randomIndex = random.nextInt(3) + 1;
			return RARE_DROPS.get(randomIndex);
		} else if (entity instanceof WitherSkeleton) {
			return RARE_DROPS.get(4);
		}
		return null;
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player))
			return;

		Player player = (Player) event.getWhoClicked();

		if (event.getInventory().getType() == InventoryType.ANVIL && tridentMap.get(player) != null) {
			event.getWhoClicked().setGameMode(GameMode.SURVIVAL);
			tridentMap.put(player, null);
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if (!(event.getPlayer() instanceof Player))
			return;

		Player player = (Player) event.getPlayer();

		if (event.getInventory().getType() == InventoryType.ANVIL && tridentMap.get(player) != null) {
			event.getPlayer().setGameMode(GameMode.SURVIVAL);
			tridentMap.put(player, null);
		}
	}

}
