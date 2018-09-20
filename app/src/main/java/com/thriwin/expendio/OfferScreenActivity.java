package com.thriwin.expendio;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.LinearLayout;

import com.facebook.ads.AdSize;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static java.util.Arrays.asList;

public class OfferScreenActivity extends GeneralActivity {

    String placementId1Facebook = "461566017681170_461566217681150";
    String placementId2Facebook = "461566017681170_461773714327067";
    String placementId3Facebook = "461566017681170_461776120993493";
    String placementId4Facebook = "461566017681170_461783124326126";
    String placementId5Facebook = "461566017681170_461783280992777";

    HashMap<String, AdSize> placementAndAdSize = new HashMap<String, AdSize>() {{
        put(placementId1Facebook, AdSize.RECTANGLE_HEIGHT_250);
        put(placementId2Facebook, AdSize.RECTANGLE_HEIGHT_250);
        put(placementId3Facebook, AdSize.RECTANGLE_HEIGHT_250);
        put(placementId4Facebook, AdSize.RECTANGLE_HEIGHT_250);
        put(placementId5Facebook, AdSize.RECTANGLE_HEIGHT_250);
    }};

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

        for (Map.Entry<String, AdSize> stringAdSizeEntry : placementAndAdSize.entrySet()) {
            adAnOffer(stringAdSizeEntry.getKey(), stringAdSizeEntry.getValue());
        }
    }


}
