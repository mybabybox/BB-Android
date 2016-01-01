package com.babybox.app;

import com.babybox.viewmodel.ActivityVM;
import com.babybox.viewmodel.CategoryVM;
import com.babybox.viewmodel.CollectionVM;
import com.babybox.viewmodel.CommentVM;
import com.babybox.viewmodel.ConversationOrderVM;
import com.babybox.viewmodel.ConversationVM;
import com.babybox.viewmodel.CountryVM;
import com.babybox.viewmodel.FeaturedItemVM;
import com.babybox.viewmodel.GameBadgeVM;
import com.babybox.viewmodel.LocationVM;
import com.babybox.viewmodel.MessageVM;
import com.babybox.viewmodel.NewCommentVM;
import com.babybox.viewmodel.NotificationCounterVM;
import com.babybox.viewmodel.PostVM;
import com.babybox.viewmodel.PostVMLite;
import com.babybox.viewmodel.ResponseStatusVM;
import com.babybox.viewmodel.EditUserInfoVM;
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

    @POST("/api/login/mobile")
    public void login(@Query("email") String email, @Query("password") String password, Callback<Response> cb);

    @POST("/authenticate/mobile/facebook")
    public void loginByFacebook(@Query("access_token") String access_token, Callback<Response> cb);

    @POST("/signup")
    public void signUp(@Query("lname") String lname,
                       @Query("fname") String fname,
                       @Query("email") String email,
                       @Query("password") String password,
                       @Query("repeatPassword") String repeatPassword,
                       Callback<Response> cb);

    @FormUrlEncoded
    @POST("/saveSignupInfo")
    public void signUpInfo(@Field("parent_displayname") String parent_displayname,
                           @Field("parent_location") Integer parent_location,
                           @Query("key") String key,
                           Callback<Response> cb);

    @GET("/api/get-districts")
    public void getDistricts(@Query("key") String key, Callback<List<LocationVM>> cb);

    @GET("/api/get-countries")
    public void getCountries(@Query("key") String key, Callback<List<CountryVM>> cb);

    @GET("/api/init-new-user")
    public void initNewUser(@Query("key") String key, Callback<UserVM> cb);

    @GET("/api/get-user-info")
    public void getUserInfo(@Query("key") String key, Callback<UserVM> cb);

    @GET("/api/get-user/{id}")
    public void getUser(@Path("id") Long id, @Query("key") String key, Callback<UserVM> cb);

    @GET("/api/get-featured-items/{itemType}")
    public void getFeaturedItems(@Path("itemType") String itemType, @Query("key") String key,Callback<List<FeaturedItemVM>> cb);

    //
    // Home feeds
    //

    @GET("/api/get-home-explore-feed/{offset}")
    public void getHomeExploreFeed(@Path("offset") Long offset, @Query("key") String key, Callback<List<PostVMLite>> callback);

    @GET("/api/get-home-following-feed/{offset}")
    public void getHomeFollowingFeed(@Path("offset") Long offset, @Query("key") String key, Callback<List<PostVMLite>> callback);

    //
    // Category feeds
    //

    @GET("/api/get-category-popular-feed/{id}/{productType}/{offset}")
    public void getCategoryPopularFeed(@Path("offset") Long offset, @Path("id") Long id, @Path("productType") String productType, @Query("key") String key, Callback<List<PostVMLite>> callback);

    @GET("/api/get-category-newest-feed/{id}/{productType}/{offset}")
    public void getCategoryNewestFeed(@Path("offset") Long offset, @Path("id") Long id, @Path("productType") String productType, @Query("key") String key, Callback<List<PostVMLite>> callback);

    @GET("/api/get-category-price-low-high-feed/{id}/{productType}/{offset}")
    public void getCategoryPriceLowHighFeed(@Path("offset") Long offset, @Path("id") Long id, @Path("productType") String productType, @Query("key") String key, Callback<List<PostVMLite>> callback);

    @GET("/api/get-category-price-high-low-feed/{id}/{productType}/{offset}")
    public void getCategoryPriceHighLowFeed(@Path("offset") Long offset, @Path("id") Long id, @Path("productType") String productType, @Query("key") String key, Callback<List<PostVMLite>> callback);

    //
    // User feeds
    //

    @GET("/api/get-user-posted-feed/{id}/{offset}")
    public void getUserPostedFeed(@Path("offset") Long offset, @Path("id") Long id, @Query("key") String key, Callback<List<PostVMLite>> callback);

    @GET("/api/get-user-liked-feed/{id}/{offset}")
    public void getUserLikedFeed(@Path("offset") Long offset, @Path("id") Long id, @Query("key") String key, Callback<List<PostVMLite>> callback);

    @GET("/api/get-followings/{userId}/{offset}")
    public void getFollowings(@Path("offset") Long offset, @Path("userId") Long userId, @Query("key") String key, Callback<List<UserVMLite>> cb);

    @GET("/api/get-followers/{userId}/{offset}")
    public void getFollowers(@Path("offset") Long offset, @Path("userId") Long userId, @Query("key") String key, Callback<List<UserVMLite>> cb);

    //
    // Category + post + comments
    //

    @GET("/api/get-categories")
    public void getCategories(@Query("key") String key, Callback<List<CategoryVM>> cb);

    @GET("/api/get-category/{id}")
    public void getCategory(@Path("id") Long id, @Query("key") String key, Callback<CategoryVM> cb);

    @GET("/api/get-post/{id}")
    public void getPost(@Path("id") Long id, @Query("key") String key, Callback<PostVM> callback);

    @POST("/api/post/new")
    public void newPost(@Body MultipartTypedOutput attachments, /*@Body NewPostVM newPost,*/ @Query("key") String key, Callback<ResponseStatusVM> cb);

    @POST("/api/post/edit")
    public void editPost(@Body MultipartTypedOutput attachments, /*@Body NewPostVM newPost,*/ @Query("key") String key, Callback<ResponseStatusVM> cb);

    @GET("/api/post/delete/{id}")
    public void deletePost(@Path("id") Long id, @Query("key") String key, Callback<Response> cb);

    @GET("/api/get-comments/{postId}/{offset}")
    public void getComments(@Path("postId") Long postId, @Path("offset") Long offset, @Query("key") String key, Callback<List<CommentVM>> cb);

    @POST("/api/comment/new")
    public void newComment(@Body NewCommentVM newComment, @Query("key") String key, Callback<ResponseStatusVM> cb);

    @GET("/api/comment/delete/{id}")
    public void deleteComment(@Path("id") Long comment_id, @Query("key") String key, Callback<Response> cb);

    @GET("/api/like-post/{id}")
    public void likePost(@Path("id") Long id, @Query("key") String key, Callback<Response> cb);

    @GET("/api/unlike-post/{id}")
    public void unlikePost(@Path("id") Long id, @Query("key") String key, Callback<Response> cb);

    @GET("/api/sold-post/{id}")
    public void soldPost(@Path("id") Long id, @Query("key") String key, Callback<Response> cb);

    @GET("/api/follow-user/{id}")
    public void followUser(@Path("id") Long id, @Query("key") String key, Callback<Response> cb);

    @GET("/api/unfollow-user/{id}")
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

    @POST("/api/user-info/edit")
    public void editUserInfo(@Body EditUserInfoVM userInfoVM, @Query("key") String key, Callback<UserVM> cb);

    @GET("/api/get-user-collections/{userId}")
    public void getUserCollections(@Path("userId") Long userId, @Query("key") String key, Callback<List<CollectionVM>> cb);

    @GET("/api/get-collection/{id}")
    public void getCollection(@Path("id") Long id, @Query("key") String key, Callback<CollectionVM> cb);

    @GET("/api/notification-counter")
    public void getNotificationCounter(@Query("key") String key, Callback<NotificationCounterVM> cb);

    @GET("/api/get-activities/{offset}")
    public void getActivities(@Path("offset") Long offset, @Query("key") String key, Callback<List<ActivityVM>> cb);

    //
    // Game badges
    //

    @GET("/api/get-game-badges/{id}")
    public void getGameBadges(@Path("id") Long id, @Query("key") String key, Callback<List<GameBadgeVM>> cb);

    @GET("/api/get-game-badges-awarded/{id}")
    public void getGameBadgesAwarded(@Path("id") Long id, @Query("key") String key, Callback<List<GameBadgeVM>> cb);

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

    @GET("/image/get-message-image/{id}")
    public void getMessageImage(@Query("key") String key, @Path("id") Long id, Callback<MessageVM> cb);

    @Multipart
    @POST("/image/upload-message-photo")
    public void uploadMessagePhoto(@Query("key") String key, @Part("messageId") Long id, @Part("send-photo0") TypedFile photo, Callback<Response> cb);

    @POST("/update-conversation-note")
    public void updateConversationNote(@Body MultipartTypedOutput attachments, /*@Body NewMessageVM message,*/ @Query("key") String key, Callback<Response> cb);

    @GET("/update-conversation-order-transaction-state/{id}/{state}")
    public void updateConversationOrderTransactionState(@Path("id") Long id, @Path("state") String state, @Query("key") String key, Callback<Response> cb);

    @GET("/highlight-conversation/{id}/{color}")
    public void highlightConversation(@Path("id") Long id, @Path("color") String color, @Query("key") String key, Callback<Response> cb);

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

    @POST("/api/save-gcm-key/{gcmKey}/{versionCode}")
    public void saveGCMKey(@Path("gcmKey") String gcmKey, @Path("versionCode") Long versionCode, @Query("key") String key, Callback<Response> cb);

    //
    // Admin
    //

    @GET("/api/delete-account/{id}")
    public void deleteAccount(@Path("id") Long id, @Query("key") String key, Callback<Response> cb);

    @GET("/api/adjust-up-post-score/{id}")
    public void adjustUpPostScore(@Path("id") Long id, @Query("key") String key, Callback<Response> cb);

    @GET("/api/adjust-down-post-score/{id}")
    public void adjustDownPostScore(@Path("id") Long id, @Query("key") String key, Callback<Response> cb);

    @GET("/api/reset-adjust-post-score/{id}")
    public void resetAdjustPostScore(@Path("id") Long id, @Query("key") String key, Callback<Response> cb);

    @GET("/api/get-users-by-signup/{offset}")
    public void getUsersBySignup(@Path("offset") Long offset, @Query("key") String key, Callback<List<UserVMLite>> cb);

    @GET("/api/get-users-by-login/{offset}")
    public void getUsersByLogin(@Path("offset") Long offset, @Query("key") String key, Callback<List<UserVMLite>> cb);
}