package app.morphe.extension.shared.requests;

import android.app.Activity;
import android.app.Application;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;

public final class AllFilesPermission {
    private AllFilesPermission() {}
    public static void request(Activity activity, Runnable confirmedAction) {
        new AlertDialog.Builder(activity).setTitle("把本机媒体移入「已锁定文件夹」")
            .setMessage("这次移动需要访问共享存储中的文件。Android 的「所有文件访问」权限也会一并允许访问其中的文档。事后可以在系统设置里撤销。授予后回到这里即可继续这次移动。")
            .setNegativeButton("取消", null).setPositiveButton("打开设置", (dialog, button) -> {
                Application.ActivityLifecycleCallbacks callback = new Application.ActivityLifecycleCallbacks() {
                    boolean paused;
                    public void onActivityPaused(Activity a) { if (a == activity) paused = true; }
                    public void onActivityResumed(Activity a) {
                        if (a != activity || !paused) return;
                        activity.getApplication().unregisterActivityLifecycleCallbacks(this);
                        if (Environment.isExternalStorageManager()) confirmedAction.run();
                        else Toast.makeText(activity, "已取消移动：没有获得文件访问权限", Toast.LENGTH_LONG).show();
                    }
                    public void onActivityDestroyed(Activity a) { if (a == activity) activity.getApplication().unregisterActivityLifecycleCallbacks(this); }
                    public void onActivityCreated(Activity a, Bundle b) {}
                    public void onActivityStarted(Activity a) {}
                    public void onActivityStopped(Activity a) {}
                    public void onActivitySaveInstanceState(Activity a, Bundle b) {}
                };
                activity.getApplication().registerActivityLifecycleCallbacks(callback);
                try { activity.startActivity(new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:" + activity.getPackageName()))); }
                catch (Exception e) {
                    activity.getApplication().unregisterActivityLifecycleCallbacks(callback);
                    Toast.makeText(activity, "无法打开文件访问设置", Toast.LENGTH_LONG).show();
                }
            }).show();
    }
}
