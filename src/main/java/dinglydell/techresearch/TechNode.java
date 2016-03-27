package dinglydell.techresearch;

public class TechNode {

	public final String id;

	public final String[] unlocks;

	public final float biology;
	public final float chemistry;
	public final float physics;

	public final String[] requiresAll;
	public final String[] requiresAny;

	public TechNode(String id, String[] unlocks, float biology,
			float chemistry, float physics, String[] requiresAll,
			String[] requiresAny) {
		this.id = id;
		this.unlocks = unlocks;
		this.biology = biology;
		this.chemistry = chemistry;
		this.physics = physics;
		this.requiresAll = requiresAll;
		this.requiresAny = requiresAny;
	}

}
