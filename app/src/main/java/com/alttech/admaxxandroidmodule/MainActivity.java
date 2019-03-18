package com.alttech.admaxxandroidmodule;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.alttech.admaxx.Ad;
import com.alttech.admaxx.AdMaxx;
import com.alttech.admaxx.BannerView;
import com.alttech.admaxx.CallBack;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // 1 - use this to load the banner image. It extend image view so you can manipulate is accordingly
    BannerView bannerView = findViewById(R.id.banner);

    // 2 - Initials AdMaxx with the partner id
    AdMaxx.init("5a9c2a46e342fc56ad8efe10");

    // 3 - Add callbacks any where in you app where you will need to receive when and ad is supplied and load the image inside the call back
    AdMaxx.get().addCallback(new CallBack() {
      @Override
      public void onSuccess(Ad ad) {
        Picasso.get().load(ad.getCompanionAd().getStaticResource().getCreative()).into(bannerView);

      }

      @Override
      public void onFailure(Throwable t) {

      }

    });

    // 4 - Request Ad
    AdMaxx.get().requestAd();

    // 5 - once and ad has finished playing register this impression.
    AdMaxx.get().registerImpression();

    //6 - call request ad anytime you need an ad
  }
}
