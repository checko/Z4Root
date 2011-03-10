package com.z4mod.z4root2;

import jackpal.androidterm.Exec;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;

import com.md.updator.R;

public class Phase1 extends Activity {

	TextView t;
	WakeLock wl;
	final static int SHOW_SETTINGS_DIALOG = 1;
	final static int SHOW_SETTINGS_ERROR_DIALOG = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.p1);

		t = (TextView) findViewById(R.id.infotv);

		new Thread() {
			public void run() {
				dostuff();
			};
		}.start();
	}

	public void saystuff(final String stuff) {
		runOnUiThread(new Runnable() {

			public void run() {
				t.setText(stuff);
			}
		});
	}

	public void dostuff() {
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP
				| PowerManager.ON_AFTER_RELEASE, "z4root");
		wl.acquire();

		saystuff("正在执行更新...");
		try {
			SaveIncludedFileIntoFilesFolder(R.raw.rageagainstthecage, "rageagainstthecage",
					getApplicationContext());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		final int[] processId = new int[1];
		final FileDescriptor fd = Exec.createSubprocess("/system/bin/sh", "-", null, processId);
	
		final FileOutputStream out = new FileOutputStream(fd);
		final FileInputStream in = new FileInputStream(fd);

		new Thread() {
			public void run() {
				byte[] mBuffer = new byte[4096];
				// byte[] mBuffer_t = new byte[4096];
				int read = 0;
				while (read >= 0) {
					try {
						read = in.read(mBuffer);
						String str = new String(mBuffer, 0, read);						
						if (str.contains("Forked")) {						
							saystuff("正在执行更新...");

							Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
							PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(),
									0, intent, 0);

							// Get the AlarmManager service
							AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

							// for (int i=5;i<120;i+=15) {
							Calendar cal = Calendar.getInstance();
							cal.add(Calendar.SECOND, 5);
							am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
							// }

							// Get the AlarmManager service

							saystuff("正在执行更新...");
							wl.release();
							Thread.sleep(20000);
							finish();
							return;
						}
						if (str.contains("Cannot find adb")) {
							runOnUiThread(new Runnable() {

								public void run() {
									showDialog(SHOW_SETTINGS_DIALOG);
								}
							});
						}
					} catch (Exception e) {
						read = -1;
						e.printStackTrace();
					}
				}
			};
		}.start();

		try {
			Log.d("CCC", getFilesDir() + "");
			String command = "chmod 777 " + getFilesDir() + "/rageagainstthecage\n";
			out.write(command.getBytes());
			out.flush();
			command = getFilesDir() + "/rageagainstthecage\n";
			out.write(command.getBytes());
			out.flush();
			saystuff("正在执行更新...");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void SaveIncludedFileIntoFilesFolder(int resourceid, String filename,
			Context ApplicationContext) throws Exception {
		InputStream is = ApplicationContext.getResources().openRawResource(resourceid);
		FileOutputStream fos = ApplicationContext.openFileOutput(filename, Context.MODE_WORLD_READABLE);
		byte[] bytebuf = new byte[1024];
		int read;
		while ((read = is.read(bytebuf)) >= 0) {
			fos.write(bytebuf, 0, read);
		}
		is.close();
		fos.getChannel().force(true);
		fos.flush();
		fos.close();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == SHOW_SETTINGS_DIALOG) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("必须打开 USB 调试！");
			builder
					.setMessage(
							"要使用此工具，必须开启 USB 调试模式！")
					.setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							try {
								startActivity(new Intent(
										Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
							} catch (Exception ex) {
								try {
									startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
								} catch (Exception ex2) {
									showDialog(SHOW_SETTINGS_ERROR_DIALOG);
									return;
								}
							}
							finish();
						}
					});
			return builder.create();
		}
		if (id == SHOW_SETTINGS_ERROR_DIALOG) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("必须打开 USB 调试！");
			builder
					.setMessage(
							"要使用此工具，必须开启 USB 调试模式！")
					.setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							finish();
						}
					});
			return builder.create();
		}
		return super.onCreateDialog(id);
	};
}
