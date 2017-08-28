package dinglydell.techresearch.experiment;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import dinglydell.techresearch.experiment.ExperimentNotebook.BlockState;
import dinglydell.techresearch.researchtype.ResearchType;

public class ExperimentNotebook extends Experiment<BlockState> {

	public static class BlockState {
		World world;
		int x;
		int y;
		int z;

		public BlockState(World world, int x, int y, int z) {
			this.world = world;
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}

	public static Map<Block, ExperimentNotebook> notebookExperiments = new HashMap<Block, ExperimentNotebook>();

	public ExperimentNotebook(String name) {
		super(name);
	}

	public ExperimentNotebook(String name,
			Map<ResearchType, Double> initialValues) {
		super(name, initialValues);
	}

	public static void registerNotebookExperiment(Block block,
			ExperimentNotebook notebook) {
		notebookExperiments.put(block, notebook);
	}

}
