package com.eltonfaust.wakeupplugin;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.util.Log;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

// credits to: https://stackoverflow.com/a/58516863/3337038 and https://github.com/judemanutd/AutoStarter

public class WakeupAutoStartHelper {
    private static final String LOG_TAG = "WakeupAutoStartHelper";
    private static final String PREF_KEY_APP_AUTO_START = "app-auto-start";

    /***
     * Xiaomi
     */
    private static final String BRAND_XIAOMI = "xiaomi";
    private static final String BRAND_XIAOMI_POCO = "poco";
    private static final String BRAND_XIAOMI_REDMI = "redmi";
    private static final String PACKAGE_XIAOMI_MAIN = "com.miui.securitycenter";
    private static final String PACKAGE_XIAOMI_COMPONENT = "com.miui.permcenter.autostart.AutoStartManagementActivity";

    /***
     * Letv
     */
    private static final String BRAND_LETV = "letv";
    private static final String PACKAGE_LETV_MAIN = "com.letv.android.letvsafe";
    private static final String PACKAGE_LETV_COMPONENT = "com.letv.android.letvsafe.AutobootManageActivity";

    /***
     * ASUS ROG
     */
    private static final String BRAND_ASUS = "asus";
    private static final String PACKAGE_ASUS_MAIN = "com.asus.mobilemanager";
    private static final String PACKAGE_ASUS_COMPONENT = "com.asus.mobilemanager.powersaver.PowerSaverSettings";

    /***
     * Honor
     */
    private static final String BRAND_HONOR = "honor";
    private static final String PACKAGE_HONOR_MAIN = "com.huawei.systemmanager";
    private static final String PACKAGE_HONOR_COMPONENT = "com.huawei.systemmanager.optimize.process.ProtectActivity";

    /**
     * Oppo
     */
    private static final String BRAND_OPPO = "oppo";
    private static final String PACKAGE_OPPO_MAIN = "com.coloros.safecenter";
    private static final String PACKAGE_OPPO_FALLBACK = "com.oppo.safe";
    private static final String PACKAGE_OPPO_COMPONENT = "com.coloros.safecenter.permission.startup.StartupAppListActivity";
    private static final String PACKAGE_OPPO_COMPONENT_FALLBACK = "com.oppo.safe.permission.startup.StartupAppListActivity";
    private static final String PACKAGE_OPPO_COMPONENT_FALLBACK_A = "com.coloros.safecenter.startupapp.StartupAppListActivity";

    /**
     * Vivo
     */
    private static final String BRAND_VIVO = "vivo";
    private static final String PACKAGE_VIVO_MAIN = "com.iqoo.secure";
    private static final String PACKAGE_VIVO_FALLBACK = "com.vivo.perm;issionmanager";
    private static final String PACKAGE_VIVO_COMPONENT = "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity";
    private static final String PACKAGE_VIVO_COMPONENT_FALLBACK = "com.vivo.permissionmanager.activity.BgStartUpManagerActivity";
    private static final String PACKAGE_VIVO_COMPONENT_FALLBACK_A = "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager";

    /**
     * Nokia
     */
    private static final String BRAND_NOKIA = "nokia";
    private static final String PACKAGE_NOKIA_MAIN = "com.evenwell.powersaving.g3";
    private static final String PACKAGE_NOKIA_COMPONENT = "com.evenwell.powersaving.g3.exception.PowerSaverExceptionActivity";

    /***
     * Samsung
     */
    private static final String BRAND_SAMSUNG = "samsung";
    private static final String PACKAGE_SAMSUNG_MAIN = "com.samsung.android.lool";
    private static final String PACKAGE_SAMSUNG_COMPONENT = "com.samsung.android.sm.ui.battery.BatteryActivity";
    private static final String PACKAGE_SAMSUNG_COMPONENT_2 = "com.samsung.android.sm.battery.ui.BatteryActivity";
    private static final String PACKAGE_SAMSUNG_COMPONENT_3 = "com.samsung.android.sm.battery.ui.usage.CheckableAppListActivity";

    /***
     * One plus
     */
    private static final String BRAND_ONE_PLUS = "oneplus";
    private static final String PACKAGE_ONE_PLUS_MAIN = "com.oneplus.security";
    private static final String PACKAGE_ONE_PLUS_COMPONENT = "com.oneplus.security.chainlaunch.view.ChainLaunchAppListActivity";
    private static final String PACKAGE_ONE_PLUS_ACTION = "com.android.settings.action.BACKGROUND_OPTIMIZE";

    private WakeupAutoStartHelper() {
    }

    public static WakeupAutoStartHelper getInstance() {
        return new WakeupAutoStartHelper();
    }

    public boolean canOpenPreferences(Context context) {
        return this.autoStart(context, false);
    }

    public boolean openAutoStartPreferences(Context context) {
        String brandName = Build.BRAND.toLowerCase();
        log("Opening preferences for: ".concat(brandName));

        return this.autoStart(context);
    }

    private boolean autoStart(final Context context) {
        return this.autoStart(context, true);
    }

    private boolean autoStart(final Context context, boolean open) {
        String brandName = Build.BRAND.toLowerCase(Locale.ROOT);

        switch (brandName) {
            case BRAND_ASUS:
                return autoStartAsus(context, open);
            case BRAND_XIAOMI:
            case BRAND_XIAOMI_POCO:
            case BRAND_XIAOMI_REDMI:
                return autoStartXiaomi(context, open);
            case BRAND_LETV:
                return autoStartLetv(context, open);
            case BRAND_HONOR:
                return autoStartHonor(context, open);
            case BRAND_OPPO:
                return autoStartOppo(context, open);
            case BRAND_VIVO:
                return autoStartVivo(context, open);
            case BRAND_NOKIA:
                return autoStartNokia(context, open);
            case BRAND_SAMSUNG:
                return autoStartSamsung(context, open);
            case BRAND_ONE_PLUS:
                return autoStartOnePlus(context, open);
        }

        return false;
    }

    private boolean autoStartAsus(final Context context, boolean open) {
        if (this.isPackageExists(context, PACKAGE_ASUS_MAIN)) {
            if (!open) {
                return true;
            }

            try {
                this.startIntent(context, PACKAGE_ASUS_MAIN, PACKAGE_ASUS_COMPONENT);
                return true;
            } catch (Exception e) {
            }
        }

        return false;
    }

    private boolean autoStartXiaomi(final Context context, boolean open) {
        if (this.isPackageExists(context, PACKAGE_XIAOMI_MAIN)) {
            if (!open) {
                return true;
            }

            try {
                this.startIntent(context, PACKAGE_XIAOMI_MAIN, PACKAGE_XIAOMI_COMPONENT);
                return true;
            } catch (Exception e) {
            }
        }

        return false;
    }

    private boolean autoStartLetv(final Context context, boolean open) {
        if (this.isPackageExists(context, PACKAGE_LETV_MAIN)) {
            if (!open) {
                return true;
            }

            try {
                this.startIntent(context, PACKAGE_LETV_MAIN, PACKAGE_LETV_COMPONENT);
                return true;
            } catch (Exception e) {
            }
        }

        return false;
    }

    private boolean autoStartHonor(final Context context, boolean open) {
        if (this.isPackageExists(context, PACKAGE_HONOR_MAIN)) {
            if (!open) {
                return true;
            }

            try {
                this.startIntent(context, PACKAGE_HONOR_MAIN, PACKAGE_HONOR_COMPONENT);
                return true;
            } catch (Exception e) {
            }
        }

        return false;
    }

    private boolean autoStartOppo(final Context context, boolean open) {
        if (this.isPackageExists(context, PACKAGE_OPPO_MAIN) || this.isPackageExists(context, PACKAGE_OPPO_FALLBACK)) {
            if (!open) {
                return true;
            }

            try {
                this.startIntent(context, PACKAGE_OPPO_MAIN, PACKAGE_OPPO_COMPONENT);
                return true;
            } catch (Exception e) {
                try {
                    this.startIntent(context, PACKAGE_OPPO_FALLBACK, PACKAGE_OPPO_COMPONENT_FALLBACK);
                    return true;
                } catch (Exception ex) {
                    try {
                        this.startIntent(context, PACKAGE_OPPO_MAIN, PACKAGE_OPPO_COMPONENT_FALLBACK_A);
                        return true;
                    } catch (Exception exx) {
                    }
                }
            }
        }

        return false;
    }

    private boolean autoStartVivo(final Context context, boolean open) {
        if (this.isPackageExists(context, PACKAGE_VIVO_MAIN) || this.isPackageExists(context, PACKAGE_VIVO_FALLBACK)) {
            if (!open) {
                return true;
            }

            try {
                this.startIntent(context, PACKAGE_VIVO_MAIN, PACKAGE_VIVO_COMPONENT);
                return true;
            } catch (Exception e) {
                try {
                    this.startIntent(context, PACKAGE_VIVO_FALLBACK, PACKAGE_VIVO_COMPONENT_FALLBACK);
                    return true;
                } catch (Exception ex) {
                    try {
                        this.startIntent(context, PACKAGE_VIVO_MAIN, PACKAGE_VIVO_COMPONENT_FALLBACK_A);
                        return true;
                    } catch (Exception exx) {
                    }
                }
            }
        }

        return false;
    }

    private boolean autoStartNokia(final Context context, boolean open) {
        if (this.isPackageExists(context, PACKAGE_NOKIA_MAIN)) {
            if (!open) {
                return true;
            }

            try {
                this.startIntent(context, PACKAGE_NOKIA_MAIN, PACKAGE_NOKIA_COMPONENT);
                return true;
            } catch (Exception e) {
            }
        }

        return false;
    }

    private boolean autoStartSamsung(final Context context, boolean open) {
        if (this.isPackageExists(context, PACKAGE_SAMSUNG_MAIN)) {
            if (!open) {
                return true;
            }

            try {
                this.startIntent(context, PACKAGE_SAMSUNG_MAIN, PACKAGE_SAMSUNG_COMPONENT);
                return true;
            } catch (Exception e) {
                try {
                    this.startIntent(context, PACKAGE_SAMSUNG_MAIN, PACKAGE_SAMSUNG_COMPONENT_2);
                    return true;
                } catch (Exception e2) {
                    try {
                        this.startIntent(context, PACKAGE_SAMSUNG_MAIN, PACKAGE_SAMSUNG_COMPONENT_3);
                        return true;
                    } catch (Exception e3) {
                    }
                }
            }
        }

        return false;
    }

    private boolean autoStartOnePlus(final Context context, boolean open) {
        if (this.isPackageExists(context, BRAND_ONE_PLUS)) {
            if (!open) {
                return true;
            }

            try {
                this.startIntent(context, PACKAGE_ONE_PLUS_MAIN, PACKAGE_ONE_PLUS_COMPONENT);
                return true;
            } catch (Exception e) {
            }
        }

        Intent intent = new Intent();
        intent.setAction(PACKAGE_ONE_PLUS_ACTION);

        if (this.isActivityFound(context, intent)) {
            if (!open) {
                return true;
            }

            try {
                context.startActivity(intent);
                return true;
            } catch (Exception e) {
                log("Failed to start action intent: ".concat(PACKAGE_ONE_PLUS_MAIN).concat(" - ").concat(PACKAGE_ONE_PLUS_ACTION));
            }
        }

        return false;
    }

    private void startIntent(Context context, String packageName, String componentName) throws Exception {
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(packageName, componentName));
            context.startActivity(intent);
        } catch (Exception e) {
            log("Failed to start intent: ".concat(packageName).concat(" - ").concat(componentName));
            throw e;
        }
    }

    private boolean isPackageExists(Context context, String targetPackage) {
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(0);

        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals(targetPackage)) {
                return true;
            }
        }

        return false;
    }

    private boolean isActivityFound(Context context, Intent intent) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * Logger
     * @param log
     */
    private void log(String log) {
        Log.v(LOG_TAG, log);
    }
}
