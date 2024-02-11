package thederpgamer.extralogic.manager;

import api.common.GameCommon;
import api.listener.Listener;
import api.listener.events.block.SegmentPieceActivateByPlayer;
import api.listener.events.block.SegmentPieceActivateEvent;
import api.listener.events.player.PlayerSpawnEvent;
import api.listener.events.register.ManagerContainerRegisterEvent;
import api.mod.StarLoader;
import thederpgamer.extralogic.ExtraLogic;
import thederpgamer.extralogic.element.ElementManager;
import thederpgamer.extralogic.element.blocks.ActivationInterface;
import thederpgamer.extralogic.element.blocks.Block;
import thederpgamer.extralogic.networking.client.ClientManager;
import thederpgamer.extralogic.systems.logic.WirelessLinkModule;

public class EventManager {

	public static void initialize(ExtraLogic instance) {
		StarLoader.registerListener(ManagerContainerRegisterEvent.class, new Listener<ManagerContainerRegisterEvent>() {
			@Override
			public void onEvent(ManagerContainerRegisterEvent event) {
				event.addModMCModule(new WirelessLinkModule(event.getSegmentController(), event.getContainer()));
			}
		}, instance);

		StarLoader.registerListener(SegmentPieceActivateByPlayer.class, new Listener<SegmentPieceActivateByPlayer>() {
			@Override
			public void onEvent(SegmentPieceActivateByPlayer event) {
				for(Block block : ElementManager.getAllBlocks()) {
					if(block instanceof ActivationInterface && block.getId() == event.getSegmentPiece().getType()) {
						((ActivationInterface) block).onPlayerActivation(event);
						return;
					}
				}
			}
		}, instance);

		StarLoader.registerListener(SegmentPieceActivateEvent.class, new Listener<SegmentPieceActivateEvent>() {
			@Override
			public void onEvent(SegmentPieceActivateEvent event) {
				for(Block block : ElementManager.getAllBlocks()) {
					if(block instanceof ActivationInterface && block.getId() == event.getSegmentPiece().getType()) {
						((ActivationInterface) block).onLogicActivation(event);
						break;
					}
				}
			}
		}, instance);

		StarLoader.registerListener(PlayerSpawnEvent.class, new Listener<PlayerSpawnEvent>() {
			@Override
			public void onEvent(PlayerSpawnEvent playerSpawnEvent) {
				if(GameCommon.isClientConnectedToServer() || GameCommon.isOnSinglePlayer()) ClientManager.requestChannelsFromServer();
			}
		}, instance);
	}
}
