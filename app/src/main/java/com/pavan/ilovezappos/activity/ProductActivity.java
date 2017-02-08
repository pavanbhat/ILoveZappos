package com.pavan.ilovezappos.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.pavan.ilovezappos.R;
import com.pavan.ilovezappos.controller.product_page;
import com.pavan.ilovezappos.databinding.ActivityProductBinding;
import com.pavan.ilovezappos.model.product_extraction;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class ProductActivity extends AppCompatActivity {


    ActivityProductBinding app_binding;
    Animation fab_show, fab_hide, fab_rotate, fab_other;
    Boolean isShowing = false;

    public ActivityProductBinding getApp_binding() {
        return app_binding;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app_binding = DataBindingUtil.setContentView(this, R.layout.activity_product);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        product_page downloadTasks = new product_page();
        Intent out = getIntent();
        String getSearchString = out.getExtras().get("search").toString();
        product_extraction productExtraction = null;
        try {
            productExtraction = downloadTasks.execute(getSearchString, "b743e26728e16b81da139182bb2094357c31d331").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (productExtraction != null) {

            URL url = null;
            InputStream is = null;
            Bitmap bmp = null;

            try {
                url = new URL(productExtraction.getResults().get(0).getThumbnailImageUrl());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            if (url != null) {
                try {
                    is = (InputStream) url.openConnection().getContent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (is != null) {
                    bmp = BitmapFactory.decodeStream(is);
                }
            }
            if (bmp != null) {
                getApp_binding().prod.imageView2.setImageBitmap(bmp);
            }

            String op = " Original Price: " + productExtraction.getResults().get(0).getBrandName();
            String d = " Discount: " + productExtraction.getResults().get(0).getOriginalPrice();
            String cp = " Current price " + productExtraction.getResults().get(0).getPrice();
            getApp_binding().prod.originalPrice.setText(op);
            getApp_binding().prod.discout.setText(d);
            getApp_binding().prod.currentPrice.setText(cp);
            Log.i("Pavan's Check", "returned product details from async task!");

            fab_show = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show);
            fab_hide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide);
            fab_rotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
            fab_other = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_other);

            getApp_binding().fabCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isShowing) {
                        getApp_binding().fabAddCart.startAnimation(fab_hide);
                        getApp_binding().fabCart.startAnimation(fab_other);
                        getApp_binding().fabAddCart.setClickable(false);
                        isShowing = false;
                    } else {
                        getApp_binding().fabAddCart.startAnimation(fab_show);
                        getApp_binding().fabCart.startAnimation(fab_rotate);
                        getApp_binding().fabAddCart.setClickable(true);
                        isShowing = true;

                    }
                }
            });

            getApp_binding().fabAddCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(ProductActivity.this, NearByDevices.class));

                }
            });

        }


    }
}
