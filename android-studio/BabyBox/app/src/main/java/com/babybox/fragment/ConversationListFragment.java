package com.babybox.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.babybox.R;
import com.babybox.activity.MessageListActivity;
import com.babybox.adapter.ConversationListAdapter;
import com.babybox.app.AppController;
import com.babybox.app.ConversationCache;
import com.babybox.app.TrackedFragment;
import com.babybox.util.DefaultValues;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.ConversationVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ConversationListFragment extends TrackedFragment {

    private static final String TAG = ConversationListFragment.class.getName();
    private ListView listView;
    private TextView tipText;
    private ConversationListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.conversation_list_fragment, container, false);

        tipText = (TextView) view.findViewById(R.id.tipText);
        listView = (ListView) view.findViewById(R.id.conversationList);

        getAllConversations();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ConversationVM item = adapter.getItem(i);
                ViewUtil.startMessageListActivityForResult(getActivity(), item.id);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final ConversationVM item = adapter.getItem(i);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setMessage(getActivity().getString(R.string.post_delete_confirm));
                alertDialogBuilder.setPositiveButton(getActivity().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteConversation(item.getId());
                    }
                });
                alertDialogBuilder.setNegativeButton(getActivity().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return true;
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        /*
        if (adapter != null) {
            Log.d(ConversationListFragment.class.getSimpleName(), "onStart");
            ConversationCache.sortConversations();
            adapter.notifyDataSetChanged();
        }
        */
    }

    @Override
    public void onResume() {
        super.onResume();

        /*
        if (adapter != null) {
            Log.d(ConversationListFragment.class.getSimpleName(), "onResume");
            ConversationCache.sortConversations();
            adapter.notifyDataSetChanged();
        }
        */
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(this.getClass().getSimpleName(), "onActivityResult: requestCode:" + requestCode + " resultCode:" + resultCode + " data:" + data);

        if (requestCode == ViewUtil.START_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK &&
                data != null && adapter != null) {
            Long conversationId = data.getLongExtra(ViewUtil.INTENT_VALUE_ID, -1L);

            Log.d(this.getClass().getSimpleName(), "onActivityResult: conversationId=" + conversationId);
            if (conversationId != -1L) {
                ConversationCache.update(conversationId, new Callback<ConversationVM>() {
                    @Override
                    public void success(ConversationVM conversation, Response response) {
                        adapter.notifyDataSetChanged();
                        ViewUtil.stopSpinner(getActivity());
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        ViewUtil.stopSpinner(getActivity());
                        Log.e(ConversationListFragment.class.getSimpleName(), "onActivityResult: failure", error);
                    }
                });
            }
        }
    }

    private void getAllConversations() {
        ViewUtil.showSpinner(getActivity());
        ConversationCache.refresh(new Callback<List<ConversationVM>>() {
            @Override
            public void success(List<ConversationVM> conversations, Response response) {
                Log.d(ConversationListFragment.class.getSimpleName(), "getAllConversations: success");
                if (conversations.size() == 0) {
                    tipText.setVisibility(View.VISIBLE);
                } else {
                    adapter = new ConversationListAdapter(getActivity(), ConversationCache.getConversations());
                    listView.setAdapter(adapter);
                }

                ViewUtil.stopSpinner(getActivity());
            }

            @Override
            public void failure(RetrofitError error) {
                ViewUtil.stopSpinner(getActivity());
                Log.e(ConversationListFragment.class.getSimpleName(), "getAllConversations: failure", error);
            }
        });
    }

    private void deleteConversation(final Long id) {
        ViewUtil.showSpinner(getActivity());
        ConversationCache.delete(id, new Callback<Response>() {
            @Override
            public void success(Response responseObject, Response response) {
                adapter.notifyDataSetChanged();
                ViewUtil.stopSpinner(getActivity());
            }

            @Override
            public void failure(RetrofitError error) {
                ViewUtil.stopSpinner(getActivity());
                Log.e(ConversationListFragment.class.getSimpleName(), "deleteConversation: failure", error);
            }
        });
    }
}
