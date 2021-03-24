package com.example.humat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Adapter extends ArrayAdapter<ItemData> {
    private Context context;
    private int layoutResId;
    private ArrayList<ItemData> dataList;

    public Adapter(@NonNull Context context, int resource, @NonNull ArrayList<ItemData> objects) {
        super(context, resource, objects);
        this.context = context;
        this.layoutResId = resource;
        this.dataList = objects;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {      // arraylist --> item_data.xml로 데이타 옮겨줌
        // Data ==> xml layout 넣어서 보이고 사용할 수 있도록 객체 생성
        // (1) item layout xml ==> Java 객체 변환
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutResId, null);

            ItemDataHolder holder = new ItemDataHolder(convertView);
            convertView.setTag(holder);
        }
        ItemDataHolder holder = (ItemDataHolder) convertView.getTag();

        TextView nameTXT = holder.nameTXT;
        TextView phoneTXT = holder.phoneTXT;
        TextView addrTXT = holder.addressTXT;

        // (3) Data 준비
        final ItemData item = dataList.get(position);

        // (4) layout < ---- > data
        nameTXT.setText(item.getName());
        phoneTXT.setText(item.getPhone());
        addrTXT.setText(item.getAddress());

        return convertView;
    }
}
