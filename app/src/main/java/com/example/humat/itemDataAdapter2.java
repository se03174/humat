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

public class itemDataAdapter2 extends ArrayAdapter<itemData2> {
    // Member Variable ------------------------------------------------------------------------
    private Context context;
    private int                         layoutResId;
    private ArrayList<itemData2> dataList;

    public itemDataAdapter2(@NonNull Context context, int resource, @NonNull ArrayList<itemData2> objects) {
        super(context, resource, objects);
        this.context=context;
        this.layoutResId=resource;
        this.dataList=objects;
    }

    // Override Method --------------------------------------------------------------------------

    @Override
    public int getCount() {
        return dataList.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Data ==> XML Layout 넣어서 보이고 사용할 수 있도록 객체 생성
        //(1) item Layout xml ==> Java 객체로 변환
        if(convertView==null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutResId, null);

            itemDataHolder2 holder = new itemDataHolder2(convertView);
            convertView.setTag(holder);
        }
        itemDataHolder2 holder = (itemDataHolder2) convertView.getTag();

        TextView food_nameTXT =  holder.foodTXT;
        ImageView food_img = holder.foodImg;

        // (3) Data 준비
        final itemData2 item = dataList.get(position);


        // (4) Layout < --- > Data
        food_nameTXT.setText(item.getFood_name());

        // Image 크기 조절
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),item.getImgResId());
        bitmap=bitmap.createScaledBitmap(bitmap,100,100,true);
        food_img.setImageBitmap(bitmap);

        food_img.setImageResource(item.getImgResId());

        return convertView;
    }


}
