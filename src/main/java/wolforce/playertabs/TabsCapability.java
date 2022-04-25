package wolforce.playertabs;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TabsCapability {

	public static final Capability<TabsCapability> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
	});

	private int currTab = 0;
	private ItemStackHandler[] tabs;

	public TabsCapability() {
		tabs = new ItemStackHandler[PlayerTabs.NUMBER_OF_TABS];
		for (int i = 0; i < tabs.length; i++) {
			tabs[i] = new ItemStackHandler(26);
		}
	}

	public int getCurrentTab() {
		return currTab;
	}

	public ItemStackHandler getTab(int i) {
		if (i >= 0 && i < tabs.length) {
			return tabs[i];
		}
		return null;
	}

	public void saveInvToCurrTab(IItemHandler inv) {
		for (int i = 0; i < 26; i++) {
			ItemStack item = inv.extractItem(i + 9, 64, false);
			tabs[currTab].setStackInSlot(i, item);
		}
	}

	public void writeTabToInv(int tabNr, IItemHandler inv) {
		for (int i = 0; i < 26; i++) {
			ItemStack item = tabs[tabNr].extractItem(i, 64, false);
			inv.extractItem(i + 9, 64, false);
			inv.insertItem(i + 9, item, false);
		}
		currTab = tabNr;
	}

	public static void switchToTab(Player player, int tabNr) {

		TabsCapability tabs = player.getCapability(CAPABILITY).resolve().get();
		IItemHandler inv = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve().get();

		tabs.saveInvToCurrTab(inv);
		tabs.writeTabToInv(tabNr, inv);

	}

	//
	// PROVIDER

	public static ICapabilityProvider getProvider() {
		return new ICapabilitySerializable<CompoundTag>() {
			TabsCapability cap = new TabsCapability();

			@Override
			public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
				if (cap == CAPABILITY)
					return LazyOptional.of(() -> cap).cast();
				return LazyOptional.empty();
			}

			@Override
			public CompoundTag serializeNBT() {

				CompoundTag tag = new CompoundTag();
				tag.putInt("currTab", cap.currTab);
				for (int i = 0; i < cap.tabs.length; i++) {
					tag.put("tab" + i, cap.tabs[i].serializeNBT());
				}
				return tag;
			}

			@Override
			public void deserializeNBT(CompoundTag tag) {

				cap.currTab = tag.getInt("currTab");
				for (int i = 0; i < cap.tabs.length; i++) {
					cap.tabs[i].deserializeNBT((CompoundTag) tag.get("tab" + i));
				}
			}

		};
	}

}
