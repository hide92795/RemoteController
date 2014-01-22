package hide92795.android.remotecontroller.config;

import hide92795.android.remotecontroller.widget.WidgetData;
import java.io.Serializable;
import java.util.HashMap;
import android.annotation.SuppressLint;

@SuppressLint("UseSparseArrays")
public class WidgetConfig implements Serializable {
	private static final long serialVersionUID = 2093514259242035466L;
	private HashMap<Integer, WidgetData> widgets;

	public WidgetConfig() {
		this.widgets = new HashMap<Integer, WidgetData>();
	}

	public HashMap<Integer, WidgetData> getWidgetDatas() {
		return widgets;
	}

	public void setWidgetDatas(HashMap<Integer, WidgetData> widgets) {
		this.widgets = widgets;
	}
}
