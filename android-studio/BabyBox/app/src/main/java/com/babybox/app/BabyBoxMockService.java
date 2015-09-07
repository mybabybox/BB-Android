package com.babybox.app;

import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.CategoryVM;
import com.babybox.viewmodel.CommunitiesParentVM;
import com.babybox.viewmodel.CommunitiesWidgetChildVM;
import com.babybox.viewmodel.CommunityPostVM;
import com.babybox.viewmodel.CommunityVM;
import com.babybox.viewmodel.PostArray;
import com.babybox.viewmodel.PostVM;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class BabyBoxMockService extends BabyBoxService {

    private long feedTime;

    private Long[] postImages = new Long[] {
            580L, 726L, 753L, 754L, 755L, 775L, 776L, 790L, 814L, 815L, 816L, 817L, 820L, 821L, 1484L, 1481L
    };

    public BabyBoxMockService(BabyBoxApi api) {
        super(api);
    }

    @Override
    public void getHomeExploreFeed(Long offset, Callback<List<PostVM>> cb) {
        final Callback<List<PostVM>> callback = cb;
        AppController.getApi().getNewsfeed(offset, AppController.getInstance().getSessionId(), new Callback<PostArray>() {
            @Override
            public void success(PostArray posts, Response response) {
                List<PostVM> array = new ArrayList<>();
                for (CommunityPostVM post : posts.posts) {
                    if (post.hasImage) {
                        array.add(new PostVM(post));
                    }
                }
                if (posts.posts.size() > 0 && array.size() == 0) {
                    CommunityPostVM post = posts.posts.get(0);
                    post.imgs = new Long[1];
                    post.imgs[0] = postImages[ViewUtil.random(0, postImages.length - 1)];
                    post.hasImage = true;
                    array.add(new PostVM(post));    // dummy to continue endless scroll
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
    public void getHomeTrendingFeed(Long offset, Callback<List<PostVM>> cb) {
        getHomeExploreFeed(offset, cb);
    }

    @Override
    public void getHomeFollowingFeed(Long offset, Callback<List<PostVM>> cb) {
        getHomeExploreFeed(offset, cb);
    }

    @Override
    public void getCategoryPopularFeed(Long offset, Long id, String productType, Callback<List<PostVM>> cb) {
        final Callback<List<PostVM>> callback = cb;
        if (offset == 0) {
            feedTime = 0;
            AppController.getApi().getCommunityInitialPosts(id, AppController.getInstance().getSessionId(), new Callback<PostArray>() {
                @Override
                public void success(PostArray posts, Response response) {
                    List<PostVM> array = new ArrayList<>();
                    for (CommunityPostVM post : posts.posts) {
                        if (post.hasImage) {
                            array.add(new PostVM(post));
                        }
                        feedTime = post.getUt();
                    }
                    if (posts.posts.size() > 0 && array.size() == 0) {
                        CommunityPostVM post = posts.posts.get(0);
                        post.imgs = new Long[1];
                        post.imgs[0] = postImages[ViewUtil.random(0, postImages.length - 1)];
                        post.hasImage = true;
                        array.add(new PostVM(post));    // dummy to continue endless scroll
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
                    List<PostVM> array = new ArrayList<>();
                    for (CommunityPostVM post : posts) {
                        if (post.hasImage) {
                            array.add(new PostVM(post));
                        }
                        feedTime = post.getUt();
                    }
                    if (posts.size() > 0 && array.size() == 0) {
                        CommunityPostVM post = posts.get(0);
                        post.imgs = new Long[1];
                        post.imgs[0] = postImages[ViewUtil.random(0, postImages.length - 1)];
                        post.hasImage = true;
                        array.add(new PostVM(post));    // dummy to continue endless scroll
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
    public void getCategoryNewestFeed(Long offset, Long id, String productType, Callback<List<PostVM>> cb) {
        getHomeExploreFeed(offset, cb);
    }

    @Override
    public void getCategoryPriceLowHighFeed(Long offset, Long id, String productType, Callback<List<PostVM>> cb) {
        getCategoryPopularFeed(offset, id, productType, cb);
    }

    @Override
    public void getCategoryPriceHighLowFeed(Long offset, Long id, String productType, Callback<List<PostVM>> cb) {
        getHomeExploreFeed(offset, cb);
    }

    public void getUserPostedFeed(Long offset, Long id, Callback<List<PostVM>> cb) {
        final Callback<List<PostVM>> callback = cb;
        AppController.getApi().getUserPosts(offset, id, AppController.getInstance().getSessionId(), new Callback<PostArray>() {
            @Override
            public void success(PostArray posts, Response response) {
                List<PostVM> array = new ArrayList<>();
                for (CommunityPostVM post : posts.posts) {
                    if (post.hasImage) {
                        array.add(new PostVM(post));
                    }
                }
                if (posts.posts.size() > 0 && array.size() == 0) {
                    CommunityPostVM post = posts.posts.get(0);
                    post.imgs = new Long[1];
                    post.imgs[0] = postImages[ViewUtil.random(0, postImages.length - 1)];
                    post.hasImage = true;
                    array.add(new PostVM(post));    // dummy to continue endless scroll
                }
                callback.success(array, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void getUserLikedFeed(Long offset, Long id, Callback<List<PostVM>> cb) {
        final Callback<List<PostVM>> callback = cb;
        AppController.getApi().getUserComments(offset, id, AppController.getInstance().getSessionId(), new Callback<PostArray>() {
            @Override
            public void success(PostArray posts, Response response) {
                List<PostVM> array = new ArrayList<>();
                for (CommunityPostVM post : posts.posts) {
                    if (post.hasImage) {
                        array.add(new PostVM(post));
                    }
                }
                if (posts.posts.size() > 0 && array.size() == 0) {
                    CommunityPostVM post = posts.posts.get(0);
                    post.imgs = new Long[1];
                    post.imgs[0] = postImages[ViewUtil.random(0, postImages.length - 1)];
                    post.hasImage = true;
                    array.add(new PostVM(post));    // dummy to continue endless scroll
                }
                callback.success(array, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void getUserCollectionFeed(Long offset, Long collectionId, Callback<List<PostVM>> cb) {
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


