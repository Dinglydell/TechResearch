package dinglydell.techresearch.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import dinglydell.techresearch.TechResearch;

public class GuiResearch extends GuiScreen {
	private static final ResourceLocation TEXTURE = new ResourceLocation(
			TechResearch.MODID + ":textures/gui/research.png");
	private static final int GUI_WIDTH = 256;
	private static final int GUI_HEIGHT = 256;

	public GuiResearch() {
	}

	@Override
	public void drawScreen(int parWidth, int parHeight, float p_73863_3_) {
		super.drawScreen(parWidth, parHeight, p_73863_3_);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		mc.getTextureManager().bindTexture(TEXTURE);
		int offsetFromScreenLeft = (width - GUI_WIDTH) / 2;
		drawTexturedModalRect(offsetFromScreenLeft,
				2,
				0,
				0,
				GUI_WIDTH,
				GUI_HEIGHT);
		int widthOfString;

	}

	public static void openGui() {
		Minecraft.getMinecraft().displayGuiScreen(new GuiResearch());
	}
}
