package wolforce.playertabs;

import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
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
	public static void attachToPlayer(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof Player){
			event.addCapability(new ResourceLocation(PlayerTabs.MOD_ID), new TabsCapability.Provider());
		}
	}
	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.getEntity() instanceof ServerPlayer serverPlayer) {
			LazyOptional<TabsCapability> Capability = TabsCapability.get(serverPlayer);
			Capability.ifPresent(cap ->{
				Net.sendToggleMessageToClient(serverPlayer, cap.getCurrentTab());
			});
		}
	}
	@SubscribeEvent
	public static void onPlayerDeath(LivingDropsEvent event) {
		if (event.getEntity() instanceof Player player && !event.getEntity().level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
			TabsCapability.get(player).ifPresent(cap -> {
				for (ItemStackHandler handler : cap.getAllOtherTabs()) {
					for (int i = 0; i < handler.getSlots(); i++) {
						Vec3 pos = player.position();
						ItemStack stack = handler.getStackInSlot(i);
						ItemEntity ent = new ItemEntity(player.level(), pos.x, pos.y, pos.z, stack);
						event.getDrops().add(ent);
					}
				}
			});
		}
	}

	@SubscribeEvent
	public static void onPlayerClone(PlayerEvent.Clone event) {
		Player originalPlayer = event.getOriginal();
		Player newPlayer = event.getEntity();
		if (!event.isWasDeath() || newPlayer.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
			TabsCapability.get(newPlayer).ifPresent(newCap -> {
				originalPlayer.reviveCaps();
				TabsCapability.get(originalPlayer).ifPresent(newCap::cloneFrom);
			});
		}
	}

	@SubscribeEvent
	public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
		if (event.getEntity() instanceof ServerPlayer serverPlayer) {
			TabsCapability.get(serverPlayer).ifPresent(cap -> {
				Net.sendToggleMessageToClient(serverPlayer, cap.getCurrentTab());
			});
		}
	}
}