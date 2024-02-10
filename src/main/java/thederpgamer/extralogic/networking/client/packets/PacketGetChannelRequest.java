package thederpgamer.extralogic.networking.client.packets;

import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import api.network.packets.PacketUtil;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.extralogic.data.LinkChannel;
import thederpgamer.extralogic.networking.server.packets.PacketGetChannelResponse;
import thederpgamer.extralogic.systems.WirelessLinkModule;

import java.io.IOException;

/**
 * [Description]
 *
 * @author Garret Reichenbach
 */
public class PacketGetChannelRequest extends Packet {

	private String id;

	public PacketGetChannelRequest() {

	}

	public PacketGetChannelRequest(String id) {
		this.id = id;
	}

	@Override
	public void readPacketData(PacketReadBuffer packetReadBuffer) throws IOException {
		id = packetReadBuffer.readString();
	}

	@Override
	public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {
		packetWriteBuffer.writeString(id);
	}

	@Override
	public void processPacketOnClient() {

	}

	@Override
	public void processPacketOnServer(PlayerState playerState) {
		LinkChannel linkChannel = WirelessLinkModule.getChannel(id);
		if(linkChannel != null) PacketUtil.sendPacketToServer(new PacketGetChannelResponse(linkChannel));
	}
}
