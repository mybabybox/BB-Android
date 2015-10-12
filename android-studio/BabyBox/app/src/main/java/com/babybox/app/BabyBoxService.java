package com.babybox.app;

import com.babybox.viewmodel.CategoryVM;
import com.babybox.viewmodel.CollectionVM;
import com.babybox.viewmodel.CommentVM;
import com.babybox.viewmodel.ConversationVM;
import com.babybox.viewmodel.EmoticonVM;
import com.babybox.viewmodel.LocationVM;
import com.babybox.viewmodel.MessageVM;
import com.babybox.viewmodel.NewCommentVM;
import com.babybox.viewmodel.NewMessageVM;
import com.babybox.viewmodel.NewPostVM;
import com.babybox.viewmodel.PostVM;
import com.babybox.viewmodel.PostVMLite;
import com.babybox.viewmodel.ResponseStatusVM;
import com.babybox.viewmodel.UserProfileDataVM;
import com.babybox.viewmodel.UserVM;
import com.babybox.viewmodel.UserVMLite;

import java.util.List;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class BabyBoxService {

    protected BabyBoxApi api;

    public BabyBoxService(BabyBoxApi api) {
        this.api = api;
    }

    public void signUp(String lname, String fname, String email, String password, String repeatPassword, Callback<Response> cb) {
        api.signUp(lname, fname, email, password, repeatPassword, cb);
    }

    public void signUpInfo(String parent_displayname, Integer parent_birth_year, Integer parent_location, String parent_type,
                           String num_children, String bb_gender1, String bb_gender2, String bb_gender3, String bb_birth_year1,
                           String bb_birth_month1, String bb_birth_day1, String bb_birth_year2, String bb_birth_month2,
                           String bb_birth_day2, String bb_birth_year3, String bb_birth_month3, String bb_birth_day3,
                           Callback<Response> cb) {
        api.signUpInfo(parent_displayname, parent_birth_year, parent_location, parent_type,
                num_children, bb_gender1, bb_gender2, bb_gender3, bb_birth_year1,
                bb_birth_month1, bb_birth_day1, bb_birth_year2, bb_birth_month2,
                bb_birth_day2, bb_birth_year3, bb_birth_month3, bb_birth_day3,
                AppController.getInstance().getSessionId(), cb);
    }

    public void getAllDistricts(Callback<List<LocationVM>> cb) {
        api.getAllDistricts(AppController.getInstance().getSessionId(), cb);
    }

    public void getEmoticons(Callback<List<EmoticonVM>> cb) {
        api.getEmoticons(AppController.getInstance().getSessionId(), cb);
    }

    public void login(String email, String password, Callback<Response> cb) {
        api.login(email, password, cb);
    }

    public void loginByFacebook(String access_token, Callback<Response> cb) {
        api.loginByFacebook(access_token, cb);
    }

    public void getUser(String access_token, Callback<UserVM> cb) {
        api.getUserInfo(access_token, cb);
    }

    public void getUser(Long id, Callback<UserVM> cb) {
        api.getUser(id, AppController.getInstance().getSessionId(), cb);
    }

    public void initNewUser(Callback<UserVM> cb) {
        api.initNewUser(AppController.getInstance().getSessionId(), cb);
    }

    public void getAllProducts(Callback<List<PostVMLite>> cb){
        api.getAllProducts(AppController.getInstance().getSessionId(), cb);
    }

    // home feeds

    public void getHomeExploreFeed(Long offset, Callback<List<PostVMLite>> cb) {
        api.getHomeExploreFeed(offset, AppController.getInstance().getSessionId(), cb);
    }

    public void getHomeFollowingFeed(Long offset, Callback<List<PostVMLite>> cb) {
        api.getHomeFollowingFeed(offset, AppController.getInstance().getSessionId(), cb);
    }

    // category feeds

    public void getCategoryPopularFeed(Long offset, Long id, String productType, Callback<List<PostVMLite>> cb) {
        api.getCategoryPopularFeed(offset, id, productType, AppController.getInstance().getSessionId(), cb);
    }

    public void getCategoryNewestFeed(Long offset, Long id, String productType, Callback<List<PostVMLite>> cb) {
        api.getCategoryNewestFeed(offset, id, productType, AppController.getInstance().getSessionId(), cb);
    }

    public void getCategoryPriceLowHighFeed(Long offset, Long id, String productType, Callback<List<PostVMLite>> cb) {
        api.getCategoryPriceLowHighFeed(offset, id, productType, AppController.getInstance().getSessionId(), cb);
    }

    public void getCategoryPriceHighLowFeed(Long offset, Long id, String productType, Callback<List<PostVMLite>> cb) {
        api.getCategoryPriceHighLowFeed(offset, id, productType, AppController.getInstance().getSessionId(), cb);
    }

    // user feeds

    public void getUserPostedFeed(Long offset, Long id, Callback<List<PostVMLite>> cb) {
        api.getUserPostedFeed(offset, id, AppController.getInstance().getSessionId(), cb);
    }

    public void getUserLikedFeed(Long offset, Long id, Callback<List<PostVMLite>> cb) {
        api.getUserLikedFeed(offset, id, AppController.getInstance().getSessionId(), cb);
    }

    public void getUserCollectionFeed(Long offset, Long collectionId, Callback<List<PostVMLite>> cb) {
        api.getUserCollectionFeed(offset, collectionId, AppController.getInstance().getSessionId(), cb);
    }

    public void getFollowers(Long offset, Long userId, Callback<List<UserVMLite>> cb) {
        api.getFollowers(offset, userId, AppController.getInstance().getSessionId(), cb);
    }

    public void getFollowings(Long offset, Long userId, Callback<List<UserVMLite>> cb) {
        api.getFollowings(offset, userId, AppController.getInstance().getSessionId(), cb);
    }

    // category + post + comments

    public void getCategories(Callback<List<CategoryVM>> cb) {
        api.getCategories(AppController.getInstance().getSessionId(), cb);
    }

    public void getCategory(Long id, Callback<CategoryVM> cb) {
        api.getCategory(id, AppController.getInstance().getSessionId(), cb);
    }

    public void getPost(Long id, Callback<PostVM> cb) {
        api.getPost(id, AppController.getInstance().getSessionId(), cb);
    }

    public void newPost(NewPostVM post, Callback<ResponseStatusVM> cb) {
        api.newPost(post.toMultipart(), AppController.getInstance().getSessionId(), cb);
    }

    public void deletePost(Long id, Callback<Response> cb) {
        api.deletePost(id, AppController.getInstance().getSessionId(), cb);
    }

    public void getComments(Long offset, Long postId, Callback<List<CommentVM>> cb) {
        api.getComments(postId, offset, AppController.getInstance().getSessionId(), cb);
    }

    public void newComment(NewCommentVM comment, Callback<ResponseStatusVM> cb) {
        api.newComment(comment, AppController.getInstance().getSessionId(), cb);
    }

    public void deleteComment(Long id, Callback<Response> cb) {
        api.deleteComment(id, AppController.getInstance().getSessionId(), cb);
    }

    public void followUser(Long id, Callback<Response> cb) {
        api.followUser(id, AppController.getInstance().getSessionId(), cb);
    }

    public void unfollowUser(Long id, Callback<Response> cb) {
        api.unfollowUser(id, AppController.getInstance().getSessionId(), cb);
    }

    public void likePost(Long id, Callback<Response> cb) {
        api.likePost(id, AppController.getInstance().getSessionId(), cb);
    }

    public void unlikePost(Long id, Callback<Response> cb) {
        api.unlikePost(id, AppController.getInstance().getSessionId(), cb);
    }

    public void soldPost(Long id, Callback<Response> cb) {
        api.soldPost(id, AppController.getInstance().getSessionId(), cb);
    }

    public void getSuggestedPosts(Long id, Callback<List<PostVMLite>> cb) {
        // TODO: TEMP!!!
        api.getUserLikedFeed(0L, UserInfoCache.getUser().id, AppController.getInstance().getSessionId(), cb);
    }

    // user profile

    public void uploadCoverPhoto(TypedFile photo, Callback<Response> cb) {
        api.uploadCoverPhoto(photo, AppController.getInstance().getSessionId(), cb);
    }

    public void uploadProfilePhoto(TypedFile photo, Callback<Response> cb) {
        api.uploadProfilePhoto(photo, AppController.getInstance().getSessionId(), cb);
    }

    public void updateUserProfileData(UserProfileDataVM userProfileDataVM, Callback<UserVM> cb) {
        api.updateUserProfileData(userProfileDataVM, AppController.getInstance().getSessionId(), cb);
    }

    public void getCollections(Long userId, Callback<List<CollectionVM>> cb) {
        api.getCollections(userId, AppController.getInstance().getSessionId(), cb);
    }

    public void getCollection(Long id, Callback<CollectionVM> cb) {
        api.getCollection(id, AppController.getInstance().getSessionId(), cb);
    }

    /*
    public void getNotificationCounts(Callback<NotificationCountVM> cb) {
        api.getNotificationCounts(AppController.getInstance().getSessionId(), cb);
    }

    public void getActivites(Callback<List<ActivityVM>> cb) {
        api.getActivities(AppController.getInstance().getSessionId(), cb);
    }
    */

    public void readActivities(Callback<Response> cb) {
        api.readActivities(AppController.getInstance().getSessionId(), cb);
    }

    // conversation

    public void getAllConversations(Callback<List<ConversationVM>> cb) {
        api.getAllConversations(AppController.getInstance().getSessionId(), cb);
    }

    public void getPostConversations(Long id, Callback<List<ConversationVM>> cb) {
        api.getPostConversations(id, AppController.getInstance().getSessionId(), cb);
    }

    public void getConversation(Long id, Callback<ConversationVM> cb) {
        api.getConversation(id, AppController.getInstance().getSessionId(), cb);
    }

    public void getMessages(Long conversationId, Long offset, Callback<Response> cb) {
        api.getMessages(conversationId, offset, AppController.getInstance().getSessionId(), cb);
    }

    public void openConversation(Long postId, Callback<ConversationVM> cb) {
        api.openConversation(postId, AppController.getInstance().getSessionId(), cb);
    }

    public void deleteConversation(Long id, Callback<Response> cb) {
        api.deleteConversation(id, AppController.getInstance().getSessionId(), cb);
    }

    public void newMessage(NewMessageVM message, Callback<MessageVM> cb) {
        api.newMessage(message.toMultipart(), AppController.getInstance().getSessionId(), cb);
    }

    public void getUnreadMessageCount(Callback<MessageVM> cb) {
        api.getUnreadMessageCount(AppController.getInstance().getSessionId(), cb);
    }

    public void getMessageImage(long id, Callback<MessageVM> cb) {
        api.getMessageImage(AppController.getInstance().getSessionId(), id, cb);
    }

    public void uploadMessagePhoto(Long id, TypedFile photo, Callback<Response> cb) {
        api.uploadMessagePhoto(AppController.getInstance().getSessionId(), id, photo, cb);
    }
}


