package thederpgamer.extralogic;

import api.config.BlockConfig;
import api.listener.events.controller.ClientInitializeEvent;
import api.listener.events.controller.ServerInitializeEvent;
import api.mod.StarMod;
import glossar.GlossarCategory;
import glossar.GlossarInit;
import org.apache.commons.io.IOUtils;
import org.schema.schine.resource.ResourceLoader;
import thederpgamer.extralogic.element.ElementManager;
import thederpgamer.extralogic.element.blocks.logic.WirelessLinkModuleBlock;
import thederpgamer.extralogic.luamade.LuaMadeAPIManager;
import thederpgamer.extralogic.manager.ConfigManager;
import thederpgamer.extralogic.manager.EventManager;
import thederpgamer.extralogic.manager.ResourceManager;
import thederpgamer.extralogic.networking.client.ClientManager;
import thederpgamer.extralogic.networking.server.ServerManager;
import thederpgamer.extralogic.systems.logic.WirelessLinkModule;

import java.io.IOException;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Main class for ExtraLogic mod.
 *
 * @author TheDerpGamer
 */
public class ExtraLogic extends StarMod {
	private static ExtraLogic instance;
	private final String[] overwriteClasses = {};

	public ExtraLogic() {instance = this;}
	public static ExtraLogic getInstance() {return instance;}
	public static void main(String[] args) {}

	@Override
	public byte[] onClassTransform(String className, byte[] byteCode) {
		for(String name : overwriteClasses) {
			if(className.endsWith(name)) return overwriteClass(className, byteCode);
		}
		return super.onClassTransform(className, byteCode);
	}

	@Override
	public void onEnable() {
		instance = this;
		ConfigManager.initialize(this);
		EventManager.initialize(this);
		ClientManager.initializePackets();
		ServerManager.initializePackets();
		registerCommands();
		LuaMadeAPIManager.initialize();
	}

	@Override
	public void onClientCreated(ClientInitializeEvent event) {
		super.onClientCreated(event);
		initGlossary();
	}

	@Override
	public void onServerCreated(ServerInitializeEvent event) {
		super.onServerCreated(event);
		WirelessLinkModule.initThread();
	}

	@Override
	public void onBlockConfigLoad(BlockConfig config) {
		ElementManager.addBlock(new WirelessLinkModuleBlock());
		ElementManager.initialize();
	}

	@Override
	public void onResourceLoad(ResourceLoader loader) {
		ResourceManager.loadResources(this, loader);
	}

	private void initGlossary() {
		GlossarInit.initGlossar(this);
		GlossarCategory extraLogic = new GlossarCategory("ExtraLogic");
		GlossarInit.addCategory(extraLogic);
	}

	private void registerCommands() {

	}

	private byte[] overwriteClass(String className, byte[] byteCode) {
		byte[] bytes = null;
		try {
			ZipInputStream file = new ZipInputStream(Files.newInputStream(getSkeleton().getJarFile().toPath()));
			while(true) {
				ZipEntry nextEntry = file.getNextEntry();
				if(nextEntry == null) break;
				if(nextEntry.getName().endsWith(className + ".class")) bytes = IOUtils.toByteArray(file);
			}
			file.close();
		} catch(IOException exception) {
			logException("Failed to overwrite class " + className, exception);
		}
		if(bytes != null) return bytes;
		else return byteCode;
	}
}
