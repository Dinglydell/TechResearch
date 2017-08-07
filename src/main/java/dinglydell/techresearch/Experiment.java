package dinglydell.techresearch;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Experiment {
	public static Experiment fall;
	public static Experiment pendulum;
	public static Experiment anvil;
	public static Experiment farming;
	public static Map<String, Experiment> experiments = new HashMap<String, Experiment>();
	static {
		// TODO: have configuration to disable this for custom experiments
		Map<ResearchType, Double> fallMap = new HashMap<ResearchType, Double>();
		// fall experiment is multiplied by fall distance - actual values much
		// larger
		fallMap.put(ResearchType.motion, 1.0);
		fall = new Experiment("falling", fallMap);

		Map<ResearchType, Double> pendMap = new HashMap<ResearchType, Double>();
		pendMap.put(ResearchType.motion, 10.0);
		// pendMap.put(ResearchType.engineering, 1.0);
		pendulum = new Experiment("pendulums", pendMap);

		Map<ResearchType, Double> anvilMap = new HashMap<ResearchType, Double>();
		anvilMap.put(ResearchType.metallurgy, 5.0);
		anvilMap.put(ResearchType.smithing, 20.0);
		anvil = new Experiment("anvils", anvilMap);

		Map<ResearchType, Double> farmMap = new HashMap<ResearchType, Double>();
		farmMap.put(ResearchType.botany, 10.0);
		farming = new Experiment("farming", farmMap);

	}
	public String name;

	public Map<ResearchType, Double> initialValues;

	public Experiment(String name) {
		this.name = name;
		if (experiments.containsKey(this.name)) {
			throw new IllegalArgumentException(this.name + " already exists.");
		}
		experiments.put(this.name, this);
	}

	public Experiment(String name, Map<ResearchType, Double> initialValues) {
		this(name);
		this.initialValues = initialValues;
		for (Entry<ResearchType, Double> value : initialValues.entrySet()) {
			if (!value.getKey().isBaseType()) {
				throw new IllegalArgumentException(value.getKey()
						+ " is not a base type!");
			}
		}
	}
}
