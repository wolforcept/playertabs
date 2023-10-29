package wolforce.playertabs;

import java.util.List;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemStackHandler;
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
		if (event.getEntity() instanceof ServerPlayer serverPlayer) {
			TabsCapability tabs = TabsCapability.get(serverPlayer);
			if (tabs != null)
				Net.sendToggleMessageToClient(serverPlayer, tabs.getCurrentTab());
		}
	}
	@SubscribeEvent
	public static void onPlayerDeath(LivingDropsEvent event) {
		if (event.getEntity() instanceof Player player && !event.getEntity().level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
			List<ItemStackHandler> otherTabs = TabsCapability.get(player).getAllOtherTabs();
			for (ItemStackHandler handler : otherTabs) {
				for (int i = 0; i < handler.getSlots(); i++) {
					Vec3 pos = player.position();
					ItemStack stack = handler.getStackInSlot(i);
					ItemEntity ent = new ItemEntity(player.level(), pos.x, pos.y, pos.z, stack);
					event.getDrops().add(ent);
				}
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerClone(PlayerEvent.Clone event) {
		if (!event.isWasDeath() || (event.isWasDeath() && event.getEntity().level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY))) {
			final Player player = event.getEntity();
			TabsCapability newCap = TabsCapability.get(player);
			event.getOriginal().reviveCaps();
			TabsCapability prevCap = TabsCapability.get(event.getOriginal());
			newCap.cloneFrom(prevCap);
		}
	}

	@SubscribeEvent
	public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			TabsCapability tabs = TabsCapability.get(player);
			Net.sendToggleMessageToClient((ServerPlayer) player, tabs.getCurrentTab());
		}
	}

}