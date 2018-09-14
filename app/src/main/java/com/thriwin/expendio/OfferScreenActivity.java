package com.thriwin.expendio;

import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;

public class OfferScreenActivity extends GeneralActivity {
    AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.offer_screen);
        super.onCreate(savedInstanceState);

        mAdView = findViewById(R.id.adView);
        AdRequest.Builder builder = new AdRequest.Builder();
        builder.addKeyword("grocery");

        AdRequest adRequest = builder.build();

        mAdView.loadAd(adRequest);
    }


}
