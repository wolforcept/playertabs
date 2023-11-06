package wolforce.playertabs.client;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
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
	public static void onInitScreenPost(ScreenEvent.Init.Post event) {
		Player player = mc.player;
		if (player == null)
			return;

		String screenName = event.getScreen().getClass().getSimpleName();
		List<String> blacklistedScreens = PlayerTabsConfigClient.getBlacklistedScreens();

		if (event.getScreen() instanceof AbstractContainerScreen<?> screen) {

			if (PlayerTabsConfigClient.isShowScreenNames())
				player.sendSystemMessage(Component.translatable(screenName));

			if (blacklistedScreens == null || !blacklistedScreens.contains(screenName)) {

				TabsCapability.get(player).ifPresent(cap ->{
					List<String> tabNames = PlayerTabsConfigClient.getTabNames();
					int nrOfTabs = PlayerTabsConfigServer.getNumberOfTabs();
					cap.update();
					if (cap.getCurrentTab() >= nrOfTabs) {
						cap.setCurrentTab(0);
						Net.sendToggleMessageToServer(0);
					}
                    TabButton[] buttons = new TabButton[nrOfTabs];
                    for (int i = 0; i < nrOfTabs; i++) {
                        final int tabNr = i;
                        String tabName = i < tabNames.size() ? tabNames.get(i) : "Tab " + tabNr;
                        int w = screen.getXSize() / nrOfTabs;
                        int dy = screenName.equals("CreativeModeInventoryScreen") ? 24 : 0;
                        buttons[i] = new TabButton(//
                                screen.getGuiLeft() + tabNr * w, screen.getGuiTop() + screen.getYSize() + dy, w, 20, //
                                tabName, //
                                button -> {
                                    Net.sendToggleMessageToServer(tabNr);
                                    buttons[cap.getCurrentTab()].active = true;
                                    buttons[tabNr].active = false;
                                    cap.setCurrentTab(tabNr);
                                });
                        if (i == cap.getCurrentTab()) {
                            buttons[i].active = false;
                        }
                        event.addListener(buttons[i]);
                    }
                });
			}
		}
	}

	public static void switchToTab(byte tab) {
		LocalPlayer player = mc.player;
		TabsCapability.get(player).ifPresent(cap ->{
			TabsCapability.switchToTab(player, tab);
		});

	}

	public static void setNumberOfTabs(int nrOfTabs) {
		PlayerTabsConfigServer.setNumberOfTabs(nrOfTabs);
		TabsCapability.get(mc.player).ifPresent(TabsCapability::update);
	}
}
