package game.engine.sidescroller;

public enum DebugMode {
	OFF, ALL, INFO_ONLY;
	private static DebugMode[] vals = values();

	public DebugMode next() {
		return vals[(this.ordinal() + 1) % vals.length];
	}
}
