package com.example.humat;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class itemDataHolder2 {
    public TextView foodTXT;
    public ImageView foodImg;

    public itemDataHolder2(View root){
        this.foodTXT=root.findViewById(R.id.food_txt);
        this.foodImg=root.findViewById(R.id.food_img);
    }


}
