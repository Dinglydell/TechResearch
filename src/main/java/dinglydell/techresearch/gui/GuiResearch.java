package dinglydell.techresearch.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dinglydell.techresearch.PlayerTechDataExtendedProps;
import dinglydell.techresearch.TechResearch;
import dinglydell.techresearch.event.TechKeyBindings;
import dinglydell.techresearch.network.PacketBuyTech;
import dinglydell.techresearch.researchtype.ResearchType;
import dinglydell.techresearch.techtree.NodeProgress;
import dinglydell.techresearch.techtree.TechNode;

//TODO: container for GuiResearch
public class GuiResearch extends GuiScreen {
	public static enum ResearchLevel {
		basic("gui.techresearch.basic", 2, new ResourceLocation(
				TechResearch.MODID + ":textures/gui/research_basic.png"), 80,
				45, 2, 160, 0xFFFFFF, 0xFFFFFF),
		notebook("gui.techresearch.notebook", 3, new ResourceLocation(
				TechResearch.MODID + ":textures/gui/research_notebook.png"),
				121, 27, 2, 121, 0x000000, 0xFFFFFF),
		table("gui.techresearch.table", 4, new ResourceLocation(
				TechResearch.MODID + ":textures/gui/research.png"), 80, 8, 0,
				160, 0xFFFFFF, 0xFFFFFF);

		public final ResourceLocation texture;
		private int numOptions;
		public final int buttonStartX;
		public final int buttonStartY;
		public final int buttonMargin;
		public final int buttonWidth;
		public final int textColour;
		public final int buttonTextColour;
		private String unlocalisedName;

		ResearchLevel(String unlocalisedName, int options,
				ResourceLocation texture, int buttonStartX, int buttonStartY,
				int buttonMargin, int buttonWidth, int textColour,
				int buttonTextColour) {
			this.unlocalisedName = unlocalisedName;
			numOptions = options;
			this.texture = texture;
			this.buttonStartX = buttonStartX;
			this.buttonStartY = buttonStartY;
			this.buttonMargin = buttonMargin;
			this.buttonWidth = buttonWidth;
			this.textColour = textColour;
			this.buttonTextColour = buttonTextColour;
		}

		public String getDisplayName() {
			return StatCollector.translateToLocal(unlocalisedName);
		}

		public int getNumOptions() {
			return numOptions;
		}
	}

	// private static final ResourceLocation TEXTURE = new ResourceLocation(
	// TechResearch.MODID + ":textures/gui/research.png");
	private static final int GUI_WIDTH = 256;
	private static final int GUI_HEIGHT = 166;
	// private static final int POINTS_WIDTH = 80;
	private PlayerTechDataExtendedProps ptdep;
	private Collection<TechNode> options;

	private List<CostComponent> components = new ArrayList<CostComponent>();
	public ResearchLevel level;

	public GuiResearch(ResearchLevel level) {
		this.level = level;
		ptdep = PlayerTechDataExtendedProps
				.get(Minecraft.getMinecraft().thePlayer);
		Collection<TechNode> nodes = ptdep.getAvailableNodes();
		options = new ArrayList<TechNode>(nodes).subList(0,
				Math.min(nodes.size(), level.getNumOptions()));

	}

	@Override
	public void initGui() {
		super.initGui();
		int i = 0;
		buttonList.clear();
		int offsetLeft = (width - GUI_WIDTH) / 2;
		int offsetTop = (height - GUI_HEIGHT) / 2;
		for (TechNode node : options) {
			buttonList.add(new OptionButton(i++, offsetLeft
					+ level.buttonStartX + 10, offsetTop + level.buttonStartY
					+ (i - 1) * 39, level.buttonWidth, 30, node, ptdep
					.getProgress(node), ptdep, level));
		}
		int textX = 8;
		int textY = 14;
		// i = 0;
		components.clear();

		ResearchType rt = ResearchType.getTopType();
		addComponents(rt, offsetLeft + textX, offsetTop + textY);

	}

	private int addComponents(ResearchType rt, int offsetLeft, int offsetTop) {
		if (ptdep.hasDiscovered(rt)) {
			addComponent(rt, offsetLeft, offsetTop);

			int offsetChange = 16;
			offsetTop += offsetChange;
			offsetLeft += 4;
		}
		for (ResearchType child : rt.getChildren()) {

			offsetTop = addComponents(child, offsetLeft, offsetTop);

		}

		return offsetTop;
		// for (ResearchType rt : ResearchType.getTypes().values()) {
		// if (rt.isBaseDiscoveredType(ptdep)
		// && !(rt.isOtherType(ptdep) && ptdep
		// .getDisplayResearchPoints(rt.name) == 0)) {
		// components.add(new CostComponent(mc, offsetLeft + textX,
		// offsetTop + textY + (i++ * 16), rt, ptdep
		// .getDisplayResearchPoints(rt.name)));
		//
		// }
		// }

	}

	private void addComponent(ResearchType rt, int offsetLeft, int offsetTop) {

		components.add(new CostComponent(mc, offsetLeft, offsetTop, rt, Math
				.round(rt.getValue(ptdep) * 100) / 100.0, ptdep));

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

		mc.getTextureManager().bindTexture(this.level.texture);
		int offsetLeft = (width - GUI_WIDTH) / 2;
		int offsetTop = (height - GUI_HEIGHT) / 2;
		drawTexturedModalRect(offsetLeft,
				offsetTop,
				0,
				0,
				GUI_WIDTH,
				GUI_HEIGHT);
		fontRendererObj.drawString(level.getDisplayName(),
				offsetLeft + 6,
				offsetTop + 4,
				level.textColour);
		// drawString(this.fontRendererObj,
		// "test",
		// offsetLeft + 6,
		// offsetTop + 4,
		// level.textColour);
		GL11.glColor4f(1, 1, 1, 1);

		List<String> tooltip = new ArrayList<String>();
		for (CostComponent cost : components) {
			cost.drawCost(level.textColour);
			boolean isHovering = cost.isHovering(x, y);
			if (isHovering) {
				cost.addTooltip(tooltip, true);

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

	public static void openGui(ResearchLevel level) {
		Minecraft.getMinecraft().displayGuiScreen(new GuiResearch(level));
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
		private PlayerTechDataExtendedProps ptdep;

		public CostComponent(Minecraft mc, int x, int y, ResearchType type,
				double cost, PlayerTechDataExtendedProps ptdep) {
			this.mc = mc;
			this.type = type;
			this.cost = cost;
			this.ptdep = ptdep;
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

		public void drawCost(int colour) {
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
					colour,
					false);
			GL11.glColor4f(1, 1, 1, 1);
		}

		private String getCostString() {
			return ((int) this.cost == this.cost ? ("" + (int) this.cost)
					: ("" + this.cost));
		}

		public void addTooltip(List<String> tooltip) {
			addTooltip(tooltip, false);
		}

		public void addTooltip(List<String> tooltip, boolean verbose) {
			tooltip.add(type.getDisplayName() + ": " + getCostString());
			if (verbose) {
				ResearchType parent = type.getParentType();
				List<ResearchType> parents = new ArrayList<ResearchType>();
				while (parent != null) {
					parents.add(parent);

					parent = parent.getParentType();
				}
				int indentNum = 1;
				for (int i = 1; i <= parents.size(); i++) {
					parent = parents.get(parents.size() - i);
					if (ptdep.hasDiscovered(parent)) {
						char[] indent = new char[indentNum++];
						Arrays.fill(indent, ' ');
						String indentString = new String(indent);
						tooltip.add(indentString + "-"
								+ parent.getDisplayName());
					}
				}
			}

		}
	}

	@SideOnly(Side.CLIENT)
	static class OptionButton extends GuiButton {
		public boolean hovering;
		TechNode tech;
		private NodeProgress progress;
		private ResearchLevel level;
		private List<CostComponent> components = new ArrayList<CostComponent>();

		public OptionButton(int id, int x, int y, int width, int height,
				TechNode tech, NodeProgress progress,
				PlayerTechDataExtendedProps ptdep, ResearchLevel level) {
			super(id, x, y, width, height, tech.getDisplayName());
			this.tech = tech;
			this.progress = progress;
			this.visible = true;
			this.level = level;
			Minecraft mc = Minecraft.getMinecraft();
			FontRenderer fontrenderer = mc.fontRenderer;
			int costX = 2 * (x + 1) + (level.buttonMargin);
			for (Entry<ResearchType, Double> cost : tech.costs.entrySet()) {
				CostComponent cc = new CostComponent(mc, costX, 2
						* (y + height - 8) - level.buttonMargin, cost.getKey(),
						cost.getValue(), ptdep);
				components.add(cc);
				costX += cc.getWidth() * 1.5;
			}
		}

		public void addTooltip(List<String> tooltip) {
			tooltip.add(tech.type.getChatColour() + tech.getDisplayName());
			for (CostComponent cost : components) {
				cost.addTooltip(tooltip);
			}
			List<String> unlocked = tech.getUnlockedDisplay();
			if (unlocked.size() > 0) {
				tooltip.add(StatCollector
						.translateToLocal("gui.techresearch.tooltip.unlocks"));
				for (String un : unlocked) {
					tooltip.add("  " + un);
				}
			}
			String typeDesc = tech.type.getDescription();
			if (typeDesc != null) {
				tooltip.add(tech.type.getChatColour() + ""
						+ EnumChatFormatting.ITALIC + typeDesc);
			}
			String desc = tech.getDescription();
			if (!desc.contains(".desc")) {
				tooltip.add("");
				int n = 40;
				// int repeat = (int) Math.ceil(desc.length() / (double) n);
				int end = n;
				for (int i = 0; i < desc.length(); i = end, end += n) {

					while (end < desc.length()
							&& end > i
							&& !(desc.charAt(end) == ' ' || desc
									.charAt(end - 1) == ' ')) {
						end--;
					}
					if (end == i) {
						end += n;
					}
					tooltip.add(desc.substring(i, Math.min(desc.length(), end)));
				}
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
				mc.getTextureManager().bindTexture(level.texture);
				int textureX = 0;
				int textureY = GUI_HEIGHT;

				if (hovering) {
					textureY += height;// + 1;
				}

				drawTexturedModalRect(xPosition,
						yPosition,
						textureX,
						textureY,
						width,
						height);

				fontrenderer.drawString(tech.getDisplayName(),
						xPosition + 2 + level.buttonMargin,
						yPosition + height / 3,
						level.buttonTextColour,
						false);
				float scale = 0.5f;
				GL11.glScalef(scale, scale, scale);
				for (CostComponent cost : components) {
					cost.drawCost(level.buttonTextColour);
				}
				int newX = (int) (xPosition / scale);
				int newY = (int) (yPosition / scale);
				fontrenderer.drawString(tech.type.getDisplayName(),
						newX + 2 + (int) (level.buttonMargin / scale),
						(int) (yPosition / scale + 2 + level.buttonMargin),
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
