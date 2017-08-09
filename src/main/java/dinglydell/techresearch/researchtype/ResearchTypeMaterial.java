package dinglydell.techresearch.researchtype;

public class ResearchTypeMaterial extends ResearchType {

	private ItemMaterial material;

	public ResearchTypeMaterial(String name, ResearchType parentType,
			ItemMaterial material) {
		super(name, parentType);
		this.material = material;
	}

	public ItemMaterial getMaterial() {
		return material;
	}

}
