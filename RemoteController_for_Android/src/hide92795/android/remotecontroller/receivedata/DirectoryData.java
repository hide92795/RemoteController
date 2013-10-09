package hide92795.android.remotecontroller.receivedata;

import android.os.Parcel;
import android.os.Parcelable;

public class DirectoryData extends ReceiveData {
	private File[] files;
	private String dir;
	private boolean root;

	public void setFiles(File[] files) {
		this.files = files;
	}

	public File[] getFiles() {
		return this.files;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public boolean isRoot() {
		return root;
	}

	public void setRoot(boolean root) {
		this.root = root;
	}

	public static class File implements Parcelable {
		private final String name;
		private final String path;
		private final boolean directory;

		public File(String name, String path, boolean directory) {
			this.name = name;
			this.path = path;
			this.directory = directory;
		}

		public File(Parcel in) {
			this.name = in.readString();
			this.path = in.readString();
			this.directory = (in.readInt() == 0) ? false : true;
		}

		public String getName() {
			return name;
		}

		public String getPath() {
			return path;
		}

		public boolean isDirectory() {
			return directory;
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			out.writeString(name);
			out.writeString(path);
			out.writeInt(directory ? 1 : 0);
		}

		public static final Parcelable.Creator<File> CREATOR = new Parcelable.Creator<File>() {
			public File createFromParcel(Parcel in) {
				return new File(in);
			}

			public File[] newArray(int size) {
				return new File[size];
			}
		};

		public static File createUpDir(String dir) {
			String updir = dir.substring(0, dir.lastIndexOf("/"));
			return new File("..", updir, true);
		}
	}
}
