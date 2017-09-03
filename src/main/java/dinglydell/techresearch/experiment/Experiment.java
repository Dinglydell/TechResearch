package dinglydell.techresearch.experiment;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import dinglydell.techresearch.PlayerTechDataExtendedProps;
import dinglydell.techresearch.TechResearch;
import dinglydell.techresearch.TechResearchSettings;
import dinglydell.techresearch.researchtype.ResearchType;

public class Experiment<TContext> {
	public static Experiment fall;
	public static Experiment mobFall;
	public static Experiment pendulum;
	public static ExperimentMaterial anvil;
	public static ExperimentMaterial crafting;
	public static Experiment farming;
	public static Map<String, Experiment> experiments = new HashMap<String, Experiment>();
	static {
		// TODO: have configuration to disable this for custom experiments
		if (TechResearchSettings.defaultExps) {
			Map<ResearchType, Double> fallMap = new HashMap<ResearchType, Double>();
			// fall experiment is multiplied by fall distance - actual values
			// much
			// larger
			fallMap.put(ResearchType.motion, 1.0);
			fall = new Experiment("falling", fallMap);

			mobFall = new Experiment("mobFalling", fallMap);

			Map<ResearchType, Double> pendMap = new HashMap<ResearchType, Double>();
			pendMap.put(ResearchType.motion, 10.0);
			// pendMap.put(ResearchType.engineering, 1.0);
			pendulum = new Experiment("pendulums", pendMap);

			Map<ResearchType, Double> anvilMap = new HashMap<ResearchType, Double>();
			// anvilMap.put(ResearchType.metallurgy, 5.0);
			anvilMap.put(ResearchType.smithing, 20.0);
			anvil = new ExperimentMaterial("anvils", anvilMap,
					ResearchType.science, 5);

			Map<ResearchType, Double> craftingMap = new HashMap<ResearchType, Double>();
			// anvilMap.put(ResearchType.metallurgy, 5.0);
			anvilMap.put(ResearchType.crafting, 20.0);
			crafting = new ExperimentMaterial("crafting", craftingMap,
					ResearchType.science, 5);

			Map<ResearchType, Double> farmMap = new HashMap<ResearchType, Double>();
			farmMap.put(ResearchType.botany, 10.0);
			farming = new Experiment("farming", farmMap);
		}
	}
	public String name;

	protected Map<ResearchType, Double> initialValues;
	protected int cooldown;

	public Experiment(String name) {
		this.name = name;
		if (experiments.containsKey(this.name)) {
			throw new IllegalArgumentException(this.name + " already exists.");
		}
		experiments.put(this.name, this);
	}

	public Experiment(String name, Map<ResearchType, Double> initialValues) {
		this(name, initialValues, 0);
	}

	public Experiment(String name, Map<ResearchType, Double> initialValues,
			int cooldown) {
		this(name);
		this.cooldown = cooldown;
		this.initialValues = initialValues;
		for (Entry<ResearchType, Double> value : initialValues.entrySet()) {
			if (!value.getKey().isBaseType()) {
				throw new IllegalArgumentException(this.name + ": "
						+ value.getKey() + " is not a base type!");
			}
		}
	}

	// TODO: something better than this
	protected double getValue(ResearchType researchType,
			PlayerTechDataExtendedProps ptdep,
			double multiplier,
			TContext context) {
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

	protected int getUses(PlayerTechDataExtendedProps ptdep, TContext context) {
		return getUses(ptdep);
	}

	public Map<ResearchType, Double> getValues(PlayerTechDataExtendedProps ptdep,
			double multiplier,
			TContext context) {
		Map<ResearchType, Double> values = new HashMap<ResearchType, Double>();
		if (cooldownValid(ptdep, context)) {
			for (Entry<ResearchType, Double> val : initialValues.entrySet()) {
				values.put(val.getKey(),
						getValue(val.getKey(), ptdep, multiplier, context));
			}
		}
		return values;
	}

	protected boolean cooldownValid(PlayerTechDataExtendedProps ptdep,
			TContext context) {
		return !ptdep.getExperiments().containsKey(this)
				|| ptdep.player.worldObj.getTotalWorldTime()
						- ptdep.getExperiments().get(this).lastUsed > getCooldown();
	}

	/** Returns how much of this type the player should gain */
	protected double getValue(ResearchType researchType,
			PlayerTechDataExtendedProps ptdep,
			double multiplier) {
		return getValue(researchType, ptdep, multiplier, null);

	}

	/** Gets the number of times this experiment has been performed */
	protected int getUses(PlayerTechDataExtendedProps ptdep) {
		int q;
		if (ptdep.getExperiments().containsKey(this)) {
			q = ptdep.getExperiments().get(this).uses;
		} else {
			q = 1;
		}
		return q;
	}

	/**
	 * Returns the amount this player should get when they perform the
	 * experiment
	 * 
	 * Return -1 for a type if this is the first time it has reached 0 - an
	 * appropriate message will be displayed notifying the player that they
	 * can't get this type from the experiment anymore
	 * */
	public Map<ResearchType, Double> getValues(PlayerTechDataExtendedProps ptdep,
			double multiplier) {

		return getValues(ptdep, multiplier, null);
	}

	public String getDisplayName() {

		return StatCollector.translateToLocal("experiment."
				+ TechResearch.MODID + "." + name);
	}

	protected int getCooldown() {
		return cooldown;
	}

	/** Creates a data object from an NBT tag */
	public PlayerExperimentData getData(NBTTagCompound expTag) {

		return new PlayerExperimentData(expTag);
	}

	/**
	 * Creates a data object for an experiment being used for the first time by
	 * a player
	 */
	public PlayerExperimentData getBlankData() {
		return new PlayerExperimentData();
	}

}
