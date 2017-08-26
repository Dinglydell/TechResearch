package dinglydell.techresearch.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import dinglydell.techresearch.PlayerTechDataExtendedProps;
import dinglydell.techresearch.experiment.ExperimentNotebook;

public class ItemNotebook extends Item {

	public ItemNotebook() {

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
		Block block = world.getBlock(x, y, z);
		if (!ExperimentNotebook.notebookExperiments.containsKey(block)) {
			player.addChatMessage(new ChatComponentText(
					"Nothing can be learned from this"));
			return false;
		}
		ExperimentNotebook exp = ExperimentNotebook.notebookExperiments
				.get(block);
		PlayerTechDataExtendedProps.get(player)
				.addResearchPoints(exp, 1, block);
		return true;
	}
}
