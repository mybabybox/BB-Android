package com.babybox.mock;

import java.util.List;

import com.babybox.viewmodel.ConversationVM;
import com.babybox.viewmodel.EmoticonVM;
import com.babybox.viewmodel.GameAccountVM;
import com.babybox.viewmodel.GameGiftVM;
import com.babybox.viewmodel.GameTransactionVM;
import com.babybox.viewmodel.MessagePostVM;
import com.babybox.viewmodel.MessageVM;
import com.babybox.viewmodel.NewPost;
import com.babybox.viewmodel.NotificationsParentVM;
import com.babybox.viewmodel.ResponseStatusVM;
import com.babybox.viewmodel.UserProfileDataVM;
import com.babybox.viewmodel.UserVM;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

public interface MockApi {

    @GET("/get-newsfeeds/{offset}") //a function in your api to get all the Newsfeed list
    public void getNewsfeed(@Path("offset") Long offset, @Query("key") String key, Callback<PostArray> callback);

    @GET("/get-my-communities") //a function in your api to get all the joined communities list
    public void getMyCommunities(@Query("key") String key, Callback<CommunitiesParentVM> callback);

    @GET("/community/{id}")
    public void getCommunity(@Path("id")Long comm_id, @Query("key") String key, Callback<CommunityVM> cb);

    @GET("/communityQnA/questions/{id}")
    public void getCommunityInitialPosts(@Path("id") Long id, @Query("key") String key, Callback<PostArray> callback);

    @GET("/communityQnA/questions/next/{id}/{time}")
    public void getCommunityNextPosts(@Path("id") Long id, @Path("time") String time, @Query("key") String key, Callback<List<CommunityPostVM>> callback);

    @GET("/qna-landing/{qnaId}/{communityId}")  //a function in your api to get one post
    public void qnaLanding(@Path("qnaId") Long qnaId, @Path("communityId") Long communityId, @Query("key") String key, Callback<CommunityPostVM> callback);

    @GET("/comments/{id}/{offset}")
    public void getComments(@Path("id")Long post_id,@Path("offset") Long offset, @Query("key") String key, Callback<List<CommunityPostCommentVM>> cb);

    @POST("/communityQnA/question/post")
    public void newCommunityPost(@Body NewPost newPost, @Query("key") String key, Callback<PostResponse> cb);

    @Multipart
    @POST("/image/uploadPostPhoto")
    public void uploadPostPhoto(@Part("postId") Long postId, @Part("post-photo0") TypedFile photo, Callback<Response> cb);

    @POST("/communityQnA/question/answer")
    public void answerOnQuestion(@Body CommentPost commentPost, @Query("key") String key, Callback<CommentResponse> cb);

    @Multipart
    @POST("/image/uploadCommentPhoto") //a function in your api upload image for comment
    public void uploadCommentPhoto(@Part("commentId") String id, @Part("comment-photo0") TypedFile photo, Callback<Response> cb);

    @GET("/bookmark-post/{post_id}")
    public void setBookmark(@Path("post_id") Long post_id, @Query("key") String key, Callback<Response> cb);

    @GET("/unbookmark-post/{post_id}")
    public void setUnBookmark(@Path("post_id") Long post_id, @Query("key") String key, Callback<Response> cb);

    @GET("/like-post/{post_id}")
    public void setLikePost(@Path("post_id") Long post_id, @Query("key") String key, Callback<Response> cb);

    @GET("/unlike-post/{post_id}")
    public void setUnLikePost(@Path("post_id") Long post_id, @Query("key") String key, Callback<Response> cb);

    @GET("/delete-post/{post_id}")
    public void deletePost(@Path("post_id") Long post_id, @Query("key") String key, Callback<Response> cb);

    @GET("/delete-comment/{comment_id}")
    public void deleteComment(@Path("comment_id") Long comment_id, @Query("key") String key, Callback<Response> cb);

    //
    // Profile APIs
    //

    @GET("/profile/{id}")
    public void getUserProfile(@Path("id") Long id, @Query("key") String key, Callback<ProfileVM> cb);

    @Multipart
    @POST("/image/upload-cover-photo")
    public void uploadCoverPhoto(@Part("profile-photo") TypedFile photo,@Query("key") String key, Callback<Response> cb);

    @Multipart
    @POST("/image/upload-profile-photo")
    public void uploadProfilePhoto(@Part("profile-photo") TypedFile photo,@Query("key") String key, Callback<Response> cb);

    @GET("/get-headerBar-data")
    //a function in your api to get all header meta data (notifications and requests).
    public void getHeaderBarData(@Query("key") String key, Callback<NotificationsParentVM> cb);

    @GET("/mark-as-read/{ids}")
    public void markAsRead(@Path("ids")String ids, @Query("key") String key, Callback<Response> cb);

    //'/ignore-it/:notify_id'
    @GET("/ignore-it/{notify_id}") //a function in your api accept invite request to Community
    public void ignoreIt(@Path("notify_id") Long notify_id, @Query("key") String key, Callback<Response> cb);

    @GET("/get-user-newsfeeds-posts/{offset}/{id}")
    public void getUserPosts(@Path("offset") Long offset,@Path("id") Long id, @Query("key") String key, Callback<PostArray> cb);

    @GET("/get-user-newsfeeds-comments/{offset}/{id}")
    public void getUserComments(@Path("offset") Long offset,@Path("id") Long id, @Query("key") String key, Callback<PostArray> cb);

    @GET("/get-bookmarked-posts/{offset}")
    public void getBookmarkedPosts(@Path("offset") Long offset,@Query("key") String key, Callback<List<CommunityPostVM>> cb);

    @GET("/image/getEmoticons")
    public void getEmoticons(@Query("key") String key, Callback<List<EmoticonVM>> cb);

    @POST("/updateUserProfileData")
    public void updateUserProfileData(@Body UserProfileDataVM userProfileDataVM, @Query("key") String key, Callback<UserVM> cb);

    //
    // Messages APIs
    //

    @GET("/get-all-conversations")
    public void getAllConversations(@Query("key") String key, Callback<List<ConversationVM>> cb);

    @GET("/get-messages/{id}/{offset}")
    public void getMessages(@Path("id") Long id, @Path("offset") Long offset,@Query("key") String key, Callback<Response> cb);

    @GET("/open-conversation/{id}")
    public void openConversation(@Path("id") Long id,@Query("key") String key, Callback<List<ConversationVM>> cb);

    @GET("/delete-conversation/{id}")
    public void deleteConversation(@Path("id") Long id, @Query("key") String key, Callback<Response> cb);

    @POST("/message/sendMsg")
    public void sendMessage(@Body MessagePostVM message, @Query("key") String key, Callback<Response> cb);

    @GET("/get-unread-msg-count")
    public void getUnreadMessageCount(@Query("key") String key, Callback<MessageVM> cb);

    @GET("/image/get-message-image-by-id/{id} ")
    public void getMessageImage(@Query("key") String key, @Part("messageId") long id, Callback<MessageVM> cb);

    @Multipart
    @POST("/image/sendMessagePhoto") //a function in your api upload image for message
    public void uploadMessagePhoto(@Query("key") String key, @Part("messageId") long id, @Part("send-photo0") TypedFile photo, Callback<Response> cb);

    //
    // Game APIs
    //

    @POST("/sign-in-for-today")
    public void signInForToday(@Query("key") String key, Callback<Response> cb);

    @GET("/get-gameaccount")
    public void getGameAccount(@Query("key") String key, Callback<GameAccountVM> cb);

    @GET("/get-all-game-gifts")
    public void getAllGameGifts(@Query("key") String key, Callback<List<GameGiftVM>> cb);

    @GET("/get-game-gift-info/{id}")
    public void getGameGiftInfo(@Path("id") Long id, @Query("key") String key, Callback<GameGiftVM> cb);

    @FormUrlEncoded
    @POST("/redeem-game-gift")
    public void redeemGameGift(@Field("id") Long id, @Query("key") String key, Callback<ResponseStatusVM> cb);

    @GET("/get-game-transactions/{offset}")
    public void getGameTransactions(@Path("offset") Long offset, @Query("key") String key, Callback<List<GameTransactionVM>> cb);

    @GET("/get-latest-game-transactions")
    public void getLatestGameTransactions(@Query("key") String key, Callback<List<GameTransactionVM>> cb);

    @GET("/get-signup-referrals")
    public void getSignupReferrals(@Query("key") String key, Callback<List<UserVM>> cb);

    //
    // GCM key APIs
    //

    @POST("/saveGCMKey/{GCMkey}")
    public void saveGCMkey(@Path("GCMkey") String GCMkey,@Query("key") String key,Callback<Response> cb);
}


