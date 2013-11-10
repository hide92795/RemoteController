package hide92795.bukkit.plugin.remotecontroller.org.apache.commons.io;

import java.io.File;

public class FilenameUtils {
	/**
	 * The system separator character.
	 */
	private static final char SYSTEM_SEPARATOR = File.separatorChar;
	/**
	 * The Windows separator character.
	 */
	private static final char WINDOWS_SEPARATOR = '\\';

	// -----------------------------------------------------------------------
	/**
	 * Determines if Windows file system is in use.
	 * 
	 * @return true if the system is Windows
	 */
	static boolean isSystemWindows() {
		return SYSTEM_SEPARATOR == WINDOWS_SEPARATOR;
	}
}
