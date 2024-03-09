package markisha.events;

import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class AllowEnchantments implements Listener {

	ItemStack trident;

	public AllowEnchantments() {
		this.trident = null;
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
					int level = -1;

					level = enchantments.entrySet().stream()
							.filter(entry -> entry.getKey().equals(Enchantment.LOOT_BONUS_MOBS)).findFirst()
							.map(Map.Entry::getValue).orElse(0);

					if (level == 0)
						return;

					trident = item1.clone();
					int tridentLootingLvl = trident.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);

					if (tridentLootingLvl == level && level != 3) {
						player.setGameMode(GameMode.CREATIVE);
						trident.addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, level + 1);
					} else if (tridentLootingLvl < level) {
						player.setGameMode(GameMode.CREATIVE);
						trident.addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, level);
					}

					event.setResult(trident);
				}
			}
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player))
			return;

		if (event.getInventory().getType() == InventoryType.ANVIL && trident != null) {
			event.getWhoClicked().setGameMode(GameMode.SURVIVAL);
			trident = null;
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if (event.getInventory().getType() == InventoryType.ANVIL && trident != null) {
			event.getPlayer().setGameMode(GameMode.SURVIVAL);
			trident = null;
		}
	}

}
