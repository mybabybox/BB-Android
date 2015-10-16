package com.babybox.activity;

import com.babybox.R;

public class SellerRulesActivity extends AbstractWebViewActivity {

    protected String getActionBarTitle() {
        return getString(R.string.seller_rules_title);
    }

    protected String getLoadUrl() {
        return SELLER_RULES_URL;
    }
}