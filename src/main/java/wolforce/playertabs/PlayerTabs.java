package wolforce.playertabs;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import wolforce.playertabs.client.PlayerTabsConfigClient;
import wolforce.playertabs.net.Net;
import wolforce.playertabs.server.PlayerTabsConfigServer;

@Mod("playertabs")
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class PlayerTabs {

	public static final String MOD_ID = "playertabs";

	public PlayerTabs() {
		MinecraftForge.EVENT_BUS.register(this);
		PlayerTabsConfigClient.init();
		PlayerTabsConfigServer.init();
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, PlayerTabsConfigServer.CONFIG_SPEC, MOD_ID + "_server.toml");
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, PlayerTabsConfigClient.CONFIG_SPEC, MOD_ID + "_client.toml");
	}

	@SubscribeEvent
	public static void commonSetup(FMLCommonSetupEvent event) {
		Net.register();
	}

}
