package wolforce.playertabs;

import static net.minecraft.commands.Commands.literal;

import java.util.function.Predicate;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import wolforce.playertabs.client.PlayerTabsConfigClient;
import wolforce.playertabs.net.Net;
import wolforce.playertabs.server.PlayerTabsConfigServer;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerTabsCommands {

	private static Predicate<CommandSourceStack> OP = (source) -> {
		return source.hasPermission(2);
	};

	@SubscribeEvent
	public static void init(RegisterCommandsEvent event) {
		CommandDispatcher<CommandSourceStack> d = event.getDispatcher();

		d.register(literal("playertabs")//
				.then(literal("setNumberOfTabs") //
						.requires(OP)//
						.then(Commands.<Integer>argument("numberOfTab", IntegerArgumentType.integer(0, Integer.MAX_VALUE)) //
								.executes(PlayerTabsCommands.setNumberOfTabs)//
						)//
				)//
				.then(literal("addTab") //
						.requires(OP)//
						.executes(PlayerTabsCommands.addTab)//
				)//
				.then(literal("setShowScreenNames") //
						.then(Commands.<Boolean>argument("showScreenNames", BoolArgumentType.bool()) //
								.executes(PlayerTabsCommands.showScreenNames)//
						)//
				)//
				.then(literal("addScreenName") //
						.then(Commands.<String>argument("screenName", StringArgumentType.word()) //
								.executes(PlayerTabsCommands.addScreenName)//
						)//
				)//
				.then(literal("removeScreenName") //
						.then(Commands.<String>argument("screenName", StringArgumentType.word()) //
								.executes(PlayerTabsCommands.removeScreenName)//
						)//
				)//
		); //
	}

	public static final Command<CommandSourceStack> setNumberOfTabs = (CommandContext<CommandSourceStack> context) -> {
		int nr = context.getArgument("numberOfTab", Integer.class);
		PlayerTabsConfigServer.setNumberOfTabs(nr);
		for (ServerPlayer serverplayer : context.getSource().getServer().getPlayerList().getPlayers())
			Net.sendNumberOfTabs(serverplayer, nr);
		return 1;
	};

	public static final Command<CommandSourceStack> addTab = (CommandContext<CommandSourceStack> context) -> {
		int nr = PlayerTabsConfigServer.getNumberOfTabs() + 1;
		PlayerTabsConfigServer.setNumberOfTabs(nr);
		for (ServerPlayer serverplayer : context.getSource().getServer().getPlayerList().getPlayers())
			Net.sendNumberOfTabs(serverplayer, nr);
		return 1;
	};

	public static final Command<CommandSourceStack> showScreenNames = (CommandContext<CommandSourceStack> context) -> {
		boolean nr = context.getArgument("showScreenNames", Boolean.class);
		PlayerTabsConfigClient.setShowScreenNames(nr);
		return 1;
	};

	public static final Command<CommandSourceStack> addScreenName = (CommandContext<CommandSourceStack> context) -> {
		String screenName = context.getArgument("screenName", String.class);
		PlayerTabsConfigClient.addScreenName(screenName);
		return 1;
	};

	public static final Command<CommandSourceStack> removeScreenName = (CommandContext<CommandSourceStack> context) -> {
		String screenName = context.getArgument("screenName", String.class);
		PlayerTabsConfigClient.removeScreenName(screenName);
		return 1;
	};
}
