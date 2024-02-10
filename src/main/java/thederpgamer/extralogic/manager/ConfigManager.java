package thederpgamer.extralogic.manager;

import api.mod.config.FileConfiguration;
import thederpgamer.extralogic.ExtraLogic;

/**
 * Manages mod config files and values.
 *
 * @author TheDerpGamer
 */
public class ConfigManager {
	private static final String[] defaultMainConfig = {};
	// Main Config
	private static FileConfiguration mainConfig;

	public static void initialize(ExtraLogic instance) {
		mainConfig = instance.getConfig("config");
		mainConfig.saveDefault(defaultMainConfig);
	}

	public static FileConfiguration getMainConfig() {
		return mainConfig;
	}
}
