package com.example.stellarsurvival;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MatchLogAdapter extends BaseAdapter {

	private final List<MatchLogItem> mItems = new ArrayList<MatchLogItem>();
	private final Context mContext;

	public MatchLogAdapter(Context context) {

		mContext = context;

	}
	
	public void add(MatchLogItem item) {

		mItems.add(item);
		notifyDataSetChanged();

	}
	
	public void setItems(List<MatchLogItem> newItems) {
		mItems.addAll(newItems);
		notifyDataSetChanged();
	}
	
	public void clear() {

		mItems.clear();
		notifyDataSetChanged();

	}

	@Override
	public int getCount() {

		return mItems.size();

	}

	@Override
	public Object getItem(int pos) {

		return mItems.get(pos);

	}

	@Override
	public long getItemId(int pos) {

		return pos;

	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final MatchLogItem matchLogItem = mItems.get(position);

		LinearLayout itemLayout;
		if (convertView == null) itemLayout = (LinearLayout) 
				((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.match_log_item, parent, false);
		else itemLayout = (LinearLayout) convertView;

		final TextView nameView = (TextView) itemLayout.findViewById(R.id.matchLogName);
		nameView.setText(matchLogItem.getName());
		
		final TextView scoreView = (TextView) itemLayout.findViewById(R.id.matchLogScore);
		scoreView.setText(matchLogItem.getScore().toString());
		
		final TextView dateView = (TextView) itemLayout.findViewById(R.id.matchLogDate);
		dateView.setText(matchLogItem.getDate());
		
		return itemLayout;

	}
}