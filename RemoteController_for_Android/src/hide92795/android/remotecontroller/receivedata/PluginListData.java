package hide92795.android.remotecontroller.receivedata;

import android.os.Parcel;
import android.os.Parcelable;

public class PluginListData extends ReceiveData {
	private PluginData[] plugins;

	public PluginData[] getPlugins() {
		return plugins;
	}

	public void setPlugins(PluginData[] plugins) {
		this.plugins = plugins;
	}

	public static class PluginData implements Parcelable {
		public final String name;
		public final boolean enable;

		public PluginData(String name, boolean enable) {
			this.name = name;
			this.enable = enable;
		}

		public PluginData(Parcel in) {
			this.name = in.readString();
			this.enable = (in.readInt() == 0) ? false : true;
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(name);
			dest.writeInt(enable ? 1 : 0);
		}

		public static final Parcelable.Creator<PluginData> CREATOR = new Parcelable.Creator<PluginData>() {
			public PluginData createFromParcel(Parcel in) {
				return new PluginData(in);
			}

			public PluginData[] newArray(int size) {
				return new PluginData[size];
			}
		};

	}
}
