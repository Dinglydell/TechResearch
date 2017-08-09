package dinglydell.techresearch.experiment;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import dinglydell.techresearch.PlayerTechDataExtendedProps;
import dinglydell.techresearch.researchtype.ItemMaterial;
import dinglydell.techresearch.researchtype.ResearchType;
import dinglydell.techresearch.researchtype.ResearchTypeMaterial;

public class ExperimentMaterial extends ExperimentContext<ItemStack> {

	static Map<Item, ItemMaterial> materialDatabase = new HashMap<Item, ItemMaterial>();

	private double value;

	private ResearchType type;

	public ExperimentMaterial(String name, ResearchType baseType,
			double initialValue) {
		super(name);
		value = initialValue;
		this.type = baseType;
	}

	@Override
	public Map<ResearchType, Double> getValues(PlayerTechDataExtendedProps ptdep,
			double multiplier,
			ItemStack item) {
		Map<ResearchType, Double> values = new HashMap<ResearchType, Double>();
		if (materialDatabase.containsKey(item.getItem())) {
			addValues(values,
					type,
					materialDatabase.get(item.getItem()),
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
