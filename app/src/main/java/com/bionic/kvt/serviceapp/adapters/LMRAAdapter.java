package com.bionic.kvt.serviceapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bionic.kvt.serviceapp.R;
import com.bionic.kvt.serviceapp.models.LMRA;

import java.util.ArrayList;

/**
 * LMRA Adapter
 */
public class LMRAAdapter extends ArrayAdapter<LMRA> {

    private static class LMRAViewHolder {
        TextView lmraName;
        TextView lmraDescription;
        Button lmraDeleteButton;
    }

    public LMRAAdapter(Context context, ArrayList<LMRA> lmraList) {
        super(context, R.layout.template_lmra, lmraList);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final LMRA lmra = getItem(position);
        final LMRAViewHolder lmraViewHolder;
        if (convertView == null) {
            lmraViewHolder = new LMRAViewHolder();
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.template_lmra, parent, false);
            lmraViewHolder.lmraName = (TextView) convertView.findViewById(R.id.title_lmra);
            lmraViewHolder.lmraDescription = (TextView) convertView.findViewById(R.id.description_lmra);
            lmraViewHolder.lmraDeleteButton = (Button) convertView.findViewById(R.id.button_lmra_delete);

            convertView.setTag(lmraViewHolder);
        } else {
            lmraViewHolder = (LMRAViewHolder) convertView.getTag();
        }
        lmraViewHolder.lmraName.setText(lmra.getLmraName());
        lmraViewHolder.lmraDescription.setText(lmra.getLmraDescription());
        lmraViewHolder.lmraDeleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), String.valueOf(getItem(position).getLmraName()), Toast.LENGTH_SHORT).show();
                remove(getItem(position));
                notifyDataSetChanged();
            }
        });
        return convertView;
    }
}
