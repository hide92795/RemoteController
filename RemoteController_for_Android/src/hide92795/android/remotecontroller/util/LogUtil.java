package hide92795.android.remotecontroller.util;

import hide92795.android.remotecontroller.Session;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

public class LogUtil {
	private static final SimpleDateFormat LOG_NAME = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS", Locale.ENGLISH);
	private static final boolean debug = true;
	private static PrintWriter logger;

	public static final void d(String msg) {
		if (debug) {
			Log.d("RemoteController", msg);
		}
		if (logger != null) {
			logger.print(LOG_NAME.format(new Date()));
			logger.print(" ");
			logger.println(msg);
			logger.flush();
		}
	}

	public static final void exception(Exception e) {
		if (logger != null) {
			logger.print(LOG_NAME.format(new Date()));
			logger.print(" ");
			logger.println(e.toString());
			StackTraceElement[] trace = e.getStackTrace();
			for (int i = 0; i < trace.length; i++) {
				logger.println("\tat " + trace[i]);
			}

			Throwable ourCause = e.getCause();
			if (ourCause != null) {
				printStackTraceToLoggerAsCause(ourCause, trace);
			}
			logger.flush();
		}
	}

	private static void printStackTraceToLoggerAsCause(Throwable t, StackTraceElement[] causedTrace) {
		StackTraceElement[] trace2 = t.getStackTrace();
		int m = trace2.length - 1, n = causedTrace.length - 1;
		while (m >= 0 && n >= 0 && trace2[m].equals(causedTrace[n])) {
			m--;
			n--;
		}
		int framesInCommon = trace2.length - 1 - m;

		logger.println("Caused by: " + t);
		for (int i = 0; i <= m; i++) {
			logger.println("\tat " + trace2[i]);
		}
		if (framesInCommon != 0) {
			logger.println("\t... " + framesInCommon + " more");
		}

		Throwable ourCause = t.getCause();
		if (ourCause != null) {
			printStackTraceToLoggerAsCause(ourCause, causedTrace);
		}
	}

	public static void startSaveLog(Session session) {
		try {
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(session);
			boolean save = pref.getBoolean(ConfigKeys.SAVE_LOG, ConfigDefaults.SAVE_LOG);
			if (save) {
				File dir = getLogDir(session);
				dir.mkdirs();
				File file = new File(dir, LOG_NAME.format(new Date()) + ".log");
				logger = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), Charset.forName("UTF-8"))));
				logger.print("MODEL: ");
				logger.println(Build.MODEL);
				logger.print("MANUFACTURER: ");
				logger.println(Build.MANUFACTURER);
				logger.print("ANDROID VERSION: ");
				logger.println(Build.VERSION.RELEASE);
				logger.println("==========================");
				d("Start save log.");
			}
		} catch (Exception e) {
			d("Can't save log.");
			e.printStackTrace();
		}
	}

	public static void stopSaveLog() {
		if (logger != null) {
			d("Stop save log.");
			logger.flush();
			logger.close();
			logger = null;
		}
	}

	public static File getLogDir(Session session) {
		return new File(new File(Environment.getExternalStorageDirectory(), "RemoteController"), "log");
	}
}
