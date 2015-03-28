package com.example.stellarsurvival;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter<String> {
		
		private Context context;
		private String[] objects;
		private Typeface tf;

		public CustomAdapter(Context context, int resourceId, String[] objects) {
			super(context, resourceId, objects);
			this.objects = objects;
			this.context = context;
			tf = Typeface.createFromAsset(context.getAssets(), "fonts/Digital_tech.otf");
		}

		@Override
		public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
			return getCustomView(position, cnvtView, prnt);
		}
		@Override
		public View getView(int pos, View cnvtView, ViewGroup prnt) {
			return getCustomView(pos, cnvtView, prnt);
		}
		public View getCustomView(int position, View convertView,
				ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
			View mySpinner = inflater.inflate(R.layout.custom_spinner, parent,
					false);
			TextView text = (TextView) mySpinner
					.findViewById(R.id.text_main_seen);
			text.setText(objects[position]);
			text.setTypeface(tf);

			return mySpinner;
		}
	}