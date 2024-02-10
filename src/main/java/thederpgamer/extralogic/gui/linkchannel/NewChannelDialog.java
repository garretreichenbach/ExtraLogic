package thederpgamer.extralogic.gui.linkchannel;

import api.common.GameClient;
import org.schema.game.client.controller.PlayerInput;
import org.schema.game.client.view.gui.GUIInputPanel;
import org.schema.game.common.data.player.PlayerState;
import org.schema.schine.common.OnInputChangedCallback;
import org.schema.schine.common.TextCallback;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.core.settings.PrefixNotFoundException;
import org.schema.schine.graphicsengine.forms.font.FontLibrary;
import org.schema.schine.graphicsengine.forms.gui.GUICallback;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import org.schema.schine.graphicsengine.forms.gui.GUITextButton;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIActivatableTextBar;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIContentPane;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIDialogWindow;
import org.schema.schine.input.InputState;
import thederpgamer.extralogic.networking.client.ClientManager;
import thederpgamer.extralogic.systems.WirelessLinkModule;

import java.util.UUID;

/**
 * [Description]
 *
 * @author Garret Reichenbach
 */
public class NewChannelDialog extends PlayerInput {

	private final NewChannelPanel inputPanel;

	public NewChannelDialog(WirelessLinkModule module, PlayerState player) {
		super(GameClient.getClientState());
		(inputPanel = new NewChannelPanel(getState(), this, module, player)).onInit();
	}

	public static void open(WirelessLinkModule module, PlayerState playerState) {
		NewChannelDialog dialog = new NewChannelDialog(module, playerState);
		dialog.activate();
		GameClient.getClientState().getController().getPlayerInputs().add(dialog);
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
	public NewChannelPanel getInputPanel() {
		return inputPanel;
	}

	@Override
	public void callback(GUIElement callingElement, MouseEvent mouseEvent) {
		if(mouseEvent.pressedLeftMouse()) {
			if(callingElement.getUserPointer() != null && callingElement.getUserPointer() instanceof String) {
				String id = ((String) callingElement.getUserPointer()).toUpperCase().trim();
				switch(id) {
					case "X":
					case "CANCEL":
						deactivate();
						break;
					case "OK":
						if(isValidId(inputPanel.getChannelID())) {
							ClientManager.createNewChannel(getState().getPlayer(), getChannelID(), getChannelName(), getChannelDescription());
							deactivate();
						}
						break;
				}
			}
		}
	}

	protected boolean isValidId(String id) {
		if(id.length() != 8) return false;
		for(char c : id.toCharArray()) {
			if(!Character.isDigit(c) && (c < 'a' || c > 'f')) {
				return false;
			}
		}
		return true;
	}

	public String getChannelName() {
		return inputPanel.getChannelName();
	}

	public String getChannelDescription() {
		return inputPanel.getChannelDescription();
	}

	public String getChannelID() {
		return inputPanel.getChannelID();
	}

	public static class NewChannelPanel extends GUIInputPanel {

		private GUIActivatableTextBar channelInput;
		private GUIActivatableTextBar nameInput;
		private GUIActivatableTextBar descriptionInput;

		public NewChannelPanel(InputState inputState, GUICallback guiCallback, WirelessLinkModule module, PlayerState player) {
			super("NEW_CHANNEL_PANEL", inputState, 500, 500, guiCallback, "", "");
		}

		@Override
		public void onInit() {
			super.onInit();
			GUIContentPane contentPane = ((GUIDialogWindow) background).getMainContentPane();
			contentPane.setTextBoxHeightLast(350);

			//Channel Input
			channelInput = new GUIActivatableTextBar(getState(), FontLibrary.FontSize.MEDIUM, 8, 1, "Channel ID", contentPane.getContent(0), new TextCallback() {
				@Override
				public String[] getCommandPrefixes() {
					return new String[0];
				}

				@Override
				public String handleAutoComplete(String s, TextCallback textCallback, String s1) throws PrefixNotFoundException {
					return null;
				}

				@Override
				public void onFailedTextCheck(String s) {

				}

				@Override
				public void onTextEnter(String s, boolean b, boolean b1) {

				}

				@Override
				public void newLine() {

				}
			}, new OnInputChangedCallback() {
				@Override
				public String onInputChanged(String s) {
					for(char c : s.toCharArray()) {
						if(!Character.isDigit(c) && (c < 'a' || c > 'f')) {
							return s.substring(0, s.length() - 1);
						}
					}
					return s;
				}
			});
			channelInput.onInit();
			channelInput.setPos(0, 0, 0);
			contentPane.getContent(0).attach(channelInput);

			//Randomize ID Button
			GUITextButton button = new GUITextButton(getState(), 400, 40, "Randomize Channel ID", new GUICallback() {
				@Override
				public void callback(org.schema.schine.graphicsengine.forms.gui.GUIElement guiElement, org.schema.schine.graphicsengine.core.MouseEvent mouseEvent) {
					if(mouseEvent.pressedLeftMouse()) {
						channelInput.setText(UUID.randomUUID().toString().substring(0, 8));
					}
				}

				@Override
				public boolean isOccluded() {
					return false;
				}
			}) {
				@Override
				public void draw() {
					super.draw();
					setWidth(channelInput.getWidth());
				}
			};
			button.onInit();
			button.setPos(0, channelInput.getHeight() + 2, 0);
			contentPane.getContent(0).attach(button);

			//Name Input
			nameInput = new GUIActivatableTextBar(getState(), FontLibrary.FontSize.MEDIUM, 128, 1, "Channel Name", contentPane.getContent(0), new DefaultTextCallback(), new DefaultTextChangedCallback());
			nameInput.onInit();
			nameInput.setPos(0, channelInput.getHeight() + 44, 0);
			contentPane.getContent(0).attach(nameInput);

			//Description Input
			descriptionInput = new GUIActivatableTextBar(getState(), FontLibrary.FontSize.SMALL, 420, 8, "Channel Description", contentPane.getContent(0), new DefaultTextCallback(), new DefaultTextChangedCallback());
			descriptionInput.onInit();
			descriptionInput.setPos(0, channelInput.getHeight() + nameInput.getHeight() + 46, 0);
//			contentPane.getContent(0).attach(descriptionInput);
		}

		@Override
		public void cleanUp() {
			super.cleanUp();
			channelInput.cleanUp();
			nameInput.cleanUp();
			descriptionInput.cleanUp();
		}

		public String getChannelName() {
			return nameInput.getText().trim();
		}

		public String getChannelDescription() {
			return descriptionInput.getText().trim();
		}

		public String getChannelID() {
			return channelInput.getText().trim();
		}
	}

	private static class DefaultTextCallback implements TextCallback {
		@Override
		public String[] getCommandPrefixes() {
			return null;
		}

		@Override
		public String handleAutoComplete(String s, TextCallback callback, String prefix) throws PrefixNotFoundException {
			return null;
		}

		@Override
		public void onFailedTextCheck(String msg) {
		}

		@Override
		public void onTextEnter(String entry, boolean send, boolean onAutoComplete) {
		}

		@Override
		public void newLine() {
		}
	}

	private static class DefaultTextChangedCallback implements OnInputChangedCallback {
		@Override
		public String onInputChanged(String t) {
			return t;
		}
	}
}
