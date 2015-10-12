package com.babybox.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.babybox.activity.CategoryActivity;
import com.babybox.util.ViewUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.babybox.R;
import com.babybox.adapter.RequestListAdapter;
import com.babybox.app.AppController;
import com.babybox.app.TrackedFragment;
import com.babybox.util.DefaultValues;
import com.babybox.util.UrlUtil;
import com.babybox.viewmodel.NotificationVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RequestListFragment extends TrackedFragment {

    private static final String TAG = RequestListFragment.class.getName();
    private RequestListAdapter adapter;
    private ListView listView;
    private TextView tipText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.request_list_fragment, container, false);

        listView = (ListView) view.findViewById(R.id.listRequest);
        tipText = (TextView) view.findViewById(R.id.tipText);

        String notif = getArguments().getString(ViewUtil.BUNDLE_KEY_LISTS);

        StringBuilder ids = new StringBuilder();

        Gson gson = new GsonBuilder().create();
        List<NotificationVM> notificationVMs = new ArrayList<>();
        JSONArray jsonArray1 = null;
        try {
            jsonArray1 = new JSONArray(notif);
            for (int i = 0; i < jsonArray1.length(); i++) {
                JSONObject json_data = jsonArray1.getJSONObject(i);
                NotificationVM vm = gson.fromJson(json_data.toString(), NotificationVM.class);
                notificationVMs.add(vm);

                if (vm.getSta()==0) {
                    if (i != 0) {
                        ids.append(",");
                    }
                    ids.append(vm.getNid());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (ids.length() != 0) {
            markAsRead();
        }

        if (notificationVMs.size() == 0) {
            tipText.setVisibility(View.VISIBLE);
        } else {
            adapter = new RequestListAdapter(getActivity(), notificationVMs);
            listView.setAdapter(adapter);
        }

        listView.setFriction(ViewConfiguration.getScrollFriction() *
                DefaultValues.LISTVIEW_SCROLL_FRICTION_SCALE_FACTOR);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NotificationVM item = adapter.getItem(position);
                if (item != null) {
                    /*
                    Log.d(RequestListFragment.this.getClass().getSimpleName(), "Status:" + item.getSta());
                    Log.d(RequestListFragment.this.getClass().getSimpleName(), "Msg:"+item.getMsg());
                    Log.d(RequestListFragment.this.getClass().getSimpleName(), "Type:"+item.getTp());
                    Log.d(RequestListFragment.this.getClass().getSimpleName(), "Update:"+item.getUpd());
                    Log.d(RequestListFragment.this.getClass().getSimpleName(), "OnClick:"+item.getUrl().getOnClick());
                    Log.d(RequestListFragment.this.getClass().getSimpleName(), "actor:"+item.getUrl().getActor());
                    Log.d(RequestListFragment.this.getClass().getSimpleName(), "target:"+item.getUrl().getTarget());
                    */

                    if (item.getTp().equals("COMM_JOIN_APPROVED")) {
                        try {
                            long commId = UrlUtil.parseCategoryUrlId(item.getUrl().getOnClick());
                            Log.d(RequestListFragment.this.getClass().getSimpleName(), "click request: commId="+commId);
                            ViewUtil.startCategoryActivity(getActivity(), commId, "FromRequest");
                        } catch (Exception e) {
                            Log.e(RequestListFragment.this.getClass().getSimpleName(), "Failed to parse comm id from url", e);
                        }
                    }
                }
            }
        });

        return view;
    }

    private void markAsRead(){
        AppController.getApiService().readActivities(new Callback<Response>() {
            @Override
            public void success(Response responseObject, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(RequestListFragment.class.getSimpleName(), "markAsRead: failure", error);
            }
        });
    }
}
