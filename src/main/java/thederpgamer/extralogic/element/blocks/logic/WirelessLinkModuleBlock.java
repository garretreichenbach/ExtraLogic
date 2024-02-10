package thederpgamer.extralogic.element.blocks.logic;

import api.config.BlockConfig;
import api.listener.events.block.SegmentPieceActivateByPlayer;
import api.listener.events.block.SegmentPieceActivateEvent;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.game.common.data.element.FactoryResource;
import org.schema.schine.graphicsengine.core.GraphicsContext;
import thederpgamer.extralogic.data.LinkChannel;
import thederpgamer.extralogic.element.blocks.ActivationInterface;
import thederpgamer.extralogic.element.blocks.Block;
import thederpgamer.extralogic.gui.linkchannel.WirelessLinkModuleDialog;
import thederpgamer.extralogic.manager.ResourceManager;
import thederpgamer.extralogic.systems.WirelessLinkModule;

import java.util.Objects;

/**
 * [Description]
 *
 * @author Garret Reichenbach
 */
public class WirelessLinkModuleBlock extends Block implements ActivationInterface {

	public WirelessLinkModuleBlock() {
		super("Wireless Link Module", ElementKeyMap.getInfo(ElementKeyMap.LOGIC_WIRELESS).getType());
	}

	@Override
	public void initialize() {
		blockInfo.setDescription("The Wireless Link Module is used to send logic signals across vast distances.\n" +
				"They do this through a channel system, where each module can be set to a specific channel to send" +
				"signals to other modules set to the same channel.\nPlayers can only access channels that are public or" +
				"are in the same faction.\nNote: The Wireless Link Module only works if it's entity is loaded.");
		blockInfo.setInRecipe(true);
		blockInfo.setShoppable(true);
		blockInfo.setPrice(ElementKeyMap.getInfo(ElementKeyMap.LOGIC_WIRELESS).price);
		blockInfo.drawLogicConnection = true;
		blockInfo.setOrientatable(false);
		blockInfo.signal = true;
//		blockInfo.canActivate = true;
		blockInfo.hasActivationTexure = true;

		addController(blockInfo.getId());
		addControlling(blockInfo.getId());
		for(short id : ElementKeyMap.getInfo(ElementKeyMap.ACTIVAION_BLOCK_ID).controlledBy) {
			addControlling(id);
			addController(id);
		}
		for(short id : ElementKeyMap.getInfo(ElementKeyMap.ACTIVAION_BLOCK_ID).controlling) {
			addControlling(id);
			addController(id);
		}

		BlockConfig.addRecipe(blockInfo, ElementKeyMap.getInfo(ElementKeyMap.LOGIC_WIRELESS).getProducedInFactoryType(),
				(int) ElementKeyMap.getInfo(ElementKeyMap.LOGIC_WIRELESS).getFactoryBakeTime(),
				new FactoryResource(1, ElementKeyMap.LOGIC_WIRELESS),
				new FactoryResource(50, ElementKeyMap.METAL_MESH));
		if(GraphicsContext.initialized) {
			try {
				short textureId = (short) ResourceManager.getTexture("wireless-link-module").getTextureId();
				blockInfo.setTextureId(new short[] {textureId, textureId, textureId, textureId, textureId, textureId});
				blockInfo.setBuildIconNum(ResourceManager.getTexture("wireless-link-module-icon").getTextureId());
			} catch(Exception ignored) {}
		}
		BlockConfig.add(blockInfo);
	}

	@Override
	public void onPlayerActivation(SegmentPieceActivateByPlayer event) {
		WirelessLinkModule module = WirelessLinkModule.getInstance(event.getSegmentPiece().getSegmentController());
		if(module != null) WirelessLinkModuleDialog.open(event.getSegmentPiece(), module, event.getPlayer());
		else throw new NullPointerException("WirelessLinkModule instance is null even though it's been placed!");
	}

	@Override
	public void onLogicActivation(SegmentPieceActivateEvent event) {
		LinkChannel channel = WirelessLinkModule.getChannel(event.getSegmentPiece());
		if(channel != null && WirelessLinkModule.checkValid(Objects.requireNonNull(WirelessLinkModule.getDataFromBlock(event.getSegmentPiece())))) WirelessLinkModule.toggleChannel(channel, event.getSegmentPiece().isActive());
	}
}