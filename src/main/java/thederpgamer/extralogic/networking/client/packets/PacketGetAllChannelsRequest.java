package thederpgamer.extralogic.networking.client.packets;

import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import api.network.packets.PacketUtil;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.extralogic.networking.server.packets.PacketGetAllChannelsResponse;

import java.io.IOException;

/**
 * [Description]
 *
 * @author Garret Reichenbach
 */
public class PacketGetAllChannelsRequest extends Packet {

	public PacketGetAllChannelsRequest() {

	}

	@Override
	public void readPacketData(PacketReadBuffer packetReadBuffer) throws IOException {

	}

	@Override
	public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {

	}

	@Override
	public void processPacketOnClient() {

	}

	@Override
	public void processPacketOnServer(PlayerState playerState) {
		PacketUtil.sendPacket(playerState, new PacketGetAllChannelsResponse(playerState));
	}
}
