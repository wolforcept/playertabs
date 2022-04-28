package wolforce.playertabs.net;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import wolforce.playertabs.client.ClientEvents;

public class MessageBroadcastTabs {

	private int nrOfTabs;

	public MessageBroadcastTabs(int nr) {
		this.nrOfTabs = nr;
	}

	public void encode(FriendlyByteBuf buff) {
		buff.writeInt(nrOfTabs);
	}

	public static MessageBroadcastTabs decode(FriendlyByteBuf buff) {
		return new MessageBroadcastTabs(buff.readInt());
	}

	@SuppressWarnings("resource")
	public void onMessage(final Supplier<NetworkEvent.Context> ctx) {
		ClientEvents.setNumberOfTabs(nrOfTabs);
		ctx.get().setPacketHandled(true);
	}

}