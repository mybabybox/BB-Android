package com.babybox.app;

import android.util.Log;

import com.babybox.viewmodel.ConversationVM;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ConversationCache {

    private static SortedSet<ConversationVM> conversations = new TreeSet<ConversationVM>() {
    };

    private static ConversationVM openedConversation;

    private ConversationCache() {}

    static {
        init();
    }

    private static void init() {
    }

    public static void refresh() {
        refresh(null);
    }

    public static void refresh(final Callback<List<ConversationVM>> callback) {
        Log.d(ConversationCache.class.getSimpleName(), "refresh");

        AppController.getApiService().getAllConversations(new Callback<List<ConversationVM>>() {
            @Override
            public void success(List<ConversationVM> vms, Response response) {
                conversations.addAll(vms);

                if (callback != null) {
                    callback.success(vms, response);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (callback != null) {
                    callback.failure(error);
                }
                Log.e(ConversationCache.class.getSimpleName(), "refresh: failure", error);
            }
        });
    }

    public static void open(final Long postId, final Callback<ConversationVM> callback) {
        Log.d(ConversationCache.class.getSimpleName(), "open: post="+postId);

        AppController.getApiService().openConversation(postId, new Callback<ConversationVM>() {
            @Override
            public void success(ConversationVM conversationVM, Response response) {
                openedConversation = conversationVM;

                if (!conversations.contains(conversationVM)) {
                    conversations.add(conversationVM);
                }

                if (callback != null) {
                    callback.success(conversationVM, response);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (callback != null) {
                    callback.failure(error);
                }
                Log.e(ConversationCache.class.getSimpleName(), "open: failure", error);
            }
        });
    }

    public static void delete(final Long id, final Callback<Response> callback) {
        Log.d(ConversationCache.class.getSimpleName(), "delete");

        AppController.getApiService().deleteConversation(id, new Callback<Response>() {
            @Override
            public void success(Response responseObject, Response response) {
                for (ConversationVM conversation : conversations) {
                    if (conversation.id == id) {
                        conversations.remove(conversation);
                    }
                }

                if (callback != null) {
                    callback.success(responseObject, response);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (callback != null) {
                    callback.failure(error);
                }
                Log.e(ConversationCache.class.getSimpleName(), "delete: failure", error);
            }
        });
    }

    public static List<ConversationVM> getConversations() {
        return new ArrayList(conversations);
    }

    public static ConversationVM getConversation(Long id) {
        for (ConversationVM conversation : ConversationCache.getConversations()) {
            if (conversation.getId().equals(id)) {
                return conversation;
            }
        }
        return null;
    }

    public static ConversationVM getOpenedConversation() {
        return openedConversation;
    }

    public static ConversationVM getOpenedConversation(Long id) {
        ConversationVM conversation = getConversation(id);
        openedConversation = conversation;
        return openedConversation;
    }

    public static void clear() {
        conversations.clear();
        openedConversation = null;
    }
}
