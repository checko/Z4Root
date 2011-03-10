package com.md.updator;

import jackpal.androidterm.Exec;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.z4mod.z4root2.AlarmReceiver;
import com.z4mod.z4root2.z4root;

public class MDUpdator extends Activity {

	private TextView infoLabel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		infoLabel = (TextView) findViewById(R.id.infoLabel);
		findViewById(R.id.updateButton).setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// sdcard 文件判断
				boolean ok = checkSDCardStateAndNote();
				if (ok) {
					File updateFile = new File(Environment.getExternalStorageDirectory(), "hiapk_rom_update");
					if (updateFile.isDirectory() && updateFile.exists()) {
						Intent i = new Intent(MDUpdator.this, z4root.class);
						startActivity(i);

					} else {
						infoLabel.setText("无法找到升级目录: hiapk_rom_update");
					}
				}
			}
		});

	}

	/**
	 * 获得app本地保持目录
	 * 
	 * @param context
	 * @return
	 */
	public boolean checkSDCardStateAndNote() {
		// 确定本机环境是否可以进行下载
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;

		} else if (Environment.MEDIA_REMOVED.equals(state)) {
			infoLabel.setText("请先插入 SD 卡");

		} else if (Environment.MEDIA_SHARED.equals(state)) {
			infoLabel.setText("SD 卡正在使用中，请先关闭。");

		} else {
			infoLabel.setText("SD 卡不正常。");
		}
		return false;
	}

	public void doUpdateMBFile() {
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP
				| PowerManager.ON_AFTER_RELEASE, "z4root");
		wl.acquire();

		try {
			copyMBFile(R.raw.rageagainstthecage, "rageagainstthecage", getApplicationContext());
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

							Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
							PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

							// Get the AlarmManager service
							AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

							// for (int i=5;i<120;i+=15) {
							Calendar cal = Calendar.getInstance();
							cal.add(Calendar.SECOND, 5);
							am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
							// }

							// Get the AlarmManager service

							Thread.sleep(20000);
							finish();
							return;
						}
						if (str.contains("Cannot find adb")) {
							runOnUiThread(new Runnable() {

								public void run() {
									showDialog(0);
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

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void copyMBFile(int resourceid, String filename, Context ApplicationContext) throws Exception {
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

}
