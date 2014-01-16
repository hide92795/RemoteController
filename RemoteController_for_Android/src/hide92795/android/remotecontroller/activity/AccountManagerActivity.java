package hide92795.android.remotecontroller.activity;

import hide92795.android.remotecontroller.GoogleAnalyticsUtil;
import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.Session;
import hide92795.android.remotecontroller.ui.adapter.AccountManagerListAdapter;
import hide92795.android.remotecontroller.ui.dialog.AccountDeleteDialogFragment;
import hide92795.android.remotecontroller.ui.dialog.AccountHandleDialogFragment;
import hide92795.android.remotecontroller.util.LogUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

public class AccountManagerActivity extends ActionBarActivity implements OnItemLongClickListener, AccountHandleDialogFragment.Callback, AccountDeleteDialogFragment.Callback {
	private AccountManagerListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.d("AccountManagerActivity#onCreate()");
		setContentView(R.layout.activity_account_manager);
		setAdapter();
		setListener();
	}

	@Override
	protected void onStart() {
		super.onStart();
		LogUtil.d("AccountManagerActivity#onStart()");
		GoogleAnalyticsUtil.startActivity(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.d("AccountManagerActivity#onResume()");
	}

	@Override
	protected void onPause() {
		super.onPause();
		LogUtil.d("AccountManagerActivity#onPause()");
	}

	@Override
	protected void onStop() {
		super.onStop();
		LogUtil.d("AccountManagerActivity#onStop()");
		GoogleAnalyticsUtil.stopActivity(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.d("AccountManagerActivity#onDestroy()");
	}

	private void setAdapter() {
		ListView list = (ListView) findViewById(R.id.list_account_manager_list);
		adapter = new AccountManagerListAdapter((Session) getApplication());
		list.setAdapter(adapter);
	}

	private void setListener() {
		ListView list = (ListView) findViewById(R.id.list_account_manager_list);
		list.setOnItemLongClickListener(this);
		list.setDivider(null);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long itemId) {
		FragmentManager manager = getSupportFragmentManager();
		AccountHandleDialogFragment fragment = new AccountHandleDialogFragment();
		Bundle b = new Bundle();
		b.putInt("POSITION", position);
		fragment.setArguments(b);
		fragment.show(manager, "account_handle_dialog");
		return true;
	}

	@Override
	public void onAccountHandled(int position, int handle) {
		switch (handle) {
		case 0: {
			adapter.moveUp(position);
			break;
		}
		case 1: {
			adapter.moveDown(position);
			break;
		}
		case 2: {
			FragmentManager manager = getSupportFragmentManager();
			AccountDeleteDialogFragment fragment = new AccountDeleteDialogFragment();
			Bundle b = new Bundle();
			b.putInt("POSITION", position);
			fragment.setArguments(b);
			fragment.show(manager, "account_delete_dialog");
			break;
		}
		default:
			break;
		}
	}

	@Override
	public void onAccountDelete(int position) {
		((Session) getApplication()).removeSavedConnection(position);
		adapter.notifyDataSetChanged();
	}
}
