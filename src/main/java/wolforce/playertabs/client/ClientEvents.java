package wolforce.playertabs.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
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
			TabsCapability tabs = TabsCapability.get(player);
			if (tabs != null) {
				TabButton[] buttons = new TabButton[PlayerTabs.NUMBER_OF_TABS];
				for (int i = 0; i < PlayerTabs.NUMBER_OF_TABS; i++) {
					final int tabNr = i;
					buttons[i] = new TabButton(//
							screen.getGuiLeft() + tabNr * 38, screen.getGuiTop() + 165, //
							38, 20, //
							new TranslatableComponent("Tab " + tabNr), //
							button -> {
								Net.sendToggleMessageToServer(tabNr);
								buttons[tabs.getCurrentTab()].active = true;
								buttons[tabNr].active = false;
								tabs.setCurrentTab(tabNr);
							}, null);
					if (i == tabs.getCurrentTab()) {
						buttons[i].active = false;
					}
					event.addListener(buttons[i]);
				}
			}
		}
	}

	public static void switchToTab(byte tab) {
		@SuppressWarnings("resource")
		LocalPlayer player = Minecraft.getInstance().player;
		TabsCapability tabs = TabsCapability.get(player);
		if (tabs != null) {
			tabs.setCurrentTab(tab);
		}
	}
}
