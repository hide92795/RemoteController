package hide92795.android.remotecontroller.activity;

import hide92795.android.remotecontroller.Connection;
import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.ReceiveListener;
import hide92795.android.remotecontroller.Session;
import hide92795.android.remotecontroller.receivedata.DirectoryData;
import hide92795.android.remotecontroller.receivedata.DirectoryData.File;
import hide92795.android.remotecontroller.receivedata.ErrorData;
import hide92795.android.remotecontroller.receivedata.FileData;
import hide92795.android.remotecontroller.receivedata.ReceiveData;
import hide92795.android.remotecontroller.ui.adapter.FileListAdapter;
import hide92795.android.remotecontroller.ui.dialog.CharsetDialogFragment;
import hide92795.android.remotecontroller.ui.dialog.FileDeleteDialogFragment;
import hide92795.android.remotecontroller.ui.dialog.FileHandleDialogFragment;
import hide92795.android.remotecontroller.ui.dialog.FileRenameDialogFragment;
import hide92795.android.remotecontroller.ui.dialog.MkDialogFragment;
import hide92795.android.remotecontroller.util.LogUtil;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class EditFileActivity extends FragmentActivity implements ReceiveListener, OnItemLongClickListener, OnItemClickListener, FileHandleDialogFragment.Callback, CharsetDialogFragment.Callback,
		FileRenameDialogFragment.Callback, FileDeleteDialogFragment.Callback, MkDialogFragment.Callback {
	private static final int REQUEST_CODE = 22;
	private FileListAdapter adapter;
	private String current_directory;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.d("EditFileActivity#onCreate()");
		setContentView(R.layout.activity_editfile);
		setListener();
		moveDirectory("");
	}

	private void setListener() {
		ListView list = (ListView) findViewById(R.id.list_editfile_files);
		adapter = new FileListAdapter(this);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		list.setOnItemLongClickListener(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.d("EditFileActivity#onDestroy()");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_edit_file, menu);
		return true;
	}

	private void moveDirectory(String dir) {
		current_directory = dir;
		Connection connection = ((Session) getApplication()).getConnection();
		int pid = connection.requests.requestDirectory(dir);
		connection.addListener(pid, this);
		((Session) getApplication()).showProgressDialog(this, false, null);
	}

	private void refresh() {
		moveDirectory(current_directory);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!adapter.isRootDirectory()) {
				moveDirectory(adapter.getItem(0).getPath());
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onReceiveData(String sended_cmd, int pid, ReceiveData data) {
		if (sended_cmd.equals("DIRECTORY")) {
			((Session) getApplication()).dismissProgressDialog();
			if (data.isSuccessed()) {
				DirectoryData directory = (DirectoryData) data;
				TextView text_dir = (TextView) findViewById(R.id.text_editfile_directory);
				text_dir.setText(directory.getDir());
				text_dir.setSelected(true);
				adapter.setDirectoryData(directory);
			} else {
				ErrorData errordata = (ErrorData) data;
				Toast.makeText(this, getString(errordata.getMessageId(), errordata.getAddtionalInfo()), Toast.LENGTH_SHORT).show();
			}
		} else if (sended_cmd.equals("FILE_OPEN")) {
			((Session) getApplication()).dismissProgressDialog();
			if (data.isSuccessed()) {
				FileData filedata = (FileData) data;
				Intent i = new Intent(this, TextEditorActivity.class);
				i.putExtra("FILE", filedata);
				startActivityForResult(i, REQUEST_CODE);
			} else {
				ErrorData errordata = (ErrorData) data;
				Toast.makeText(this, getString(errordata.getMessageId(), errordata.getAddtionalInfo()), Toast.LENGTH_SHORT).show();
			}
		} else if (sended_cmd.equals("FILE_RENAME") || sended_cmd.equals("FILE_DELETE") || sended_cmd.equals("FILE_EDIT") || sended_cmd.equals("MK")) {
			refresh();
			((Session) getApplication()).dismissProgressDialog();
			if (data.isSuccessed()) {
				Toast.makeText(this, R.string.str_success, Toast.LENGTH_SHORT).show();
			} else {
				ErrorData errordata = (ErrorData) data;
				Toast.makeText(this, getString(errordata.getMessageId(), errordata.getAddtionalInfo()), Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				FileData new_data = intent.getParcelableExtra("FILE");
				Connection connection = ((Session) getApplication()).getConnection();
				int pid = connection.requests.requestFileEdit(new_data);
				connection.addListener(pid, this);
				((Session) getApplication()).showProgressDialog(this, false, null);
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_mkdir:
		case R.id.menu_mkfile: {
			FragmentManager manager = getSupportFragmentManager();
			MkDialogFragment fragment = new MkDialogFragment();
			Bundle b = new Bundle();
			b.putString("CURRENT", current_directory);
			b.putBoolean("DIRECTORY", (item.getItemId() == R.id.menu_mkdir));
			fragment.setArguments(b);
			fragment.show(manager, "mk_dialog");
			return true;
		}
		default:
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
		File file = adapter.getItem(position);
		if (file.isDirectory()) {
			moveDirectory(file.getPath());
		} else {
			Connection connection = ((Session) getApplication()).getConnection();
			int pid = connection.requests.requestFileOpen(file.getPath(), "UTF-8");
			connection.addListener(pid, this);
			((Session) getApplication()).showProgressDialog(this, false, null);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long id) {
		File file = adapter.getItem(position);
		FragmentManager manager = getSupportFragmentManager();
		FileHandleDialogFragment fragment = new FileHandleDialogFragment();
		Bundle b = new Bundle();
		b.putParcelable("FILE", file);
		fragment.setArguments(b);
		fragment.show(manager, "file_handle_dialog");
		return false;
	}

	@Override
	public void onDialogSelected(File file, int result) {
		switch (result) {
		case FileHandleDialogFragment.HANDLE_OPEN: {
			if (file.isDirectory()) {
				return;
			}
			Connection connection = ((Session) getApplication()).getConnection();
			int pid = connection.requests.requestFileOpen(file.getPath(), "UTF-8");
			connection.addListener(pid, this);
			((Session) getApplication()).showProgressDialog(this, false, null);
			break;
		}
		case FileHandleDialogFragment.HANDLE_OPEN_SELECT_CHARSET: {
			if (file.isDirectory()) {
				return;
			}
			FragmentManager manager = getSupportFragmentManager();
			CharsetDialogFragment fragment = new CharsetDialogFragment();
			Bundle b = new Bundle();
			b.putParcelable("FILE", file);
			fragment.setArguments(b);
			fragment.show(manager, "open_charset_dialog");
			break;
		}
		case FileHandleDialogFragment.HANDLE_RENAME: {
			FragmentManager manager = getSupportFragmentManager();
			FileRenameDialogFragment fragment = new FileRenameDialogFragment();
			Bundle b = new Bundle();
			b.putParcelable("FILE", file);
			fragment.setArguments(b);
			fragment.show(manager, "file_rename_dialog");
			break;
		}
		case FileHandleDialogFragment.HANDLE_DELETE: {
			FragmentManager manager = getSupportFragmentManager();
			FileDeleteDialogFragment fragment = new FileDeleteDialogFragment();
			Bundle b = new Bundle();
			b.putParcelable("FILE", file);
			fragment.setArguments(b);
			fragment.show(manager, "file_delete_dialog");
			break;
		}
		default:
			break;
		}
	}

	@Override
	public void onCharsetSelected(String charset, Bundle arg) {
		File file = arg.getParcelable("FILE");
		Connection connection = ((Session) getApplication()).getConnection();
		int pid = connection.requests.requestFileOpen(file.getPath(), charset);
		connection.addListener(pid, this);
		((Session) getApplication()).showProgressDialog(this, false, null);
	}

	@Override
	public void onFileRenameSelected(File file, String new_name) {
		Connection connection = ((Session) getApplication()).getConnection();
		int pid = connection.requests.requestFileRename(file.getPath(), new_name);
		connection.addListener(pid, this);
		((Session) getApplication()).showProgressDialog(this, false, null);
	}

	@Override
	public void onFileDeleteSelected(File file) {
		Connection connection = ((Session) getApplication()).getConnection();
		int pid = connection.requests.requestFileDelete(file.getPath());
		connection.addListener(pid, this);
		((Session) getApplication()).showProgressDialog(this, false, null);
	}

	@Override
	public void onMkSelected(String path) {
		Connection connection = ((Session) getApplication()).getConnection();
		int pid = connection.requests.requestMk(path);
		connection.addListener(pid, this);
		((Session) getApplication()).showProgressDialog(this, false, null);
	}
}
