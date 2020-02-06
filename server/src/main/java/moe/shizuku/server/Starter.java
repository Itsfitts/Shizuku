package moe.shizuku.server;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Process;
import android.os.ServiceManager;
import android.system.Os;

import java.util.UUID;

import moe.shizuku.api.ShizukuApiConstants;
import moe.shizuku.server.api.SystemService;

import static moe.shizuku.server.utils.Logger.LOGGER;

public class Starter {

    private static void fixFilesOwner() {
        if (Process.myUid() == 0) {
            try {
                Os.chown("/data/local/tmp/shizuku_starter", 2000, 2000);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    private static void waitSystemService(String name) {
        while (ServiceManager.getService(name) == null) {
            try {
                LOGGER.i("service " + name + " is not started, wait 1s.");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                LOGGER.w(e.getMessage(), e);
            }
        }
    }

    private static void checkManagerApp() {
        try {
            ApplicationInfo ai = SystemService.getApplicationInfo(ShizukuApiConstants.MANAGER_APPLICATION_ID, 0, 0);
            if (ai == null)
                System.exit(ServerConstants.MANAGER_APP_NOT_FOUND);
        } catch (Throwable tr) {
            LOGGER.e(tr, "checkManagerApp");
            System.exit(ServerConstants.MANAGER_APP_NOT_FOUND);
        }
    }

    private static UUID getToken(String[] args) {
        if (args.length > 0) {
            try {
                return UUID.fromString(args[0]);
            } catch (IllegalArgumentException ignored) {
            }
        }
        return null;
    }

    public static void main(String[] args) {
        fixFilesOwner();

        waitSystemService("package");
        waitSystemService("activity");
        waitSystemService(Context.USER_SERVICE);
        waitSystemService(Context.APP_OPS_SERVICE);

        checkManagerApp();

        /*if (Build.VERSION.SDK_INT >= 28) {
            disableHiddenApiBlacklist();
        }*/

        ShizukuService.main(getToken(args));
    }
}
