// com.lan.campsiteproject.map.OpenMapClickListener.java
package com.lan.campsiteproject.map;

import android.content.Context;
import android.content.Intent;
import android.view.View;

public class OpenMapClickListener implements View.OnClickListener {
    private Context context;

    public OpenMapClickListener(Context context) {
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, MapActivity.class);
        context.startActivity(intent);
    }
}
