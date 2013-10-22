package hide92795.android.remotecontroller.ui;

import hide92795.android.remotecontroller.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.text.InputFilter;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UnitablleEditTextPreference extends DialogPreference {
	private static final int DEFAULT_MAX_LENGTH = 5;
	private String unit = "";
	private TextView inputValueTextView;
	private EditText editText;
	private int inputType;
	private int maxLength;
	private String defaultVal;
	private String saveVal;

	public UnitablleEditTextPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray tArray = context.obtainStyledAttributes(attrs, R.styleable.UnitablleEditTextPreference);
		unit = tArray.getString(R.styleable.UnitablleEditTextPreference_unit);
		defaultVal = tArray.getString(R.styleable.UnitablleEditTextPreference_defaultValue);
		String argInpType = tArray.getString(R.styleable.UnitablleEditTextPreference_inputType);
		if (argInpType == null || argInpType.equals("number")) {
			inputType = EditorInfo.TYPE_CLASS_NUMBER;
		} else {
			inputType = InputType.TYPE_NULL;
		}
		try {
			maxLength = Integer.parseInt(tArray.getString(R.styleable.UnitablleEditTextPreference_unit));
		} catch (Exception e) {
			maxLength = DEFAULT_MAX_LENGTH;
		}
		tArray.recycle();
	}

	// @Override
	// protected View onCreateView(ViewGroup parent) {
	// LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	// View preference_view = layoutInflater.inflate(R.layout.show_value_preference, null);
	// inputValueTextView = (TextView) preference_view.findViewById(R.id.inputValue);
	// setLayoutResource(android.R.id.widget_frame);
	// return preference_view;
	// }

	@Override
	protected void onBindView(View view) {
		SharedPreferences pref = getSharedPreferences();
		if (pref == null) {
			saveVal = defaultVal;
		} else {
			saveVal = pref.getString(getKey(), defaultVal);
		}
		super.onBindView(view);
		setUpView(view);
		updateInputValue();
	}

	private void setUpView(View view) {
		if (view == null) {
			return;
		}
		inputValueTextView = new TextView(getContext());
		inputValueTextView.setTextColor(getContext().getResources().getColor(R.color.aqua_blue));
		LinearLayout frame = ((LinearLayout) view.findViewById(android.R.id.widget_frame));
		if (frame == null) {
			return;
		}
		frame.setVisibility(View.VISIBLE);
		float density = getContext().getResources().getDisplayMetrics().density;
		frame.setPadding(frame.getPaddingLeft(), frame.getPaddingTop(), (int) (density * 8), frame.getPaddingBottom());
		int count = frame.getChildCount();
		if (count > 0) {
			frame.removeViews(0, count);
		}
		frame.addView(inputValueTextView);
		frame.setMinimumWidth(0);
	}

	@Override
	protected View onCreateDialogView() {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.unitable_edittext_preference_dialog, null);
		editText = (EditText) view.findViewById(R.id.text);
		TextView unitView = (TextView) view.findViewById(R.id.unit);
		unitView.setText(unit);
		editText.setInputType(inputType);
		editText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(maxLength) });
		editText.setText(saveVal);
		return view;
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		if (!positiveResult) {
		} else {
			saveVal = editText.getText().toString();
			if (saveVal == null || saveVal.length() == 0) {
				saveVal = defaultVal;
			}
			SharedPreferences.Editor editor = getEditor();
			editor.putString(getKey(), saveVal);
			editor.commit();
			updateInputValue();
		}
	}

	private void updateInputValue() {
		if (saveVal == null || saveVal.length() == 0) {
			saveVal = defaultVal;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(saveVal);
		sb.append(" ");
		sb.append(unit);
		inputValueTextView.setText(sb.toString().trim());
	}
}
