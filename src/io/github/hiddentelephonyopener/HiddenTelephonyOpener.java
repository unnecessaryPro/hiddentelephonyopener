
package io.github.hiddentelephonyopener;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleObject;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent;
import com.google.appinventor.components.runtime.ComponentContainer;

@DesignerComponent(
    version = 2,
    description = "Tries to open the hidden Phone Info (RadioInfo) screen. "
        + "Falls back to the dialer with *#*#4636#*#* prefilled, then to normal network settings.",
    category = ComponentCategory.EXTENSION,
    nonVisible = true,
    iconName = ""
)
@SimpleObject(external = true)
public class HiddenTelephonyOpener extends AndroidNonvisibleComponent {

    private final Context context;

    public HiddenTelephonyOpener(ComponentContainer container) {
        super(container.$form());
        this.context = container.$context();
    }

    /** Known RadioInfo / testing activities across AOSP and OEM builds. */
    private static final String[][] TARGETS = new String[][] {
        // Modern AOSP (Android 8+)
        {"com.android.phone", "com.android.phone.settings.RadioInfo"},
        // Older AOSP
        {"com.android.phone", "com.android.phone.RadioInfo"},
        // Some OEM / older builds
        {"com.android.settings", "com.android.settings.RadioInfo"},
        // Testing menu variants
        {"com.android.settings", "com.android.settings.TestingSettings"},
        {"com.android.settings", "com.android.settings.Settings$TestingSettingsActivity"},
        // OPPO / Realme / OnePlus (ColorOS / OxygenOS)
        {"com.coloros.settings", "com.coloros.settings.RadioInfo"},
        {"com.oplus.settings", "com.oplus.settings.RadioInfo"}
    };

    @SimpleFunction(description = "Attempts to open the hidden Phone Info screen. "
        + "Returns SUCCESS, FALLBACK or FAILED with details.")
    public String OpenPhoneInfoScreen() {
        StringBuilder log = new StringBuilder();

        // 1) Try explicit activities
        for (String[] t : TARGETS) {
            try {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(t[0], t[1]));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return "SUCCESS: Opened " + t[0] + "/" + t[1];
            } catch (SecurityException e) {
                log.append(t[1]).append(": SECURITY_DENIED (not exported)\n");
            } catch (ActivityNotFoundException e) {
                log.append(t[1]).append(": NOT_FOUND\n");
            } catch (Exception e) {
                log.append(t[1]).append(": ").append(e.getClass().getSimpleName()).append("\n");
            }
        }

        // 2) Fallback: open dialer with the secret code prefilled (user presses call)
        try {
            Intent dial = new Intent(Intent.ACTION_DIAL,
                Uri.parse("tel:" + Uri.encode("*#*#4636#*#*")));
            dial.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(dial);
            return "FALLBACK: Opened dialer with *#*#4636#*#* prefilled. Press call.\n" + log;
        } catch (Exception e) {
            log.append("DIALER: ").append(e.getClass().getSimpleName()).append("\n");
        }

        return "FAILED: Could not open Phone Info.\n" + log;
    }

    @SimpleFunction(description = "Opens the dialer with *#*#4636#*#* prefilled. User must press call.")
    public String OpenDialerWithCode() {
        try {
            Intent dial = new Intent(Intent.ACTION_DIAL,
                Uri.parse("tel:" + Uri.encode("*#*#4636#*#*")));
            dial.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(dial);
            return "SUCCESS: Dialer opened";
        } catch (Exception e) {
            return "FAILED: " + e.getClass().getSimpleName();
        }
    }

    @SimpleFunction(description = "Opens normal Mobile Network settings (use if hidden menu fails).")
    public String OpenNormalSettings() {
        try {
            Intent intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return "SUCCESS: Opened mobile network settings";
        } catch (Exception e1) {
            try {
                Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return "SUCCESS: Opened wireless settings";
            } catch (Exception e2) {
                return "FAILED: " + e2.getClass().getSimpleName();
            }
        }
    }
}
