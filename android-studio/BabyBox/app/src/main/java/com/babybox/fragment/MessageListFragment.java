package com.babybox.fragment;

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
import com.babybox.activity.MessageDetailActivity;
import com.babybox.adapter.ConversationListAdapter;
import com.babybox.app.AppController;
import com.babybox.app.TrackedFragment;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.ConversationVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MessageListFragment extends TrackedFragment {

    private static final String TAG = MessageListFragment.class.getName();
    private ListView listView;
    private TextView tipText;
    private ConversationListAdapter adapter;
    private List<ConversationVM> conversationVMList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.message_list_fragment, container, false);

        tipText = (TextView) view.findViewById(R.id.tipText);
        listView = (ListView) view.findViewById(R.id.conversationList);

        getAllConversations();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ConversationVM item = adapter.getItem(i);
                Intent intent = new Intent(getActivity(), MessageDetailActivity.class);
                intent.putExtra("user_name",item.getNm());
                intent.putExtra("uid",item.getUid());
                intent.putExtra(ViewUtil.BUNDLE_KEY_ID,item.getId());

                AppController.getInstance().setConversationId(item.getId());
                startActivity(intent);
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
        getAllConversations();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void getAllConversations() {
        ViewUtil.showSpinner(getActivity());
        AppController.getMockApi().getAllConversations(AppController.getInstance().getSessionId(), new Callback<List<ConversationVM>>() {
            @Override
            public void success(List<ConversationVM> conversationVMs, Response response) {
                conversationVMList = conversationVMs;

                if (conversationVMList.size() == 0) {
                    tipText.setVisibility(View.VISIBLE);
                } else {
                    adapter = new ConversationListAdapter(getActivity(), conversationVMList);
                    listView.setAdapter(adapter);
                }

                ViewUtil.stopSpinner(getActivity());
            }

            @Override
            public void failure(RetrofitError error) {
                ViewUtil.stopSpinner(getActivity());
                Log.e(MessageListFragment.class.getSimpleName(), "getAllConversations: failure", error);
            }
        });
    }

    private void deleteConversation(Long id) {
        ViewUtil.showSpinner(getActivity());
        AppController.getMockApi().deleteConversation(id,AppController.getInstance().getSessionId(),new Callback<Response>() {
            @Override
            public void success(Response response, Response response1) {
                getAllConversations();
                ViewUtil.stopSpinner(getActivity());
            }

            @Override
            public void failure(RetrofitError error) {
                ViewUtil.stopSpinner(getActivity());
                Log.e(MessageListFragment.class.getSimpleName(), "deleteConversation: failure", error);
            }
        });
    }
}
