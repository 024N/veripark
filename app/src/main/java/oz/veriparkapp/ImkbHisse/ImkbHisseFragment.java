package oz.veriparkapp.ImkbHisse;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

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
import oz.veriparkapp.R;

public class ImkbHisseFragment extends Fragment {

    private static final String TAG = "ImkbHisseFragment";
    private String key;
    private List<ImkbHisseRowItem> rowItems;
    private ImkbHisseRowItem detailItem;
    private ListView mylistview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_imkb_hisse, container, false);
        rowItems = new ArrayList<ImkbHisseRowItem>();
        mylistview = (ListView) view.findViewById(R.id.list_imkb_hisse);

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String list = sharedPref.getString("list","Default");
        key = sharedPref.getString("key","Default");
        String fragmentName = sharedPref.getString("fragment","Default");

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
                detailItem = null;
                //Calendar calendar = Calendar.getInstance();
                //String period = calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.WEEK_OF_YEAR) + "/" + calendar.get(Calendar.MONTH);

                String xml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\">\n" +
                        "<soapenv:Header/><soapenv:Body><tem:GetForexStocksandIndexesInfo><tem:request><tem:IsIPAD>true</tem:IsIPAD><tem:DeviceID>test</tem:DeviceID><tem:DeviceType>ipad</tem:DeviceType>\n" +
                        "<tem:RequestKey>" + key + "</tem:RequestKey>\n" +
                        "<tem:RequestedSymbol>" + rowItems.get(position).getSembol() + "</tem:RequestedSymbol>\n" +
                        "<tem:Period>" + "Month" + "</tem:Period>\n" +
                        "</tem:request></tem:GetForexStocksandIndexesInfo></soapenv:Body></soapenv:Envelope>";

                if(isNetworkAvailable()){
                    detailItem = rowItems.get(position);
                    request(xml);
                }
                else
                    Toast.makeText(getActivity(), getString(R.string.check_internet_con), Toast.LENGTH_LONG).show();
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

            ImkbHisseRowItem item = new ImkbHisseRowItem(symbol, price, difference, volume, buying, selling, hour);
            rowItems.add(item);

            if(i==50)
                break;
        }
        ImkbHisseAdapter adapter = new ImkbHisseAdapter(getActivity(), rowItems);
        mylistview.setAdapter(adapter);
    }

    public void request(String sendData) {
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

                Log.i(TAG, "Request Code: " + code);

                if(code == 200) {
                    try {
                        getEncryptResponseDetail(mMessage);
                    } catch (SAXException e) {
                        e.printStackTrace();
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void getEncryptResponseDetail(String response) throws IOException, SAXException, ParserConfigurationException {

        DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document parser = newDocumentBuilder.parse(new ByteArrayInputStream(response.getBytes()));

        int listLenght = parser.getElementsByTagName("StocknIndexesGraphicInfos").item(0).getChildNodes().getLength();
        String price, date;
        final ArrayList<String> priceList = new ArrayList<String>();
        final ArrayList<String> dateList = new ArrayList<String>();

        for (int i = 0; i < listLenght; i++) {
            price = parser.getElementsByTagName("Price").item(i).getTextContent();
            date = parser.getElementsByTagName("Date").item(i).getTextContent();
            priceList.add(price);
            dateList.add(date);
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showPopup(priceList, dateList);
             }
        });

    }

    private void showPopup(ArrayList<String> priceList, ArrayList<String> dateList) {
        final Dialog myDialog = new Dialog(getActivity());
        myDialog.setContentView(R.layout.popup_imkb_hisse);

        TextView popupClose, popupSymbol, popupPrice, popupDifference, popupVolume, popupBuying, popupSelling, popupHour;
        ImageView image;
        //LineChartView lineChart;

        popupClose = myDialog.findViewById(R.id.tv_popup_close);

        popupSymbol =  myDialog.findViewById(R.id.tv_popup_symbol);
        popupPrice = myDialog.findViewById(R.id.tv_popup_price);
        popupDifference =  myDialog.findViewById(R.id.tv_popup_difference);
        popupVolume =  myDialog.findViewById(R.id.tv_popup_volume);
        popupBuying =  myDialog.findViewById(R.id.tv_popup_buying);
        popupSelling =  myDialog.findViewById(R.id.tv_popup_selling);
        popupHour =  myDialog.findViewById(R.id.tv_popup_hour);
        image =  myDialog.findViewById(R.id.image);
        //lineChart =  myDialog.findViewById(R.id.lineChart);

        popupSymbol.setText(detailItem.getSembol());
        popupPrice.setText(detailItem.getFiyat());
        popupDifference.setText(detailItem.getFark());
        popupVolume.setText(detailItem.getIslemHacmi());
        popupBuying.setText(detailItem.getAlis());
        popupSelling.setText(detailItem.getSatis());
        popupHour.setText(detailItem.getSaat());

        if(detailItem.getFark().charAt(0) == "-".charAt(0))
            image.setImageResource(R.drawable.down);
        else
            image.setImageResource(R.drawable.up);

        DataPoint[] dataList = new DataPoint[priceList.size()];
        for(int i=0; i<priceList.size(); i++){
            StringTokenizer tokensFirst = new StringTokenizer(dateList.get(priceList.size()-i-1), "T");
            String first = tokensFirst.nextToken();

            StringTokenizer tokensSecond = new StringTokenizer(first, "-");
            String year = tokensSecond.nextToken();
            String month = tokensSecond.nextToken();
            String day = tokensSecond.nextToken();
            String date = month + "." + day;

            dataList[i] = new DataPoint(Float.parseFloat(date),Float.parseFloat(priceList.get(priceList.size()-i-1)));
        }


        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataList);

        GraphView graph = (GraphView) myDialog.findViewById(R.id.lineChart);
        graph.addSeries(series);

        popupClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}