package wolforce.playertabs;

import static net.minecraft.commands.Commands.literal;

import java.util.function.Predicate;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
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
		); //
	}

	public static final Command<CommandSourceStack> setNumberOfTabs = (CommandContext<CommandSourceStack> context) -> {
		int nr = context.getArgument("numberOfTab", Integer.class);
		PlayerTabsConfigServer.setNumberOfTabs(nr);
		for (ServerPlayer serverplayer : context.getSource().getServer().getPlayerList().getPlayers())
			Net.sendNumberOfTabs(serverplayer, nr);
		return 1;
	};

}
