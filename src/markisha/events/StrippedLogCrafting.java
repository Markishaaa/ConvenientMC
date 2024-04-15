package markisha.events;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class StrippedLogCrafting implements Listener {

	private Map<Player, ItemStack> axe;
	private Map<Player, ItemStack> logs;

	private Random random;

	public StrippedLogCrafting() {
		this.random = new Random();
		this.axe = new HashMap<Player, ItemStack>();
		this.logs = new HashMap<Player, ItemStack>();
	}

	@EventHandler
	public void beforeStrippedLogCrafted(PrepareItemCraftEvent event) {
		if (event.isRepair() || event.getRecipe() == null)
			return;

		ItemStack result = event.getRecipe().getResult();

		if (!result.getType().name().contains("STRIPPED_") || (!result.getType().name().contains("_LOG")
				&& !result.getType().name().contains("_STEM") && !result.getType().name().contains("_BLOCK")))
			return;

		RecipeChoice axeChoice = new RecipeChoice.MaterialChoice(StrippedLogCR.AXES);
		List<ItemStack> logsItems = StrippedLogCR.LOGS.stream().map(ItemStack::new).collect(Collectors.toList());
		RecipeChoice logChoice = new RecipeChoice.ExactChoice(logsItems);
		Player player = (Player) event.getView().getPlayer();

		ItemStack[] matrix = event.getInventory().getMatrix();
		for (int i = 0; i < matrix.length; i++) {
			ItemStack item = matrix[i];
			if (item != null) {
				if (axeChoice.test(item)) {
					axe.put(player, item);
				} else if (logChoice.test(item)) {
					logs.put(player, item);
				}
			}
		}
	}

	@EventHandler
	public void onStrippedLogCrafted(CraftItemEvent event) {
		CraftingInventory craftingInventory = (CraftingInventory) event.getInventory();
		ItemStack result = craftingInventory.getResult();
		Player player = (Player) event.getWhoClicked();

		if (!result.getType().name().contains("STRIPPED_") || (!result.getType().name().contains("_LOG")
				&& !result.getType().name().contains("_STEM") && !result.getType().name().contains("_BLOCK")))
			return;

		if (result == null || logs.get(player) == null || axe.get(player) == null)
			return;

		event.setCancelled(true);

		Damageable dmgAxe = (Damageable) axe.get(player).getItemMeta();
		int dmgAmount = 0;

		if (dmgAxe.getDamage() == axe.get(player).getType().getMaxDurability() - 1) {
			player.sendMessage(Component.text("[ConvenientMC]: Your axe is about to break.").color(TextColor.fromHexString("#FFFF00")));
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

				logs.get(player).setAmount(logs.get(player).getAmount() - 1);
			} else if (cursorItem.getType().equals(result.getType()) && cursorItem.getAmount() < 64) {
				dmgAmount = 1;
				ItemStack item = player.getItemOnCursor();
				item.setAmount(item.getAmount() + 1);
				player.setItemOnCursor(item);

				logs.get(player).setAmount(logs.get(player).getAmount() - 1);
			}
		}

		dmgAmount = getDamageAmountBasedOnChance(dmgAxe, dmgAmount);
		dmgAxe.setDamage(dmgAxe.getDamage() + dmgAmount);
		axe.get(player).setItemMeta(dmgAxe);

		if (logs.get(player).getAmount() == 0) {
			craftingInventory.setResult(null);
			dmgAmount = 0;
		}
	}

	private int craftWithShiftClickAndGetAxeDamage(CraftItemEvent event, CraftingInventory craftingInventory,
			Material strippedLog) {
		Player player = (Player) event.getWhoClicked();

		int dmgAmount = 0;
		int logAmount = logs.get(player).getAmount();

		PlayerInventory playerInventory = player.getInventory();
		int logSlotWithSpace = findLogsSlotWithSpace(playerInventory, strippedLog);

		Damageable dmgAxe = (Damageable) axe.get(player).getItemMeta();
		int axeDurability = axe.get(player).getType().getMaxDurability() - dmgAxe.getDamage();

		int amountToCraft = Math.min(axeDurability - 1, logAmount);

		if (logSlotWithSpace == -1) {
			int emptySlot = findEmptySlot(playerInventory);

			if (emptySlot == -1 || logAmount <= 0)
				return 0;

			dmgAmount += stripAllLogs(strippedLog, player, playerInventory, emptySlot);

			return dmgAmount;
		}

		ItemStack invLog = playerInventory.getItem(logSlotWithSpace);
		int spaceRemaining = 64 - invLog.getAmount();

		if (spaceRemaining <= 0)
			return 0;

		int logsToTransfer = Math.min(amountToCraft, spaceRemaining);

		logs.get(player).setAmount(logAmount - logsToTransfer);
		invLog.setAmount(invLog.getAmount() + logsToTransfer);
		dmgAmount += logsToTransfer;

		int emptySlot = findEmptySlot(playerInventory);

		if (emptySlot == 1 || logs.get(player).getAmount() <= 0)
			return dmgAmount;

		stripAllLogs(strippedLog, player, playerInventory, emptySlot);

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

	private int stripAllLogs(Material strippedLog, Player player, PlayerInventory playerInventory, int emptySlot) {
		Damageable dmgAxe = (Damageable) axe.get(player).getItemMeta();
		int axeDurability = axe.get(player).getType().getMaxDurability() - dmgAxe.getDamage();

		int logAmount = logs.get(player).getAmount();
		int amountToCraft = Math.min(axeDurability - 1, logAmount);

		ItemStack strippedLogs = new ItemStack(strippedLog);

		strippedLogs.setAmount(amountToCraft);
		logs.get(player).setAmount(logAmount - amountToCraft);

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
