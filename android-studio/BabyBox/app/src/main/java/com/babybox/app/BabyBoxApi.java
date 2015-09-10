package com.babybox.app;

import com.babybox.viewmodel.CategoryVM;
import com.babybox.viewmodel.CollectionVM;
import com.babybox.viewmodel.CommentVM;
import com.babybox.viewmodel.ConversationVM;
import com.babybox.viewmodel.EmoticonVM;
import com.babybox.viewmodel.GameAccountVM;
import com.babybox.viewmodel.GameGiftVM;
import com.babybox.viewmodel.GameTransactionVM;
import com.babybox.viewmodel.LocationVM;
import com.babybox.viewmodel.MessagePostVM;
import com.babybox.viewmodel.MessageVM;
import com.babybox.viewmodel.NewCommentVM;
import com.babybox.viewmodel.NewPostVM;
import com.babybox.viewmodel.NotificationsParentVM;
import com.babybox.viewmodel.PostVM;
import com.babybox.mock.ProfileVM;
import com.babybox.viewmodel.ResponseStatusVM;
import com.babybox.viewmodel.UserProfileDataVM;
import com.babybox.viewmodel.UserVM;
import com.babybox.viewmodel.UserVMLite;

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

    @GET("/get-user/{id}")
    public void getUser(@Path("id") Long id, @Query("key") String key, Callback<UserVM> cb);

    @GET("/image/getEmoticons")
    public void getEmoticons(@Query("key") String key, Callback<List<EmoticonVM>> cb);

    //
    // Home feeds
    //

    @GET("/get-home-explore-feed/{offset}")
    public void getHomeExploreFeed(@Path("offset") Long offset, @Query("key") String key, Callback<List<PostVM>> callback);

    @GET("/get-home-following-feed/{offset}")
    public void getHomeFollowingFeed(@Path("offset") Long offset, @Query("key") String key, Callback<List<PostVM>> callback);

    //
    // Category feeds
    //

    @GET("/get-category-popular-feed/{id}/{productType}/{offset}")
    public void getCategoryPopularFeed(@Path("offset") Long offset, @Path("id") Long id, @Path("productType") String productType, @Query("key") String key, Callback<List<PostVM>> callback);

    @GET("/get-category-newest-feed/{id}/{productType}/{offset}")
    public void getCategoryNewestFeed(@Path("offset") Long offset, @Path("id") Long id, @Path("productType") String productType, @Query("key") String key, Callback<List<PostVM>> callback);

    @GET("/get-category-price-low-high-feed/{id}/{productType}/{offset}")
    public void getCategoryPriceLowHighFeed(@Path("offset") Long offset, @Path("id") Long id, @Path("productType") String productType, @Query("key") String key, Callback<List<PostVM>> callback);

    @GET("/get-category-price-high-low-feed/{id}/{productType}/{offset}")
    public void getCategoryPriceHighLowFeed(@Path("offset") Long offset, @Path("id") Long id, @Path("productType") String productType, @Query("key") String key, Callback<List<PostVM>> callback);

    //
    // User feeds
    //

    @GET("/get-user-posted-feed/{id}/{offset}")
    public void getUserPostedFeed(@Path("offset") Long offset, @Path("id") Long id, @Query("key") String key, Callback<List<PostVM>> callback);

    @GET("/get-user-liked-feed/{id}/{offset}")
    public void getUserLikedFeed(@Path("offset") Long offset, @Path("id") Long id, @Query("key") String key, Callback<List<PostVM>> callback);

    @GET("/get-user-collection-feed/{collectionId}/{offset}")
    public void getUserCollectionFeed(@Path("offset") Long offset, @Path("collectionId") Long collectionId, @Query("key") String key, Callback<List<PostVM>> callback);

    @GET("/followings/{userId}/{offset}")
    public void getFollowings(@Path("offset") Long offset, @Path("userId") Long userId, @Query("key") String key, Callback<List<UserVMLite>> cb);

    @GET("/followers/{userId}/{offset}")
    public void getFollowers(@Path("offset") Long offset, @Path("userId") Long userId, @Query("key") String key, Callback<List<UserVMLite>> cb);

    //
    // Category + post + comments
    //

    @GET("/categories")
    public void getCategories(@Query("key") String key, Callback<List<CategoryVM>> cb);

    @GET("/category/{id}")
    public void getCategory(@Path("id") Long id, @Query("key") String key, Callback<CategoryVM> cb);

    @GET("/post/{id}")
    public void getPost(@Path("id") Long id, @Query("key") String key, Callback<PostVM> callback);

    @POST("/post/new")
    public void newPost(@Body NewPostVM newPost, @Query("key") String key, Callback<ResponseStatusVM> cb);

    @GET("/post/delete/{id}")
    public void deletePost(@Path("post_id") Long post_id, @Query("key") String key, Callback<Response> cb);

    @Multipart
    @POST("/image/upload-post-image")
    public void uploadPostImage(@Part("id") String id, @Part("post-image") TypedFile image, Callback<Response> cb);

    @GET("/comments/{postId}/{offset}")
    public void getComments(@Path("postId") Long postId, @Path("offset") int offset, @Query("key") String key, Callback<List<CommentVM>> cb);

    @POST("/comment/new")
    public void newComment(@Body NewCommentVM newComment, @Query("key") String key, Callback<ResponseStatusVM> cb);

    @GET("/comment/delete/{id}")
    public void deleteComment(@Path("id") Long comment_id, @Query("key") String key, Callback<Response> cb);

    @Multipart
    @POST("/image/upload-comment-image")
    public void uploadCommentImage(@Part("id") String id, @Part("comment-image") TypedFile photo, Callback<Response> cb);

    @GET("/like-post/{id}")
    public void likePost(@Path("id") Long id, @Query("key") String key, Callback<Response> cb);

    @GET("/unlike-post/{id}")
    public void unlikePost(@Path("id") Long id, @Query("key") String key, Callback<Response> cb);

    @GET("/follow-user/{id}")
    public void followUser(@Path("id") Long id, @Query("key") String key, Callback<Response> cb);

    @GET("/unfollow-user/{id}")
    public void unfollowUser(@Path("id") Long id, @Query("key") String key, Callback<Response> cb);

    //
    // Profile
    //

    @GET("/profile/{id}")
    public void getUserProfile(@Path("id") Long id, @Query("key") String key, Callback<ProfileVM> cb);

    @Multipart
    @POST("/image/upload-cover-photo")
    public void uploadCoverPhoto(@Part("profile-photo") TypedFile photo, @Query("key") String key, Callback<Response> cb);

    @Multipart
    @POST("/image/upload-profile-photo")
    public void uploadProfilePhoto(@Part("profile-photo") TypedFile photo, @Query("key") String key, Callback<Response> cb);

    @GET("/get-notifs")
    public void getNotifs(@Query("key") String key, Callback<NotificationsParentVM> cb);

    @GET("/read-notifs/{ids}")
    public void readNotifs(@Path("ids") String ids, @Query("key") String key, Callback<Response> cb);

    @POST("/updateUserProfileData")
    public void updateUserProfileData(@Body UserProfileDataVM userProfileDataVM, @Query("key") String key, Callback<UserVM> cb);

    @GET("/collections/{userId}")
    public void getCollections(@Path("userId") Long userId, @Query("key") String key, Callback<List<CollectionVM>> cb);

    @GET("/collection/{id}")
    public void getCollection(@Path("id") Long id, @Query("key") String key, Callback<CollectionVM> cb);

    //
    // Messages
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
    // Game
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
    // GCM
    //

    @POST("/saveGCMKey/{gcmKey}")
    public void saveGCMkey(@Path("gcmKey") String gcmKey, @Query("key") String key, Callback<Response> cb);
}


