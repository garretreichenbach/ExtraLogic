package thederpgamer.extralogic.utils;

import api.common.GameClient;
import api.common.GameCommon;
import thederpgamer.extralogic.ExtraLogic;

public class DataUtils {
	public static String getWorldDataPath() {
		String universeName = GameCommon.getUniqueContextId();
		if(!universeName.contains(":")) {
			return getResourcesPath() + "/data/" + universeName;
		} else {
			try {
				ExtraLogic.getInstance().logWarning("Client " + GameClient.getClientPlayerState().getName() + " attempted to illegally access server data.");
			} catch(Exception ignored) {
			}
			return null;
		}
	}

	public static String getResourcesPath() {
		return ExtraLogic.getInstance().getSkeleton().getResourcesFolder().getPath().replace('\\', '/');
	}
}
