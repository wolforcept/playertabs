package wolforce.playertabs.client;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TabButton extends AbstractButton {

	interface Action {
		void execute(TabButton button);
	}

	private Action onPress;
	private ItemStack stack;

	public TabButton(int x, int y, int w, int h, String string, Action onPress) {
		super(x, y, w, h, Util.getItem(string) == null ? new TextComponent(string) : new TextComponent(""));

		Item item = Util.getItem(string);
		stack = new ItemStack(item);
//		stack = (item != null && item != Items.AIR) ?  : null;
		this.onPress = onPress;
	}

	@Override
	public void onPress() {
		this.onPress.execute(this);
	}

	public void renderButton(PoseStack pose, int mx, int my, float f) {
		pose.pushPose();
		pose.setIdentity();
		super.renderButton(pose, mx, my, f);
		if (Util.isValid(stack)) {
			Minecraft.getInstance().getItemRenderer().renderGuiItem(stack, x + width / 2 - 8, y + height / 2 - 8);
		}
		pose.popPose();
	}

	@Override
	public void updateNarration(NarrationElementOutput n) {
	}

}