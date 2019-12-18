package oz.veriparkapp.ImkbUpDown;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import oz.veriparkapp.ImkbHisse.ImkbHisseRowItem;
import oz.veriparkapp.R;


public class ImkbUpDownAdapter extends BaseAdapter
{
	Context context;
	List<ImkbUpDownRowItem> rowItemList;

	public ImkbUpDownAdapter(List<ImkbUpDownRowItem> rowItem)
	{
		this.rowItemList = rowItem;
	}

	public ImkbUpDownAdapter(Context context, List<ImkbUpDownRowItem> rowItem) {
		this.context = context;
		this.rowItemList = rowItem;
	}

	@Override
	public int getCount()
	{
		return rowItemList.size();
	}

	@Override
	public Object getItem(int position)
	{
		return rowItemList.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return rowItemList.indexOf(getItem(position));
	}

	private class ViewHolder {
		TextView sembol;
		TextView fiyat;
		TextView fark;
		TextView islemHacmi;
		TextView alis;
		TextView satis;
		TextView saat;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;

		LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_row_imkb_up_down, null);
			holder = new ViewHolder();
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.sembol = (TextView) convertView.findViewById(R.id.sembol);
		holder.fiyat = (TextView) convertView.findViewById(R.id.fiyat);
		holder.fark = (TextView) convertView.findViewById(R.id.fark);
		holder.islemHacmi = (TextView) convertView.findViewById(R.id.islem_hacmi);
		holder.alis = (TextView) convertView.findViewById(R.id.alis);
		holder.satis = (TextView) convertView.findViewById(R.id.satis);
		holder.saat = (TextView) convertView.findViewById(R.id.saat);

		holder.sembol.setText(rowItemList.get(position).getSembol());
		holder.fiyat.setText(rowItemList.get(position).getFiyat());
		holder.fark.setText("%"+rowItemList.get(position).getFark());
		holder.islemHacmi.setText(rowItemList.get(position).getIslemHacmi());
		holder.alis.setText(rowItemList.get(position).getAlis());
		holder.satis.setText(rowItemList.get(position).getSatis());
		holder.saat.setText(rowItemList.get(position).getSaat());

		return convertView;
	}
}
