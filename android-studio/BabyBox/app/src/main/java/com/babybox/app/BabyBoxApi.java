package com.babybox.app;

import com.babybox.viewmodel.BookmarkSummaryVM;
import com.babybox.viewmodel.CommentPost;
import com.babybox.viewmodel.CommentResponse;
import com.babybox.viewmodel.CommunitiesParentVM;
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

import java.util.List;

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

public interface BabyBoxApi {

    @POST("/signup")
    public void signUp(@Query("lname") String lname, @Query("fname") String fname, @Query("email") String email, @Query("password") String password, @Query("repeatPassword") String repeatPassword, Callback<Response> cb);
    //http://localhost:9000/signup?lname=asd&fname=dsa&email=shwashank12@gmail.com&password=qwerty&repeatPassword=qwerty

    @FormUrlEncoded
    @POST("/saveSignupInfo")
    public void signUpInfo(@Field("parent_displayname") String parent_displayname,
                           @Field("parent_birth_year") Integer parent_birth_year,
                           @Field("parent_location") Integer parent_location,
                           @Field("parent_type") String parent_type,
                           @Field("num_children") String num_children,
                           @Field("bb_gender1") String bb_gender1,
                           @Field("bb_gender2") String bb_gender2,
                           @Field("bb_gender3") String bb_gender3,
                           @Field("bb_birth_year1") String bb_birth_year1,
                           @Field("bb_birth_month1") String bb_birth_month1,
                           @Field("bb_birth_day1") String bb_birth_day1,
                           @Field("bb_birth_year2") String bb_birth_year2,
                           @Field("bb_birth_month2") String bb_birth_month2,
                           @Field("bb_birth_day2") String bb_birth_day2,
                           @Field("bb_birth_year3") String bb_birth_year3,
                           @Field("bb_birth_month3") String bb_birth_month3,
                           @Field("bb_birth_day3") String bb_birth_day3,
                           @Query("key") String key,
                           Callback<Response> cb);

    @GET("/get-all-districts")
    public void getAllDistricts(@Query("key") String key, Callback<List<LocationVM>> cb);

    @POST("/mobile/login")
    public void login(@Query("email") String email, @Query("password") String password, Callback<Response> cb);

    @POST("/authenticate/mobile/facebook")
    public void loginByFacebook(@Query("access_token") String access_token, Callback<Response> cb);

    @GET("/init-new-user")
    public void initNewUser(@Query("key") String key, Callback<UserVM> cb);

    @GET("/get-user-info")
    public void getUserInfo(@Query("key") String key, Callback<UserVM> cb);

    @GET("/get-newsfeeds/{offset}")
    public void getNewsfeed(@Path("offset") Long offset, @Query("key") String key, Callback<PostArray> callback);

    @GET("/get-my-communities")
    public void getMyCommunities(@Query("key") String key, Callback<CommunitiesParentVM> callback);

    @GET("/community/{id}")
    public void getCommunity(@Path("id") Long comm_id, @Query("key") String key, Callback<CommunityVM> cb);

    @GET("/communityQnA/questions/{id}")
    public void getCommunityInitialPosts(@Path("id") Long id, @Query("key") String key, Callback<PostArray> callback);

    @GET("/communityQnA/questions/next/{id}/{time}")
    public void getCommunityNextPosts(@Path("id") Long id, @Path("time") String time, @Query("key") String key, Callback<List<CommunityPostVM>> callback);

    @GET("/qna-landing/{qnaId}/{communityId}")  //a function in your api to get one post
    public void qnaLanding(@Path("qnaId") Long qnaId, @Path("communityId") Long communityId, @Query("key") String key, Callback<CommunityPostVM> callback);

    @GET("/comments/{id}/{offset}")
    public void getComments(@Path("id") Long post_id, @Path("offset") int offset, @Query("key") String key, Callback<List<CommunityPostCommentVM>> cb);

    @POST("/communityQnA/question/post")
    public void newCommunityPost(@Body NewPost newPost, @Query("key") String key, Callback<PostResponse> cb);

    @Multipart
    @POST("/image/uploadPostPhoto") //a function in your api upload image for comment
    public void uploadPostPhoto(@Part("postId") String id, @Part("post-photo0") TypedFile photo, Callback<Response> cb);

    @POST("/communityQnA/question/answer")
    public void answerOnQuestion(@Body CommentPost commentPost, @Query("key") String key, Callback<CommentResponse> cb);

    @Multipart
    @POST("/image/uploadCommentPhoto") //a function in your api upload image for comment
    public void uploadCommentPhoto(@Part("commentId") String id, @Part("comment-photo0") TypedFile photo, Callback<Response> cb);

    @GET("/bookmark-post/{post_id}")
    public void setBookmark(@Path("post_id") Long post_id, @Query("key") String key, Callback<Response> cb);

    @GET("/unbookmark-post/{post_id}")
    public void setUnBookmark(@Path("post_id") Long post_id, @Query("key") String key, Callback<Response> cb);

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
    public void uploadCoverPhoto(@Part("profile-photo") TypedFile photo, @Query("key") String key, Callback<Response> cb);

    @Multipart
    @POST("/image/upload-profile-photo")
    public void uploadProfilePhoto(@Part("profile-photo") TypedFile photo, @Query("key") String key, Callback<Response> cb);

    @GET("/get-bookmark-summary") //a function in your api get bookmark summary
    public void getBookmarkSummary(@Query("key") String key, Callback<BookmarkSummaryVM> cb);

    @GET("/get-headerBar-data")
    //a function in your api to get all header meta data (notifications and requests).
    public void getHeaderBarData(@Query("key") String key, Callback<NotificationsParentVM> cb);

    @GET("/mark-as-read/{ids}")
    public void markAsRead(@Path("ids") String ids, @Query("key") String key, Callback<Response> cb);

    @GET("/get-user-newsfeeds-posts/{offset}/{id}")
    public void getUserPosts(@Path("offset") Long offset, @Path("id") Long id, @Query("key") String key, Callback<PostArray> cb);

    @GET("/get-user-newsfeeds-comments/{offset}/{id}")
    public void getUserComments(@Path("offset") Long offset, @Path("id") Long id, @Query("key") String key, Callback<PostArray> cb);

    @GET("/get-bookmarked-posts/{offset}")
    public void getBookmarkedPosts(@Path("offset") Long offset, @Query("key") String key, Callback<List<CommunityPostVM>> cb);

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
    public void getMessages(@Path("id") Long id, @Path("offset") Long offset, @Query("key") String key, Callback<Response> cb);

    @GET("/open-conversation/{id}")
    public void openConversation(@Path("id") Long id, @Query("key") String key, Callback<List<ConversationVM>> cb);

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

    @POST("/saveGCMKey/{gcmKey}")
    public void saveGCMkey(@Path("gcmKey") String gcmKey, @Query("key") String key, Callback<Response> cb);
}


