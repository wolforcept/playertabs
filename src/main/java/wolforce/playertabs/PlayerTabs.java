package wolforce.playertabs;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import wolforce.playertabs.net.Net;

@Mod("playertabs")
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class PlayerTabs {

	public static final int NUMBER_OF_TABS = 3;
	public static final String MOD_ID = "playertabs";

	public PlayerTabs() {
//		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		MinecraftForge.EVENT_BUS.register(this);
	}

//	private void setup(final FMLCommonSetupEvent event) {
//	}
//
//	@SubscribeEvent
//	public void onServerStarting(ServerStartingEvent event) {
//	}

	@SubscribeEvent
	public static void commonSetup(FMLCommonSetupEvent event) {
		Net.register();
	}

}
