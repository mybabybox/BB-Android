package com.babybox.activity;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.babybox.R;
import com.babybox.adapter.GameBadgesViewAdapter;
import com.babybox.app.AppController;
import com.babybox.app.TrackedFragmentActivity;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.GameBadgeVM;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GameBadgesActivity extends TrackedFragmentActivity {
    private static final String TAG = GameBadgesActivity.class.getName();

    public static int RECYCLER_VIEW_COLUMN_SIZE = 4;

    private static final int SIDE_MARGIN = ViewUtil.getRealDimension(10);

    private RecyclerView badgesView;
    private GameBadgesViewAdapter badgesAdapter;
    private GridLayoutManager layoutManager;
    private View headerView;
    private ImageView backImage;

    private Long userId;
    private List<GameBadgeVM> items;

    public boolean hasHeader() {
        return headerView != null;
    }

    private View getHeaderView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.game_badges_view_header, null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.game_badges_activity);

        badgesView = (RecyclerView) findViewById(R.id.gameBadgesView);
        badgesView.addItemDecoration(
                new RecyclerView.ItemDecoration() {
                    @Override
                    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                        ViewUtil.FeedItemPosition feedItemPosition =
                                ViewUtil.getFeedItemPosition(view, RECYCLER_VIEW_COLUMN_SIZE, hasHeader());
                        if (feedItemPosition == ViewUtil.FeedItemPosition.HEADER) {
                            outRect.set(0, 0, 0, 0);
                        }
                        /*
                        else if (feedItemPosition == ViewUtil.FeedItemPosition.LEFT_COLUMN) {
                            outRect.set(outRect.left + SIDE_MARGIN, outRect.top, outRect.right, outRect.bottom);
                        } else if (feedItemPosition == ViewUtil.FeedItemPosition.RIGHT_COLUMN) {
                            outRect.set(outRect.left, outRect.top, outRect.right + SIDE_MARGIN, outRect.bottom);
                        } else {
                            outRect.set(outRect.left, outRect.top, outRect.right, outRect.bottom);
                        }
                        */
                    }
                });

        // header
        headerView = getHeaderView(getLayoutInflater());

        // adapter
        items = new ArrayList<>();
        badgesAdapter = new GameBadgesViewAdapter(this, items, headerView);
        badgesView.setAdapter(badgesAdapter);

        // layout manager
        layoutManager = new GridLayoutManager(this, RECYCLER_VIEW_COLUMN_SIZE);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return badgesAdapter.isHeader(position) ? layoutManager.getSpanCount() : 1;
            }
        });
        badgesView.setLayoutManager(layoutManager);

        backImage = (ImageView) this.findViewById(R.id.backImage);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        userId = getIntent().getLongExtra(ViewUtil.BUNDLE_KEY_ID, 0L);

        loadBadges(userId);
    }

    private void loadBadges(Long userId) {
        ViewUtil.showSpinner(this);

        AppController.getApiService().getGameBadges(userId, new Callback<List<GameBadgeVM>>() {
            @Override
            public void success(final List<GameBadgeVM> badges, Response response) {
                items.clear();
                items.addAll(badges);
                badgesAdapter.notifyDataSetChanged();

                ViewUtil.stopSpinner(GameBadgesActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {
                ViewUtil.stopSpinner(GameBadgesActivity.this);
                Log.e(ProductActivity.class.getSimpleName(), "loadBadges: failure", error);
            }
        });
    }
}


