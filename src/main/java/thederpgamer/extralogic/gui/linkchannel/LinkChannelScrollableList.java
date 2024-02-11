package thederpgamer.extralogic.gui.linkchannel;

import api.common.GameClient;
import api.utils.gui.SimplePlayerTextInput;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.font.FontLibrary;
import org.schema.schine.graphicsengine.forms.gui.*;
import org.schema.schine.graphicsengine.forms.gui.newgui.*;
import org.schema.schine.input.InputState;
import thederpgamer.extralogic.data.linkmodule.LinkChannel;
import thederpgamer.extralogic.networking.client.ClientManager;
import thederpgamer.extralogic.systems.logic.WirelessLinkModule;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;

/**
 * [Description]
 *
 * @author Garret Reichenbach
 */
public class LinkChannelScrollableList extends ScrollableTableList<LinkChannel> {

	private final GUIElement panel;
	protected WirelessLinkModule module;
	protected WirelessLinkModule.WirelessLinkModuleData data;

	public LinkChannelScrollableList(InputState state, GUIElement panel, WirelessLinkModule module, WirelessLinkModule.WirelessLinkModuleData data) {
		super(state, panel.getWidth(), panel.getHeight(), panel);
		this.panel = panel;
		this.module = module;
		this.data = data;
		panel.attach(this);
	}

	@Override
	protected Collection<LinkChannel> getElementList() {
		return ClientManager.getClientAccessibleChannels();
	}

	@Override
	public void initColumns() {
		addColumn("ID", 0.3f, new Comparator<LinkChannel>() {
			@Override
			public int compare(LinkChannel o1, LinkChannel o2) {
				String first = String.valueOf(o1.getId());
				String second = String.valueOf(o2.getId());
				return first.compareTo(second);
			}
		});
		addColumn("Name", 0.3f, new Comparator<LinkChannel>() {
			@Override
			public int compare(LinkChannel o1, LinkChannel o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		addColumn("Status", 0.3f, new Comparator<LinkChannel>() {
			@Override
			public int compare(LinkChannel o1, LinkChannel o2) {
				return Boolean.compare(o1.isActive(), o2.isActive());
			}
		});
		addTextFilter(new GUIListFilterText<LinkChannel>() {
			@Override
			public boolean isOk(String s, LinkChannel channel) {
				return channel.getName().toLowerCase().contains(s.toLowerCase());
			}
		}, "SEARCH", ControllerElement.FilterRowStyle.LEFT);
		addDropdownFilter(new GUIListFilterDropdown<LinkChannel, Integer>(0, 1, 2) {
			@Override
			public boolean isOk(Integer i, LinkChannel channel) {
				switch(i) {
					case 0:
						return true;
					case 1:
						return channel.isActive();
					case 2:
						return !channel.isActive();
				}
				return false;
			}
		}, new CreateGUIElementInterface<Integer>() {
			@Override
			public GUIElement create(Integer i) {
				GUIAncor anchor = new GUIAncor(getState(), 10.0F, 24.0F);
				GUITextOverlayTableDropDown dropDown;
				(dropDown = new GUITextOverlayTableDropDown(10, 10, getState())).setTextSimple(i == 0 ? "ALL" : i == 1 ? "ACTIVE" : "INACTIVE");
				dropDown.setPos(4.0F, 4.0F, 0.0F);
				anchor.setUserPointer(i);
				anchor.attach(dropDown);
				return anchor;
			}

			@Override
			public GUIElement createNeutral() {
				return null;
			}
		}, ControllerElement.FilterRowStyle.RIGHT);
		activeSortColumnIndex = 1;
	}

	@Override
	public void updateListEntries(GUIElementList guiElementList, Set<LinkChannel> set) {
		guiElementList.deleteObservers();
		guiElementList.addObserver(this);
		for(final LinkChannel channel : set) {
			GUIClippedRow idRow = createRow(channel.getId());
			GUIClippedRow nameRow = createRow(channel.getName());
			GUIClippedRow statusRow = createRow(channel.isActive() ? "Active" : "Inactive");

			LinkChannelScrollableListRow listRow = new LinkChannelScrollableListRow(getState(), channel, idRow, nameRow, statusRow);
			GUIAncor anchor = new GUIAncor(getState(), panel.getWidth() - 28.0f, 68.0F);
			GUIHorizontalButtonTablePane buttonPane = new GUIHorizontalButtonTablePane(getState(), 3, 1, anchor);
			buttonPane.onInit();

			buttonPane.addButton(0, 0, "SET CHANNEL", GUIHorizontalArea.HButtonColor.GREEN, new GUICallback() {
				@Override
				public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
					if(mouseEvent.pressedLeftMouse()) {
						data.setChannel(channel);
						module.flagUpdatedData();
						flagDirty();
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
						if(channel.getOwner().equals(GameClient.getClientPlayerState().getName()) || GameClient.getClientPlayerState().isAdmin()) {
							(new SimplePlayerTextInput("Rename Channel", "") {
								@Override
								public boolean onInput(String s) {
									if(!s.trim().isEmpty()) {
										channel.setName(s);
										WirelessLinkModule.updateChannel(channel);
										module.flagUpdatedData();
										flagDirty();
										return true;
									}
									return false;
								}
							}).activate();
						}
					}
				}

				@Override
				public boolean isOccluded() {
					return getState().getController().getPlayerInputs().get(getState().getController().getPlayerInputs().size() - 1) instanceof NewChannelDialog || (channel.getOwner().equals(GameClient.getClientPlayerState().getName()) && !GameClient.getClientPlayerState().isAdmin());
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
			buttonPane.addButton(2, 0, "DELETE CHANNEL", GUIHorizontalArea.HButtonColor.RED, new GUICallback() {
				@Override
				public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
					if(mouseEvent.pressedLeftMouse()) {
						if(channel.getOwner().equals(GameClient.getClientPlayerState().getName()) || GameClient.getClientPlayerState().isAdmin()) {
							WirelessLinkModule.removeChannel(channel);
							data.setChannel(null);
							module.flagUpdatedData();
							flagDirty();
						}
					}
				}

				@Override
				public boolean isOccluded() {
					return getState().getController().getPlayerInputs().get(getState().getController().getPlayerInputs().size() - 1) instanceof NewChannelDialog || (channel.getOwner().equals(GameClient.getClientPlayerState().getName()) && !GameClient.getClientPlayerState().isAdmin());
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
			anchor.attach(buttonPane);

			GUITextOverlayTableInnerDescription description = new GUITextOverlayTableInnerDescription(10, 10, getState());
			description.setFont(FontLibrary.FontSize.SMALL.getFont());
			description.onInit();
			description.setTextSimple(channel.getDescription());
			description.setPos(0, buttonPane.getHeight() + 2, 0);
			anchor.attach(description);

			listRow.expanded = new GUIElementList(getState());
			listRow.expanded.add(new GUIListElement(anchor, getState()));
			listRow.expanded.attach(anchor);
			listRow.onInit();
			guiElementList.addWithoutUpdate(listRow);
		}
		guiElementList.updateDim();
	}

	private GUIClippedRow createRow(String label) {
		GUITextOverlayTable element = new GUITextOverlayTable(10, 10, getState());
		element.setTextSimple(label);
		GUIClippedRow row = new GUIClippedRow(getState());
		row.attach(element);
		return row;
	}

	private class LinkChannelScrollableListRow extends ScrollableTableList<LinkChannel>.Row {

		public LinkChannelScrollableListRow(InputState state, LinkChannel userData, GUIElement... elements) {
			super(state, userData, elements);
			highlightSelect = true;
			highlightSelectSimple = true;
			setAllwaysOneSelected(true);
		}
	}
}
