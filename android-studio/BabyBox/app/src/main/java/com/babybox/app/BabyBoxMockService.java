package com.babybox.app;

import com.babybox.viewmodel.CategoryVM;
import com.babybox.viewmodel.CommunitiesParentVM;
import com.babybox.viewmodel.CommunitiesWidgetChildVM;
import com.babybox.viewmodel.CommunityPostVM;
import com.babybox.viewmodel.CommunityVM;
import com.babybox.viewmodel.PostArray;
import com.babybox.viewmodel.PostVM;
import com.babybox.viewmodel.PostVMArray;
import com.babybox.viewmodel.UserVM;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class BabyBoxMockService extends BabyBoxService {

    private long feedTime;

    public BabyBoxMockService(BabyBoxApi api) {
        super(api);
    }

    @Override
    public void getHomeExploreFeed(Long offset, Callback<PostVMArray> cb) {
        final Callback<PostVMArray> callback = cb;
        AppController.getApi().getNewsfeed(offset, AppController.getInstance().getSessionId(), new Callback<PostArray>() {
            @Override
            public void success(PostArray posts, Response response) {
                PostVMArray array = new PostVMArray();
                array.posts = new ArrayList<>();
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

    @Override
    public void getHomeTrendingFeed(Long offset, Callback<PostVMArray> cb) {
        getHomeExploreFeed(offset, cb);
    }

    @Override
    public void getHomeFollowingFeed(Long offset, Callback<PostVMArray> cb) {
        getHomeExploreFeed(offset, cb);
    }

    @Override
    public void getCategoryPopularFeed(Long offset, Long id, String productType, Callback<PostVMArray> cb) {
        final Callback<PostVMArray> callback = cb;
        if (offset == 0) {
            feedTime = 0;
            AppController.getApi().getCommunityInitialPosts(id, AppController.getInstance().getSessionId(), new Callback<PostArray>() {
                @Override
                public void success(PostArray posts, Response response) {
                    PostVMArray array = new PostVMArray();
                    array.posts = new ArrayList<>();
                    for (CommunityPostVM post : posts.posts) {
                        if (post.hasImage) {
                            array.posts.add(new PostVM(post));
                        }
                        feedTime = post.getUt();
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
        } else {
            AppController.getApi().getCommunityNextPosts(id, feedTime + "", AppController.getInstance().getSessionId(), new Callback<List<CommunityPostVM>>() {
                @Override
                public void success(List<CommunityPostVM> posts, Response response) {
                    PostVMArray array = new PostVMArray();
                    array.posts = new ArrayList<>();
                    for (CommunityPostVM post : posts) {
                        if (post.hasImage) {
                            array.posts.add(new PostVM(post));
                        }
                        feedTime = post.getUt();
                    }
                    if (posts.size() > 0 && array.posts.size() == 0) {
                        array.posts.add(new PostVM(posts.get(0)));    // dummy to continue endless scroll
                    }
                    callback.success(array, response);
                }

                @Override
                public void failure(RetrofitError error) {
                    callback.failure(error);
                }
            });
        }
    }

    @Override
    public void getCategoryNewestFeed(Long offset, Long id, String productType, Callback<PostVMArray> cb) {
        getHomeExploreFeed(offset, cb);
    }

    @Override
    public void getCategoryPriceLowHighFeed(Long offset, Long id, String productType, Callback<PostVMArray> cb) {
        getCategoryPopularFeed(offset, id, productType, cb);
    }

    @Override
    public void getCategoryPriceHighLowFeed(Long offset, Long id, String productType, Callback<PostVMArray> cb) {
        getHomeExploreFeed(offset, cb);
    }

    public void getUserPostedFeed(Long offset, Long id, Callback<PostVMArray> cb) {
        getHomeExploreFeed(offset, cb);
    }

    public void getUserLikedFeed(Long offset, Long id, Callback<PostVMArray> cb) {
        getHomeExploreFeed(offset, cb);
    }

    public void getUserCollectionFeed(Long offset, Long collectionId, Callback<PostVMArray> cb) {
        getHomeExploreFeed(offset, cb);
    }

    @Override
    public void getCategories(Callback<List<CategoryVM>> cb) {
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

    @Override
    public void getCategory(Long id, Callback<CategoryVM> cb) {
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
}


