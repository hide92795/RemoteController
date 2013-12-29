package hide92795.android.remotecontroller.activity;

import hide92795.android.remotecontroller.GoogleAnalyticsUtil;
import hide92795.android.remotecontroller.R;
import hide92795.android.remotecontroller.billing.IabHelper;
import hide92795.android.remotecontroller.billing.IabHelper.OnConsumeFinishedListener;
import hide92795.android.remotecontroller.billing.IabResult;
import hide92795.android.remotecontroller.billing.Inventory;
import hide92795.android.remotecontroller.billing.Purchase;
import hide92795.android.remotecontroller.util.Base64Coder;
import hide92795.android.remotecontroller.util.LogUtil;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DonateActivity extends FragmentActivity implements OnClickListener {
	private static final String SKU_DONATE_100 = "donate_100";
	private static final String SKU_DONATE_500 = "donate_500";
	private static final String BILLING_PUBLIC_KEY = "TUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUEyT2p1Rkk0bm92VFVlT3NpQU15Z3dwckhUTzJKb1M5Q1dzdm1yY1pYWlRLQzZ0eU9LOTh6cHJPSlVzdmVnQTZxTmRmMlV3K1V5UHQ3UUpSL2JSTDNuQmg2ZVU3TXlNYVFlRWM1VHFZVmtTN1g2VWk2SGh4VDZ6U1JuV256cGlxN2QydTI4eFQ2L2NaUk1laUtOeElLWkd6YXc5MC9jdVhyVnkxWVNSSVh4UGUzNUk2aEpsMGx1VVRXb0ZWL2o4U08yQUtsZlVQMHUySzN1MXlqLy9ESGxFOFZCZ3Y3Wk8yYTU5akFmNnFOVWo2c25yTDZTSDF5SGdTeW9xMHpWZUhyazI3blZ3TmtxNmlTRnVKdXJmbS9ScmlaOUtvamxUUGRKRDRBNXptbVM0bDdVNkk0VHV6aUtEbEppckNDakVCb3grMWVsUisyNCtPaS9GdmdLSmtaeVFJREFRQUI=";
	private static final int RC_REQUEST = 92795;

	private boolean consume_donate_100 = false;
	private boolean consume_donate_500 = false;

	private IabHelper mBillingHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.d("DonateActivity#onCreate()");
		setContentView(R.layout.activity_donate);
		setListener();
		setupBilling();
	}

	private void setListener() {
		Button btn_donate_100 = (Button) findViewById(R.id.btn_donate_100);
		btn_donate_100.setOnClickListener(this);
		Button btn_donate_500 = (Button) findViewById(R.id.btn_donate_500);
		btn_donate_500.setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.d("DonateActivity#onDestroy()");
		shutdownBilling();
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

	private void setupBilling() {
		mBillingHelper = new IabHelper(this, new String(Base64Coder.decode(BILLING_PUBLIC_KEY)));
		// mBillingHelper.enableDebugLogging(true);
		mBillingHelper.enableDebugLogging(false);
		mBillingHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) {
				LogUtil.d("DonateActivity#IabHelper | Setup finished.");
				if (result.isFailure()) {
					Button btn_donate_100 = (Button) findViewById(R.id.btn_donate_100);
					Button btn_donate_500 = (Button) findViewById(R.id.btn_donate_500);
					TextView text_infomation = (TextView) findViewById(R.id.text_donate_infomation);
					btn_donate_100.setEnabled(false);
					btn_donate_500.setEnabled(false);
					text_infomation.setText(R.string.str_error_on_starting_service);
					LogUtil.d("DonateActivity#IabHelper | Problem setting up in-app billing: " + result);
					return;
				} else {
					mBillingHelper.queryInventoryAsync(mGotInventoryListener);
				}

			}
		});
	}

	private void shutdownBilling() {
		if (mBillingHelper != null) {
			mBillingHelper.dispose();
		}
		mBillingHelper = null;
	}

	private void checkAllConsumed() {
		if (consume_donate_100 && consume_donate_500) {
			Button btn_donate_100 = (Button) findViewById(R.id.btn_donate_100);
			Button btn_donate_500 = (Button) findViewById(R.id.btn_donate_500);
			TextView text_infomation = (TextView) findViewById(R.id.text_donate_infomation);
			btn_donate_100.setEnabled(true);
			btn_donate_500.setEnabled(true);
			text_infomation.setText("");
		}
	}

	private IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
		public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
			LogUtil.d("DonateActivity#IabHelper | Query inventory finished.");
			if (result.isFailure()) {
				Button btn_donate_100 = (Button) findViewById(R.id.btn_donate_100);
				Button btn_donate_500 = (Button) findViewById(R.id.btn_donate_500);
				TextView text_infomation = (TextView) findViewById(R.id.text_donate_infomation);
				btn_donate_100.setEnabled(false);
				btn_donate_500.setEnabled(false);
				text_infomation.setText(R.string.str_error_on_starting_service);
				LogUtil.d("DonateActivity#IabHelper | Query inventory was failed.");
				return;
			}

			LogUtil.d("DonateActivity#IabHelper | Query inventory was successful.");

			Purchase donate_100 = inventory.getPurchase(SKU_DONATE_100);
			Purchase donate_500 = inventory.getPurchase(SKU_DONATE_500);

			if (donate_100 != null) {
				mBillingHelper.consumeAsync(donate_100, new OnConsumeFinishedListener() {
					@Override
					public void onConsumeFinished(Purchase purchase, IabResult result) {
						LogUtil.d("DonateActivity#IabHelper | Consume successful.");
						consume_donate_100 = true;
						checkAllConsumed();
					}
				});
			} else {
				consume_donate_100 = true;
			}

			if (donate_500 != null) {
				mBillingHelper.consumeAsync(donate_500, new OnConsumeFinishedListener() {
					@Override
					public void onConsumeFinished(Purchase purchase, IabResult result) {
						LogUtil.d("DonateActivity#IabHelper | Consume successful.");
						consume_donate_500 = true;
						checkAllConsumed();
					}
				});
			} else {
				consume_donate_500 = true;
			}

			checkAllConsumed();
		}
	};

	private IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
			LogUtil.d("DonateActivity#IabHelper | Purchase finished: " + result + ", purchase: " + purchase);
			TextView text_infomation = (TextView) findViewById(R.id.text_donate_infomation);
			if (result.isSuccess()) {
				LogUtil.d("DonateActivity#IabHelper | Purchase successful.");
				text_infomation.setText(R.string.donate_thanks);
				mBillingHelper.consumeAsync(purchase, new OnConsumeFinishedListener() {
					@Override
					public void onConsumeFinished(Purchase purchase, IabResult result) {
						LogUtil.d("DonateActivity#IabHelper | Consume successful.");
					}
				});
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (!mBillingHelper.handleActivityResult(requestCode, resultCode, data)) {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_donate_100: {
			mBillingHelper.launchPurchaseFlow(this, SKU_DONATE_100, RC_REQUEST, mPurchaseFinishedListener, "");
			break;
		}
		case R.id.btn_donate_500: {
			mBillingHelper.launchPurchaseFlow(this, SKU_DONATE_500, RC_REQUEST, mPurchaseFinishedListener, "");
			break;
		}
		default:
			break;
		}
	}
}
