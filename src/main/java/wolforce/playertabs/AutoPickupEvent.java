package wolforce.playertabs;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;


@Mod.EventBusSubscriber
public class AutoPickupEvent {
    private static int tabNr = 0;
    @SubscribeEvent
    public static void onPlayerItemPickup(EntityItemPickupEvent event) {
        ItemStack item = event.getItem().getItem();
        Player player = event.getEntity();
        Level level = player.level();
        if (level.isClientSide()) {
            return;
        }
        TabsCapability.get(player).ifPresent(cap ->{
            cap.updateItemSort();
            Map<Item, Integer> itemSortMap = cap.getItemSortMap();
            tabNr = cap.getCurrentTab();
            for (Map.Entry<Item, Integer> entry : itemSortMap.entrySet()) {
                Item itemSort = entry.getKey();
                Integer tab = entry.getValue();
                if (item.getItem() == itemSort) {
                    cap.switchToTab(player, tab);
                }
            }
        });
    }
    @SubscribeEvent
    public static void onPlayerItemPickup1(net.minecraftforge.event.entity.player.PlayerEvent.ItemPickupEvent event) {
        Item item = event.getStack().getItem();
        Player player = event.getEntity();
        Level level = player.level();
        if (level.isClientSide()) {
            return;
        }
        TabsCapability.switchToTab(player,tabNr);
    }
}
