package thederpgamer.extralogic.luamade;

import api.mod.StarLoader;
import thederpgamer.extralogic.ExtraLogic;

import java.util.Objects;

public class LuaMadeAPIManager {

	public static boolean initialize() {
		if(StarLoader.getModFromName("LuaMade") != null && Objects.requireNonNull(StarLoader.getModFromName("LuaMade")).isEnabled()) {
			ExtraLogic.getInstance().logInfo("Loaded LuaMade API integration.");
			return true;
		} else {
			ExtraLogic.getInstance().logInfo("LuaMade API not found. Skipping integration.");
			return false;
		}
	}
}
