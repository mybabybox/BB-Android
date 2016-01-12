package com.babybox.app;

import android.util.Log;

import com.babybox.viewmodel.ConversationOrderVM;
import com.babybox.viewmodel.ConversationVM;
import com.babybox.viewmodel.MessageVM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ConversationCache {

    private static List<ConversationVM> conversations = new ArrayList<>();

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

        AppController.getApiService().getConversations(new Callback<List<ConversationVM>>() {
            @Override
            public void success(List<ConversationVM> vms, Response response) {
                conversations = vms;
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
                ConversationVM toDelete = getConversation(id);
                if (toDelete != null) {
                    conversations.remove(toDelete);
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

    public static void update(final Long id, final Callback<ConversationVM> callback) {
        Log.d(ConversationCache.class.getSimpleName(), "update");

        AppController.getApiService().getConversation(id, new Callback<ConversationVM>() {
            @Override
            public void success(ConversationVM conversation, Response response) {
                ConversationVM toDelete = getConversation(id);
                if (toDelete != null) {
                    conversations.remove(toDelete);
                }

                conversations.add(conversation);
                ConversationCache.sortConversations();

                if (callback != null) {
                    callback.success(conversation, response);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (callback != null) {
                    callback.failure(error);
                }
                Log.e(ConversationCache.class.getSimpleName(), "update: failure", error);
            }
        });
    }

    public static List<ConversationVM> sortConversations() {
        Collections.sort(conversations, new Comparator<ConversationVM>() {
            public int compare(ConversationVM c1, ConversationVM c2) {
                return c2.getLastMessageDate().compareTo(c1.getLastMessageDate());  // reverse chron order
            }
        });
        return conversations;
    }

    public static List<ConversationVM> getConversations() {
        return conversations;
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

    public static ConversationVM updateConversationOrder(Long conversationId, ConversationOrderVM order) {
        ConversationVM conversation = getConversation(conversationId);
        if (conversation != null) {
            conversation.setOrder(order);
        }
        return conversation;
    }

    public static void clear() {
        conversations.clear();
        openedConversation = null;
    }
}
