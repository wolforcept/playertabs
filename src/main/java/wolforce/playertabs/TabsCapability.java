package wolforce.playertabs;

import java.util.*;
import com.google.common.collect.Maps;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wolforce.playertabs.server.PlayerTabsConfigServer;

public class TabsCapability {

	public static final Capability<TabsCapability> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
	});
	private static final int INV_SIZE = 27;

	private int currTab = 0;
	private ItemStackHandler[] tabs;
	private final Map<Item,Integer> ItemSortMap = Maps.newHashMap();

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

	public Map<Item, Integer> getItemSortMap() {
		return ItemSortMap;
	}

	public static
	LazyOptional<TabsCapability> get(Player player) {
		if (player == null) return LazyOptional.empty();
		return player.getCapability(CAPABILITY);
	}
	public void update() {
		int number = PlayerTabsConfigServer.getNumberOfTabs();
		if (number != tabs.length) {
			ItemStackHandler[] newTabs = new ItemStackHandler[number];
			for (int i = 0; i < number; i++)
				newTabs[i] = new ItemStackHandler(INV_SIZE);
			for (int i = 0; i < Math.min(number, tabs.length); i++)
				newTabs[i] = tabs[i];
			tabs = newTabs;
		}
	}
	public void updateItemSort(){
		for (int i = 0; i < tabs.length; i++) {
			for (int j = 0; j < tabs[i].getSlots(); j++) {
				ItemStack itemStack = tabs[i].getStackInSlot(j);
				if (!itemStack.isEmpty()){
					ItemSortMap.put(itemStack.getItem(),i);
				}
			}
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
		TabsCapability.get(player).ifPresent(cap ->{
			player.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemHand ->{
				cap.update();
				cap.saveInvToCurrTab(itemHand);
				cap.writeTabToInv(tabNr, itemHand);
			});
		});
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
	public static class Provider implements ICapabilitySerializable<CompoundTag>{
		TabsCapability capability = new TabsCapability();
		LazyOptional<TabsCapability> cap_provider = LazyOptional.of(() -> capability);

		@Override
		public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
			return cap == CAPABILITY ? cap_provider.cast() : LazyOptional.empty();
		}

		@Override
		public CompoundTag serializeNBT() {
			CompoundTag tag = new CompoundTag();
			tag.putInt("currTab", capability.currTab);
			for (int i = 0; i < capability.tabs.length; i++) {
				tag.put("tab" + i, capability.tabs[i].serializeNBT());
			}
			return tag;
		}

		@Override
		public void deserializeNBT(CompoundTag nbt) {
			capability.update();
			capability.currTab = nbt.getInt("currTab");
			for (int i = 0; i < capability.tabs.length; i++) {
				Tag tabTag = nbt.get("tab" + i);
				if (tabTag instanceof CompoundTag compoundTag)
					capability.tabs[i].deserializeNBT(compoundTag);
			}
		}
	}
}
