package com.example.humat;

import android.view.View;
import android.widget.TextView;

public class ItemDataHolder {
    public TextView nameTXT;
    public TextView addressTXT;
    public TextView phoneTXT;

    public ItemDataHolder(View root) {
        this.nameTXT =root.findViewById(R.id.nameTXT);
        this.phoneTXT =root.findViewById(R.id.phoneTXT);
        this.addressTXT =root.findViewById(R.id.addressTXT);
    }
}
