package thederpgamer.extralogic.element.blocks;

import api.common.GameClient;
import api.listener.events.block.SegmentPieceActivateByPlayer;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.controller.elements.ManagerModuleCollection;
import org.schema.game.common.controller.elements.factory.CargoCapacityElementManagerInterface;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementCategory;
import org.schema.game.common.data.player.inventory.Inventory;
import org.schema.game.common.data.player.inventory.InventoryHolder;
import org.schema.game.network.objects.remote.RemoteInventory;
import thederpgamer.extralogic.utils.ServerUtils;

import java.lang.reflect.Field;

public abstract class InventoryBlock extends Block implements ActivationInterface {
	public InventoryBlock(String name, ElementCategory category) {
		super(name, category);
	}

	@Override
	public void onPlayerActivation(SegmentPieceActivateByPlayer event) {
		ManagerContainer<?> managerContainer = ServerUtils.getManagerContainer(event.getSegmentPiece().getSegmentController());
		assert managerContainer != null;
		Inventory inventory = managerContainer.getInventory(event.getSegmentPiece().getAbsoluteIndex());
		if(inventory == null) {
			inventory = createInventory(managerContainer, event.getSegmentPiece());
			addInventory(inventory, managerContainer, event.getSegmentPiece());
			inventory.getInventoryHolder().getInventoryNetworkObject().getInventoriesChangeBuffer().add(new RemoteInventory(inventory, inventory.getInventoryHolder(), true, inventory.getInventoryHolder().getInventoryNetworkObject().isOnServer()));
		} else inventory.clientUpdate();
		if(GameClient.getClientState() != null) GameClient.getClientState().getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().inventoryAction(inventory);
	}

	public abstract Inventory createInventory(InventoryHolder holder, SegmentPiece segmentPiece);

	private void addInventory(Inventory inventory, ManagerContainer<?> managerContainer, SegmentPiece segmentPiece) {
		try {
			Field field = managerContainer.getClass().getDeclaredField("delayedInventoryAdd");
			field.setAccessible(true);
			ObjectArrayList<Inventory> inventoryAddList = (ObjectArrayList<Inventory>) field.get(managerContainer);
			inventoryAddList.add(inventory);
		} catch(NoSuchFieldException | IllegalAccessException exception) {
			exception.printStackTrace();
		}
		try {
			ManagerModuleCollection<?, ?, ?> managerModule = managerContainer.getModulesControllerMap().get(segmentPiece.getType());
			if(managerModule instanceof CargoCapacityElementManagerInterface) {
				managerContainer.getModulesControllerMap().get(segmentPiece.getType()).addControllerBlockFromAddedBlock(segmentPiece.getAbsoluteIndex(), segmentPiece.getSegment(), true);
			}
		} catch(Exception exception) {
			exception.printStackTrace();
		}
		try {
			LongOpenHashSet set = managerContainer.getSegmentController().getControlElementMap().getControllingMap().getAll().get(segmentPiece.getAbsoluteIndex());
			if(set != null && set.size() > 0) {
				Field field = managerContainer.getClass().getDeclaredField("filterInventories");
				field.setAccessible(true);
				LongOpenHashSet hashSet = (LongOpenHashSet) field.get(managerContainer);
				hashSet.add(segmentPiece.getAbsoluteIndex());
			}
		} catch(NoSuchFieldException | IllegalAccessException exception) {
			exception.printStackTrace();
		}
	}
}
