package wolforce.playertabs.client;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import wolforce.playertabs.TabsCapability;
import wolforce.playertabs.net.Net;
import wolforce.playertabs.server.PlayerTabsConfigServer;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {

	private static final Minecraft mc = Minecraft.getInstance();

	@SubscribeEvent
	public static void onInitScreenPost(ScreenEvent.InitScreenEvent.Post event) {

		if (event.getScreen() instanceof InventoryScreen) {

			InventoryScreen screen = (InventoryScreen) event.getScreen();
			@SuppressWarnings("resource")
			Player player = Minecraft.getInstance().player;
			TabsCapability tabs = TabsCapability.get(player);
			List<String> tabNames = PlayerTabsConfigClient.getTabNames();
			int nrOfTabs = PlayerTabsConfigServer.getNumberOfTabs();
			tabs.update();
			if (tabs.getCurrentTab() >= nrOfTabs) {
				tabs.setCurrentTab(0);
				Net.sendToggleMessageToServer(0);
			}
			if (tabs != null) {
				TabButton[] buttons = new TabButton[nrOfTabs];
				for (int i = 0; i < nrOfTabs; i++) {
					final int tabNr = i;
					String tabName = i < tabNames.size() ? tabNames.get(i) : "Tab " + tabNr;
					int w = screen.getXSize() / nrOfTabs;
					buttons[i] = new TabButton(//
							screen.getGuiLeft() + tabNr * w, screen.getGuiTop() + 165, //
							w, 20, //
							tabName, //
							button -> {
								Net.sendToggleMessageToServer(tabNr);
								buttons[tabs.getCurrentTab()].active = true;
								buttons[tabNr].active = false;
								tabs.setCurrentTab(tabNr);
								screen.buttonClicked = true;
							});
					if (i == tabs.getCurrentTab()) {
						buttons[i].active = false;
					}
					event.addListener(buttons[i]);
				}
			}
		}
	}

	public static void switchToTab(byte tab) {
		LocalPlayer player = mc.player;
		TabsCapability tabs = TabsCapability.get(player);
		if (tabs != null) {
			tabs.setCurrentTab(tab);
		}
	}

	public static void setNumberOfTabs(int nrOfTabs) {
		PlayerTabsConfigServer.setNumberOfTabs(nrOfTabs);
		TabsCapability.get(mc.player).update();
	}
}
