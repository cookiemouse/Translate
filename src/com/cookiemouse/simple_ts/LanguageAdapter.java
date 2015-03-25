package com.cookiemouse.simple_ts;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LanguageAdapter extends ArrayAdapter<Language> {
	
	private int resourceId;

	public LanguageAdapter(Context context, int resource, List<Language> objects) {
		super(context, resource, objects);
		resourceId = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Language language = getItem(position);
		if (convertView == null)
		{
			convertView = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
		}
		ImageView languageImage = (ImageView)convertView.findViewById(R.id.language_image);
		TextView languageName = (TextView)convertView.findViewById(R.id.language_name);
		languageImage.setImageResource(language.getImageId());
		languageName.setText(language.getName());
		
		return convertView;
	}

}
