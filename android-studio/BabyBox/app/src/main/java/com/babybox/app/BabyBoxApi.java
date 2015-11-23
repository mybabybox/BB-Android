package com.babybox.app;

import com.babybox.viewmodel.ActivityVM;
import com.babybox.viewmodel.CategoryVM;
import com.babybox.viewmodel.CollectionVM;
import com.babybox.viewmodel.CommentVM;
import com.babybox.viewmodel.ConversationOrderVM;
import com.babybox.viewmodel.ConversationVM;
import com.babybox.viewmodel.EmoticonVM;
import com.babybox.viewmodel.LocationVM;
import com.babybox.viewmodel.MessageVM;
import com.babybox.viewmodel.NewCommentVM;
import com.babybox.viewmodel.NotificationCounterVM;
import com.babybox.viewmodel.PostVM;
import com.babybox.viewmodel.PostVMLite;
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
import retrofit.mime.MultipartTypedOutput;
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

    @GET("/get-all-feed-products")
    public void getAllProducts(@Query("key") String key,Callback<List<PostVMLite>> cb);

    //
    // Home feeds
    //

    @GET("/get-home-explore-feed/{offset}")
    public void getHomeExploreFeed(@Path("offset") Long offset, @Query("key") String key, Callback<List<PostVMLite>> callback);

    @GET("/get-home-following-feed/{offset}")
    public void getHomeFollowingFeed(@Path("offset") Long offset, @Query("key") String key, Callback<List<PostVMLite>> callback);

    //
    // Category feeds
    //

    @GET("/get-category-popular-feed/{id}/{productType}/{offset}")
    public void getCategoryPopularFeed(@Path("offset") Long offset, @Path("id") Long id, @Path("productType") String productType, @Query("key") String key, Callback<List<PostVMLite>> callback);

    @GET("/get-category-newest-feed/{id}/{productType}/{offset}")
    public void getCategoryNewestFeed(@Path("offset") Long offset, @Path("id") Long id, @Path("productType") String productType, @Query("key") String key, Callback<List<PostVMLite>> callback);

    @GET("/get-category-price-low-high-feed/{id}/{productType}/{offset}")
    public void getCategoryPriceLowHighFeed(@Path("offset") Long offset, @Path("id") Long id, @Path("productType") String productType, @Query("key") String key, Callback<List<PostVMLite>> callback);

    @GET("/get-category-price-high-low-feed/{id}/{productType}/{offset}")
    public void getCategoryPriceHighLowFeed(@Path("offset") Long offset, @Path("id") Long id, @Path("productType") String productType, @Query("key") String key, Callback<List<PostVMLite>> callback);

    //
    // User feeds
    //

    @GET("/get-user-posted-feed/{id}/{offset}")
    public void getUserPostedFeed(@Path("offset") Long offset, @Path("id") Long id, @Query("key") String key, Callback<List<PostVMLite>> callback);

    @GET("/get-user-liked-feed/{id}/{offset}")
    public void getUserLikedFeed(@Path("offset") Long offset, @Path("id") Long id, @Query("key") String key, Callback<List<PostVMLite>> callback);

    @GET("/get-user-collection-feed/{collectionId}/{offset}")
    public void getUserCollectionFeed(@Path("offset") Long offset, @Path("collectionId") Long collectionId, @Query("key") String key, Callback<List<PostVMLite>> callback);

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
    public void newPost(@Body MultipartTypedOutput attachments, /*@Body NewPostVM newPost,*/ @Query("key") String key, Callback<ResponseStatusVM> cb);

    @POST("/post/edit")
    public void editPost(@Body MultipartTypedOutput attachments, /*@Body NewPostVM newPost,*/ @Query("key") String key, Callback<ResponseStatusVM> cb);

    @GET("/post/delete/{id}")
    public void deletePost(@Path("id") Long id, @Query("key") String key, Callback<Response> cb);

    @GET("/comments/{postId}/{offset}")
    public void getComments(@Path("postId") Long postId, @Path("offset") Long offset, @Query("key") String key, Callback<List<CommentVM>> cb);

    @POST("/comment/new")
    public void newComment(@Body NewCommentVM newComment, @Query("key") String key, Callback<ResponseStatusVM> cb);

    @GET("/comment/delete/{id}")
    public void deleteComment(@Path("id") Long comment_id, @Query("key") String key, Callback<Response> cb);

    @GET("/like-post/{id}")
    public void likePost(@Path("id") Long id, @Query("key") String key, Callback<Response> cb);

    @GET("/unlike-post/{id}")
    public void unlikePost(@Path("id") Long id, @Query("key") String key, Callback<Response> cb);

    @GET("/sold-post/{id}")
    public void soldPost(@Path("id") Long id, @Query("key") String key, Callback<Response> cb);

    @GET("/follow-user/{id}")
    public void followUser(@Path("id") Long id, @Query("key") String key, Callback<Response> cb);

    @GET("/unfollow-user/{id}")
    public void unfollowUser(@Path("id") Long id, @Query("key") String key, Callback<Response> cb);

    //
    // Profile
    //

    @Multipart
    @POST("/image/upload-cover-photo")
    public void uploadCoverPhoto(@Part("profile-photo") TypedFile photo, @Query("key") String key, Callback<Response> cb);

    @Multipart
    @POST("/image/upload-profile-photo")
    public void uploadProfilePhoto(@Part("profile-photo") TypedFile photo, @Query("key") String key, Callback<Response> cb);

    @POST("/updateUserProfileData")
    public void updateUserProfileData(@Body UserProfileDataVM userProfileDataVM, @Query("key") String key, Callback<UserVM> cb);

    @GET("/collections/{userId}")
    public void getCollections(@Path("userId") Long userId, @Query("key") String key, Callback<List<CollectionVM>> cb);

    @GET("/collection/{id}")
    public void getCollection(@Path("id") Long id, @Query("key") String key, Callback<CollectionVM> cb);

    @GET("/notification-counter")
    public void getNotificationCounter(@Query("key") String key, Callback<NotificationCounterVM> cb);

    @GET("/get-activities/{offset}")
    public void getActivities(@Path("offset") Long offset, @Query("key") String key, Callback<List<ActivityVM>> cb);

    //
    // Conversation
    //

    @GET("/get-all-conversations")
    public void getAllConversations(@Query("key") String key, Callback<List<ConversationVM>> cb);

    @GET("/get-post-conversations/{id}")
    public void getPostConversations(@Path("id") Long id, @Query("key") String key, Callback<List<ConversationVM>> cb);

    @GET("/get-conversation/{id}")
    public void getConversation(@Path("id") Long id, @Query("key") String key, Callback<ConversationVM> cb);

    @GET("/get-messages/{conversationId}/{offset}")
    public void getMessages(@Path("conversationId") Long conversationId, @Path("offset") Long offset, @Query("key") String key, Callback<Response> cb);

    @GET("/open-conversation/{postId}")
    public void openConversation(@Path("postId") Long postId, @Query("key") String key, Callback<ConversationVM> cb);

    @GET("/delete-conversation/{id}")
    public void deleteConversation(@Path("id") Long id, @Query("key") String key, Callback<Response> cb);

    @POST("/message/new")
    public void newMessage(@Body MultipartTypedOutput attachments, /*@Body NewMessageVM message,*/ @Query("key") String key, Callback<MessageVM> cb);

    @GET("/image/get-message-image/{id} ")
    public void getMessageImage(@Query("key") String key, @Path("id") Long id, Callback<MessageVM> cb);

    @Multipart
    @POST("/image/upload-message-photo")
    public void uploadMessagePhoto(@Query("key") String key, @Part("messageId") Long id, @Part("send-photo0") TypedFile photo, Callback<Response> cb);

    //
    // Conversation Order
    //

    @GET("/conversation-order/new/{conversationId}")
    public void newConversationOrder(@Path("conversationId") Long conversationId, @Query("key") String key, Callback<ConversationOrderVM> cb);

    @GET("/conversation-order/cancel/{id}")
    public void cancelConversationOrder(@Path("id") Long id, @Query("key") String key, Callback<ConversationOrderVM> cb);

    @GET("/conversation-order/accept/{id}")
    public void acceptConversationOrder(@Path("id") Long id, @Query("key") String key, Callback<ConversationOrderVM> cb);

    @GET("/conversation-order/decline/{id}")
    public void declineConversationOrder(@Path("id") Long id, @Query("key") String key, Callback<ConversationOrderVM> cb);

    //
    // GCM
    //

    @POST("/save-gcm-key/{gcmKey}/{versionCode}")
    public void saveGCMKey(@Path("gcmKey") String gcmKey, @Path("versionCode") Long versionCode, @Query("key") String key, Callback<Response> cb);

    //
    // Admin
    //

    @GET("/delete-account/{id}")
    public void deleteAccount(@Path("id") Long id, @Query("key") String key, Callback<Response> cb);

    @GET("/adjust-up-post-score/{id}")
    public void adjustUpPostScore(@Path("id") Long id, @Query("key") String key, Callback<Response> cb);

    @GET("/adjust-down-post-score/{id}")
    public void adjustDownPostScore(@Path("id") Long id, @Query("key") String key, Callback<Response> cb);

    @GET("/reset-adjust-post-score/{id}")
    public void resetAdjustPostScore(@Path("id") Long id, @Query("key") String key, Callback<Response> cb);
}


