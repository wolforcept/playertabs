package wolforce.playertabs;

import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TabsCapability {

	public static final Capability<TabsCapability> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
	});

	private int currTab = 0;
	private ItemStackHandler[] tabs;
	private int INV_SIZE = 27;

	public TabsCapability() {
		tabs = new ItemStackHandler[PlayerTabs.NUMBER_OF_TABS];
		for (int i = 0; i < tabs.length; i++) {
			tabs[i] = new ItemStackHandler(INV_SIZE);
		}
	}

	public int getCurrentTab() {
		return currTab;
	}

	public void setCurrentTab(int tabNr) {
		currTab = tabNr;
	}

	public ItemStackHandler getTab(int i) {
		if (i >= 0 && i < tabs.length) {
			return tabs[i];
		}
		return null;
	}

	public void saveInvToCurrTab(IItemHandler inv) {
		for (int i = 0; i < INV_SIZE; i++) {
			ItemStack item = inv.extractItem(i + 9, 64, false);
			tabs[currTab].setStackInSlot(i, item);
		}
	}

	public void writeTabToInv(int tabNr, IItemHandler inv) {
		for (int i = 0; i < INV_SIZE; i++) {
			ItemStack item = tabs[tabNr].extractItem(i, 64, false);
			inv.extractItem(i + 9, 64, false);
			inv.insertItem(i + 9, item, false);
		}
		currTab = tabNr;
	}

	public static void switchToTab(Player player, int tabNr) {
		TabsCapability tabs = TabsCapability.get(player);
		IItemHandler inv = TabsCapability.getInv(player);
		if (tabs != null && inv != null) {
			tabs.saveInvToCurrTab(inv);
			tabs.writeTabToInv(tabNr, inv);
		}
	}

	//
	// PROVIDER

	public static void attachToPlayer(AttachCapabilitiesEvent<Entity> event) {

//		event.addCapability(new ResourceLocation(PlayerTabs.MOD_ID), TabsCapability.getProvider());

		ICapabilityProvider cap = new ICapabilitySerializable<CompoundTag>() {
			TabsCapability tabs = new TabsCapability();
			LazyOptional<TabsCapability> cap_provider = LazyOptional.of(() -> tabs);

			@Override
			public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
				if (capability == CAPABILITY)
					return cap_provider.cast();
				return LazyOptional.empty();
			}

			@Override
			public CompoundTag serializeNBT() {

				CompoundTag tag = new CompoundTag();
				tag.putInt("currTab", tabs.currTab);
				for (int i = 0; i < tabs.tabs.length; i++) {
					tag.put("tab" + i, tabs.tabs[i].serializeNBT());
				}
				return tag;
			}

			@Override
			public void deserializeNBT(CompoundTag tag) {
				tabs.currTab = tag.getInt("currTab");
				for (int i = 0; i < tabs.tabs.length; i++) {
					tabs.tabs[i].deserializeNBT((CompoundTag) tag.get("tab" + i));
				}
			}

		};

		event.addCapability(new ResourceLocation(PlayerTabs.MOD_ID), cap);

	}

	public static TabsCapability get(Player player) {
		if (player != null) {
			LazyOptional<TabsCapability> cap = player.getCapability(TabsCapability.CAPABILITY);
			if (cap != null && cap.isPresent()) {
				Optional<TabsCapability> resolved = cap.resolve();
				if (resolved != null && resolved.isPresent()) {
					return resolved.get();
				}
			}
		}
		return null;
	}

	public static IItemHandler getInv(Player player) {
		if (player != null) {
			LazyOptional<IItemHandler> cap = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
			if (cap != null && cap.isPresent()) {
				Optional<IItemHandler> resolved = cap.resolve();
				if (resolved != null && resolved.isPresent()) {
					return resolved.get();
				}
			}
		}
		return null;
	}
}
