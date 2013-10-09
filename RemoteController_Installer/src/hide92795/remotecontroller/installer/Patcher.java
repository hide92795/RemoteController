package hide92795.remotecontroller.installer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javax.swing.SwingWorker;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

public class Patcher extends SwingWorker<Patcher.Result, String> {
	private final Main main;
	private final File bukkit_file;

	public Patcher(Main main, File file) {
		this.main = main;
		this.bukkit_file = file;
	}

	@Override
	protected Result doInBackground() throws Exception {
		File temp = new File(bukkit_file.getParentFile(), "temp");
		temp.mkdirs();
		Result result = new Result();
		String name = null;

		System.out.print("Unzipping jar... ");
		publish("Unzipping jar...");
		{
			try (ZipArchiveInputStream archive = new ZipArchiveInputStream(new FileInputStream(bukkit_file))) {
				ZipArchiveEntry entry;
				while ((entry = archive.getNextZipEntry()) != null) {
					String _name = entry.getName();
					if (_name.endsWith("ColouredConsoleSender.class")) {
						name = _name;
						if (name.startsWith("/")) {
							name.replaceFirst("/", "");
						}
					}

					File file = new File(temp, _name);
					if (entry.isDirectory()) {
						file.mkdirs();
					} else {
						if (!file.getParentFile().exists()) {
							file.getParentFile().mkdirs();
						}
						OutputStream out = new FileOutputStream(file);
						IOUtils.copy(archive, out);
						out.close();
					}
				}
			} catch (Exception e) {
				System.out.println();
				e.printStackTrace();
				result.result = Result.FAILED;
				result.message = e.toString();
				return result;
			}
			if (name == null) {
				System.out.println();
				result.result = Result.FAILED;
				result.message = "Target class file not found.";
				return result;
			}
		}
		System.out.println("Success");

		System.out.print("Patching class... ");
		publish("Patching class...");
		{
			ClassPool pool = ClassPool.getDefault();
			pool.insertClassPath(temp.getCanonicalPath());
			CtClass cc = pool.get(name.replaceAll("/", ".").replaceAll("\\.class", ""));
			CtClass[] cc_args = { pool.get("java.lang.String") };
			CtMethod cm = cc.getDeclaredMethod("sendMessage", cc_args);
			pool.insertClassPath(bukkit_file.getCanonicalPath());
			cm.insertBefore("org.bukkit.event.server.ServerBroadcastEvent event = new org.bukkit.event.server.ServerBroadcastEvent($1);"
					+ "org.bukkit.Bukkit.getServer().getPluginManager().callEvent(event);");
			cc.writeFile(temp.getCanonicalPath());
		}
		System.out.println("Success");

		System.out.print("Creating event class... ");
		publish("Creating event class...");
		{
			ClassPool pool = ClassPool.getDefault();
			CtClass cc = pool.get("org.bukkit.event.server.ServerBroadcastEvent");
			cc.writeFile(temp.getCanonicalPath());
		}
		System.out.println("Success");

		System.out.print("Creating patched jar... ");
		publish("Creating patched jar...");
		{
			File patched_jar = new File(bukkit_file.getParentFile(), bukkit_file.getName().replaceAll("\\.jar", "_patched.jar"));
			try (ZipArchiveOutputStream zaos = new ZipArchiveOutputStream(new BufferedOutputStream(new FileOutputStream(patched_jar)))) {
				zaos.setEncoding("Windows-31J");

				String basePath = temp.getCanonicalPath() + File.separator;
				addAll(zaos, basePath, temp);

				zaos.finish();
				zaos.flush();
				zaos.close();

				zaos.flush();
			} catch (Exception e) {
				System.out.println();
				e.printStackTrace();
				result.result = Result.FAILED;
				result.message = e.toString();
				return result;
			}
		}
		System.out.println("Success");

		System.out.print("Deleting temp folder... ");
		publish("Deleting temp folder...");
		{
			deleteAll(temp);
		}
		System.out.println("Success");

		result.result = Result.SUCCESS;

		return result;
	}

	public void deleteAll(File root) {
		if (root == null || !root.exists()) {
			return;
		}
		if (root.isFile()) {
			if (root.exists() && !root.delete()) {
				root.deleteOnExit();
			}
		} else {
			File[] list = root.listFiles();
			for (int i = 0; i < list.length; i++) {
				deleteAll(list[i]);
			}
			if (root.exists() && !root.delete()) {
				root.deleteOnExit();
			}
		}
	}

	private void addAll(ArchiveOutputStream aos, String baseDir, File target) throws IOException {
		if (target.isDirectory()) {
			File[] children = target.listFiles();

			if (children.length == 0) {
				addDir(aos, baseDir, target);
			} else {
				for (File file : children) {
					addAll(aos, baseDir, file);
				}
			}
		} else {
			addFile(aos, baseDir, target);
		}
	}

	private void addFile(ArchiveOutputStream aos, String baseDir, File file) throws IOException {
		String path = file.getCanonicalPath();
		String name = path.substring(baseDir.length());

		aos.putArchiveEntry(new ZipArchiveEntry(name));
		IOUtils.copy(new FileInputStream(file), aos);
		aos.closeArchiveEntry();
	}

	private void addDir(ArchiveOutputStream aos, String baseDir, File file) throws IOException {
		String path = file.getCanonicalPath();
		String name = path.substring(baseDir.length());

		aos.putArchiveEntry(new ZipArchiveEntry(name + "/"));
		aos.closeArchiveEntry();
	}

	@Override
	protected void process(List<String> chunks) {
		String message = chunks.get(chunks.size() - 1);
		main.publish(message);
	}

	@Override
	protected void done() {
		try {
			Result result = get();
			switch (result.result) {
			case Result.SUCCESS:
				main.success();
				break;
			case Result.FAILED:
				main.errorOnProcess(result.message);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			main.errorOnProcess(e.toString());
			e.printStackTrace();
		}
	}

	public class Result {
		public static final int SUCCESS = 0;
		public static final int FAILED = 1;

		private int result;
		private String message;

	}
}
