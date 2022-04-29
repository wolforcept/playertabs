package wolforce.playertabs;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
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
import wolforce.playertabs.server.PlayerTabsConfigServer;

public class TabsCapability {

	public static final Capability<TabsCapability> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
	});
	private static final int INV_SIZE = 27;

	private int currTab = 0;
	private ItemStackHandler[] tabs;

	public TabsCapability() {
		tabs = new ItemStackHandler[PlayerTabsConfigServer.getNumberOfTabs()];
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

	public void update() {
		int nr = PlayerTabsConfigServer.getNumberOfTabs();
		if (nr != tabs.length) {
			ItemStackHandler[] newTabs = new ItemStackHandler[nr];
			for (int i = 0; i < nr; i++)
				newTabs[i] = new ItemStackHandler(INV_SIZE);
			for (int i = 0; i < Math.min(nr, tabs.length); i++)
				newTabs[i] = tabs[i];
			tabs = newTabs;
		}
	}

	public ItemStackHandler getTab(int i) {
		update();
		if (i >= 0 && i < tabs.length) {
			return tabs[i];
		}
		return null;
	}

	public void saveInvToCurrTab(IItemHandler inv) {
		update();
		if (currTab < tabs.length)
			for (int i = 0; i < INV_SIZE; i++) {
				ItemStack item = inv.extractItem(i + 9, 64, false);
				tabs[currTab].setStackInSlot(i, item);
			}
	}

	public void writeTabToInv(int tabNr, IItemHandler inv) {
		update();
		for (int i = 0; i < INV_SIZE; i++) {
			ItemStack item = tabs[tabNr].extractItem(i, 64, false);
			inv.extractItem(i + 9, 64, false);
			inv.insertItem(i + 9, item, false);
		}
		currTab = tabNr;
	}

	public static void switchToTab(Player player, int tabNr) {
		TabsCapability tabs = TabsCapability.get(player);
		if (tabs == null)
			return;
		IItemHandler inv = TabsCapability.getInv(player);
		if (tabs != null && inv != null) {
			tabs.update();
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
				tabs.update();
				tabs.currTab = tag.getInt("currTab");
				for (int i = 0; i < tabs.tabs.length; i++) {
					Tag tabTag = tag.get("tab" + i);
					if (tabTag != null && tabTag instanceof CompoundTag)
						tabs.tabs[i].deserializeNBT((CompoundTag) tabTag);
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

	public List<ItemStackHandler> getAllOtherTabs() {
		List<ItemStackHandler> otherTabsList = new LinkedList<>();
		for (int i = 0; i < this.tabs.length; i++) {
			if (i != currTab)
				otherTabsList.add(this.tabs[i]);
		}
		return otherTabsList;
	}

	public void cloneFrom(TabsCapability prev) {
		this.currTab = prev.currTab;
		this.tabs = prev.tabs;
	}
}
