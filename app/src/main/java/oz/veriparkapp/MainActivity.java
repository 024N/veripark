package oz.veriparkapp;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(isNetworkAvailable()) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.addToBackStack("MainPageFragment");
            transaction.replace(R.id.content, new MainPageFragment()).commit();
        }
        else
            Toast.makeText(this, getString(R.string.check_internet_con), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {

        int index = getFragmentManager().getBackStackEntryCount() - 1;
        FragmentManager.BackStackEntry backEntry = getFragmentManager().getBackStackEntryAt(index);
        String tag = backEntry.getName();
        Log.d("onBackPressed TAG: ", tag);

        //FragmentManager fragmentManager = getFragmentManager();
        //FragmentTransaction transaction = fragmentManager.beginTransaction();
        //Log.i("Backback", fragmentManager.findFragmentById(R.id.content)+"");
        /*
        if (fragmentManager.getBackStackEntryCount() < 1) {
            transaction.addToBackStack("outerPost");
            transaction.replace(R.id.content, new OuterPost(),"outerPost").commit();

        }
        else if(fragmentManager.findFragmentById(R.id.content).getTag().equals("outerPost"))
            fab.setVisibility(View.VISIBLE);


        if (fragmentManager.getBackStackEntryCount() < 1 || fragmentManager.getBackStackEntryCount() > 2) {
            //Log.i("XXXXXXXXXX", fragmentManager.getBackStackEntryCount()+"");
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE); //fragmentleri temizler
            transaction.replace(R.id.content, new OuterPost(),"outerPost").commit();
            fab.setVisibility(View.VISIBLE);
        }
        */
        super.onBackPressed();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
