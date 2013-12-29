package hide92795.bukkit.plugin.remotecontroller;

public class Modifiable {
	private final boolean read;
	private final boolean write;

	public Modifiable(String mode) {
		if (mode == null) {
			mode = "";
		}
		switch (mode.toLowerCase()) {
		case "r":
			this.read = false;
			this.write = false;
			break;
		case "w":
			this.read = true;
			this.write = false;
			break;
		default:
			this.read = true;
			this.write = true;
			break;
		}
	}

	public boolean canRead() {
		return read;
	}

	public boolean canWrite() {
		return write;
	}
}
