package com.babybox.activity;

import com.babybox.R;

public class TermsActivity extends AbstractWebViewActivity {

    protected String getActionBarTitle() {
        return getString(R.string.signup_terms);
    }

    protected String getLoadUrl() {
        return TERMS_URL;
    }
}