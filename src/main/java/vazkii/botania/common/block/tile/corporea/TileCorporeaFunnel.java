/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Feb 16, 2015, 2:18:30 PM (GMT)]
 */
package vazkii.botania.common.block.tile.corporea;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import vazkii.botania.api.corporea.CorporeaHelper;
import vazkii.botania.api.corporea.ICorporeaSpark;
import vazkii.botania.common.core.helper.InventoryHelper;
import vazkii.botania.common.lib.LibBlockNames;
import vazkii.botania.common.lib.LibMisc;

public class TileCorporeaFunnel extends TileCorporeaBase {

	public void doRequest() {
		ICorporeaSpark spark = getSpark();
		if(spark != null && spark.getMaster() != null) {
			IInventory inv = InventoryHelper.getInventory(worldObj, xCoord, yCoord - 1, zCoord);
			if(inv == null)
				inv = InventoryHelper.getInventory(worldObj, xCoord, yCoord - 2, zCoord);

			List<ItemStack> filter = getFilter();
			if(!filter.isEmpty()) {
				ItemStack stack = filter.get(worldObj.rand.nextInt(filter.size()));

				List<ItemStack> stacks = CorporeaHelper.requestItem(stack, spark, true, true);
				spark.onItemsRequested(stacks);
				for(ItemStack reqStack : stacks)
					if(stack != null) {
						if(inv != null && reqStack.stackSize == InventoryHelper.testInventoryInsertion(inv, stack, ForgeDirection.UP))
							InventoryHelper.insertItemIntoInventory(inv, reqStack);
						else {
							EntityItem item = new EntityItem(worldObj, xCoord + 0.5, yCoord + 1.5, zCoord + 0.5, stack);
							worldObj.spawnEntityInWorld(item);
						}
					}
			}
		}
	}

	public List<ItemStack> getFilter() {
		List<ItemStack> filter = new ArrayList();

		final int[] orientationToDir = new int[] {
				3, 4, 2, 5
		};

		for(ForgeDirection dir : LibMisc.CARDINAL_DIRECTIONS) {
			List<EntityItemFrame> frames = worldObj.getEntitiesWithinAABB(EntityItemFrame.class, AxisAlignedBB.getBoundingBox(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ, xCoord + dir.offsetX + 1, yCoord + dir.offsetY + 1, zCoord + dir.offsetZ + 1));
			for(EntityItemFrame frame : frames) {
				int orientation = frame.hangingDirection;
				if(orientationToDir[orientation] == dir.ordinal())
					filter.add(frame.getDisplayedItem());
			}
		}

		return filter;
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public String getInventoryName() {
		return LibBlockNames.CORPOREA_FUNNEL;
	}

}
