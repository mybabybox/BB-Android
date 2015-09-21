package com.babybox.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.babybox.R;
import com.babybox.app.TrackedFragmentActivity;
import com.babybox.listener.InfiniteScrollListener;
import com.babybox.util.DefaultValues;
import com.babybox.util.ViewUtil;

public abstract class AbstractListViewActivity extends TrackedFragmentActivity {

    protected ImageView backImage;
    protected TextView titleText;
    protected TextView noItemText;
    protected ListView listView;
    protected BaseAdapter listAdapter;

    abstract protected String getTitleText();

    abstract protected String getNoItemText();

    abstract protected BaseAdapter getListAdapter();

    abstract protected void loadListItems(Long objId, int offset);

    protected void showNoItemText() {
        noItemText.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.child_list_view);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(getLayoutInflater().inflate(R.layout.view_actionbar, null),
                new ActionBar.LayoutParams(
                        ActionBar.LayoutParams.MATCH_PARENT,
                        ActionBar.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER
                )
        );

        titleText = (TextView) findViewById(R.id.titleText);
        noItemText = (TextView) findViewById(R.id.noItemText);
        listView = (ListView) findViewById(R.id.listView);

        titleText.setText(getTitleText());
        noItemText.setText(getNoItemText());

        backImage = (ImageView) this.findViewById(R.id.backImage);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // list
        listAdapter = getListAdapter();
        listView.setAdapter(listAdapter);

        final Long objId = getIntent().getLongExtra(ViewUtil.BUNDLE_KEY_ID, 0L);
        listView.setOnScrollListener(new InfiniteScrollListener(
                DefaultValues.DEFAULT_INFINITE_SCROLL_VISIBLE_THRESHOLD, false, false) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadListItems(objId, page - 1);
            }
        });

        loadListItems(objId, 0);
    }
}
