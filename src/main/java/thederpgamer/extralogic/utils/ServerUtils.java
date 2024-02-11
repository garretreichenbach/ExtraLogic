package thederpgamer.extralogic.utils;

import api.common.GameServer;
import org.schema.game.common.controller.Planet;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.Ship;
import org.schema.game.common.controller.SpaceStation;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.server.data.ServerConfig;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 08/13/2021
 */
public class ServerUtils {
	public static int getSectorSize() {
		return (int) ServerConfig.SECTOR_SIZE.getCurrentState();
	}

	public static ManagerContainer<?> getManagerContainer(SegmentController segmentController) {
		switch(segmentController.getType()) {
			case SHIP:
				return ((Ship) segmentController).getManagerContainer();
			case SPACE_STATION:
				return ((SpaceStation) segmentController).getManagerContainer();
			case PLANET_SEGMENT:
				return ((Planet) segmentController).getManagerContainer();
			default:
				return null;
		}
	}

	public static SegmentController getSegmentControllerFromDBID(long entityID) {
		for(SegmentController segmentController : GameServer.getServerState().getSegmentControllersByName().values()) {
			if(segmentController.getDbId() == entityID) return segmentController;
		}
		return null;
	}
}
