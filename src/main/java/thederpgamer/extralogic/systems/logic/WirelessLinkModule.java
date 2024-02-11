package thederpgamer.extralogic.systems.logic;

import api.common.GameCommon;
import api.common.GameServer;
import api.mod.config.PersistentObjectUtil;
import api.utils.game.module.ModManagerContainerModule;
import api.utils.game.module.util.SimpleDataStorageMCModule;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.Ship;
import org.schema.game.common.controller.SpaceStation;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.data.ManagedSegmentController;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.game.common.data.world.SimpleTransformableSendableObject;
import org.schema.schine.graphicsengine.core.Timer;
import thederpgamer.extralogic.ExtraLogic;
import thederpgamer.extralogic.data.linkmodule.LinkChannel;
import thederpgamer.extralogic.data.linkmodule.LinkModuleRunnable;
import thederpgamer.extralogic.element.ElementManager;
import thederpgamer.extralogic.networking.client.ClientManager;
import thederpgamer.extralogic.utils.ServerUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;

/**
 * [Description]
 *
 * @author Garret Reichenbach
 */
public class WirelessLinkModule extends SimpleDataStorageMCModule {

	public static final HashMap<LinkChannel, LinkModuleRunnable> activationMap = new HashMap<>();
	private static final HashMap<WirelessLinkModuleData, SegmentPiece> moduleMapTemp = new HashMap<>();

	public static void initThread() {
		(new Thread("Wireless Link Module Activation Thread") {
			@Override
			public void run() {
				while(true) {
					if(GameCommon.isDedicatedServer() || GameCommon.isOnSinglePlayer()) {
						for(LinkModuleRunnable runnable : activationMap.values()) {
							runnable.run();
						}
						activationMap.clear();
					}
					try {
						Thread.sleep(100);
					} catch(InterruptedException exception) {
						ExtraLogic.getInstance().logException("WirelessLinkModule Exception", exception);
					}
				}
			}
		}).start();
	}

	public WirelessLinkModule(SegmentController ship, ManagerContainer<?> managerContainer) {
		super(ship, managerContainer, ExtraLogic.getInstance(), ElementManager.getBlock("Wireless Link Module").getId());
	}

	public static WirelessLinkModule getInstance(SegmentController segmentController) {
		ManagerContainer<?> managerContainer = null;
		if(segmentController.getType() == SimpleTransformableSendableObject.EntityType.SHIP) managerContainer = ((Ship) segmentController).getManagerContainer();
		else if(segmentController.getType() == SimpleTransformableSendableObject.EntityType.SPACE_STATION) managerContainer = ((SpaceStation) segmentController).getManagerContainer();
		if(managerContainer != null) {
			ModManagerContainerModule module = managerContainer.getModMCModule(ElementManager.getBlock("Wireless Link Module").getId());
			if(module instanceof WirelessLinkModule) return (WirelessLinkModule) module;
		}
		return null;
	}

	public static WirelessLinkModuleData getDataFromBlock(SegmentPiece segmentPiece) {
		try {
			ManagerContainer<?> managerContainer = null;
			if(segmentPiece.getSegmentController().getType() == SimpleTransformableSendableObject.EntityType.SHIP) managerContainer = ((Ship) segmentPiece.getSegmentController()).getManagerContainer();
			else if(segmentPiece.getSegmentController().getType() == SimpleTransformableSendableObject.EntityType.SPACE_STATION) managerContainer = ((SpaceStation) segmentPiece.getSegmentController()).getManagerContainer();
			if(managerContainer != null) {
				ModManagerContainerModule module = managerContainer.getModMCModule(segmentPiece.getType());
				if(module instanceof WirelessLinkModule) return ((WirelessLinkModule) module).getData(segmentPiece);
			}
		} catch(Exception ignored) {
		}
		return null;
	}

	public static boolean checkValid(WirelessLinkModuleData data) {
		if(data.hasChannel()) {
			LinkChannel channel = getChannel(data.channel.getId());
			if(channel == null) data.setChannel(null);
			else return true;
		}
		return false;
	}

	public static void addChannel(LinkChannel channel) {
		if(GameCommon.isDedicatedServer() || GameCommon.isOnSinglePlayer()) {
			PersistentObjectUtil.addObject(ExtraLogic.getInstance().getSkeleton(), channel);
			PersistentObjectUtil.save(ExtraLogic.getInstance().getSkeleton());
		}
	}

	public static LinkChannel getChannel(String id) {
		if(GameCommon.isDedicatedServer() || GameCommon.isOnSinglePlayer()) {
			for(Object object : PersistentObjectUtil.getObjects(ExtraLogic.getInstance().getSkeleton(), LinkChannel.class)) {
				LinkChannel channel = (LinkChannel) object;
				if(channel.getId().equals(id)) return channel;
			}
			return null;
		}
		return ClientManager.getChannel(id);
	}

	public static LinkChannel getChannel(SegmentPiece segmentPiece) {
		WirelessLinkModuleData data = getDataFromBlock(segmentPiece);
		if(data != null && data.hasChannel()) return data.getChannel();
		return null;
	}

	public static void updateChannel(LinkChannel channel) {
		if(GameCommon.isDedicatedServer() || GameCommon.isOnSinglePlayer()) {
			removeChannel(channel);
			PersistentObjectUtil.addObject(ExtraLogic.getInstance().getSkeleton(), channel);
			PersistentObjectUtil.save(ExtraLogic.getInstance().getSkeleton());
		} else ClientManager.updateChannel(channel);
	}

	public static void removeChannel(LinkChannel channel) {
		if(GameCommon.isDedicatedServer() || GameCommon.isOnSinglePlayer()) {
			for(LinkChannel obj : PersistentObjectUtil.getCopyOfObjects(ExtraLogic.getInstance().getSkeleton(), LinkChannel.class)) {
				if(obj.getId().equals(channel.getId())) PersistentObjectUtil.removeObject(ExtraLogic.getInstance().getSkeleton(), obj);
			}
			PersistentObjectUtil.save(ExtraLogic.getInstance().getSkeleton());
		} else ClientManager.requestChannelRemove(channel);
	}

	/**
	 * Toggles the active state of a channel.
	 *
	 * @param linkChannel The channel to toggle
	 * @param activatorData The data of the module that activated the channel
	 * @param activator The segment piece of the module that activated the channel
	 */
	public static void toggleChannel(LinkChannel linkChannel, WirelessLinkModuleData activatorData, SegmentPiece activator) {
		if(GameCommon.isDedicatedServer() || GameCommon.isOnSinglePlayer()) {
			if(activationMap.get(linkChannel) != null) return;
			for(SegmentController controller : GameServer.getServerState().getSegmentControllersByName().values()) {
				if(controller instanceof ManagedSegmentController<?>) {
					ManagerContainer<?> container = ServerUtils.getManagerContainer(controller);
					if(container != null) {
						WirelessLinkModule module = (WirelessLinkModule) container.getModMCModule(ElementManager.getBlock("Wireless Link Module").getId());
						for(WirelessLinkModuleData data : module.getDataMap().dataMap.values()) {
							if(data.hasChannel() && data.getChannel().getId().equals(linkChannel.getId()) && !data.equals(activatorData)) {
								if(data.getSegmentPiece() != null) moduleMapTemp.put(data, data.getSegmentPiece());
							}
						}
					}
				}
			}
			if(moduleMapTemp.isEmpty()) return;
			activationMap.put(linkChannel, new LinkModuleRunnable(activatorData, activator, moduleMapTemp));
			moduleMapTemp.clear();
		}
	}

	private SegmentPiece getSegmentPiece(WirelessLinkModuleData data) {
		return getManagerContainer().getSegmentController().getSegmentBuffer().getPointUnsave(ElementCollection.getPosIndexFrom4(data.indexAndOrientation));
	}

	@Override
	public String getName() {
		return "WirelessLinkModule";
	}

	@Override
	public void handle(Timer timer) {

	}

	public WirelessLinkModuleMap getDataMap() {
		if(!(data instanceof WirelessLinkModuleMap)) data = new WirelessLinkModuleMap();
		return (WirelessLinkModuleMap) data;
	}

	public WirelessLinkModuleData getData(SegmentPiece segmentPiece) {
		return getData(ElementCollection.getIndex4(segmentPiece.getAbsoluteIndex(), segmentPiece.getOrientation()));
	}

	public WirelessLinkModuleData getData(long indexAndOrientation) {
		if(getDataMap().dataMap.containsKey(indexAndOrientation)) return getDataMap().dataMap.get(indexAndOrientation);
		else return createNewData(indexAndOrientation);
	}

	public WirelessLinkModuleData createNewData(long indexAndOrientation) {
		long absIndex = ElementCollection.getPosIndexFrom4(indexAndOrientation);
		SegmentPiece segmentPiece = getManagerContainer().getSegmentController().getSegmentBuffer().getPointUnsave(absIndex);
		WirelessLinkModuleData data = new WirelessLinkModuleData(segmentPiece);
		getDataMap().dataMap.put(data.indexAndOrientation, data);
		flagUpdatedData();
		return data;
	}

	public static class WirelessLinkModuleMap {

		private final HashMap<Long, WirelessLinkModuleData> dataMap = new HashMap<>();
	}

	public class WirelessLinkModuleData implements Serializable {

		public long indexAndOrientation;
		public long entityID;
		public String linkID;
		public LinkChannel channel;

		public WirelessLinkModuleData() {

		}

		public WirelessLinkModuleData(SegmentPiece segmentPiece) {
			indexAndOrientation = ElementCollection.getIndex4(segmentPiece.getAbsoluteIndex(), segmentPiece.getOrientation());
			entityID = segmentPiece.getSegmentController().getDbId();
			linkID = UUID.randomUUID().toString();
		}

		public LinkChannel getChannel() {
			return channel;
		}

		public void setChannel(LinkChannel channel) {
			this.channel = channel;
		}

		public boolean hasChannel() {
			return channel != null;
		}

		@Override
		public boolean equals(Object obj) {
			if(obj instanceof WirelessLinkModuleData) {
				WirelessLinkModuleData data = (WirelessLinkModuleData) obj;
				return data.linkID.equals(linkID) && data.entityID == entityID && data.indexAndOrientation == indexAndOrientation;
			}
			return false;
		}

		public SegmentPiece getSegmentPiece() {
			try {
				SegmentController controller = ServerUtils.getSegmentControllerFromDBID(entityID);
				if(controller != null) {
					ManagerContainer<?> container = ServerUtils.getManagerContainer(controller);
					if(container != null) {
						return container.getSegmentController().getSegmentBuffer().getPointUnsave(ElementCollection.getPosIndexFrom4(indexAndOrientation));
					}
				}
//				return getManagerContainer().getSegmentController().getSegmentBuffer().getPointUnsave(ElementCollection.getPosIndexFrom4(indexAndOrientation));
			} catch(Exception exception) {
				ExtraLogic.getInstance().logException("WirelessLinkModuleData Exception", exception);
			}
			return null;
		}
	}
}
