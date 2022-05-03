package com.example.filemanager;


import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class baseAdapter extends BaseAdapter {
    private List<String> data=new ArrayList<>();

    public void setData(List<String> data) {
        if (data != null) {
            this.data.clear();
            if (data.size() > 0) {
                this.data.addAll(data);
            }
            notifyDataSetChanged();
        }
    }


    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public String getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        if(convertView==null)
        {
            convertView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
            convertView.setTag(new ViewHolder((TextView) convertView.findViewById(R.id.textitem)));
        }
        ViewHolder holder=(ViewHolder) convertView.getTag();
        final String item=getItem(i);
        holder.info.setText(item.substring(item.lastIndexOf('/')+1));
        if(selection!=null)
        {
            if(selection[i])
            {
                holder.info.setBackgroundColor(Color.argb(100,9,9,9));
            }
            else
            {
                holder.info.setBackgroundColor(Color.WHITE);
            }



        }
        return  convertView;
    }
    private boolean[] selection;
    void  setSelection(boolean[] selection)
    {
        if(selection!=null)
        {
            this.selection=new boolean[selection.length];
            for (int j=0;j<selection.length;j++)
            {
                this.selection[j]=selection[j];
            }
            notifyDataSetChanged();
        }
    }


}
class  ViewHolder
{
    TextView info;
    ViewHolder(TextView info) {
        this.info = info;
    }
}
