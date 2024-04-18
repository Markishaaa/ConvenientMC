package markisha.events;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class UndyeTerracotta implements Listener {

	private final Plugin plugin;

	private static final List<Material> TERRACOTTA_LIST = Arrays.asList(Material.WHITE_TERRACOTTA,
			Material.ORANGE_TERRACOTTA, Material.MAGENTA_TERRACOTTA, Material.LIGHT_BLUE_TERRACOTTA,
			Material.YELLOW_TERRACOTTA, Material.LIME_TERRACOTTA, Material.PINK_TERRACOTTA, Material.GRAY_TERRACOTTA,
			Material.LIGHT_GRAY_TERRACOTTA, Material.CYAN_TERRACOTTA, Material.PURPLE_TERRACOTTA,
			Material.BLUE_TERRACOTTA, Material.BROWN_TERRACOTTA, Material.GREEN_TERRACOTTA, Material.RED_TERRACOTTA,
			Material.BLACK_TERRACOTTA);

	public UndyeTerracotta(Plugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		Item item = event.getItemDrop();

		if (!TERRACOTTA_LIST.contains(item.getItemStack().getType()))
			return;

		new BukkitRunnable() {
			private int ticksElapsed = 0;

			@Override
			public void run() {
				Block cauldron = item.getLocation().getBlock();

				if (cauldron.getType() != Material.AIR && item.isOnGround()
						&& cauldron.getType() == Material.WATER_CAULDRON) {
					processItem(item, cauldron);
					cancel();
				} else if (ticksElapsed >= 5 * 20) {
					cancel();
				} else {
					ticksElapsed++;
				}
			}
		}.runTaskTimer(plugin, 0L, 1L);
	}

	private void processItem(Item item, Block cauldron) {
		Levelled cauldronLevel = (Levelled) cauldron.getBlockData();
		int level = cauldronLevel.getLevel();
		int amount = item.getItemStack().getAmount();

		int itemsPerLevel = item.getItemStack().getMaxStackSize() / 3;
		int waterToConsume = Math.min((amount + itemsPerLevel - 1) / itemsPerLevel, 3);
		boolean washed = false;

		if (level == cauldronLevel.getMaximumLevel() && amount == item.getItemStack().getMaxStackSize()) {
			cauldron.setType(Material.CAULDRON);
			item.setItemStack(new ItemStack(Material.TERRACOTTA, amount));
			washed = true;
		} else if (level >= waterToConsume) {
			int newLevel = level - waterToConsume;
			if (newLevel > 0) {
				cauldronLevel.setLevel(newLevel);
				cauldron.setBlockData(cauldronLevel);
			} else {
				cauldron.setType(Material.CAULDRON);
			}

			int washedAmount = Math.min(amount, waterToConsume * itemsPerLevel);

			if (washedAmount > 0) {
				ItemStack washedTerracotta = new ItemStack(Material.TERRACOTTA, washedAmount);
				item.getWorld().dropItem(item.getLocation(), washedTerracotta);
				washed = true;
			}

			if (amount > washedAmount) {
				item.getItemStack().setAmount(amount - washedAmount);
				washed = true;
			} else {
				item.remove();
			}
		}
		
		if (washed)
			cauldron.getWorld().playSound(cauldron.getLocation(), Sound.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1f, 1f);
	}

}
