package com.jewelzqiu.oneclicklock;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

@SuppressWarnings("deprecation")
public class Locker extends DeviceAdminReceiver {

	@Override
	public CharSequence onDisableRequested(Context context, Intent intent) {
		return "Disable to uninstall.";
	}

	public static class Controller extends Activity {
		static final int RESULT_ENABLE = 1;

		DevicePolicyManager mDPM;
		KeyguardManager mKeyguardManager;
		KeyguardLock mKeyguardLock;
		ComponentName mDeviceAdmin;

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
			mDeviceAdmin = new ComponentName(Controller.this, Locker.class);

			if (!mDPM.isAdminActive(mDeviceAdmin)) {
				Intent intent = new Intent(
						DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
				intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
						mDeviceAdmin);
				intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
						"One key lockscreen.");
				startActivityForResult(intent, RESULT_ENABLE);
			} else {
				mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
				mKeyguardLock = mKeyguardManager
						.newKeyguardLock(getPackageName());
				if (mKeyguardLock != null) {
					mKeyguardLock.disableKeyguard();
				}
				mDPM.lockNow();
				if (mKeyguardLock != null) {
					mKeyguardLock.reenableKeyguard();
				}
			}

			this.finish();
		}

		@Override
		protected void onActivityResult(int requestCode, int resultCode,
				Intent data) {
			switch (requestCode) {
			case RESULT_ENABLE:
				if (resultCode == Activity.RESULT_OK) {
					Log.d("debug", "Admin enabled!");
				} else {
					Log.d("debug", "Admin enable FAILED!");
				}
				return;
			}

			super.onActivityResult(requestCode, resultCode, data);
		}
	}
}
