package oz.veriparkapp.Utils;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import oz.veriparkapp.R;

public class ProgressDialog extends DialogFragment {

    public static ProgressDialog newInstance(String message) {
        ProgressDialog progressDialog = new ProgressDialog();
        Bundle bundle = new Bundle();
        bundle.putString("message", message);
        progressDialog.setArguments(bundle);

        return progressDialog;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_progress_dialog, container, true);
        getDialog().requestWindowFeature(STYLE_NO_TITLE);
        setCancelable(false);

        super.onCreate(savedInstanceState);
        String message = null;

        if (getArguments() != null) {
            message = getArguments().getString("message");
        }

        TextView txtMessageView = view.findViewById(R.id.txtMessage);

        if (!TextUtils.isEmpty(message)) {
            txtMessageView.setText(message);
            txtMessageView.setVisibility(View.VISIBLE);
        } else {
            txtMessageView.setVisibility(View.GONE);
        }

        return view;
    }

}
