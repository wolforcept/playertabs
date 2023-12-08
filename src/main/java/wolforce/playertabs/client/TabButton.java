package wolforce.playertabs.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
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
		super(x, y, w, h, Util.getItem(string) == null ? Component.translatable(string) : Component.translatable(""));
		Item item = Util.getItem(string);
		if (item != null){
			stack = new ItemStack(item);
		}
		this.onPress = onPress;
	}

	@Override
	public void onPress() {
		this.onPress.execute(this);
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mx, int my, float f) {
		//渲染item
		PoseStack pose = RenderSystem.getModelViewStack();
		pose.pushPose();
		pose.setIdentity();
		super.render(guiGraphics,mx, my, f);
		if (Util.isValid(stack)) {
			guiGraphics.renderItem(stack, getX() + width / 2 - 8, getY() + height / 2 - 8);
		}
		pose.popPose();
	}
	@Override
	protected void updateWidgetNarration(NarrationElementOutput p_259858_) {}


	public void m_142291_(NarrationElementOutput p_142291_) {
		// Add your implementation here
	}
}