package wolforce.playertabs.client;

import static net.minecraft.commands.Commands.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class PlayerTabsCommandsClient {

	@SubscribeEvent
	public static void init(RegisterClientCommandsEvent event) {
		CommandDispatcher<CommandSourceStack> d = event.getDispatcher();

		d.register(literal("playertabs")//
				.then(literal("setTabName") //
						.then(Commands.<Integer>argument("numberOfTab", IntegerArgumentType.integer(0, Integer.MAX_VALUE)) //
								.then(Commands.<String>argument("nameOfTab", StringArgumentType.greedyString()) //
										.executes(PlayerTabsCommandsClient.setTabName)//
								)//
						)//
				)//
		); //
	}

	public static final Command<CommandSourceStack> setTabName = (CommandContext<CommandSourceStack> context) -> {
		int nr = context.getArgument("numberOfTab", Integer.class);
		String newName = context.getArgument("nameOfTab", String.class);
		PlayerTabsConfigClient.setTabName(nr, newName);
		return 1;
	};

}
