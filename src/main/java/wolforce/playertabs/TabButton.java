package wolforce.playertabs;

import java.util.function.ObjIntConsumer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class TabButton extends Button {

//		private final Minecraft mc = Minecraft.getInstance();

	public static final ResourceLocation TEXTURE = new ResourceLocation(PlayerTabs.MOD_ID, "textures/gui/tab.png");

	public TabButton(int x, int y, int width, int height, Component message, Button.OnPress onPress, ObjIntConsumer<TabButton> onCreativeTabChanged) {
		super(x, y, width, height, message, onPress);
	}

	@Override
	public void renderButton(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		super.renderButton(matrix, mouseX, mouseY, partialTicks);

//			if (matrix != null)
//				return;
//
//			RenderSystem.setShader(GameRenderer::getPositionTexShader);
//			RenderSystem.setShaderTexture(0, TEXTURE);
//			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
//			RenderSystem.disableDepthTest();
//			if (isHoveredOrFocused()) {
//				blit(matrix, x, y, 10, 166, 10, 10);
//				drawCenteredString(matrix, mc.font, getMessage(), x + 5, y + height, 0xffffff);
//			} else {
//				blit(matrix, x, y, 0, 166, 10, 10);
//			}
//			RenderSystem.enableDepthTest();
	}

}