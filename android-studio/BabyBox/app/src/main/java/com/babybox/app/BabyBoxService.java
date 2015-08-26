package com.babybox.app;

import android.util.Log;

import com.babybox.viewmodel.BookmarkSummaryVM;
import com.babybox.viewmodel.CategoryVM;
import com.babybox.viewmodel.CommentPost;
import com.babybox.viewmodel.CommentResponse;
import com.babybox.viewmodel.CommunitiesParentVM;
import com.babybox.viewmodel.CommunitiesWidgetChildVM;
import com.babybox.viewmodel.CommunityCategoryMapVM;
import com.babybox.viewmodel.CommunityPostCommentVM;
import com.babybox.viewmodel.CommunityPostVM;
import com.babybox.viewmodel.CommunityVM;
import com.babybox.viewmodel.ConversationVM;
import com.babybox.viewmodel.EmoticonVM;
import com.babybox.viewmodel.GameAccountVM;
import com.babybox.viewmodel.GameGiftVM;
import com.babybox.viewmodel.GameTransactionVM;
import com.babybox.viewmodel.LocationVM;
import com.babybox.viewmodel.MessagePostVM;
import com.babybox.viewmodel.MessageVM;
import com.babybox.viewmodel.NewPost;
import com.babybox.viewmodel.NotificationsParentVM;
import com.babybox.viewmodel.PostArray;
import com.babybox.viewmodel.PostResponse;
import com.babybox.viewmodel.ProfileVM;
import com.babybox.viewmodel.ResponseStatusVM;
import com.babybox.viewmodel.UserProfileDataVM;
import com.babybox.viewmodel.UserVM;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class BabyBoxService {

    private BabyBoxApi api;

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

    public void login(String email, String password, Callback<Response> cb) {
        api.login(email, password, cb);
    }

    public void loginByFacebook(String access_token, Callback<Response> cb) {
        api.loginByFacebook(access_token, cb);
    }

    public void initNewUser(Callback<UserVM> cb) {
        api.initNewUser(AppController.getInstance().getSessionId(), cb);
    }

    public void getUserInfo(Callback<UserVM> cb) {
        api.getUserInfo(AppController.getInstance().getSessionId(), cb);
    }

    public void getNewsfeed(Long offset, Callback<PostArray> cb) {
        api.getNewsfeed(offset, AppController.getInstance().getSessionId(), cb);
    }

    public void getCategories(Callback<List<CategoryVM>> cb) {
        // TEMP - for api testing
        final Callback<List<CategoryVM>> callback = cb;
        api.getMyCommunities(AppController.getInstance().getSessionId(), new Callback<CommunitiesParentVM>() {
            @Override
            public void success(CommunitiesParentVM communitiesParentVM, Response response) {
                List<CategoryVM> categories = new ArrayList<CategoryVM>();
                for (CommunitiesWidgetChildVM comm : communitiesParentVM.getCommunities()) {
                    categories.add(new CategoryVM(comm));
                }
                callback.success(categories, response);

            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });

    }

    public void getCommunity(Long comm_id, Callback<CommunityVM> cb) {
        api.getCommunity(comm_id, AppController.getInstance().getSessionId(), cb);
    }

    public void getCommunityInitialPosts(Long id, Callback<PostArray> cb) {
        api.getCommunityInitialPosts(id, AppController.getInstance().getSessionId(), cb);
    }

    public void getCommunityNextPosts(Long id, String time, Callback<List<CommunityPostVM>> cb) {
        api.getCommunityNextPosts(id, time, AppController.getInstance().getSessionId(), cb);
    }

    public void qnaLanding(Long qnaId, Long communityId, Callback<CommunityPostVM> cb) {
        api.qnaLanding(qnaId, communityId, AppController.getInstance().getSessionId(), cb);
    }

    public void getComments(Long post_id, int offset, Callback<List<CommunityPostCommentVM>> cb) {
        api.getComments(post_id, offset, AppController.getInstance().getSessionId(), cb);
    }

    public void newCommunityPost(NewPost newPost, Callback<PostResponse> cb) {
        api.newCommunityPost(newPost, AppController.getInstance().getSessionId(), cb);
    }

    public void uploadPostPhoto(String id, TypedFile photo, Callback<Response> cb) {
        api.uploadPostPhoto(id, photo, cb);
    }

    public void answerOnQuestion(CommentPost commentPost, Callback<CommentResponse> cb) {
        api.answerOnQuestion(commentPost, AppController.getInstance().getSessionId(), cb);
    }

    public void uploadCommentPhoto(String id, TypedFile photo, Callback<Response> cb) {
        api.uploadCommentPhoto(id, photo, cb);
    }

    public void likePost(Long post_id, Callback<Response> cb) {
        api.setBookmark(post_id, AppController.getInstance().getSessionId(), cb);
    }

    public void unlikePost(Long post_id, Callback<Response> cb) {
        api.setUnBookmark(post_id, AppController.getInstance().getSessionId(), cb);
    }

    public void deletePost(Long post_id, Callback<Response> cb) {
        api.deletePost(post_id, AppController.getInstance().getSessionId(), cb);
    }

    public void deleteComment(Long comment_id, Callback<Response> cb) {
        api.deleteComment(comment_id, AppController.getInstance().getSessionId(), cb);
    }

    //
    // Profile APIs
    //

    public void getUserProfile(Long id, Callback<ProfileVM> cb) {
        api.getUserProfile(id, AppController.getInstance().getSessionId(), cb);
    }

    public void uploadCoverPhoto(TypedFile photo, Callback<Response> cb) {
        api.uploadCoverPhoto(photo, AppController.getInstance().getSessionId(), cb);
    }

    public void uploadProfilePhoto(TypedFile photo, Callback<Response> cb) {
        api.uploadProfilePhoto(photo, AppController.getInstance().getSessionId(), cb);
    }

    public void getLikeSummary(Callback<BookmarkSummaryVM> cb) {
        api.getBookmarkSummary(AppController.getInstance().getSessionId(), cb);
    }

    public void getHeaderBarData(Callback<NotificationsParentVM> cb) {
        api.getHeaderBarData(AppController.getInstance().getSessionId(), cb);
    }

    public void markAsRead(String ids, Callback<Response> cb) {
        api.markAsRead(ids, AppController.getInstance().getSessionId(), cb);
    }

    public void getUserPosts(Long offset, Long id, Callback<PostArray> cb) {
        api.getUserPosts(offset, id, AppController.getInstance().getSessionId(), cb);
    }

    public void getUserComments(Long offset, Long id, Callback<PostArray> cb) {
        api.getUserComments(offset, id, AppController.getInstance().getSessionId(), cb);
    }

    public void getLikedPosts(Long offset, Callback<List<CommunityPostVM>> cb) {
        api.getBookmarkedPosts(offset, AppController.getInstance().getSessionId(), cb);
    }

    public void getEmoticons(Callback<List<EmoticonVM>> cb) {
        api.getEmoticons(AppController.getInstance().getSessionId(), cb);
    }

    public void updateUserProfileData(UserProfileDataVM userProfileDataVM, Callback<UserVM> cb) {
        api.updateUserProfileData(userProfileDataVM, AppController.getInstance().getSessionId(), cb);
    }

    //
    // Messages APIs
    //

    public void getAllConversations(Callback<List<ConversationVM>> cb) {
        api.getAllConversations(AppController.getInstance().getSessionId(), cb);
    }

    public void getMessages(Long id, Long offset, Callback<Response> cb) {
        api.getMessages(id, offset, AppController.getInstance().getSessionId(), cb);
    }

    public void openConversation(Long id, Callback<List<ConversationVM>> cb) {
        api.openConversation(id, AppController.getInstance().getSessionId(), cb);
    }

    public void deleteConversation(Long id, Callback<Response> cb) {
        api.deleteConversation(id, AppController.getInstance().getSessionId(), cb);
    }

    public void sendMessage(MessagePostVM message, Callback<Response> cb) {
        api.sendMessage(message, AppController.getInstance().getSessionId(), cb);
    }

    public void getUnreadMessageCount(Callback<MessageVM> cb) {
        api.getUnreadMessageCount(AppController.getInstance().getSessionId(), cb);
    }

    public void getMessageImage(long id, Callback<MessageVM> cb) {
        api.getMessageImage(AppController.getInstance().getSessionId(), id, cb);
    }

    public void uploadMessagePhoto(long id, TypedFile photo, Callback<Response> cb) {
        api.uploadMessagePhoto(AppController.getInstance().getSessionId(), id, photo, cb);
    }

    //
    // Game APIs
    //

    public void signInForToday(Callback<Response> cb) {
        api.signInForToday(AppController.getInstance().getSessionId(), cb);
    }

    public void getGameAccount(Callback<GameAccountVM> cb) {
        api.getGameAccount(AppController.getInstance().getSessionId(), cb);
    }

    public void getAllGameGifts(Callback<List<GameGiftVM>> cb) {
        api.getAllGameGifts(AppController.getInstance().getSessionId(), cb);
    }

    public void getGameGiftInfo(Long id, Callback<GameGiftVM> cb) {
        api.getGameGiftInfo(id, AppController.getInstance().getSessionId(), cb);
    }

    public void redeemGameGift(Long id, Callback<ResponseStatusVM> cb) {
        api.redeemGameGift(id, AppController.getInstance().getSessionId(), cb);
    }

    public void getGameTransactions(Long offset, Callback<List<GameTransactionVM>> cb) {
        api.getGameTransactions(offset, AppController.getInstance().getSessionId(), cb);
    }

    public void getLatestGameTransactions(Callback<List<GameTransactionVM>> cb) {
        api.getLatestGameTransactions(AppController.getInstance().getSessionId(), cb);
    }

    public void getSignupReferrals(Callback<List<UserVM>> cb) {
        api.getSignupReferrals(AppController.getInstance().getSessionId(), cb);
    }

    //
    // GCM key APIs
    //

    public void saveGCMkey(String gcmKey, Callback<Response> cb) {
        api.saveGCMkey(gcmKey, AppController.getInstance().getSessionId(), cb);
    }
}


