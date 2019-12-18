package oz.veriparkapp.ImkbHacim;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import oz.veriparkapp.R;

public class ImkbHacimFragment extends Fragment {

    private static final String TAG = "ImkbHacimFragment";
    private String key;
    private List<ImkbHacimRowItem> rowItems;
    private ListView mylistview;
    private int hacim;

    private RelativeLayout relativeLayout;
    private Button btnSearch;
    private EditText etSearch;

    private String searchedWord;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_imkb_hacim, container, false);

        rowItems = new ArrayList<ImkbHacimRowItem>();
        mylistview = (ListView) view.findViewById(R.id.list_imkb_hacim);

        relativeLayout = view.findViewById(R.id.relative);
        btnSearch = view.findViewById(R.id.btn_search);
        etSearch = view.findViewById(R.id.search_edit);

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        final String list = sharedPref.getString("list","Default");
        key = sharedPref.getString("key","Default");
        String fragmentName = sharedPref.getString("fragment","Default");

        if(fragmentName.equals("Imkb30")) {
            hacim = 30;
        }
        else if(fragmentName.equals("Imkb50")) {
            hacim = 50;
        }
        else if(fragmentName.equals("Imkb100")) {
            hacim = 100;
            relativeLayout.setVisibility(View.VISIBLE);
        }
        try {
            getEncryptResponse(list, "list");
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etSearch.getText() != null){
                    searchedWord = etSearch.getText().toString().trim();
                    try {
                        getEncryptResponse(list, "search");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (SAXException e) {
                        e.printStackTrace();
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        mylistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "Selected position and symbol :" + position + "/" + rowItems.get(position).getSembol());
            }
        });

        return view;
    }

    public void getEncryptResponse(String response, String key) throws ParserConfigurationException, SAXException, IOException {

        List<ImkbHacimRowItem> rowItemList = new ArrayList<ImkbHacimRowItem>();

        if(key.equals("search")) {
            for (int i = 0; i < rowItems.size(); i++){
                if (searchedWord != null && (searchedWord.equals(rowItems.get(i).getSembol()) || searchedWord.equals(rowItems.get(i).getName())))
                    rowItemList.add(rowItems.get(i));
                else if(searchedWord == null || searchedWord.isEmpty()){
                    rowItemList.add(rowItems.get(i));
                }
            }
        }
        else
            rowItemList = parser(response);

        ImkbHacimAdapter adapter = new ImkbHacimAdapter(getActivity(), rowItemList);
        mylistview.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        mylistview.invalidateViews();
    }

    public List<ImkbHacimRowItem> parser(String response) throws IOException, SAXException, ParserConfigurationException {

        DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document parser = newDocumentBuilder.parse(new ByteArrayInputStream(response.getBytes()));

        rowItems.clear();
        String TagName = "IMKB" + hacim + "List";
        int elementLenght = parser.getElementsByTagName(TagName).item(0).getChildNodes().getLength();

        String symbol, name, gain, fund;

        for(int i=0; i<elementLenght; i++){
            symbol = parser.getElementsByTagName("Symbol").item(i).getTextContent();
            name = parser.getElementsByTagName("Name").item(i).getTextContent();
            gain = parser.getElementsByTagName("Gain").item(i).getTextContent();
            fund = parser.getElementsByTagName("Fund").item(i).getTextContent();

            ImkbHacimRowItem item = new ImkbHacimRowItem(symbol, name, gain, fund);
            rowItems.add(item);
        }

        return rowItems;
    }
}