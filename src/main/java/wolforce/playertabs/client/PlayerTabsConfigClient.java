package wolforce.playertabs.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

@SuppressWarnings("unchecked")
public class PlayerTabsConfigClient {
	public static ForgeConfigSpec CONFIG_SPEC;
	public static PlayerTabsConfigClient CONFIG;

	public final ConfigValue<List<? extends String>> tabNames;
	public final ConfigValue<List<? extends String>> screensBlacklist;
	public final ConfigValue<Boolean> showScreenNames;
	public final ConfigValue<Boolean> AutoPickup;

	public static void init() {
		Pair<PlayerTabsConfigClient, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(PlayerTabsConfigClient::new);
		CONFIG_SPEC = specPair.getRight();
		CONFIG = specPair.getLeft();
	}

	PlayerTabsConfigClient(ForgeConfigSpec.Builder builder) {
		List<String> defaultTabNames = Util.listOf("Tab 0", "Tab 1", "Tab 2", "Tab 3", "Tab 4", "Tab 5", "Tab 6", "Tab 7", "Tab 8");
		tabNames = builder.comment("The names of the tabs.").<String>defineList("tabNames", defaultTabNames, str -> true);
		screensBlacklist = builder.comment("The names of the screens that tabs should NOT be rendered in.").<String>defineList("screensBlacklist", Util.<String>listOf(), str -> true);
		showScreenNames = builder.comment("Show the names of the opened screens in chat.").define("showScreenNames", false);
		AutoPickup = builder.comment("AutoPickup").define("AutoPickup", true);
	}

	public static List<String> getTabNames() {
		return (List<String>) CONFIG.tabNames.get();
	}

	public static void setTabName(int nr, String newName) {
		try {
			List<String> list = (List<String>) CONFIG.tabNames.get();
			if (nr >= list.size()) {
				List<String> newlist = new ArrayList<>(Collections.nCopies(nr + 1, ""));
				Collections.copy(newlist, list);
				list = newlist;
			}
			list.set(nr, newName);
			CONFIG.tabNames.set(list);
			CONFIG.tabNames.save();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<String> getBlacklistedScreens() {
		return (List<String>) CONFIG.screensBlacklist.get();
	}

	public static boolean isShowScreenNames() {
		return CONFIG.showScreenNames.get();
	}

	public static void setShowScreenNames(boolean val) {
		CONFIG.showScreenNames.set(val);
	}

	public static void addScreenName(String screenName) {
		try {
			List<String> blacklist = (List<String>) CONFIG.screensBlacklist.get();

			if (blacklist.contains(screenName)) {
				blacklist.remove(screenName);
				CONFIG.screensBlacklist.set(blacklist);
				CONFIG.screensBlacklist.save();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean getAutoPickup() {
		return CONFIG.AutoPickup.get();
	}


	public static void removeScreenName(String screenName) {
		try {
			List<String> blacklist = (List<String>) CONFIG.screensBlacklist.get();
			if (!blacklist.contains(screenName)) {
				blacklist.add(screenName);
				CONFIG.screensBlacklist.set(blacklist);
				CONFIG.screensBlacklist.save();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}