package wolforce.playertabs.net;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import wolforce.playertabs.TabsCapability;

public class MessageToggle {

	private byte tab;

	public MessageToggle(byte tab) {
		this.tab = tab;
	}

	public void encode(FriendlyByteBuf buff) {
		buff.writeByte(tab);
	}

	public static MessageToggle decode(FriendlyByteBuf buff) {
		return new MessageToggle(buff.readByte());
	}

	public void onMessage(final Supplier<NetworkEvent.Context> ctx) {
		Player player = ctx.get().getSender();
		TabsCapability.switchToTab(player, tab);
		ctx.get().setPacketHandled(true);
	}

}