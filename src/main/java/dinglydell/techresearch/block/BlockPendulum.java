package dinglydell.techresearch.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import dinglydell.techresearch.Experiment;
import dinglydell.techresearch.PlayerTechDataExtendedProps;

public class BlockPendulum extends Block {

	public BlockPendulum() {
		super(Material.wood);

	}

	@Override
	public boolean onBlockActivated(World world,
			int x,
			int y,
			int z,
			EntityPlayer player,
			int p_149727_6_,
			float p_149727_7_,
			float p_149727_8_,
			float p_149727_9_) {
		if (!world.isRemote) {
			PlayerTechDataExtendedProps.get(player)
					.addPhysics(Experiment.pendulum);
		}
		return false;
	}
}
