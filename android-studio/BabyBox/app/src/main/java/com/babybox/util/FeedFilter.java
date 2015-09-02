package com.babybox.util;

/**
 *
 */
public class FeedFilter {

    public enum FeedType {
        HOME_EXPLORE,
        HOME_TRENDING,
        HOME_FOLLOWING,
        CATEGORY_POPULAR,
        CATEGORY_NEWEST,
        CATEGORY_PRICE_LOW_HIGH,
        CATEGORY_PRICE_HIGH_LOW
    }

    public enum FeedProductType {
        ALL,
        NEW,
        USED
    }

    public FeedType feedType;
    public FeedProductType productType;
    public Long objId;

    public FeedFilter(FeedType feedType) {
        this(feedType, FeedProductType.ALL, -1L);
    }

    public FeedFilter(FeedType feedType, FeedProductType productType) {
        this(feedType, productType, -1L);
    }

    public FeedFilter(FeedType feedType, FeedProductType productType, Long objId) {
        this.feedType = feedType;
        this.productType = productType;
        this.objId = objId;
    }
}
