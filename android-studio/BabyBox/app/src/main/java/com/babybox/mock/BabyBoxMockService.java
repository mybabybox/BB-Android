package com.babybox.mock;

import com.babybox.app.AppController;
import com.babybox.app.BabyBoxApi;
import com.babybox.app.BabyBoxService;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.CategoryVM;
import com.babybox.viewmodel.CommentVM;
import com.babybox.viewmodel.NewCommentVM;
import com.babybox.viewmodel.NewPost;
import com.babybox.viewmodel.NewPostVM;
import com.babybox.viewmodel.PostVM;
import com.babybox.viewmodel.PostVMLite;
import com.babybox.viewmodel.ResponseStatusVM;
import com.babybox.viewmodel.UserVM;

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

    public void getUser(String access_token, Callback<UserVM> cb) {
        final Callback<UserVM> callback = cb;
        api.getUserInfo(access_token, new Callback<UserVM>() {
            @Override
            public void success(UserVM user, Response response) {
                user.numPosts = user.getQuestionsCount();
                user.numSold = user.getAnswersCount();
                user.numLikes = user.getAnswersCount();

                user.isFollowing = false;
                user.numFollowers = 0L;
                user.numFollowings = 0L;
                callback.success(user, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    @Override
    public void getUser(Long id, Callback<UserVM> cb) {
        final Callback<UserVM> callback = cb;
        AppController.getApi().getUserProfile(id, AppController.getInstance().getSessionId(), new Callback<ProfileVM>() {
            @Override
            public void success(ProfileVM profile, Response response) {
                UserVM user = new UserVM(profile);
                callback.success(user, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    @Override
    public void getHomeExploreFeed(Long offset, Callback<List<PostVMLite>> cb) {
        final Callback<List<PostVMLite>> callback = cb;
        AppController.getApi().getNewsfeed(offset, AppController.getInstance().getSessionId(), new Callback<PostArray>() {
            @Override
            public void success(PostArray posts, Response response) {
                List<PostVMLite> array = new ArrayList<>();
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
    public void getHomeFollowingFeed(Long offset, Callback<List<PostVMLite>> cb) {
        getHomeExploreFeed(offset, cb);
    }

    @Override
    public void getCategoryPopularFeed(Long offset, Long id, String productType, Callback<List<PostVMLite>> cb) {
        final Callback<List<PostVMLite>> callback = cb;
        if (offset == 0) {
            feedTime = 0;
            AppController.getApi().getCommunityInitialPosts(id, AppController.getInstance().getSessionId(), new Callback<PostArray>() {
                @Override
                public void success(PostArray posts, Response response) {
                    List<PostVMLite> array = new ArrayList<>();
                    for (CommunityPostVM post : posts.posts) {
                        if (post.hasImage) {
                            array.add(new PostVM(post));
                        } else {
                            post.imgs = new Long[1];
                            post.imgs[0] = postImages[ViewUtil.random(0, postImages.length - 1)];
                            post.hasImage = true;
                            array.add(new PostVM(post));    // dummy to continue endless scroll
                        }
                        feedTime = post.getUt();
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
                    List<PostVMLite> array = new ArrayList<>();
                    for (CommunityPostVM post : posts) {
                        if (post.hasImage) {
                            array.add(new PostVM(post));
                        } else {
                            post.imgs = new Long[1];
                            post.imgs[0] = postImages[ViewUtil.random(0, postImages.length - 1)];
                            post.hasImage = true;
                            array.add(new PostVM(post));    // dummy to continue endless scroll
                        }
                        feedTime = post.getUt();
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
    public void getCategoryNewestFeed(Long offset, Long id, String productType, Callback<List<PostVMLite>> cb) {
        getHomeExploreFeed(offset, cb);
    }

    @Override
    public void getCategoryPriceLowHighFeed(Long offset, Long id, String productType, Callback<List<PostVMLite>> cb) {
        getCategoryPopularFeed(offset, id, productType, cb);
    }

    @Override
    public void getCategoryPriceHighLowFeed(Long offset, Long id, String productType, Callback<List<PostVMLite>> cb) {
        getHomeExploreFeed(offset, cb);
    }

    public void getUserPostedFeed(Long offset, Long id, Callback<List<PostVMLite>> cb) {
        final Callback<List<PostVMLite>> callback = cb;
        AppController.getApi().getUserPosts(offset, id, AppController.getInstance().getSessionId(), new Callback<PostArray>() {
            @Override
            public void success(PostArray posts, Response response) {
                List<PostVMLite> array = new ArrayList<>();
                for (CommunityPostVM post : posts.posts) {
                    if (post.hasImage) {
                        array.add(new PostVM(post));
                    } else {
                        post.imgs = new Long[1];
                        post.imgs[0] = postImages[ViewUtil.random(0, postImages.length - 1)];
                        post.hasImage = true;
                        array.add(new PostVM(post));    // dummy to continue endless scroll
                    }
                }
                callback.success(array, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void getUserLikedFeed(Long offset, Long id, Callback<List<PostVMLite>> cb) {
        final Callback<List<PostVMLite>> callback = cb;
        AppController.getApi().getUserComments(offset, id, AppController.getInstance().getSessionId(), new Callback<PostArray>() {
            @Override
            public void success(PostArray posts, Response response) {
                List<PostVMLite> array = new ArrayList<>();
                for (CommunityPostVM post : posts.posts) {
                    if (post.hasImage) {
                        array.add(new PostVM(post));
                    } else {
                        post.imgs = new Long[1];
                        post.imgs[0] = postImages[ViewUtil.random(0, postImages.length - 1)];
                        post.hasImage = true;
                        array.add(new PostVM(post));    // dummy to continue endless scroll
                    }
                }
                callback.success(array, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void getUserCollectionFeed(Long offset, Long collectionId, Callback<List<PostVMLite>> cb) {
        getHomeExploreFeed(offset, cb);
    }

    @Override
    public void getCategories(Callback<List<CategoryVM>> cb) {
        final Callback<List<CategoryVM>> callback = cb;
        AppController.getApi().getMyCommunities(AppController.getInstance().getSessionId(), new Callback<CommunitiesParentVM>() {
            @Override
            public void success(CommunitiesParentVM communitiesParentVM, Response response) {
                List<CategoryVM> categories = new ArrayList<>();
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

    @Override
    public void getPost(Long id, Callback<PostVM> cb) {
        final Callback<PostVM> callback = cb;
        AppController.getApi().qnaLanding(id, -1L, AppController.getInstance().getSessionId(), new Callback<CommunityPostVM>() {
            @Override
            public void success(CommunityPostVM post, Response response) {
                if (!post.hasImage) {
                    post.imgs = new Long[1];
                    post.imgs[0] = postImages[ViewUtil.random(0, postImages.length - 1)];
                    post.hasImage = true;
                }
                callback.success(new PostVM(post), response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    @Override
    public void newPost(NewPostVM post, Callback<ResponseStatusVM> cb) {
        final Callback<ResponseStatusVM> callback = cb;
        NewPost newPost = new NewPost(post.catId, post.title, post.desc, true);
        AppController.getApi().newCommunityPost(newPost, AppController.getInstance().getSessionId(), new Callback<PostResponse>() {
            @Override
            public void success(PostResponse postResponse, Response response) {
                ResponseStatusVM responseStatus = new ResponseStatusVM();
                responseStatus.objId = Long.valueOf(postResponse.id);
                responseStatus.messages = new ArrayList<>();
                responseStatus.messages.add(postResponse.text);
                callback.success(responseStatus, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void getComments(int offset, Long postId, Callback<List<CommentVM>> cb) {
        final Callback<List<CommentVM>> callback = cb;
        AppController.getApi().getComments(postId, offset, AppController.getInstance().getSessionId(), new Callback<List<CommunityPostCommentVM>>() {
            @Override
            public void success(List<CommunityPostCommentVM> vms, Response response) {
                List<CommentVM> comments = new ArrayList<>();
                for (CommunityPostCommentVM vm : vms) {
                    comments.add(new CommentVM(vm));
                }
                callback.success(comments, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    public void newComment(NewCommentVM comment, Callback<ResponseStatusVM> cb) {
        final Callback<ResponseStatusVM> callback = cb;
        CommentPost newComment = new CommentPost(comment.postId, comment.desc, false);
        AppController.getApi().answerOnQuestion(newComment, AppController.getInstance().getSessionId(), new Callback<CommentResponse>() {
            @Override
            public void success(CommentResponse commentResponse, Response response) {
                ResponseStatusVM responseStatus = new ResponseStatusVM();
                responseStatus.objId = Long.valueOf(commentResponse.id);
                responseStatus.messages = new ArrayList<>();
                responseStatus.messages.add(commentResponse.text);
                callback.success(responseStatus, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    @Override
    public void likePost(Long id, Callback<Response> cb) {
        final Callback<Response> callback = cb;
        AppController.getApi().setLikePost(id, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                callback.success(response, response2);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    @Override
    public void unlikePost(Long id, Callback<Response> cb) {
        final Callback<Response> callback = cb;
        AppController.getApi().setUnLikePost(id, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                callback.success(response, response2);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }
}


