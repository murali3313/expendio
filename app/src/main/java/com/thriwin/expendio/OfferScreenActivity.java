package com.thriwin.expendio;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.LinearLayout;

import java.util.List;

import static java.util.Arrays.asList;

public class OfferScreenActivity extends GeneralActivity {

    String placementId1Facebook = "461566017681170_461566217681150";
    String placementId2Facebook = "461566017681170_461773714327067";
    String placementId3Facebook = "461566017681170_461776120993493";
    String placementId4Facebook = "461566017681170_461783124326126";
    String placementId5Facebook = "461566017681170_461783280992777";

    String googleAdUnitId1 = "ca-app-pub-8899454204921425/4906816055";
    String googleAdUnitId2 = "ca-app-pub-8899454204921425/6028326033";
    String googleAdUnitId3 = "ca-app-pub-8899454204921425/4514814258";
    String googleAdUnitId4 = "ca-app-pub-8899454204921425/6958264323";
    String googleAdUnitId5 = "ca-app-pub-8899454204921425/8079774307";

    List<String> googleAdUnitIds = asList(googleAdUnitId1, googleAdUnitId2, googleAdUnitId3, googleAdUnitId4, googleAdUnitId5);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.offer_screen);
        super.onCreate(savedInstanceState);

        refreshOffers();

        SwipeRefreshLayout swipeForChangeOffer = (SwipeRefreshLayout) findViewById(R.id.swipeForMoreAdd);
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

        for (String googleAdUnit : googleAdUnitIds) {
            addAdMobOffer(googleAdUnit, com.google.android.gms.ads.AdSize.MEDIUM_RECTANGLE, getKeyWordsForGoogle());
        }
    }


}
