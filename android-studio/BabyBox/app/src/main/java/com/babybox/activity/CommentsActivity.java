package com.babybox.activity;

import android.util.Log;
import android.widget.BaseAdapter;

import com.babybox.R;
import com.babybox.adapter.CommentListAdapter;
import com.babybox.app.AppController;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.CommentVM;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CommentsActivity extends AbstractListViewActivity {

    private List<CommentVM> items;

    @Override
    protected String getTitleText() {
        return getString(R.string.comments);
    }

    @Override
    protected String getNoItemText() {
        return getString(R.string.no_product_comments);
    }

    @Override
    protected BaseAdapter getListAdapter() {
        if (items == null) {
            items = new ArrayList<>();
        }
        return new CommentListAdapter(CommentsActivity.this, items);
    }

    @Override
    protected void loadListItems(final Long objId, final Long offset) {
        ViewUtil.showSpinner(this);

        AppController.getApiService().getComments(offset, objId, new Callback<List<CommentVM>>() {
            @Override
            public void success(List<CommentVM> comments, Response response) {
                Log.d(CommentsActivity.class.getSimpleName(), "loadListItems.getComments: offset=" + offset +
                        " size=" + (comments == null? 0 : comments.size()));

                if (offset == 0 && (comments == null || comments.size() == 0)) {
                    showNoItemText();
                    return;
                }

                if (comments != null && comments.size() > 0) {
                    items.addAll(comments);
                    listAdapter.notifyDataSetChanged();
                }

                ViewUtil.stopSpinner(CommentsActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {
                ViewUtil.stopSpinner(CommentsActivity.this);
                Log.e(CommentsActivity.class.getSimpleName(), "loadListItems.getComments: failure", error);
            }
        });
    }
}
