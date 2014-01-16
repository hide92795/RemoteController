package hide92795.android.remotecontroller.activity;

import hide92795.android.remotecontroller.GoogleAnalyticsUtil;
import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.receivedata.FileData;
import hide92795.android.remotecontroller.ui.dialog.CharsetDialogFragment;
import hide92795.android.remotecontroller.ui.dialog.CharsetDialogFragment.Callback;
import hide92795.android.remotecontroller.ui.dialog.FileCloseConfirmationDialogFragment;
import hide92795.android.remotecontroller.util.LogUtil;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class TextEditorActivity extends ActionBarActivity implements Callback, hide92795.android.remotecontroller.ui.dialog.FileCloseConfirmationDialogFragment.Callback {
	private FileData old_data;
	private FileData new_data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.d("TextEditorActivity#onCreate()");
		setContentView(R.layout.activity_text_editor);
		setListener();
		setData();
	}

	@Override
	protected void onStart() {
		super.onStart();
		LogUtil.d("TextEditorActivity#onStart()");
		GoogleAnalyticsUtil.startActivity(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.d("TextEditorActivity#onResume()");
	}

	@Override
	protected void onPause() {
		super.onPause();
		LogUtil.d("TextEditorActivity#onPause()");
	}

	@Override
	protected void onStop() {
		super.onStop();
		LogUtil.d("TextEditorActivity#onStop()");
		GoogleAnalyticsUtil.stopActivity(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.d("TextEditorActivity#onDestroy()");
	}

	private void setListener() {
		EditText editor = (EditText) findViewById(R.id.edittext_text_editor_editor);
		editor.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {
			}

			@Override
			public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				new_data.setData(editable.toString());
			}
		});
	}

	private void setData() {
		FileData data = (FileData) getIntent().getSerializableExtra("FILE");
		old_data = data;
		new_data = data.clone();
		EditText editor = (EditText) findViewById(R.id.edittext_text_editor_editor);
		editor.setText(data.getData());
		setTitle(getString(R.string.str_editfile) + ":" + data.getEncoding() + ":" + data.getFile());
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_text_editor, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!old_data.equals(new_data)) {
				FragmentManager manager = getSupportFragmentManager();
				FileCloseConfirmationDialogFragment fragment = new FileCloseConfirmationDialogFragment();
				fragment.show(manager, "close_confirm_dialog");
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		MenuItem save_and_close = (MenuItem) menu.findItem(R.id.menu_editor_save_and_close);
		if (old_data.equals(new_data)) {
			save_and_close.setEnabled(false);
		} else {
			save_and_close.setEnabled(true);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean ret = super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.menu_editor_save_and_close: {
			Intent i = new Intent();
			i.putExtra("FILE", new_data);
			setResult(RESULT_OK, i);
			finish();
			return true;
		}
		case R.id.menu_editor_close_without_save: {
			setResult(RESULT_CANCELED);
			finish();
			return true;
		}
		case R.id.menu_editor_change_encoding: {
			FragmentManager manager = getSupportFragmentManager();
			CharsetDialogFragment fragment = new CharsetDialogFragment();
			fragment.show(manager, "charset_dialog");
			return true;
		}
		default:
		}
		return ret;
	}

	@Override
	public void onCharsetSelected(String charset, Bundle arg) {
		new_data.setEncoding(charset);
		setTitle(getString(R.string.str_editfile) + ":" + new_data.getEncoding() + ":" + new_data.getFile());
	}

	@Override
	public void onDialogClose(int result) {
		switch (result) {
		case FileCloseConfirmationDialogFragment.BUTTON_YES:
			Intent i = new Intent();
			i.putExtra("FILE", new_data);
			setResult(RESULT_OK, i);
			finish();
			break;
		case FileCloseConfirmationDialogFragment.BUTTON_NO:
			setResult(RESULT_CANCELED);
			finish();
			break;
		case FileCloseConfirmationDialogFragment.BUTTON_CANCEL:
			break;
		default:
			break;
		}
	}
}
