package com.babybox.app;

import com.babybox.viewmodel.CategoryVM;
import com.babybox.viewmodel.CommunitiesParentVM;
import com.babybox.viewmodel.CommunitiesWidgetChildVM;
import com.babybox.viewmodel.CommunityPostVM;
import com.babybox.viewmodel.CommunityVM;
import com.babybox.viewmodel.LocationVM;
import com.babybox.viewmodel.PostArray;
import com.babybox.viewmodel.PostVM;
import com.babybox.viewmodel.PostVMArray;
import com.babybox.viewmodel.UserVM;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

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

    public void getUserInfo(String access_token, Callback<UserVM> cb) {
        api.getUserInfo(access_token, cb);
    }

    public void initNewUser(Callback<UserVM> cb) {
        api.initNewUser(AppController.getInstance().getSessionId(), cb);
    }

    public void getHomeExploreFeed(Long offset, Callback<PostVMArray> cb) {
        //api.getHomeExploreFeed(AppController.getInstance().getSessionId(), cb);

        // TEMP - for api testing
        final Callback<PostVMArray> callback = cb;
        AppController.getApi().getNewsfeed(offset, AppController.getInstance().getSessionId(), new Callback<PostArray>() {
            @Override
            public void success(PostArray posts, Response response) {
                PostVMArray array = new PostVMArray();
                array.posts = new ArrayList<PostVM>();
                for (CommunityPostVM post : posts.posts) {
                    if (post.hasImage) {
                        array.posts.add(new PostVM(post));
                    }
                }
                if (posts.posts.size() > 0 && array.posts.size() == 0) {
                    array.posts.add(new PostVM(posts.posts.get(0)));    // dummy to continue endless scroll
                }
                callback.success(array, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void getHomeTrendingFeed(Long offset, Callback<PostVMArray> cb) {
        //api.getHomeTrendingFeed(AppController.getInstance().getSessionId(), cb);
        getHomeExploreFeed(offset, cb);
    }

    public void getHomeFollowingFeed(Long offset, Callback<PostVMArray> cb) {
        //api.getHomeFollowingFeed(AppController.getInstance().getSessionId(), cb);
        getHomeExploreFeed(offset, cb);
    }

    public void getCategoryPopularFeed(Long offset, Long catId, String productType, Callback<PostVMArray> cb) {
        //api.getCategoryPopularFeed(AppController.getInstance().getSessionId(), cb);
        getHomeExploreFeed(offset, cb);
    }

    public void getCategoryNewestFeed(Long offset, Long catId, String productType, Callback<PostVMArray> cb) {
        //api.getCategoryNewestFeed(AppController.getInstance().getSessionId(), cb);
        getHomeExploreFeed(offset, cb);
    }

    public void getCategoryPriceLowHighFeed(Long offset, Long catId, String productType, Callback<PostVMArray> cb) {
        //api.getCategoryPriceLowHighFeed(AppController.getInstance().getSessionId(), cb);
        getHomeExploreFeed(offset, cb);
    }

    public void getCategoryPriceHighLowFeed(Long offset, Long catId, String productType, Callback<PostVMArray> cb) {
        //api.getCategoryPriceHighLowFeed(AppController.getInstance().getSessionId(), cb);
        getHomeExploreFeed(offset, cb);
    }

    public void getCategories(Callback<List<CategoryVM>> cb) {
        //api.getCategories(AppController.getInstance().getSessionId(), cb);

        // TEMP - for api testing
        final Callback<List<CategoryVM>> callback = cb;
        AppController.getApi().getMyCommunities(AppController.getInstance().getSessionId(), new Callback<CommunitiesParentVM>() {
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

    public void getCategory(Long id, Callback<CategoryVM> cb) {
        //api.getCategory(id, AppController.getInstance().getSessionId(), cb);

        // TEMP - for api testing
        final Callback<CategoryVM> callback = cb;
        AppController.getApi().getCommunity(id, AppController.getInstance().getSessionId(), new Callback<CommunityVM>() {
            @Override
            public void success(CommunityVM comm, Response response) {
                callback.success(new CategoryVM(comm), response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void getPost(Long id, Callback<PostVM> cb) {
        api.getPost(id, AppController.getInstance().getSessionId(), cb);
    }
}


