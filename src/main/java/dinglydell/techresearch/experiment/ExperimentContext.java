package dinglydell.techresearch.experiment;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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

	// TODO: something better than this
	protected double getValue(ResearchType researchType,
			PlayerTechDataExtendedProps ptdep,
			double multiplier,
			T context) {
		double amount = initialValues.get(researchType) * multiplier;
		double amt = amount;

		double threshhold = 1.0;
		int q = getUses(ptdep, context);

		amt /= q;
		amt = Math.round(amt * 10) / 10.0;
		// experiments.put(this, q + 1);

		if (amt < threshhold
				&& Math.round(10 * amount / (q - 1)) / 10.0 >= threshhold) {
			return -1;
			// player.addChatMessage(new ChatComponentText(
			// "You can no longer gain " + discoveredType.getDisplayName()
			// + " knowledge by observing " + name + " here."));
		}

		if (amt < threshhold) {
			return 0;
		}

		// player.addChatMessage(new ChatComponentText("Your observations of "
		// + name + " have earned you " + amt + " "
		// + discoveredType.getDisplayName()
		// + (discoveredType.name.equals("research") ? "" : " research.")));
		return amt;

	}

	protected int getUses(PlayerTechDataExtendedProps ptdep, T context) {
		return getUses(ptdep);
	}

	public Map<ResearchType, Double> getValues(PlayerTechDataExtendedProps ptdep,
			double multiplier,
			T context) {
		Map<ResearchType, Double> values = new HashMap<ResearchType, Double>();
		for (Entry<ResearchType, Double> val : initialValues.entrySet()) {
			values.put(val.getKey(),
					getValue(val.getKey(), ptdep, multiplier, context));
		}
		return values;
	}

}
