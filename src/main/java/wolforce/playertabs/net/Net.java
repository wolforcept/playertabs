package wolforce.playertabs.net;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import wolforce.playertabs.PlayerTabs;

public class Net {

	private static SimpleChannel INSTANCE;

	public static void register() {

		INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(PlayerTabs.MOD_ID, "network"), //
				() -> PlayerTabs.MOD_ID, //
				PlayerTabs.MOD_ID::equals, //
				PlayerTabs.MOD_ID::equals //
		);

		int id = 0;
		INSTANCE.registerMessage(id++, MessageToggle.class, MessageToggle::encode, MessageToggle::decode, MessageToggle::onMessage);
	}

	public static void sendToggleMessage(int tab) {
		INSTANCE.sendToServer(new MessageToggle((byte) tab));
	}

}
