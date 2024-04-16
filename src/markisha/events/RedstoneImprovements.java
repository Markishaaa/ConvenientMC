package markisha.events;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Levelled;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

import io.papermc.paper.event.block.BlockPreDispenseEvent;

public class RedstoneImprovements implements Listener {

	@EventHandler
	public void beforeDispensing(BlockPreDispenseEvent event) {
		if (event.getBlock().getType() != Material.DISPENSER)
			return;

		if (!hasBucketOrFilledBucket(event.getItemStack())) {
			event.setCancelled(false);
			return;
		}

		event.setCancelled(true);

		Dispenser dispenser = (Dispenser) event.getBlock().getState();
		ItemStack bucket = event.getItemStack();

		Directional directional = (Directional) dispenser.getBlockData();
		BlockFace facing = directional.getFacing();

		if (facing != BlockFace.DOWN) {
			event.setCancelled(false);
			return;
		}

		Block blockNext = dispenser.getBlock().getRelative(facing);

		if (blockNext.getType() == Material.WATER_CAULDRON || blockNext.getType() == Material.POWDER_SNOW_CAULDRON) {
			handleLevelledCauldron(blockNext, bucket, dispenser, event.getSlot());
		} else if (blockNext.getType() == Material.LAVA_CAULDRON) {
			handleFilledCauldron(blockNext, bucket, dispenser, event.getSlot());
		} else if (blockNext.getType() == Material.CAULDRON) {
			handleEmptyCauldron(blockNext, bucket, dispenser, event.getSlot());
		} else {
			event.setCancelled(false);
		}
	}

	private void handleLevelledCauldron(Block cauldron, ItemStack bucket, Dispenser dispenser, int slot) {
		Material material = bucket.getType();
		Levelled cauldronLevel = (Levelled) cauldron.getBlockData();
		ItemMeta bucketMeta = bucket.getItemMeta();

		if (cauldron.getType() == Material.WATER_CAULDRON && material == Material.GLASS_BOTTLE) {
			if (cauldronLevel.getLevel() > 1) {
				cauldronLevel.setLevel(cauldronLevel.getLevel() - 1);
				cauldron.setBlockData(cauldronLevel);
			} else {
				cauldron.setType(Material.CAULDRON);
			}

			ItemStack waterBottle = createWaterBottle();
			swapBuckets(waterBottle, dispenser, slot);
		} else if (cauldron.getType() == Material.WATER_CAULDRON && bucketMeta instanceof PotionMeta
				&& ((PotionMeta) bucketMeta).getBasePotionType() == PotionType.WATER) {
			if (cauldronLevel.getLevel() < cauldronLevel.getMaximumLevel()) {
				cauldronLevel.setLevel(cauldronLevel.getLevel() + 1);
				cauldron.setBlockData(cauldronLevel);
			}

			swapBuckets(new ItemStack(Material.GLASS_BOTTLE), dispenser, slot);
		} else if (material == Material.BUCKET) {
			if (cauldronLevel.getLevel() < cauldronLevel.getMaximumLevel())
				return;

			String cauldronType = cauldron.getType().name().replace("_CAULDRON", "");
			cauldron.setType(Material.CAULDRON);

			Material newBucket = Material.getMaterial(cauldronType + "_BUCKET");

			swapBuckets(new ItemStack(newBucket), dispenser, slot);
		} else {
			if (material == Material.GLASS_BOTTLE || bucketMeta instanceof PotionMeta)
				 return;
			
			swapCauldronContents(cauldron, bucket);

			if (material == Material.WATER_BUCKET || material == Material.POWDER_SNOW_BUCKET) {
				cauldronLevel = (Levelled) cauldron.getBlockData();
				cauldronLevel.setLevel(cauldronLevel.getMaximumLevel());
				cauldron.setBlockData(cauldronLevel);
			}

			swapBuckets(new ItemStack(Material.BUCKET), dispenser, slot);
		}
	}

	private void handleFilledCauldron(Block cauldron, ItemStack bucket, Dispenser dispenser, int slot) {
		Material material = bucket.getType();
		
		 if (material == Material.GLASS_BOTTLE || bucket.getItemMeta() instanceof PotionMeta)
			 return;

		if (material == Material.BUCKET) {
			String bucketType = cauldron.getType().name().replace("_CAULDRON", "_BUCKET");
			cauldron.setType(Material.CAULDRON);

			Material newBucket = Material.getMaterial(bucketType);

			swapBuckets(new ItemStack(newBucket), dispenser, slot);
		} else {
			swapCauldronContents(cauldron, bucket);
			if (material == Material.WATER_BUCKET || material == Material.POWDER_SNOW_BUCKET) {
				setMaximumCauldronLevel(cauldron);
			}

			swapBuckets(new ItemStack(Material.BUCKET), dispenser, slot);
		}
	}

	private void handleEmptyCauldron(Block cauldron, ItemStack bucket, Dispenser dispenser, int slot) {
		Material material = bucket.getType();
		ItemMeta bucketMeta = bucket.getItemMeta();

		if (bucketMeta instanceof PotionMeta && ((PotionMeta) bucketMeta).getBasePotionType() == PotionType.WATER) {
			cauldron.setType(Material.WATER_CAULDRON);

			swapBuckets(new ItemStack(Material.GLASS_BOTTLE), dispenser, slot);
		} else if (material == Material.WATER_BUCKET) {
			cauldron.setType(Material.WATER_CAULDRON);

			setMaximumCauldronLevel(cauldron);
			
			swapBuckets(new ItemStack(Material.BUCKET), dispenser, slot);
		} else if (material == Material.LAVA_BUCKET) {
			cauldron.setType(Material.LAVA_CAULDRON);
			
			swapBuckets(new ItemStack(Material.BUCKET), dispenser, slot);
		} else if (material == Material.POWDER_SNOW_BUCKET) {
			cauldron.setType(Material.POWDER_SNOW_CAULDRON);
			setMaximumCauldronLevel(cauldron);
			
			swapBuckets(new ItemStack(Material.BUCKET), dispenser, slot);
		}
	}

	private void setMaximumCauldronLevel(Block cauldron) {
		Levelled cauldronLevel = (Levelled) cauldron.getBlockData();
		cauldronLevel.setLevel(cauldronLevel.getMaximumLevel());
		cauldron.setBlockData(cauldronLevel);
	}

	private boolean hasBucketOrFilledBucket(ItemStack item) {
		Material itemType = item.getType();
		ItemMeta itemMeta = item.getItemMeta();

		if (itemType == Material.BUCKET || itemType == Material.WATER_BUCKET || itemType == Material.LAVA_BUCKET
				|| itemType == Material.POWDER_SNOW_BUCKET || itemType == Material.GLASS_BOTTLE
				|| (itemMeta instanceof PotionMeta
						&& ((PotionMeta) itemMeta).getBasePotionType() == PotionType.WATER)) {
			return true;
		}

		return false;
	}

	private void swapBuckets(ItemStack newBucket, Dispenser dispenser, int slot) {
		Inventory dispenserInventory = dispenser.getInventory();

		if (dispenserInventory.getStorageContents()[slot].getAmount() > 1) {
			dispenserInventory.getStorageContents()[slot].add(-1);
		} else {
			dispenserInventory.clear(slot);
		}

		dispenserInventory.addItem(newBucket);
	}

	private void swapCauldronContents(Block cauldron, ItemStack bucket) {
		String cauldronType = bucket.getType().name().replace("_BUCKET", "_CAULDRON");
		Material newCauldron = Material.getMaterial(cauldronType);
		cauldron.setType(newCauldron);
	}

	private ItemStack createWaterBottle() {
		ItemStack waterBottle = new ItemStack(Material.POTION);
		PotionMeta potionMeta = (PotionMeta) waterBottle.getItemMeta();
		potionMeta.setBasePotionType(PotionType.WATER);
		waterBottle.setItemMeta(potionMeta);
		return waterBottle;
	}

}
