package wolforce.playertabs.server;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class PlayerTabsConfigServer {
	public static ForgeConfigSpec CONFIG_SPEC;
	public static PlayerTabsConfigServer CONFIG;

	public final IntValue numberOfTabs;

	public static void init() {
		Pair<PlayerTabsConfigServer, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(PlayerTabsConfigServer::new);
		CONFIG_SPEC = specPair.getRight();
		CONFIG = specPair.getLeft();
	}

	PlayerTabsConfigServer(ForgeConfigSpec.Builder builder) {
		numberOfTabs = builder.comment("The number Yeah,of tabs.").defineInRange("tabNames", 3, 1, Integer.MAX_VALUE);
	}

	public static int getNumberOfTabs() {
		return CONFIG.numberOfTabs.get();
	}

	public static void setNumberOfTabs(int nrOfTabs) {
		CONFIG.numberOfTabs.set(nrOfTabs);
		CONFIG.numberOfTabs.save();
	}

}