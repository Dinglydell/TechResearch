package dinglydell.techresearch.experiment;

import java.util.Map;

import net.minecraft.item.ItemStack;
import dinglydell.techresearch.PlayerTechDataExtendedProps;
import dinglydell.techresearch.researchtype.ItemMaterial;
import dinglydell.techresearch.researchtype.ResearchType;
import dinglydell.techresearch.researchtype.ResearchTypeMaterial;

public class ExperimentMaterial extends Experiment<ItemStack> {

	private double value;

	private ResearchType type;

	public ExperimentMaterial(String name, Map<ResearchType, Double> values,
			ResearchType superType, double initialValue) {
		super(name, values);
		value = initialValue;
		this.type = superType;
	}

	@Override
	public Map<ResearchType, Double> getValues(PlayerTechDataExtendedProps ptdep,
			double multiplier,
			ItemStack item) {
		Map<ResearchType, Double> values = super.getValues(ptdep,
				multiplier,
				item);
		if (ItemMaterial.hasEntry(item.getItem())) {
			addValues(values,
					type,
					ItemMaterial.get(item.getItem()),
					multiplier);
		}

		return values;
	}

	public void addValues(Map<ResearchType, Double> values,
			ResearchType type,
			ItemMaterial itemMaterial,
			double multiplier) {
		if (type.isBaseType() && type instanceof ResearchTypeMaterial
				&& ((ResearchTypeMaterial) type).getMaterial() == itemMaterial) {
			values.put(type, value * multiplier);
		}

		for (ResearchType child : type.getChildren()) {
			addValues(values, child, itemMaterial, multiplier);
		}

	}
}
