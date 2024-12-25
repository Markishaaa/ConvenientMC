package markisha.events;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Door;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class DoubleDoorInteract implements Listener {

	@EventHandler
	public void playerInteractWithDoor(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		Block clickedBlock = event.getClickedBlock();

		if (!isBlockDoor(clickedBlock))
			return;

		handleDoorInteraction(clickedBlock);
	}
	
	private void handleDoorInteraction(Block block) {
		Door clickedDoor = (Door) block.getBlockData();

		Block adjacentDoorBlock = findAdjacentDoor(block, clickedDoor);
		if (adjacentDoorBlock != null) {
			Door adjacentDoor = (Door) adjacentDoorBlock.getBlockData();
			adjacentDoor.setOpen(!clickedDoor.isOpen());
			adjacentDoorBlock.setBlockData(adjacentDoor);
		}
	}

	private boolean isBlockDoor(Block block) {
		return block.getType().toString().contains("_DOOR");
	}

	private Block findAdjacentDoor(Block doorBlock, Door clickedDoor) {
		Block[] adjacentBlocks = { doorBlock.getRelative(BlockFace.NORTH), doorBlock.getRelative(BlockFace.EAST),
				doorBlock.getRelative(BlockFace.SOUTH), doorBlock.getRelative(BlockFace.WEST) };

		for (Block adjacentBlock : adjacentBlocks) {
			if (isBlockDoor(adjacentBlock)) {
				Door adjacentDoor = (Door) adjacentBlock.getBlockData();
				
				if (clickedDoor.getHalf() == adjacentDoor.getHalf() && isKnobsFacingInward(clickedDoor, adjacentDoor, doorBlock, adjacentBlock)) {
					return adjacentBlock;
				}
			}
		}

		return null;
	}

	private boolean isKnobsFacingInward(Door clickedDoor, Door adjacentDoor, Block clickedBlock, Block adjacentBlock) {
		BlockFace clickedFacing = clickedDoor.getFacing();
		BlockFace adjacentFacing = adjacentDoor.getFacing();

		Door.Hinge clickedHinge = clickedDoor.getHinge();
		Door.Hinge adjacentHinge = adjacentDoor.getHinge();

		// doors must face the same direction and their hinges must be opposite
		if ((clickedFacing == adjacentFacing) && ((clickedHinge == Door.Hinge.LEFT && adjacentHinge == Door.Hinge.RIGHT)
				|| (clickedHinge == Door.Hinge.RIGHT && adjacentHinge == Door.Hinge.LEFT))) {
			return true;
		}

		return false;
	}

}
