package com.bionic.kvt.serviceapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.models.LMRA;

import java.util.ArrayList;

/**
 * LMRA Adapter
 */
public class LMRAAdapter extends ArrayAdapter<LMRA> {

    private static class LMRAViewHolder{
        TextView lmraName;
        TextView lmraDescription;
    }

    public LMRAAdapter (Context context, ArrayList<LMRA> lmraList) {
        super(context, R.layout.template_lmra,lmraList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LMRA lmra = getItem(position);
        LMRAViewHolder lmraViewHolder;
        if (convertView == null){
            lmraViewHolder = new LMRAViewHolder();
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.template_lmra, parent,false);
            lmraViewHolder.lmraName = (TextView) convertView.findViewById(R.id.title_lmra);
            lmraViewHolder.lmraDescription = (TextView) convertView.findViewById(R.id.description_lmra);
            convertView.setTag(lmraViewHolder);
        } else {
            lmraViewHolder = (LMRAViewHolder) convertView.getTag();
        }
        lmraViewHolder.lmraName.setText(lmra.getLmraName());
        lmraViewHolder.lmraDescription.setText(lmra.getLmraDescription());
        return convertView;
    }
}
