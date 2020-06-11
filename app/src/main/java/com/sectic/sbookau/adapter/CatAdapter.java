package com.sectic.sbookau.adapter;

import com.sectic.sbookau.R;
import com.sectic.sbookau.model.Catalog;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CatAdapter extends BaseAdapter {
	
	private Context context;
	private List<Catalog> aLstCat;
	
	public CatAdapter(Context context, List<Catalog> iALstCat){
        this.aLstCat = new ArrayList<>();
		this.context = context;
		this.aLstCat.clear();
		if(iALstCat != null){
            this.aLstCat.addAll(iALstCat);
        }
	}

    public void updateParams(Context context, List<Catalog> iALstCat)
    {
        this.context = context;
        this.aLstCat.clear();
        this.aLstCat.addAll(iALstCat);
    }

	@Override
	public int getCount() {
		return aLstCat.size();
	}

	@Override
	public Object getItem(int position) {		
		return aLstCat.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        convertView = mInflater.inflate(R.layout.activity_main_list_catalog_item, null);

        ImageView imgIcon = convertView.findViewById(R.id.img_main_list_catalog_item_icon);
        TextView txt_Name = convertView.findViewById(R.id.txt_main_list_catalog_item_name);
        TextView txt_Total_Book = convertView.findViewById(R.id.txt_main_list_catalog_item_book_counter);

        imgIcon.setImageResource(R.drawable.ic_content_copy_white_24dp);
        txt_Name.setText(aLstCat.get(position).value);

        txt_Total_Book.setText(String.valueOf(aLstCat.get(position).refCount));
        txt_Total_Book.setVisibility(View.VISIBLE);

        return convertView;
	}
}
