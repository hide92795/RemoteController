package hide92795.android.remotecontroller.util;

import hide92795.android.remotecontroller.R;
import java.util.HashMap;

public class Message {
	private static final HashMap<String, Integer> messages;

	static {
		messages = new HashMap<String, Integer>();
		messages.put("EXCEPTION", R.string.str_exception);
		messages.put("FAILED", R.string.str_failed);
		messages.put("NOT_AUTH", R.string.str_not_auth);
		messages.put("NO_PLAYER", R.string.str_no_player);
		messages.put("NO_ITEM", R.string.str_no_item);
		messages.put("NO_PLUGIN", R.string.str_no_plugin);
		messages.put("PLUGIN_ALREADY_ENABLED", R.string.str_plugin_already_enabled);
		messages.put("PLUGIN_ALREADY_DISABLED", R.string.str_plugin_already_disabled);
	}

	public static final int getMessageID(String str_id) {
		Integer return_val = messages.get(str_id);
		if (return_val == null) {
			return R.string.str_unknown_message;
		}
		return return_val;
	}
}
