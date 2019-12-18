package oz.veriparkapp.ImkbUpDown;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import oz.veriparkapp.ImkbHisse.ImkbHisseAdapter;
import oz.veriparkapp.ImkbHisse.ImkbHisseRowItem;
import oz.veriparkapp.R;

public class ImkbUpDownFragment extends Fragment {

    private static final String TAG = "ImkbUpDownFragment";
    private String key;
    private Boolean upOrDown;
    private List<ImkbUpDownRowItem> rowItems;
    private ListView mylistview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_imkb_up_down, container, false);

        rowItems = new ArrayList<ImkbUpDownRowItem>();
        mylistview = (ListView) view.findViewById(R.id.list_imkb_up_down);

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String list = sharedPref.getString("list","Default");
        key = sharedPref.getString("key","Default");
        String fragmentName = sharedPref.getString("fragment","Default");

        if(fragmentName.equals("ImkbUp"))
            upOrDown = true;
        else if(fragmentName.equals("ImkbDown"))
            upOrDown = false;

        try {
            getEncryptResponse(list);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mylistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "Selected position and symbol :" + position + "/" + rowItems.get(position).getSembol());
            }
        });

        return view;
    }

    public void getEncryptResponse(String response) throws IOException, SAXException, ParserConfigurationException {

        DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document parser = newDocumentBuilder.parse(new ByteArrayInputStream(response.getBytes()));

        int elementLenght = parser.getElementsByTagName("StocknIndexesResponseList").item(0).getChildNodes().getLength();

        String symbol, price, difference, volume, buying, selling, hour, dayPeakPrice, dayLowestPrice, total;

        for(int i=0; i<elementLenght; i++){
            symbol = parser.getElementsByTagName("Symbol").item(i).getTextContent();
            price = parser.getElementsByTagName("Price").item(i).getTextContent();
            difference = parser.getElementsByTagName("Difference").item(i).getTextContent();
            volume = parser.getElementsByTagName("Volume").item(i).getTextContent();
            buying = parser.getElementsByTagName("Buying").item(i).getTextContent();
            selling = parser.getElementsByTagName("Selling").item(i).getTextContent();
            hour = parser.getElementsByTagName("Hour").item(i).getTextContent();
            //dayPeakPrice = parser.getElementsByTagName("DayPeakPrice").item(i).getTextContent();
            //dayLowestPrice = parser.getElementsByTagName("DayLowestPrice").item(i).getTextContent();
            //total = parser.getElementsByTagName("Total").item(i).getTextContent();

            if(difference.charAt(0) != "-".charAt(0) && upOrDown){
                ImkbUpDownRowItem item = new ImkbUpDownRowItem(symbol, price, difference, volume, buying, selling, hour);
                rowItems.add(item);
            }
            else if(difference.charAt(0) == "-".charAt(0) && !upOrDown){
                ImkbUpDownRowItem item = new ImkbUpDownRowItem(symbol, price, difference, volume, buying, selling, hour);
                rowItems.add(item);
            }

            if(i==50)
                break;
        }

        ImkbUpDownAdapter adapter = new ImkbUpDownAdapter(getActivity(), rowItems);
        mylistview.setAdapter(adapter);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}