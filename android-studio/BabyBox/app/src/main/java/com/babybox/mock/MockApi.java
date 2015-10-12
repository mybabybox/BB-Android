package com.babybox.mock;

import java.util.List;

import com.babybox.viewmodel.ConversationVM;
import com.babybox.viewmodel.EmoticonVM;
import com.babybox.viewmodel.GameAccountVM;
import com.babybox.viewmodel.GameGiftVM;
import com.babybox.viewmodel.GameTransactionVM;
import com.babybox.viewmodel.MessageVM;
import com.babybox.viewmodel.NewMessageVM;
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

    //
    // Game APIs
    //

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


