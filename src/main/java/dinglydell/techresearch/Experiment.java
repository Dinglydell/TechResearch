package dinglydell.techresearch;

public enum Experiment {
	fall("falling", 10), pendulum("pendulums", 20);
	public String name;
	public int initialValue;

	private Experiment(String name, int initialValue) {
		this.name = name;
		this.initialValue = initialValue;
	}
}
