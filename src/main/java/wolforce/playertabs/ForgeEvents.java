package wolforce.playertabs;

import java.util.List;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemStackHandler;
import wolforce.playertabs.client.Util;
import wolforce.playertabs.net.Net;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents {

	@SubscribeEvent
	public static void onRegisterCaps(RegisterCapabilitiesEvent event) {
		event.register(TabsCapability.class);
	}

	@SubscribeEvent
	public static void onAttachCaps(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof Player) {
			TabsCapability.attachToPlayer(event);
		}
	}

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
		if (event.getPlayer() instanceof ServerPlayer) {
			ServerPlayer serverplayer = (ServerPlayer) event.getEntity();
			TabsCapability tabs = TabsCapability.get(serverplayer);
			if (tabs != null)
				Net.sendToggleMessageToClient(serverplayer, tabs.getCurrentTab());
		}
	}

	@SubscribeEvent
	public static void onPlayerDeath(LivingDeathEvent event) {
		if (event.getEntityLiving() instanceof Player) {
			Player player = (Player) event.getEntityLiving();
			List<ItemStackHandler> otherTabs = TabsCapability.get(player).getAllOtherTabs();
			for (ItemStackHandler handler : otherTabs) {
				for (int i = 0; i < handler.getSlots(); i++) {
					Util.spawnItem(player.level, player.position(), handler.getStackInSlot(i));
				}
			}
		}
	}
}