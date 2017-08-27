package dinglydell.techresearch.experiment;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import dinglydell.techresearch.researchtype.ResearchType;

public class ExperimentNotebook extends ExperimentContext<Block> {

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
