package thederpgamer.extralogic.gui.linkchannel;

import api.common.GameClient;
import api.utils.gui.SimplePlayerTextInput;
import org.schema.game.client.controller.PlayerInput;
import org.schema.game.client.view.gui.GUIInputPanel;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.player.PlayerState;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.GUIActivationCallback;
import org.schema.schine.graphicsengine.forms.gui.GUICallback;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIContentPane;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIDialogWindow;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIHorizontalArea;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIHorizontalButtonTablePane;
import org.schema.schine.input.InputState;
import thederpgamer.extralogic.data.linkmodule.LinkChannel;
import thederpgamer.extralogic.systems.logic.WirelessLinkModule;

/**
 * [Description]
 *
 * @author Garret Reichenbach
 */
public class WirelessLinkModuleDialog extends PlayerInput {

	public static void open(SegmentPiece segmentPiece, WirelessLinkModule module, PlayerState player) {
		WirelessLinkModuleDialog dialog = new WirelessLinkModuleDialog(segmentPiece, module, player);
		dialog.activate();
		GameClient.getClientState().getController().getPlayerInputs().add(dialog);
	}

	private final WirelessLinkModulePanel inputPanel;

	public WirelessLinkModuleDialog(SegmentPiece segmentPiece, WirelessLinkModule module, PlayerState player) {
		super(GameClient.getClientState());
		(inputPanel = new WirelessLinkModulePanel(getState(),this, segmentPiece, module, player)).onInit();
	}

	@Override
	public void onDeactivate() {
		inputPanel.cleanUp();
		getState().getController().getPlayerInputs().remove(this);
	}

	@Override
	public void handleMouseEvent(MouseEvent mouseEvent) {

	}

	@Override
	public WirelessLinkModulePanel getInputPanel() {
		return inputPanel;
	}

	@Override
	public void callback(GUIElement callingElement, MouseEvent mouseEvent) {
	}

	public static class WirelessLinkModulePanel extends GUIInputPanel {

		private final SegmentPiece segmentPiece;
		private final WirelessLinkModule module;
		private final PlayerState player;

		public WirelessLinkModulePanel(InputState inputState, GUICallback guiCallback, final SegmentPiece segmentPiece, final WirelessLinkModule module, PlayerState player) {
			super("WIRELESS_LINK_MODULE_UI", inputState, 600, 500, guiCallback, (new Object() {
				@Override
				public String toString() {
					try {
						if(module != null && module.getData(segmentPiece) != null && module.getData(segmentPiece).hasChannel()) {
							return "Wireless Link Module - " + module.getData(segmentPiece).getChannel().getName();
						}
					} catch(Exception ignored) {}
					return "Wireless Link Module";
				}
			}.toString()), "");
			this.segmentPiece = segmentPiece;
			this.module = module;
			this.player = player;
		}

		@Override
		public void onInit() {
			super.onInit();
			GUIContentPane contentPane = ((GUIDialogWindow) background).getMainContentPane();
			contentPane.setTextBoxHeightLast(28);

			//Button Pane
			GUIHorizontalButtonTablePane buttonPane = new GUIHorizontalButtonTablePane(getState(), 3, 1, contentPane.getContent(0));
			buttonPane.onInit();
			buttonPane.addButton(0, 0, "NEW CHANNEL", GUIHorizontalArea.HButtonColor.GREEN, new GUICallback() {
				@Override
				public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
					if(mouseEvent.pressedLeftMouse()) {
						NewChannelDialog.open(module, player);
					}
				}

				@Override
				public boolean isOccluded() {
					return getState().getController().getPlayerInputs().get(getState().getController().getPlayerInputs().size() - 1) instanceof NewChannelDialog;
				}
			}, new GUIActivationCallback() {
				@Override
				public boolean isVisible(InputState inputState) {
					return true;
				}

				@Override
				public boolean isActive(InputState inputState) {
					return true;
				}
			});
			buttonPane.addButton(1, 0, "RENAME CHANNEL", GUIHorizontalArea.HButtonColor.BLUE, new GUICallback() {
				@Override
				public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
					if(mouseEvent.pressedLeftMouse()) {
						LinkChannel channel = module.getData(segmentPiece).getChannel();
						if(channel.getOwner().equals(player.getName()) || player.isAdmin()) {
							(new SimplePlayerTextInput("Rename Channel", "") {
								@Override
								public boolean onInput(String s) {
									if(!s.trim().isEmpty()) {
										LinkChannel channel = module.getData(segmentPiece).getChannel();
										if(channel != null) {
											channel.setName(s);
											WirelessLinkModule.updateChannel(channel);
											module.flagUpdatedData();
											return true;
										}
									}
									return false;
								}
							}).activate();
						}
					}
				}

				@Override
				public boolean isOccluded() {
					return module.getData(segmentPiece) == null || !module.getData(segmentPiece).hasChannel() || getState().getController().getPlayerInputs().get(getState().getController().getPlayerInputs().size() - 1) instanceof NewChannelDialog || (!module.getData(segmentPiece).getChannel().getOwner().equals(player.getName()) && !player.isAdmin());
				}
			}, new GUIActivationCallback() {
				@Override
				public boolean isVisible(InputState inputState) {
					return true;
				}

				@Override
				public boolean isActive(InputState inputState) {
					return module.getData(segmentPiece) != null && module.getData(segmentPiece).hasChannel();
				}
			});
			buttonPane.addButton(2, 0, "DELETE CHANNEL", GUIHorizontalArea.HButtonColor.RED, new GUICallback() {
				@Override
				public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
					if(mouseEvent.pressedLeftMouse()) {
						if(module.getData(segmentPiece).getChannel().getOwner().equals(player.getName()) || player.isAdmin()) {
							LinkChannel channel = module.getData(segmentPiece).getChannel();
							if(channel != null) {
								WirelessLinkModule.removeChannel(channel);
								module.getData(segmentPiece).setChannel(null);
								module.flagUpdatedData();
							}
						}
					}
				}

				@Override
				public boolean isOccluded() {
					return module.getData(segmentPiece) == null || !module.getData(segmentPiece).hasChannel() || getState().getController().getPlayerInputs().get(getState().getController().getPlayerInputs().size() - 1) instanceof NewChannelDialog || (!module.getData(segmentPiece).getChannel().getOwner().equals(player.getName()) && !player.isAdmin());
				}
			}, new GUIActivationCallback() {
				@Override
				public boolean isVisible(InputState inputState) {
					return true;
				}

				@Override
				public boolean isActive(InputState inputState) {
					return module.getData(segmentPiece) != null && module.getData(segmentPiece).hasChannel();
				}
			});
			contentPane.getContent(0).attach(buttonPane);

			//Channel List
			contentPane.addNewTextBox(0, 500 - 32);
			LinkChannelScrollableList channelList = new LinkChannelScrollableList(getState(), contentPane.getContent(1), module, module.getData(segmentPiece));
			channelList.onInit();
		}
	}
}
