package wolforce.playertabs.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import wolforce.playertabs.PlayerTabs;
import wolforce.playertabs.TabButton;
import wolforce.playertabs.TabsCapability;
import wolforce.playertabs.net.Net;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {

	@SubscribeEvent
	public static void onInitScreenPost(ScreenEvent.InitScreenEvent.Post event) {

		if (event.getScreen() instanceof InventoryScreen) {

			InventoryScreen screen = (InventoryScreen) event.getScreen();
			@SuppressWarnings("resource")
			Player player = Minecraft.getInstance().player;
			TabsCapability tabs = player.getCapability(TabsCapability.CAPABILITY).resolve().get();

			for (int i = 0; i < PlayerTabs.NUMBER_OF_TABS; i++) {
				final int tabNr = i;
				TabButton tabButton = new TabButton(//
						screen.getGuiLeft() + tabNr * 38, screen.getGuiTop() + 165, //
						38, 20, //
						new TranslatableComponent("Tab " + tabNr), //
						button -> {
							Net.sendToggleMessage(tabNr);
						}, null);
				if (tabs.getCurrentTab() == i) {
					tabButton.active = false;
				}
				event.addListener(tabButton);
			}

		}
	}
}
