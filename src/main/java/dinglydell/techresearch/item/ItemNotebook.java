package dinglydell.techresearch.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import dinglydell.techresearch.PlayerTechDataExtendedProps;
import dinglydell.techresearch.TechResearch;
import dinglydell.techresearch.experiment.ExperimentNotebook;
import dinglydell.techresearch.experiment.ExperimentNotebook.BlockState;
import dinglydell.techresearch.gui.GuiResearch;
import dinglydell.techresearch.gui.GuiResearch.ResearchLevel;

public class ItemNotebook extends Item {

	public ItemNotebook() {

	}

	@Override
	public boolean onItemUse(ItemStack stack,
			EntityPlayer player,
			World world,
			int x,
			int y,
			int z,
			int p_77648_7_,
			float p_77648_8_,
			float p_77648_9_,
			float p_77648_10_) {
		if (player.isSneaking()) {
			return false;
		}
		world.playSoundAtEntity(player, TechResearch.MODID
				+ ":item.notebook.open", 1f, 1f);
		if (world.isRemote) {
			GuiResearch.openGui(ResearchLevel.notebook);
		}
		return true;
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack,
			EntityPlayer player,
			World world,
			int x,
			int y,
			int z,
			int side,
			float hitX,
			float hitY,
			float hitZ) {

		if (player.isSneaking()) {
			if (world.isRemote) {
				return false;
			}
			Block block = world.getBlock(x, y, z);
			if (!ExperimentNotebook.notebookExperiments.containsKey(block)) {
				player.addChatMessage(new ChatComponentText(
						"Nothing can be learned from this"));
				return false;
			}
			ExperimentNotebook exp = ExperimentNotebook.notebookExperiments
					.get(block);
			PlayerTechDataExtendedProps.get(player).addResearchPoints(exp,
					1,
					new BlockState(world, x, y, z));
			return true;
		}

		return false;
	}
}
