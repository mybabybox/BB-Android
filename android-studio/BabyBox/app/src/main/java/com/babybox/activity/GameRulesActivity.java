package com.babybox.activity;

import com.babybox.R;

public class GameRulesActivity extends AbstractWebViewActivity {

    protected String getActionBarTitle() {
        return getString(R.string.game_rules_title);
    }

    protected String getLoadUrl() {
        return GAME_RULES_URL;
    }
}