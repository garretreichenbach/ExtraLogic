package thederpgamer.extralogic.systems;

import api.common.GameCommon;
import api.mod.config.PersistentObjectUtil;
import api.utils.game.module.ModManagerContainerModule;
import api.utils.game.module.util.SimpleDataStorageMCModule;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.Ship;
import org.schema.game.common.controller.SpaceStation;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.game.common.data.world.SimpleTransformableSendableObject;
import org.schema.schine.graphicsengine.core.Timer;
import thederpgamer.extralogic.ExtraLogic;
import thederpgamer.extralogic.data.LinkChannel;
import thederpgamer.extralogic.element.ElementManager;
import thederpgamer.extralogic.networking.client.ClientManager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * [Description]
 *
 * @author Garret Reichenbach
 */
public class WirelessLinkModule extends SimpleDataStorageMCModule {

	private static final Queue<Runnable> activationQueue = new LinkedList<>();

	//Tweak these values as needed in case of server lag or other issues
	private static final int MAX_ACTIVE_THREADS = 5;
	private static final int DELAY = 300;
	private static final int QUEUE_HANDLE_TIMER = 1000;
	//

	private static long lastQueueHandle = System.currentTimeMillis();

	public WirelessLinkModule(SegmentController ship, ManagerContainer<?> managerContainer) {
		super(ship, managerContainer, ExtraLogic.getInstance(), ElementManager.getBlock("Wireless Link Module").getId());
	}

	public static void initialize() {
		activationQueue.clear();
		(new Thread("WirelessLinkModuleQueueHandler") {
			@Override
			public void run() {
				while(true) {
					try {
						if(System.currentTimeMillis() - lastQueueHandle > QUEUE_HANDLE_TIMER) {
							runTasks();
							lastQueueHandle = System.currentTimeMillis();
						}
					} catch(Exception exception) {
						exception.printStackTrace();
						//Try to re-initialize the queue handler if it fails
						ExtraLogic.getInstance().logException("WirelessLinkModuleQueueHandler failed to run. Re-initializing...", exception);
						initialize();
					}
				}
			}
		}).start();
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
			if(channel == null) {
				data.setChannel(null);
				ExtraLogic.getInstance().logInfo("Channel " + data.channel.getId() + " does not exist. Removing from block...");
			} else return true;
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

	public static void toggleChannel(LinkChannel linkChannel, final boolean active) {
		if(GameCommon.isDedicatedServer() || GameCommon.isOnSinglePlayer()) {
			for(final WirelessLinkModule module : PersistentObjectUtil.getCopyOfObjects(ExtraLogic.getInstance().getSkeleton(), WirelessLinkModule.class)) {
				for(final WirelessLinkModuleData data : module.getDataMap().dataMap.values()) {
					if(data.hasChannel() && data.getChannel().getId().equals(linkChannel.getId())) {
						//Let's spawn a couple threads to handle this just in case somebody gets some funny ideas and tries to break the server
						//Probably also want to limit the number of threads that can be spawned at once, and any extras should be queued up
						addToQueue(new Runnable() {
							@Override
							public void run() {
								//Let's also add a slight delay to discourage people from making spam clocks
								try {
									Thread.sleep(DELAY);
								} catch(InterruptedException exception) {
									exception.printStackTrace();
								}
								data.getChannel().setActive(active);
								SegmentPiece segmentPiece = module.getSegmentPiece(data);
								if(segmentPiece != null) {
									module.getManagerContainer().handleBlockActivate(segmentPiece, segmentPiece.isActive(), active);
									segmentPiece.setActive(active);
								}
								/* Probably don't want to send packets to every client every time, so let's just update the data and let the built-in activation system handle the rest
								module.flagUpdatedData();
								updateChannel(linkChannel);
								 */
							}
						});
					}
				}
			}
		}
	}

	private static void addToQueue(Runnable runnable) {
		activationQueue.add(runnable);
	}

	private static void runTasks() {
		if(activationQueue.size() > 0) {
			for(int i = 0; i < MAX_ACTIVE_THREADS; i++) {
				if(activationQueue.size() > 0) {
					Runnable runnable = activationQueue.poll();
					if(runnable != null) runnable.run();
				}
			}
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

	public static class WirelessLinkModuleData {

		public final long indexAndOrientation;
		private LinkChannel channel;

		public WirelessLinkModuleData(SegmentPiece segmentPiece) {
			indexAndOrientation = ElementCollection.getIndex4(segmentPiece.getAbsoluteIndex(), segmentPiece.getOrientation());
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
	}
}
