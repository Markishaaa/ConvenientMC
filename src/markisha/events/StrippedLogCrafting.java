package markisha.events;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.meta.Damageable;

import markisha.items.StrippedLogCR;

public class StrippedLogCrafting implements Listener {

	private ItemStack axe;
	private ItemStack logs;

	private Random random;

	public StrippedLogCrafting() {
		this.random = new Random();
	}

	@EventHandler
	public void beforeStrippedLogCrafted(PrepareItemCraftEvent event) {
		if (event.isRepair() || event.getRecipe() == null)
			return;

		RecipeChoice axeChoice = new RecipeChoice.MaterialChoice(StrippedLogCR.AXES);
		List<ItemStack> logsItems = StrippedLogCR.LOGS.stream().map(ItemStack::new).collect(Collectors.toList());
		RecipeChoice logChoice = new RecipeChoice.ExactChoice(logsItems);

		if (event.getRecipe().getResult().getType().name().contains("STRIPPED_")) {
			ItemStack[] matrix = event.getInventory().getMatrix();
			for (int i = 0; i < matrix.length; i++) {
				ItemStack item = matrix[i];
				if (item != null) {
					if (axeChoice.test(item)) {
						axe = item;
					} else if (logChoice.test(item)) {
						logs = item;
					}
				}
			}
		}
	}

	@EventHandler
	public void onStrippedLogCrafted(CraftItemEvent event) {
		CraftingInventory craftingInventory = (CraftingInventory) event.getInventory();
		ItemStack result = craftingInventory.getResult();

		if (result == null || !result.getType().name().contains("STRIPPED_") || logs == null || axe == null)
			return;

		event.setCancelled(true);

		Player player = (Player) event.getWhoClicked();

		Damageable dmgAxe = (Damageable) axe.getItemMeta();
		int dmgAmount = 0;

		if (dmgAxe.getDamage() == axe.getType().getMaxDurability() - 1) {
			player.sendMessage(ChatColor.YELLOW + "[ConvenientMC]: Your axe is about to break.");
			craftingInventory.setResult(null);
			return;
		}

		if (event.isShiftClick()) {
			dmgAmount = craftWithShiftClickAndGetAxeDamage(event, craftingInventory, result.getType());
		} else {
			ItemStack cursorItem = player.getItemOnCursor();

			if (cursorItem.getType().equals(Material.AIR)) {
				dmgAmount = 1;
				player.setItemOnCursor(result);

				logs.setAmount(logs.getAmount() - 1);
			} else if (cursorItem.getType().equals(result.getType()) && cursorItem.getAmount() < 64) {
				dmgAmount = 1;
				ItemStack item = player.getItemOnCursor();
				item.setAmount(item.getAmount() + 1);
				player.setItemOnCursor(item);

				logs.setAmount(logs.getAmount() - 1);
			}
		}

		dmgAmount = getDamageAmountBasedOnChance(dmgAxe, dmgAmount);
		dmgAxe.setDamage(dmgAxe.getDamage() + dmgAmount);
		axe.setItemMeta(dmgAxe);

		if (logs.getAmount() == 0) {
			craftingInventory.setResult(null);
			dmgAmount = 0;
		}
	}

	private int craftWithShiftClickAndGetAxeDamage(CraftItemEvent event, CraftingInventory craftingInventory,
			Material strippedLog) {
		int dmgAmount = 0;
		int logAmount = logs.getAmount();

		PlayerInventory playerInventory = event.getWhoClicked().getInventory();
		int logSlotWithSpace = findLogsSlotWithSpace(playerInventory, strippedLog);

		Damageable dmgAxe = (Damageable) axe.getItemMeta();
		int axeDurability = axe.getType().getMaxDurability() - dmgAxe.getDamage();

		int amountToCraft = Math.min(axeDurability - 1, logAmount);

		if (logSlotWithSpace == -1) {
			int emptySlot = findEmptySlot(playerInventory);

			if (emptySlot == -1 || logAmount <= 0)
				return 0;

			dmgAmount += stripAllLogs(strippedLog, playerInventory, emptySlot);

			return dmgAmount;
		}

		ItemStack invLog = playerInventory.getItem(logSlotWithSpace);
		int spaceRemaining = 64 - invLog.getAmount();

		if (spaceRemaining <= 0)
			return 0;

		int logsToTransfer = Math.min(amountToCraft, spaceRemaining);

		logs.setAmount(logAmount - logsToTransfer);
		invLog.setAmount(invLog.getAmount() + logsToTransfer);
		dmgAmount += logsToTransfer;

		int emptySlot = findEmptySlot(playerInventory);

		if (emptySlot == 1 || logs.getAmount() <= 0)
			return dmgAmount;

		stripAllLogs(strippedLog, playerInventory, emptySlot);

		return amountToCraft;
	}

	private int getDamageAmountBasedOnChance(Damageable axe, int n) {
		int dmgAmount = 0;
		double chance = getAxeDamageChance(axe);

		for (int i = 0; i < n; i++) {
			if (random.nextDouble() < chance / 100.0)
				dmgAmount++;
		}

		return dmgAmount;
	}

	private int stripAllLogs(Material strippedLog, PlayerInventory playerInventory, int emptySlot) {
		Damageable dmgAxe = (Damageable) axe.getItemMeta();
		int axeDurability = axe.getType().getMaxDurability() - dmgAxe.getDamage();

		int logAmount = logs.getAmount();
		int amountToCraft = Math.min(axeDurability - 1, logAmount);

		ItemStack strippedLogs = new ItemStack(strippedLog);

		strippedLogs.setAmount(amountToCraft);
		logs.setAmount(logAmount - amountToCraft);

		playerInventory.setItem(emptySlot, strippedLogs);
		return amountToCraft;
	}

	private int findEmptySlot(PlayerInventory inventory) {
		ItemStack[] contents = inventory.getStorageContents();
		for (int i = 0; i < contents.length; i++) {
			ItemStack item = contents[i];
			if (item == null || item.getType() == Material.AIR) {
				return i;
			}
		}
		return -1;
	}

	private int findLogsSlotWithSpace(PlayerInventory inventory, Material resultMaterial) {
		ItemStack[] contents = inventory.getStorageContents();
		for (int i = 0; i < contents.length; i++) {
			ItemStack item = contents[i];
			if (item != null && item.getType().equals(resultMaterial) && item.getAmount() < 64) {
				return i;
			}
		}
		return -1;
	}

	private double getAxeDamageChance(Damageable axe) {
		return 100 / (axe.getEnchantLevel(Enchantment.DURABILITY) + 1);
	}

}
