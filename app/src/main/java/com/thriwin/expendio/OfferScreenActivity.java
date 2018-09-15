package com.thriwin.expendio;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.LinearLayout;

import com.facebook.ads.AdSize;

import java.util.List;
import java.util.Random;

import static java.util.Arrays.asList;

public class OfferScreenActivity extends GeneralActivity {

    private List<AdSize> adSizes = asList(AdSize.BANNER_HEIGHT_90, AdSize.BANNER_HEIGHT_50, AdSize.RECTANGLE_HEIGHT_250);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.offer_screen);
        super.onCreate(savedInstanceState);


        refreshOffers();

        SwipeRefreshLayout swipeForChangeOffer = findViewById(R.id.swipeForMoreAdd);
        swipeForChangeOffer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((LinearLayout) findViewById(R.id.banner_container)).removeAllViews();
                refreshOffers();
                swipeForChangeOffer.setRefreshing(false);
            }
        });

    }

    private void refreshOffers() {
        String placementId1Facebook = "461566017681170_461566217681150";
        String placementId2Facebook = "461566017681170_461773714327067";
        String placementId3Facebook = "461566017681170_461776120993493";
        String placementId4Facebook = "461566017681170_461783124326126";
        String placementId5Facebook = "461566017681170_461783280992777";
        adAnOffer(placementId1Facebook, adSizes.get(getRandomAdSize()));
        adAnOffer(placementId2Facebook, adSizes.get(getRandomAdSize()));
        adAnOffer(placementId3Facebook, adSizes.get(getRandomAdSize()));
        adAnOffer(placementId4Facebook, adSizes.get(getRandomAdSize()));
        adAnOffer(placementId5Facebook, adSizes.get(getRandomAdSize()));
    }

    private int getRandomAdSize() {
        Random random = new Random();
        return random.nextInt(3);
    }


}
