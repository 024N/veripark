package oz.veriparkapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import oz.veriparkapp.ImkbHacim.ImkbHacimFragment;
import oz.veriparkapp.ImkbHisse.ImkbHisseFragment;
import oz.veriparkapp.ImkbUpDown.ImkbUpDownFragment;
import oz.veriparkapp.Utils.UITools;

public class TitlePageFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "TitlePageFragment";

    private String list, encryptResponse;

    private Button btnImkbHisse, btnImkbYukselen, btnImkbDusen, btnImkbHacim30, btnImkbHacim50, btnImkbHacim100;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_title_page, container, false);

        UITools.startProgressDialog();

        btnImkbHisse = (Button) view.findViewById(R.id.button_imkb_hisse);
        btnImkbYukselen = (Button) view.findViewById(R.id.button_imkb_yukselen);
        btnImkbDusen = (Button) view.findViewById(R.id.button_imkb_dusen);
        btnImkbHacim30 = (Button) view.findViewById(R.id.button_imkb_hacim_30);
        btnImkbHacim50 = (Button) view.findViewById(R.id.button_imkb_hacim_50);
        btnImkbHacim100 = (Button) view.findViewById(R.id.button_imkb_hacim_100);

        btnImkbHisse.setOnClickListener(this);
        btnImkbYukselen.setOnClickListener(this);
        btnImkbDusen.setOnClickListener(this);
        btnImkbHacim30.setOnClickListener(this);
        btnImkbHacim50.setOnClickListener(this);
        btnImkbHacim100.setOnClickListener(this);

        getKey();

        return view;
    }

    public void getKey() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yyyy HH:mm");
        String currentDateandTime = sdf.format(new Date());

        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope " +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance" +
                "\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema" +
                "\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><Encrypt " +
                "xmlns=\"http://tempuri.org/\"><request>RequestIsValid" +
                currentDateandTime + "</request></Encrypt></soap:Body></soap:Envelope>";

        if(isNetworkAvailable())
            request(xml, "key");
        else
            Toast.makeText(getActivity(), getString(R.string.check_internet_con), Toast.LENGTH_LONG).show();
    }

    public void getList(String encryptResponse)
    {
        String xml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\"><soapenv:Header/><soapenv:Body><tem:GetForexStocksandIndexesInfo><tem:request><tem:IsIPAD>true</tem:IsIPAD><tem:DeviceID>test</tem:DeviceID><tem:DeviceType>ipad</tem:DeviceType>\n" +
                "<tem:RequestKey>" +
                encryptResponse +
                "</tem:RequestKey><tem:Period>Day</tem:Period></tem:request></tem:GetForexStocksandIndexesInfo></soapenv:Body></soapenv:Envelope>";

        if(isNetworkAvailable())
            request(xml, "list");
        else
            Toast.makeText(getActivity(), getString(R.string.check_internet_con), Toast.LENGTH_LONG).show();
    }

    public void request(String sendData, final String requestType) {
        MediaType SOAP_MEDIA_TYPE = MediaType.parse("text/xml");
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(SOAP_MEDIA_TYPE, sendData);

        final Request request = new Request.Builder()
                .url("http://mobileexam.veripark.com/mobileforeks/service.asmx")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String mMessage = e.getMessage().toString();
                Log.w("failure Response", mMessage);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String mMessage = response.body().string();
                int code = response.code();

                if(code == 200 && requestType.equals("key")) {
                    try {
                        getEncryptResponse(mMessage);
                    } catch (SAXException e) {
                        e.printStackTrace();
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                    }
                }
                else if(code == 200 && requestType.equals("list")) {
                    list = mMessage;
                    SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("list", list);
                    editor.putString("key", encryptResponse);
                    editor.apply();

                    Log.d(TAG, "Request is successfull");
                    UITools.closeProgressDialog();
                }
                else{
                    Toast.makeText(getActivity(), getString(R.string.request_problem), Toast.LENGTH_LONG).show();
                    UITools.closeProgressDialog();
                }
            }
        });
    }

    public void getEncryptResponse(String response) throws IOException, SAXException, ParserConfigurationException {

        DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document parser = newDocumentBuilder.parse(new ByteArrayInputStream(response.getBytes()));

        encryptResponse = parser.getElementsByTagName("EncryptResponse").item(0).getTextContent();
        Log.d(TAG, "EncryptResponse " + encryptResponse);
        getList(encryptResponse);
    }

    @Override
    public void onClick(View view) {
        if(!isNetworkAvailable()) {
            Toast.makeText(getActivity(), getString(R.string.check_internet_con), Toast.LENGTH_LONG).show();
            return;
        }

        if(view == btnImkbHisse) {
            ImkbHisseFragment imkbHisseFragment = new ImkbHisseFragment();
            openFragment(imkbHisseFragment,"imkbHisse");
        }
        else if(view == btnImkbYukselen) {
            ImkbUpDownFragment imkbUpDownFragment = new ImkbUpDownFragment();
            openFragment(imkbUpDownFragment,"ImkbUp");
        }
        else if(view == btnImkbDusen) {
            ImkbUpDownFragment imkbUpDownFragment = new ImkbUpDownFragment();
            openFragment(imkbUpDownFragment,"ImkbDown");
        }
        else if(view == btnImkbHacim30) {
            ImkbHacimFragment imkbHacimFragment = new ImkbHacimFragment();
            openFragment(imkbHacimFragment,"Imkb30");
        }
        else if(view == btnImkbHacim50) {
            ImkbHacimFragment imkbHacimFragment = new ImkbHacimFragment();
            openFragment(imkbHacimFragment,"Imkb50");
        }
        else if(view == btnImkbHacim100) {
            ImkbHacimFragment imkbHacimFragment = new ImkbHacimFragment();
            openFragment(imkbHacimFragment,"Imkb100");
        }
    }

    private void openFragment(Fragment fragment, String data) {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("fragment", data);
        editor.apply();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.addToBackStack(data);
        transaction.replace(R.id.content, fragment).commit();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    class MyTimer extends TimerTask {
        public void run() {
            if(getActivity() == null)
                return;

            getActivity().runOnUiThread(new Runnable() {
                public void run()
                {
                    if(!isNetworkAvailable()) {
                        Toast.makeText(getActivity(), "İnternet Bağlantınızı Kontrol Edin!", Toast.LENGTH_LONG).show();
                    }
                    else
                        UITools.startProgressDialog();
                    //cancel();
                }
            });
        }
    }

    private class ProgressBarAsync extends AsyncTask<Void, Integer, Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void...params) {
            //ImkbHisseFragment imkbHisseFragment = new ImkbHisseFragment();
            //openFragment(imkbHisseFragment,"imkbHisse");
            ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("sadasdasdasd");
            progressDialog.show();
            Log.d(TAG, "here");
            /// /UITools.startProgressDialog();
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //UITools.closeProgressDialog();
        }
    }
}