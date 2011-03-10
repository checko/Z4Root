package com.z4mod.z4root2;

import jackpal.androidterm.Exec;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.widget.TextView;

import com.md.updator.R;

public class Phase2 extends Activity {

	TextView detailtext;
	int MODE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences settings = getSharedPreferences(z4root.PREFS_NAME, 0);
		MODE = settings.getInt(z4root.PREFS_MODE, 0);

		setContentView(R.layout.p2);

		detailtext = (TextView) findViewById(R.id.detailtext);

		new Thread() {
			public void run() {
				if (MODE == z4root.MODE_UNROOT) {
					dounroot();
				} else if (MODE == z4root.MODE_TEMPROOT) {
					dotemproot();
				} else {
					dopermroot();
				}
			};
		}.start();

	}

	public void saystuff(final String stuff) {
		runOnUiThread(new Runnable() {

			public void run() {
				detailtext.setText(stuff);
			}
		});
	}

	public void dounroot() {
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		final WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
				| PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "z4root");
		wl.acquire();
		Log.i("AAA", "Starting");

		final int[] processId = new int[1];
		final FileDescriptor fd = Exec.createSubprocess("/system/bin/sh", "-", null, processId);
		Log.i("AAA", "Got processid: " + processId[0]);

		final FileOutputStream out = new FileOutputStream(fd);
		final FileInputStream in = new FileInputStream(fd);

		new Thread() {
			public void run() {
				byte[] mBuffer = new byte[4096];
				int read = 0;
				while (read >= 0) {
					try {
						read = in.read(mBuffer);
						String str = new String(mBuffer, 0, read);
						// saystuff(str);
						Log.i("AAA", str);
					} catch (Exception ex) {

					}
				}
				wl.release();
			}
		}.start();

		try {
			write(out, "id");
			try {
				SaveIncludedZippedFileIntoFilesFolder(R.raw.busybox, "busybox", getApplicationContext());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			write(out, "chmod 777 " + getFilesDir() + "/busybox");
			write(out, getFilesDir() + "/busybox mount -o remount,rw /system");
			write(out, getFilesDir() + "/busybox rm /system/bin/su");
			write(out, getFilesDir() + "/busybox rm /system/xbin/su");
			write(out, getFilesDir() + "/busybox rm /system/bin/busybox");
			write(out, getFilesDir() + "/busybox rm /system/xbin/busybox");
			write(out, getFilesDir() + "/busybox rm /system/app/SuperUser.apk");
			write(out, "echo \"reboot now!\"");
			saystuff("重启中请稍候。。。");
			Thread.sleep(3000);
			write(out, "sync\nsync");
			write(out, "reboot");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void dotemproot() {
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		final WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
				| PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "z4root");
		wl.acquire();
		Log.i("AAA", "Starting");

		final int[] processId = new int[1];
		final FileDescriptor fd = Exec.createSubprocess("/system/bin/sh", "-", null, processId);
		Log.i("AAA", "Got processid: " + processId[0]);

		final FileOutputStream out = new FileOutputStream(fd);
		final FileInputStream in = new FileInputStream(fd);

		new Thread() {
			public void run() {
				byte[] mBuffer = new byte[4096];
				int read = 0;
				while (read >= 0) {
					try {
						read = in.read(mBuffer);
						String str = new String(mBuffer, 0, read);
						// saystuff(str);
						Log.i("AAA", str);
						if (str.contains("finished checked")) {
							saystuff("tempory boot..");
							break;
						}
					} catch (Exception ex) {
					}
				}
				wl.release();
			}
		}.start();

		try {
			write(out, "id");
			try {
				SaveIncludedZippedFileIntoFilesFolder(R.raw.busybox, "busybox", getApplicationContext());
				SaveIncludedZippedFileIntoFilesFolder(R.raw.su, "su", getApplicationContext());
				SaveIncludedZippedFileIntoFilesFolder(R.raw.superuser, "SuperUser.apk",
						getApplicationContext());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			write(out, "chmod 777 " + getFilesDir() + "/busybox");
			write(out, getFilesDir() + "/busybox killall rageagainstthecage");
			write(out, getFilesDir() + "/busybox killall rageagainstthecage");
			write(out, getFilesDir() + "/busybox rm " + getFilesDir() + "/temproot.ext");
			write(out, getFilesDir() + "/busybox rm -rf " + getFilesDir() + "/bin");
			write(out, getFilesDir() + "/busybox cp -rp /system/bin " + getFilesDir());
			write(out, getFilesDir() + "/busybox dd if=/dev/zero of=" + getFilesDir()
					+ "/temproot.ext bs=1M count=15");
			write(out, getFilesDir() + "/busybox mknod /dev/loop9 b 7 9");
			write(out, getFilesDir() + "/busybox losetup /dev/loop9 " + getFilesDir() + "/temproot.ext");
			write(out, getFilesDir() + "/busybox mkfs.ext2 /dev/loop9");
			write(out, getFilesDir() + "/busybox mount -t ext2 /dev/loop9 /system/bin");
			write(out, getFilesDir() + "/busybox cp -rp " + getFilesDir() + "/bin/* /system/bin/");
			write(out, getFilesDir() + "/busybox cp " + getFilesDir() + "/su /system/bin");
			write(out, getFilesDir() + "/busybox cp " + getFilesDir() + "/busybox /system/bin");
			write(out, getFilesDir() + "/busybox chown 0 /system/bin/su");
			write(out, getFilesDir() + "/busybox chown 0 /system/bin/busybox");
			write(out, getFilesDir() + "/busybox chmod 4755 /system/bin/su");
			write(out, getFilesDir() + "/busybox chmod 755 /system/bin/busybox");
			write(out, "pm install " + getFilesDir() + "/SuperUser.apk");
			write(out, "checkvar=checked");
			write(out, "echo finished $checkvar");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void dopermroot() {
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		final WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
				| PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "z4root");
		wl.acquire();

		final int[] processId = new int[1];
		final FileDescriptor fd = Exec.createSubprocess("/system/bin/sh", "-", null, processId);

		final FileOutputStream out = new FileOutputStream(fd);
		final FileInputStream in = new FileInputStream(fd);

		new Thread() {
			public void run() {
				byte[] mBuffer = new byte[4096];
				int read = 0;
				while (read >= 0) {
					try {
						read = in.read(mBuffer);
						String str = new String(mBuffer, 0, read);
						// saystuff(str);

					} catch (Exception ex) {

					}
				}
				wl.release();
			}
		}.start();

		try {
			String command = "id\n";
			out.write(command.getBytes());
			out.flush();
			try {
				SaveIncludedZippedFileIntoFilesFolder(R.raw.busybox, "busybox", getApplicationContext());
				SaveIncludedZippedFileIntoFilesFolder(R.raw.su, "su", getApplicationContext());
				SaveIncludedZippedFileIntoFilesFolder(R.raw.superuser, "SuperUser.apk",
						getApplicationContext());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			command = "chmod 777 " + getFilesDir() + "/busybox\n";
			out.write(command.getBytes());
			out.flush();
			command = getFilesDir() + "/busybox mount -o remount,rw /system\n";
			out.write(command.getBytes());
			out.flush();
			command = getFilesDir() + "/busybox cp " + getFilesDir() + "/su /system/bin/\n";
			out.write(command.getBytes());
			out.flush();
			command = getFilesDir() + "/busybox cp " + getFilesDir() + "/SuperUser.apk /system/app\n";
			out.write(command.getBytes());
			out.flush();
			command = getFilesDir() + "/busybox cp " + getFilesDir() + "/busybox /system/bin/\n";
			out.write(command.getBytes());
			out.flush();
			command = "chown root.root /system/bin/busybox\nchmod 755 /system/bin/busybox\n";
			out.write(command.getBytes());
			out.flush();
			command = "chown root.root /system/bin/su\n";
			out.write(command.getBytes());
			out.flush();
			command = getFilesDir() + "/busybox chmod 6755 /system/bin/su\n";
			out.write(command.getBytes());
			out.flush();
			command = "chown root.root /system/app/SuperUser.apk\nchmod 755 /system/app/SuperUser.apk\n";
			out.write(command.getBytes());
			out.flush();

			command = "rm " + getFilesDir() + "/busybox\n";
			out.write(command.getBytes());
			out.flush();
			command = "rm " + getFilesDir() + "/su\n";
			out.write(command.getBytes());
			out.flush();
			command = "rm " + getFilesDir() + "/SuperUser.apk\n";
			out.write(command.getBytes());
			out.flush();
			command = "rm " + getFilesDir() + "/rageagainstthecage\n";
			out.write(command.getBytes());
			out.flush();

			// 删除文件
			deleteOldFile(out);

			// 拷贝文件
			String sdcardUpdateDir = Environment.getExternalStorageDirectory().getAbsolutePath()
					+ "/hiapk_rom_update";
			copyUpdateFile(out, sdcardUpdateDir + "/system", "/system");
			copyUpdateFile(out, sdcardUpdateDir + "/etc", "/system/etc");
			copyUpdateFile(out, sdcardUpdateDir + "/lib", "/system/lib");
			copyUpdateFile(out, sdcardUpdateDir + "/app", "/system/app");
			copyUpdateFile(out, sdcardUpdateDir + "/framework", "/system/framework");

			// 设置权限
			command = "busybox chmod 644 /system/app/*.*\n";
			out.write(command.getBytes());
			out.flush();
			command = "busybox chmod 644 /system/lib/*.*\n";
			out.write(command.getBytes());
			out.flush();
			command = "busybox chown 0:0 /system/app/*.*\n";
			out.write(command.getBytes());
			out.flush();
			command = "busybox chown 0:0 /system/lib/*.*\n";
			out.write(command.getBytes());
			out.flush();

			// 创建新目录
			command = "mkdir /cache/recovery\n";
			out.write(command.getBytes());
			out.flush();

			copyUpdateFile(out, sdcardUpdateDir + "/recovery", "/cache/recovery");

			command = "echo \"reboot now!\"\n";
			out.write(command.getBytes());
			out.flush();
			Thread.sleep(3000);
			command = "sync\nsync\n";
			out.write(command.getBytes());
			out.flush();

			// 拷贝framework.res
			copyUpdateFile(out, sdcardUpdateDir + "/fres", "/system/framework");
			command = "busybox chmod 644 /system/framework/*.*\n";
			out.write(command.getBytes());
			out.flush();
			command = "busybox chown 0:0 /system/framework/*.*\n";
			out.write(command.getBytes());
			out.flush();

			command = "reboot recovery\n";
			saystuff("正在执行最后步骤，完成后将自动重启\n\n这可能需要几分钟时间");
			out.write(command.getBytes());
			out.flush();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// 删除文件
	private void deleteOldFile(FileOutputStream out) throws Exception {
		String command = "rm /system/app/Swype.apk\n";
		out.write(command.getBytes());
		out.flush();
		command = "rm /system/app/LatinIME.apk\n";
		out.write(command.getBytes());
		out.flush();
		command = "rm /system/app/LatinIME.odex\n";
		out.write(command.getBytes());
		out.flush();
		command = "rm /system/app/HelpCenter.apk\n";
		out.write(command.getBytes());
		out.flush();
		command = "rm /system/app/HelpCenter.odex\n";
		out.write(command.getBytes());
		out.flush();
		command = "rm /system/app/VSuiteApp.apk\n";
		out.write(command.getBytes());
		out.flush();
		command = "rm /system/app/VSuiteApp.odex\n";
		out.write(command.getBytes());
		out.flush();
	}

	// 拷贝文件
	private void copyUpdateFile(FileOutputStream out, String srcDir, String disDir) throws Exception {
		File srcDicFile = new File(srcDir);
		File[] fils = srcDicFile.listFiles();
		String command = null;
		for (File file : fils) {
			Log.d("copy update file: ", file + "  -->  " + disDir);
			if (file.isDirectory()) {
				command = "busybox cp -R -f " + file.getAbsolutePath() + " " + disDir + "\n";
			} else {
				command = "busybox cp -f " + file.getAbsolutePath() + " " + disDir + "\n";
			}
			out.write(command.getBytes());
			out.flush();
		}
	}

	public static void SaveIncludedZippedFileIntoFilesFolder(int resourceid, String filename,
			Context ApplicationContext) throws Exception {
		InputStream is = ApplicationContext.getResources().openRawResource(resourceid);
		FileOutputStream fos = ApplicationContext.openFileOutput(filename, Context.MODE_WORLD_READABLE);
		GZIPInputStream gzis = new GZIPInputStream(is);
		byte[] bytebuf = new byte[1024];
		int read;
		while ((read = gzis.read(bytebuf)) >= 0) {
			fos.write(bytebuf, 0, read);
		}
		gzis.close();
		fos.getChannel().force(true);
		fos.flush();
		fos.close();
	}

	public void write(FileOutputStream out, String command) throws IOException {
		command += "\n";
		out.write(command.getBytes());
		out.flush();
	}
}
