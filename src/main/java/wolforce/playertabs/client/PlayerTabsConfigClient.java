package wolforce.playertabs.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class PlayerTabsConfigClient {
	public static ForgeConfigSpec CONFIG_SPEC;
	public static PlayerTabsConfigClient CONFIG;

	public final ConfigValue<List<? extends String>> tabNames;

	public static void init() {
		Pair<PlayerTabsConfigClient, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(PlayerTabsConfigClient::new);
		CONFIG_SPEC = specPair.getRight();
		CONFIG = specPair.getLeft();
	}

	PlayerTabsConfigClient(ForgeConfigSpec.Builder builder) {
		List<String> defaultTabNames = Util.<String>listOf("Tab 1", "Tab 2", "Tab 3", "Tab 4", "Tab 5", "Tab 6", "Tab 7", "Tab 8");
		tabNames = builder.comment("The names of the tabs.").<String>defineList("tabNames", defaultTabNames, str -> true);
	}

	@SuppressWarnings("unchecked")
	public static List<String> getTabNames() {
		return (List<String>) CONFIG.tabNames.get();
	}

	public static void setTabName(int nr, String newName) {
		try {
			@SuppressWarnings("unchecked")
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
}