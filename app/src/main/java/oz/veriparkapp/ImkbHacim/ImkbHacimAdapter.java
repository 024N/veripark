package oz.veriparkapp.ImkbHacim;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import oz.veriparkapp.R;


public class ImkbHacimAdapter extends BaseAdapter
{
	Context context;
	List<ImkbHacimRowItem> rowItemList;

	public ImkbHacimAdapter(List<ImkbHacimRowItem> rowItem)
	{
		this.rowItemList = rowItem;
	}

	public ImkbHacimAdapter(Context context, List<ImkbHacimRowItem> rowItem) {
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
		TextView name;
		TextView gain;
		TextView fund;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;

		LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_row_imkb_hacim, null);
			holder = new ViewHolder();
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.sembol = (TextView) convertView.findViewById(R.id.sembol);
		holder.name = (TextView) convertView.findViewById(R.id.name);
		holder.gain = (TextView) convertView.findViewById(R.id.gain);
		holder.fund = (TextView) convertView.findViewById(R.id.fund);

		holder.sembol.setText(rowItemList.get(position).getSembol());
		holder.name.setText(rowItemList.get(position).getName());
		holder.gain.setText(rowItemList.get(position).getGain());
		holder.fund.setText(rowItemList.get(position).getFund());

		return convertView;
	}

	/*
	public void setData(List<ImkbHisseRowItem> data) {
		this.data = data;
		notifyDataSetChanged();
	}
	*/
}
