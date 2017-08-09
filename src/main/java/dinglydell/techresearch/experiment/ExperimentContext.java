package dinglydell.techresearch.experiment;

import java.util.Map;

import dinglydell.techresearch.PlayerTechDataExtendedProps;
import dinglydell.techresearch.researchtype.ResearchType;

public abstract class ExperimentContext<T> extends Experiment {

	public ExperimentContext(String name) {
		super(name);
	}

	public ExperimentContext(String name,
			Map<ResearchType, Double> initialValues) {
		super(name, initialValues);
	}

	public abstract Map<ResearchType, Double> getValues(PlayerTechDataExtendedProps ptdep,
			double multiplier,
			T context);

}
