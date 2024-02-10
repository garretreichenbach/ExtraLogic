package thederpgamer.extralogic.networking.server;

import api.network.packets.PacketUtil;
import thederpgamer.extralogic.networking.server.packets.PacketGetAllChannelsResponse;
import thederpgamer.extralogic.networking.server.packets.PacketGetChannelResponse;
import thederpgamer.extralogic.networking.server.packets.PacketRemoveChannelResponse;
import thederpgamer.extralogic.networking.server.packets.PacketUpdateChannelResponse;

/**
 * [Description]
 *
 * @author Garret Reichenbach
 */
public class ServerManager {

	public static void initializePackets() {
		PacketUtil.registerPacket(PacketGetChannelResponse.class);
		PacketUtil.registerPacket(PacketUpdateChannelResponse.class);
		PacketUtil.registerPacket(PacketRemoveChannelResponse.class);
		PacketUtil.registerPacket(PacketGetAllChannelsResponse.class);
	}
}
