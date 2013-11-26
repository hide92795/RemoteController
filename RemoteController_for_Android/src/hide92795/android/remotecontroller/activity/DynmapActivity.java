package hide92795.android.remotecontroller.activity;

import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.Session;
import hide92795.android.remotecontroller.receivedata.DynmapData;
import hide92795.android.remotecontroller.ui.dialog.CircleProgressDialogFragment.OnCancelListener;
import hide92795.android.remotecontroller.util.LogUtil;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class DynmapActivity extends FragmentActivity implements OnCancelListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.d("DynmapActivity#onCreate()");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_dynmap);
		startLoad();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void startLoad() {
		LogUtil.d("DynmapActivity#onResume()");
		DynmapData data = ((Session) getApplication()).getServerInfo().getDynmapData();
		if (data != null) {
			String address = ((Session) getApplication()).getConnection().getServerAddress();
			int port = data.getPort();
			final String dynmap_address = "http://" + address + ":" + port;

			WebView webview = (WebView) findViewById(R.id.web_dynmap_web);
			webview.getSettings().setJavaScriptEnabled(true);
			webview.getSettings().setBuiltInZoomControls(true);
			webview.getSettings().setAppCacheEnabled(false);
			webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
			webview.setHorizontalScrollbarOverlay(true);
			webview.setHorizontalScrollBarEnabled(false);
			webview.setVerticalScrollbarOverlay(true);
			webview.setVerticalScrollBarEnabled(false);
			webview.setWebViewClient(new WebViewClient() {
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					return url.equals(dynmap_address);
				}

				@Override
				public void onPageStarted(WebView view, String url, Bitmap favicon) {
					((Session) getApplication()).showProgressDialog(DynmapActivity.this, true, DynmapActivity.this);
				}

				@Override
				public void onPageFinished(WebView view, String url) {
					((Session) getApplication()).dismissProgressDialog();
				}
			});
			webview.loadUrl(dynmap_address);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.d("DynmapActivity#onDestroy()");
	}

	@Override
	public void onCancel() {
		finish();
	}
}
