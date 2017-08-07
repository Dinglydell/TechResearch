package dinglydell.techresearch.gui;

import java.util.Collection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dinglydell.techresearch.NodeProgress;
import dinglydell.techresearch.PlayerTechDataExtendedProps;
import dinglydell.techresearch.ResearchType;
import dinglydell.techresearch.TechNode;
import dinglydell.techresearch.TechResearch;
import dinglydell.techresearch.event.TechKeyBindings;
import dinglydell.techresearch.network.PacketBuyTech;

public class GuiResearch extends GuiScreen {
	private static final ResourceLocation TEXTURE = new ResourceLocation(
			TechResearch.MODID + ":textures/gui/research.png");
	private static final int GUI_WIDTH = 256;
	private static final int GUI_HEIGHT = 164;
	private static final int POINTS_WIDTH = 80;
	private PlayerTechDataExtendedProps ptdep;
	private Collection<TechNode> options;

	public GuiResearch() {
		ptdep = PlayerTechDataExtendedProps
				.get(Minecraft.getMinecraft().thePlayer);
		options = ptdep.getAvailableNodes();

	}

	@Override
	public void initGui() {
		super.initGui();
		int i = 0;
		buttonList.clear();
		int offsetLeft = (width - GUI_WIDTH) / 2;
		int offsetTop = (height - GUI_HEIGHT) / 2;
		for (TechNode node : ptdep.getAvailableNodes()) {
			buttonList.add(new OptionButton(i++,
					offsetLeft + POINTS_WIDTH + 10, offsetTop + 8 + (i - 1)
							* 32, 160, 30, node, ptdep.getProgress(node)));
		}
	}

	@Override
	public void updateScreen() {

		super.updateScreen();
	}

	@Override
	protected void actionPerformed(GuiButton parButton) {
		if (parButton instanceof OptionButton) {
			OptionButton button = (OptionButton) parButton;

			EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			TechResearch.snw.sendToServer(new PacketBuyTech(button.tech));

		}
	}

	@Override
	public void drawScreen(int parWidth, int parHeight, float p_73863_3_) {

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		mc.getTextureManager().bindTexture(TEXTURE);
		int offsetLeft = (width - GUI_WIDTH) / 2;
		int offsetTop = (height - GUI_HEIGHT) / 2;
		drawTexturedModalRect(offsetLeft,
				offsetTop,
				0,
				0,
				GUI_WIDTH,
				GUI_HEIGHT);

		int textX = 8;
		int textY = 8;
		float scale = 0.75f;
		GL11.glScalef(scale, scale, scale);
		// draw stuff
		for (ResearchType rt : ResearchType.getTypes().values()) {
			if (rt.isBaseDiscoveredType(ptdep)) {
				String displayName = rt.getDisplayName();
				if (rt.isOtherType(ptdep)) {
					if (ptdep.getDisplayResearchPoints(rt.name) == 0) {
						continue;
					}
					displayName = StatCollector
							.translateToLocal("gui.techresearch.other")
							+ " "
							+ displayName;
				}

				String drawStr = displayName + ": "
						+ ptdep.getDisplayResearchPoints(rt.name);
				if (fontRendererObj.getStringWidth(drawStr) > POINTS_WIDTH) {
					drawStr = displayName.substring(0, 3) + ": "
							+ ptdep.getDisplayResearchPoints(rt.name);
				}
				fontRendererObj.drawString(drawStr,
						(int) ((offsetLeft + textX) / scale),
						(int) ((offsetTop + textY) / scale),
						16777215,
						false);
				textY += 10;
			}
		}
		GL11.glScalef(1 / scale, 1 / scale, 1 / scale);
		super.drawScreen(parWidth, parHeight, p_73863_3_);

	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	protected void keyTyped(char key, int keyCode) {
		super.keyTyped(key, keyCode);

		if (TechKeyBindings.openTable.getKeyCode() == keyCode) {
			Minecraft.getMinecraft().thePlayer.closeScreen();
		}

	}

	public static void openGui() {
		Minecraft.getMinecraft().displayGuiScreen(new GuiResearch());
	}

	@SideOnly(Side.CLIENT)
	static class OptionButton extends GuiButton {
		TechNode tech;
		private NodeProgress progress;

		public OptionButton(int id, int x, int y, int width, int height,
				TechNode tech, NodeProgress progress) {
			super(id, x, y, width, height, tech.getDisplayName());
			this.tech = tech;
			this.progress = progress;
			this.visible = true;
		}

		@Override
		public void drawButton(Minecraft mc, int parX, int parY) {
			if (visible) {
				FontRenderer fontrenderer = mc.fontRenderer;
				boolean isButtonPressed = (parX >= xPosition
						&& parY >= yPosition && parX < xPosition + width && parY < yPosition
						+ height);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(TEXTURE);
				int textureX = 0;
				int textureY = GUI_HEIGHT + 1;

				if (isButtonPressed) {
					textureY += height + 1;
				}

				drawTexturedModalRect(xPosition,
						yPosition,
						textureX,
						textureY,
						width,
						height);

				fontrenderer.drawString(tech.getDisplayName(),
						xPosition + 2,
						yPosition + height / 3,
						0xFFFFFF,
						false);
				float scale = 0.5f;
				GL11.glScalef(scale, scale, scale);
				int newX = (int) (xPosition / scale);
				int newY = (int) (yPosition / scale);
				fontrenderer.drawString(tech.type.getDisplayName(),
						newX + 2,
						(int) (yPosition / scale + 2),
						tech.type.getColour(),
						false);
				fontrenderer.drawString(tech.costsAsString(),
						newX + 2,
						newY + (int) (height / scale)
								- fontrenderer.FONT_HEIGHT,
						0xFFFFFF);
				GL11.glScalef(1 / scale, 1 / scale, 1 / scale);

			}
		}

	}
}
