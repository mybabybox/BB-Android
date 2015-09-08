package com.babybox.app;

import com.babybox.viewmodel.CategoryVM;
import com.babybox.viewmodel.CollectionVM;
import com.babybox.viewmodel.LocationVM;
import com.babybox.viewmodel.NewPost;
import com.babybox.viewmodel.NewPostVM;
import com.babybox.viewmodel.PostVM;
import com.babybox.viewmodel.ResponseStatusLiteVM;
import com.babybox.viewmodel.UserVM;

import java.util.List;

import retrofit.Callback;
import retrofit.client.Response;

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

    public void login(String email, String password, Callback<Response> cb) {
        api.login(email, password, cb);
    }

    public void loginByFacebook(String access_token, Callback<Response> cb) {
        api.loginByFacebook(access_token, cb);
    }

    public void getUserInfo(String access_token, Callback<UserVM> cb) {
        api.getUserInfo(access_token, cb);
    }

    public void initNewUser(Callback<UserVM> cb) {
        api.initNewUser(AppController.getInstance().getSessionId(), cb);
    }

    // home feeds

    public void getHomeExploreFeed(Long offset, Callback<List<PostVM>> cb) {
        api.getHomeExploreFeed(offset, AppController.getInstance().getSessionId(), cb);
    }

    public void getHomeTrendingFeed(Long offset, Callback<List<PostVM>> cb) {
        api.getHomeTrendingFeed(offset, AppController.getInstance().getSessionId(), cb);
    }

    public void getHomeFollowingFeed(Long offset, Callback<List<PostVM>> cb) {
        api.getHomeFollowingFeed(offset, AppController.getInstance().getSessionId(), cb);
    }

    // category feeds

    public void getCategoryPopularFeed(Long offset, Long id, String productType, Callback<List<PostVM>> cb) {
        api.getCategoryPopularFeed(offset, id, productType, AppController.getInstance().getSessionId(), cb);
    }

    public void getCategoryNewestFeed(Long offset, Long id, String productType, Callback<List<PostVM>> cb) {
        api.getCategoryNewestFeed(offset, id, productType, AppController.getInstance().getSessionId(), cb);
    }

    public void getCategoryPriceLowHighFeed(Long offset, Long id, String productType, Callback<List<PostVM>> cb) {
        api.getCategoryPriceLowHighFeed(offset, id, productType, AppController.getInstance().getSessionId(), cb);
    }

    public void getCategoryPriceHighLowFeed(Long offset, Long id, String productType, Callback<List<PostVM>> cb) {
        api.getCategoryPriceHighLowFeed(offset, id, productType, AppController.getInstance().getSessionId(), cb);
    }

    // user feeds

    public void getUserPostedFeed(Long offset, Long id, Callback<List<PostVM>> cb) {
        api.getUserPostedFeed(offset, id, AppController.getInstance().getSessionId(), cb);
    }

    public void getUserLikedFeed(Long offset, Long id, Callback<List<PostVM>> cb) {
        api.getUserLikedFeed(offset, id, AppController.getInstance().getSessionId(), cb);
    }

    public void getUserCollectionFeed(Long offset, Long collectionId, Callback<List<PostVM>> cb) {
        api.getUserCollectionFeed(offset, collectionId, AppController.getInstance().getSessionId(), cb);
    }

    public void getFollowers(Long offset, Long userId, Callback<List<UserVM>> cb) {
        api.getFollowers(offset, userId, AppController.getInstance().getSessionId(), cb);
    }

    public void getFollowings(Long offset, Long userId, Callback<List<UserVM>> cb) {
        api.getFollowings(offset, userId, AppController.getInstance().getSessionId(), cb);
    }

    // category

    public void getCategories(Callback<List<CategoryVM>> cb) {
        api.getCategories(AppController.getInstance().getSessionId(), cb);
    }

    public void getCategory(Long id, Callback<CategoryVM> cb) {
        api.getCategory(id, AppController.getInstance().getSessionId(), cb);
    }

    public void getPost(Long id, Callback<PostVM> cb) {
        api.getPost(id, AppController.getInstance().getSessionId(), cb);
    }

    public void newPost(NewPostVM post, Callback<ResponseStatusLiteVM> cb) {
        api.newPost(post, AppController.getInstance().getSessionId(), cb);
    }

    // user profile

    public void getCollections(Long userId, Callback<List<CollectionVM>> cb) {
        api.getCollections(userId, AppController.getInstance().getSessionId(), cb);
    }

    public void getCollection(Long id, Callback<CollectionVM> cb) {
        api.getCollection(id, AppController.getInstance().getSessionId(), cb);
    }
}


