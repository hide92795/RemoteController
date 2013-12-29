package hide92795.android.remotecontroller;

import android.app.Activity;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

public class GoogleAnalyticsUtil {
	public static void startActivity(Activity activity) {
		if (!Session.isDebug()) {
			EasyTracker.getInstance(activity.getApplicationContext()).activityStart(activity);
		}
	}

	public static void stopActivity(Activity activity) {
		if (!Session.isDebug()) {
			EasyTracker.getInstance(activity.getApplicationContext()).activityStop(activity);
		}
	}

	public static void dispatchConsoleCommand(Activity activity) {
		if (!Session.isDebug()) {
			EasyTracker.getInstance(activity.getApplicationContext()).send(MapBuilder.createEvent("Action", "Dispatch", "Console", 0l).build());
		}
	}

	public static void dispatchChat(Activity activity) {
		if (!Session.isDebug()) {
			EasyTracker.getInstance(activity.getApplicationContext()).send(MapBuilder.createEvent("Action", "Dispatch", "Chat", 0l).build());
		}
	}

	public static void performBan(Activity activity) {
		if (!Session.isDebug()) {
			EasyTracker.getInstance(activity.getApplicationContext()).send(MapBuilder.createEvent("Action", "Perform", "Ban", 0l).build());
		}
	}

	public static void performKick(Activity activity) {
		if (!Session.isDebug()) {
			EasyTracker.getInstance(activity.getApplicationContext()).send(MapBuilder.createEvent("Action", "Perform", "Kick", 0l).build());
		}
	}

	public static void performGive(Activity activity) {
		if (!Session.isDebug()) {
			EasyTracker.getInstance(activity.getApplicationContext()).send(MapBuilder.createEvent("Action", "Perform", "Give", 0l).build());
		}
	}

	public static void performGamemode(Activity activity) {
		if (!Session.isDebug()) {
			EasyTracker.getInstance(activity.getApplicationContext()).send(MapBuilder.createEvent("Action", "Perform", "Gamemode", 0l).build());
		}
	}

	public static void pluginEnable(Activity activity) {
		if (!Session.isDebug()) {
			EasyTracker.getInstance(activity.getApplicationContext()).send(MapBuilder.createEvent("Action", "Plugin", "Enable", 0l).build());
		}
	}

	public static void pluginDisable(Activity activity) {
		if (!Session.isDebug()) {
			EasyTracker.getInstance(activity.getApplicationContext()).send(MapBuilder.createEvent("Action", "Plugin", "Disable", 0l).build());
		}
	}

	public static void pluginShow(Activity activity) {
		if (!Session.isDebug()) {
			EasyTracker.getInstance(activity.getApplicationContext()).send(MapBuilder.createEvent("Action", "Plugin", "Show", 0l).build());
		}
	}
}
