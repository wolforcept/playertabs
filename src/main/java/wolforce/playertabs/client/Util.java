package wolforce.playertabs.client;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

public class Util {

	@SafeVarargs
	public static <T> List<T> listOf(T... objs) {
		ArrayList<T> list = new ArrayList<>(objs.length);
		for (int i = 0; i < objs.length; i++)
			list.add(objs[i]);
		return list;
	}

	public static @Nullable Item getItem(String str) {
		try {
			ResourceLocation res = new ResourceLocation(str);
			if (ForgeRegistries.ITEMS.containsKey(res))
				return ForgeRegistries.ITEMS.getValue(res);
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return null;
	}

	public static void spawnItem(Level world, Vec3 pos, ItemStack stack) {
		if (!Util.isValid(stack))
			return;
		ItemEntity entityitem = new ItemEntity(world, pos.x(), pos.y(), pos.z(), stack);
		entityitem.setDeltaMovement(new Vec3(//
				Math.random() * .4 - .2, //
				Math.random() * .2, //
				Math.random() * .4 - .2 //
		));
		entityitem.setDefaultPickUpDelay();
		world.addFreshEntity(entityitem);
	}

	public static boolean isValid(ItemStack stack) {
		return stack != null && stack.getCount() > 0 && !stack.isEmpty() && !stack.getItem().equals(Items.AIR);
	}

}
