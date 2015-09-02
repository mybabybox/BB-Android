package com.babybox.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import com.babybox.R;
import com.babybox.adapter.TopicCommunityListAdapter;
import com.babybox.app.LocalCommunityTabCache;
import com.babybox.app.TrackedFragment;
import com.babybox.util.DefaultValues;
import com.babybox.viewmodel.CommunitiesWidgetChildVM;

public class TopicCommunityFragment extends TrackedFragment {

    private ListView listView;
    private TopicCommunityListAdapter topicAdapter;
    private List<CommunitiesWidgetChildVM> communities;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.topic_community_fragment, container, false);

        listView = (ListView) rootView.findViewById(R.id.listTopic);

        // Tricky... listview header and footer dividers need to add in code...
        listView.addHeaderView(new View(getActivity().getBaseContext()), null, true);
        listView.addFooterView(new View(getActivity().getBaseContext()), null, true);

        topicAdapter = new TopicCommunityListAdapter(getActivity(), this.communities);
        listView.setAdapter(topicAdapter);
        listView.setFriction(ViewConfiguration.getScrollFriction() *
                DefaultValues.LISTVIEW_SCROLL_FRICTION_SCALE_FACTOR);

        /*
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String noPost = "-";

                position = position - 1;    // offset by header
                CommunitiesWidgetChildVM childVM = topicAdapter.getItem(position);
                if (childVM != null) {
                    Log.d(this.getClass().getSimpleName(), "onCreateView: listView.onItemClick with commId - " + childVM.getId());
                    Intent intent = new Intent(getActivity(), CommunityActivity.class);
                    intent.putExtra(ViewUtil.BUNDLE_KEY_ID, childVM.getId());
                    intent.putExtra(ViewUtil.BUNDLE_KEY_SOURCE, "FromTopicFragment");
                    startActivity(intent);
                }
            }
        });
        */

        LocalCommunityTabCache.addTopicCommunityFragment(this);

        return rootView;
    }

    public void notifyChange() {
        topicAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public List<CommunitiesWidgetChildVM> getCommunities() {
        return communities;
    }

    public void setCommunities(List<CommunitiesWidgetChildVM> communities) {
        this.communities = communities;
    }
}
