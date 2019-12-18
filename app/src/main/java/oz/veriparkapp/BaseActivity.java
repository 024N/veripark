package oz.veriparkapp;

import oz.veriparkapp.Utils.ProgressDialog;
import oz.veriparkapp.Utils.ProgressDialogEvent;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public abstract class BaseActivity extends AppCompatActivity {

    final String TAG = this.getClass().getSimpleName();
    private ProgressDialog mProgressDialog = null;

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onProgressDialogEvent(ProgressDialogEvent event) {
        Log.d(TAG, "onProgressDialogEvent: " + event.getEventType());

        if (event.getEventType() == ProgressDialogEvent.EventType.START) {
            String message = null;
            if (event.getEventMessage() == null) {
                message = "Data Loading";
            }

            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.newInstance(message);
                mProgressDialog.show(getSupportFragmentManager(), "ProgressDialogFragment");
            }

        } else if (event.getEventType() == ProgressDialogEvent.EventType.STOP) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        }
        EventBus.getDefault().removeStickyEvent(event);
    }

}
