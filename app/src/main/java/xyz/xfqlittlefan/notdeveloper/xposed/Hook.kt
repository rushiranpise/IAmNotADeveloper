package xyz.xfqlittlefan.notdeveloper.xposed

import android.content.ContentResolver
import android.provider.Settings
import androidx.annotation.Keep
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import xyz.xfqlittlefan.notdeveloper.ADB_ENABLED
import xyz.xfqlittlefan.notdeveloper.ADB_WIFI_ENABLED
import xyz.xfqlittlefan.notdeveloper.BuildConfig
import xyz.xfqlittlefan.notdeveloper.DEVELOPMENT_SETTINGS_ENABLED
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

@Keep
class Hook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName.startsWith("android") || lpparam.packageName.startsWith(
                "com.android"
            )
        ) {
            return
        }

        if (lpparam.packageName == BuildConfig.APPLICATION_ID) {
            XposedHelpers.findAndHookMethod(
                "xyz.xfqlittlefan.notdeveloper.xposed.ModuleStatusKt",
                lpparam.classLoader,
                "isModuleActive",
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        param.result = true
                    }
                })
        }

        val preferences = XSharedPreferences(BuildConfig.APPLICATION_ID)

        XposedHelpers.findAndHookMethod(
            Settings.Global::class.java,
            "getInt",
            ContentResolver::class.java,
            String::class.java,
            Int::class.java,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    preferences.reload()
                    when {
                        preferences.getBoolean(
                            DEVELOPMENT_SETTINGS_ENABLED,
                            true
                        ) && param.args[1] == DEVELOPMENT_SETTINGS_ENABLED -> {
                            param.result = 0
                        }
                        preferences.getBoolean(
                            ADB_ENABLED,
                            true
                        ) && param.args[1] == ADB_ENABLED -> {
                            param.result = 0
                        }
                        preferences.getBoolean(
                            ADB_WIFI_ENABLED,
                            true
                        ) && param.args[1] == ADB_WIFI_ENABLED -> {
                            param.result = 0
                        }
                    }
                }
            })

        XposedHelpers.findAndHookMethod(
            Settings.Global::class.java,
            "getInt",
            ContentResolver::class.java,
            String::class.java,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    preferences.reload()
                    when {
                        preferences.getBoolean(
                            DEVELOPMENT_SETTINGS_ENABLED,
                            true
                        ) && param.args[1] == DEVELOPMENT_SETTINGS_ENABLED -> {
                            param.result = 0
                        }
                        preferences.getBoolean(
                            ADB_ENABLED,
                            true
                        ) && param.args[1] == ADB_ENABLED -> {
                            param.result = 0
                        }
                        preferences.getBoolean(
                            ADB_WIFI_ENABLED,
                            true
                        ) && param.args[1] == ADB_WIFI_ENABLED -> {
                            param.result = 0
                        }
                    }
                }
            })

        XposedHelpers.findAndHookMethod(
            Settings.Secure::class.java,
            "getInt",
            ContentResolver::class.java,
            String::class.java,
            Int::class.java,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    preferences.reload()
                    when {
                        preferences.getBoolean(
                            DEVELOPMENT_SETTINGS_ENABLED,
                            true
                        ) && param.args[1] == DEVELOPMENT_SETTINGS_ENABLED -> {
                            param.result = 0
                        }
                        preferences.getBoolean(
                            ADB_ENABLED,
                            true
                        ) && param.args[1] == ADB_ENABLED -> {
                            param.result = 0
                        }
                    }
                }
            })

        XposedHelpers.findAndHookMethod(
            Settings.Secure::class.java,
            "getInt",
            ContentResolver::class.java,
            String::class.java,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    preferences.reload()
                    when {
                        preferences.getBoolean(
                            DEVELOPMENT_SETTINGS_ENABLED,
                            true
                        ) && param.args[1] == DEVELOPMENT_SETTINGS_ENABLED -> {
                            param.result = 0
                        }
                        preferences.getBoolean(
                            ADB_ENABLED,
                            true
                        ) && param.args[1] == ADB_ENABLED -> {
                            param.result = 0
                        }
                    }
                }
            })

        // Add code to set sys.usb.config and persist.sys.usb.reboot.func to "usb"
        XposedHelpers.findAndHookMethod(
            Class.forName("android.os.SystemProperties"),
            "get",
            String::class.java,
            String::class.java,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val propName = param.args[0] as String
                    if (propName == "sys.usb.ffs.ready") {
                        param.result = "0"
                    } else if (propName == "sys.usb.state" || propName == "sys.usb.config" || propName == "persist.sys.usb.reboot.func") {
                        param.result = "usb"
                    }
                }

                override fun afterHookedMethod(param: MethodHookParam) {
                    val propName = param.args[0] as String
                    val propValue = param.result as String
                    if (propName == "sys.usb.ffs.ready" && propValue == "0") {
                        // Property sys.usb.ffs.ready is set to 0
                    } else if ((propName == "sys.usb.state" || propName == "sys.usb.config" || propName == "persist.sys.usb.reboot.func") && propValue == "usb") {
                        // Properties sys.usb.state, sys.usb.config, and persist.sys.usb.reboot.func are set to "usb"
                    }
                }
            }
        )
    }
}
