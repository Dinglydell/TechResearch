package dinglydell.techresearch.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.registry.GameData;
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

	private List<CostComponent> components = new ArrayList<CostComponent>();

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
							* 39, 160, 30, node, ptdep.getProgress(node)));
		}
		int textX = 8;
		int textY = 8;
		i = 0;
		components.clear();
		for (ResearchType rt : ResearchType.getTypes().values()) {
			if (rt.isBaseDiscoveredType(ptdep)
					&& !(rt.isOtherType(ptdep) && ptdep
							.getDisplayResearchPoints(rt.name) == 0)) {
				components.add(new CostComponent(mc, offsetLeft + textX,
						offsetTop + textY + (i++ * 16), rt, ptdep
								.getDisplayResearchPoints(rt.name)));

			}
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
	public void drawScreen(int x, int y, float p_73863_3_) {

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
		List<String> tooltip = new ArrayList<String>();
		for (CostComponent cost : components) {
			cost.drawCost();
			boolean isHovering = cost.isHovering(x, y);
			if (isHovering) {
				cost.addTooltip(tooltip);

			}
		}
		// int textX = 8;
		// int textY = 8;
		// float scale = 0.8f;
		// GL11.glScalef(scale, scale, scale);
		// draw stuff
		// for (ResearchType rt : ResearchType.getTypes().values()) {
		// if (rt.isBaseDiscoveredType(ptdep)) {
		// String displayName = rt.getDisplayName();
		// if (rt.isOtherType(ptdep)) {
		// if (ptdep.getDisplayResearchPoints(rt.name) == 0) {
		// continue;
		// }
		// displayName = StatCollector
		// .translateToLocal("gui.techresearch.other")
		// + " "
		// + displayName;
		// }
		//
		// String drawStr = displayName + ": "
		// + ptdep.getDisplayResearchPoints(rt.name);
		// if (fontRendererObj.getStringWidth(drawStr) > POINTS_WIDTH) {
		// drawStr = (rt.isOtherType(ptdep) ? "O. " : "")
		// + rt.getDisplayName().substring(0, 3) + ": "
		// + ptdep.getDisplayResearchPoints(rt.name);
		// }
		// // fontRendererObj.drawString(drawStr,
		// // (int) ((offsetLeft + textX) / scale),
		// // (int) ((offsetTop + textY) / scale),
		// // 16777215,
		// // false);
		// int trueX = (int) ((offsetLeft + textX) / scale);
		// int trueY = (int) ((offsetTop + textY) / scale);
		// mc.getTextureManager().bindTexture(rt.icon);
		// float texScale = 1 / 24f;
		//
		// scaleIcon();
		// drawTexturedModalRect((int) (trueX / texScale),
		// (int) (trueY / texScale),
		// 0,
		// 0,
		// 256,
		// 256);
		// unscaleIcon();
		//
		// fontRendererObj.drawString(""
		// + ptdep.getDisplayResearchPoints(rt.name),
		// trueX + 12,
		// trueY,
		// 0xFFFFFF,
		// false);
		// textY += 20;
		// }
		// }
		// GL11.glScalef(1 / scale, 1 / scale, 1 / scale);

		super.drawScreen(x, y, p_73863_3_);
		for (Object b : buttonList) {
			OptionButton ob = ((OptionButton) b);
			if (ob.hovering) {
				ob.addTooltip(tooltip);
			}
		}
		this.drawHoveringText(tooltip, x, y, fontRendererObj);
	}

	static void scaleIcon() {
		float texScale = 1 / 24f;
		GL11.glScalef(texScale, texScale, texScale);
	}

	static void unscaleIcon() {
		float texScale = 1 / 24f;
		GL11.glScalef(1 / texScale, 1 / texScale, 1 / texScale);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	protected void keyTyped(char key, int keyCode) {
		super.keyTyped(key, keyCode);

		if (TechKeyBindings.openTable.getKeyCode() == keyCode) {
			mc.thePlayer.closeScreen();
		}

	}

	public static void openGui() {
		Minecraft.getMinecraft().displayGuiScreen(new GuiResearch());
	}

	@SideOnly(Side.CLIENT)
	static class CostComponent extends Gui {
		private static final float SCALE = 1 / 20f;
		// private PlayerTechDataExtendedProps ptdep;
		private ResearchType type;
		private double cost;
		private int posX;
		private int posY;
		private Minecraft mc;

		public CostComponent(Minecraft mc, int x, int y, ResearchType type,
				double cost) {
			this.mc = mc;
			this.type = type;
			this.cost = cost;

			posX = x;
			posY = y;

		}

		public boolean isHovering(int x, int y) {

			return (x >= this.posX && y >= this.posY
					&& x < this.posX + this.getWidth() && y < this.posY + 16);
		}

		public int getWidth() {
			FontRenderer fontrenderer = mc.fontRenderer;
			return (int) (256 * SCALE)
					+ fontrenderer.getStringWidth("" + getCostString());

		}

		public void drawCost() {
			FontRenderer fontrenderer = mc.fontRenderer;
			int height = 16;

			mc.getTextureManager().bindTexture(type.icon);

			float scale = SCALE;
			GL11.glScalef(scale, scale, scale);
			drawTexturedModalRect((int) (posX / scale),
					(int) (posY / scale),
					0,
					0,
					256,
					256);

			GL11.glScalef(1 / scale, 1 / scale, 1 / scale);

			fontrenderer.drawString(getCostString(),
					posX + 16,
					posY + (int) (256 * scale / 2) - fontrenderer.FONT_HEIGHT
							/ 2,
					0xFFFFFF,
					false);

		}

		private String getCostString() {
			return ((int) this.cost == this.cost ? ("" + (int) this.cost)
					: ("" + this.cost));
		}

		public void addTooltip(List<String> tooltip) {
			tooltip.add(type.getDisplayName() + ": " + getCostString());

		}
	}

	@SideOnly(Side.CLIENT)
	static class OptionButton extends GuiButton {
		public boolean hovering;
		TechNode tech;
		private NodeProgress progress;
		private List<CostComponent> components = new ArrayList<CostComponent>();

		public OptionButton(int id, int x, int y, int width, int height,
				TechNode tech, NodeProgress progress) {
			super(id, x, y, width, height, tech.getDisplayName());
			this.tech = tech;
			this.progress = progress;
			this.visible = true;
			Minecraft mc = Minecraft.getMinecraft();
			FontRenderer fontrenderer = mc.fontRenderer;
			for (Entry<ResearchType, Double> cost : tech.costs.entrySet()) {
				components.add(new CostComponent(mc, 2 * (x + 1), 2 * (y
						+ height - 8), cost.getKey(), cost.getValue()));
			}
		}

		public void addTooltip(List<String> tooltip) {
			tooltip.add(tech.getDisplayName());
			for (CostComponent cost : components) {
				cost.addTooltip(tooltip);
			}
			if (tech.unlocks.length > 0) {
				tooltip.add(StatCollector
						.translateToLocal("gui.techresearch.tooltip.unlocks"));
				for (String un : tech.unlocks) {
					Item it = GameData.getItemRegistry().getObject(un);
					tooltip.add("  "
							+ StatCollector.translateToLocal(it
									.getUnlocalizedName() + ".name"));
				}
			}
			String desc = tech.getDescription();
			if (!desc.contains(".desc")) {
				tooltip.add(desc);
			}
		}

		@Override
		public void drawButton(Minecraft mc, int parX, int parY) {
			if (visible) {
				FontRenderer fontrenderer = mc.fontRenderer;
				hovering = (parX >= xPosition && parY >= yPosition
						&& parX < xPosition + width && parY < yPosition
						+ height);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(TEXTURE);
				int textureX = 0;
				int textureY = GUI_HEIGHT + 1;

				if (hovering) {
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
				for (CostComponent cost : components) {
					cost.drawCost();
				}
				int newX = (int) (xPosition / scale);
				int newY = (int) (yPosition / scale);
				fontrenderer.drawString(tech.type.getDisplayName(),
						newX + 2,
						(int) (yPosition / scale + 2),
						tech.type.getColour(),
						false);

				// String str;
				// if (progress == null) {
				// str = tech.costsAsString();
				// } else {
				// str = tech.costsAsString(progress);
				// }
				// fontrenderer.drawString(tech.costsAsString(),
				// newX + 2,
				// newY + (int) (height / scale)
				// - fontrenderer.FONT_HEIGHT,
				// 0xFFFFFF);
				GL11.glScalef(1 / scale, 1 / scale, 1 / scale);

			}
		}

	}
}
