package oz.veriparkapp.Utils;

import android.support.annotation.Nullable;
import org.greenrobot.eventbus.EventBus;

public class UITools {

    public static void startProgressDialog(@Nullable final String message) {
        EventBus.getDefault().postSticky(new ProgressDialogEvent(ProgressDialogEvent.EventType.START, message));
    }

    public static void startProgressDialog() {
        EventBus.getDefault().postSticky(new ProgressDialogEvent(ProgressDialogEvent.EventType.START));
    }

    public static void closeProgressDialog() {
        EventBus.getDefault().postSticky(new ProgressDialogEvent(ProgressDialogEvent.EventType.STOP));
    }
}