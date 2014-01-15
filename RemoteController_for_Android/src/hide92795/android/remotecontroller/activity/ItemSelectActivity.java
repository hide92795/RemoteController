package hide92795.android.remotecontroller.activity;

import hide92795.android.remotecontroller.GoogleAnalyticsUtil;
import hide92795.android.remotecontroller.Items;
import hide92795.android.remotecontroller.Items.ItemData;
import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.Session;
import hide92795.android.remotecontroller.ui.adapter.ItemSelectListAdapter;
import hide92795.android.remotecontroller.util.LogUtil;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

public class ItemSelectActivity extends ActionBarActivity implements OnItemClickListener {
	private ItemSelectListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_select);
		LogUtil.d("ItemSelectActivity#onCreate()");
		setListener();
	}

	private void setListener() {
		ListView item_list = (ListView) findViewById(R.id.list_item_select_list);
		EditText input = (EditText) findViewById(R.id.edittext_item_select_item);
		this.adapter = new ItemSelectListAdapter(this);
		onInputTextChanged("");
		item_list.setAdapter(adapter);
		item_list.setOnItemClickListener(this);
		input.addTextChangedListener(new TextWatcher() {
			int currentLength = 0;

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				currentLength = s.toString().length();
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().length() < currentLength) {
					onInputTextChanged(s.toString());
					return;
				}
				boolean unfixed = false;
				Object[] spanned = s.getSpans(0, s.length(), Object.class);
				if (spanned != null) {
					for (Object obj : spanned) {
						if ((s.getSpanFlags(obj) & Spanned.SPAN_COMPOSING) == Spanned.SPAN_COMPOSING) {
							unfixed = true;
						}
					}
				}
				if (!unfixed) {
					onInputTextChanged(s.toString());
				}
			}
		});
	}

	private void onInputTextChanged(String filter) {
		ItemData[] filtered = Items.filter((Session) getApplication(), filter);
		adapter.setItems(filtered);
		if (filtered.length > 0) {
			findViewById(R.id.text_item_select_empty).setVisibility(View.GONE);
		} else {
			findViewById(R.id.text_item_select_empty).setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.d("ItemSelectActivity#onDestroy()");
	}

	@Override
	protected void onStart() {
		super.onStart();
		GoogleAnalyticsUtil.startActivity(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		GoogleAnalyticsUtil.stopActivity(this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
		ItemData itemdata = adapter.getItem(position);
		if (itemdata != null) {
			String item_id = itemdata.item_id;
			Intent i = new Intent();
			Bundle data = new Bundle();
			data.putString("ITEM_ID", item_id);
			i.putExtras(data);
			setResult(RESULT_OK, i);
			finish();
		}
	}
}
