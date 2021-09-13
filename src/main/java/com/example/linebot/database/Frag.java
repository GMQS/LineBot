package com.example.linebot.database;

public class Frag implements IFragCollection {
    private static final int MAX_VALUE = 7;
    private static final int MIN_VALUE = 0;
    private int value;

    public Frag(int value) {
        if (MIN_VALUE > this.value || this.value > MAX_VALUE) {
            throw new IllegalArgumentException("不正な値です");
        }
        this.value = value;
    }

    @Override
    public boolean isDisabled() {
        return this.value == DISABLE;
    }

    @Override
    public int getValue() {
        switch (this.value) {
            case SEARCH_MODE:
                return SEARCH_MODE;
            case IMAGE_SEARCH_MODE:
                return IMAGE_SEARCH_MODE;
            case PRODUCT_SEARCH_MODE:
                return PRODUCT_SEARCH_MODE;
            case MOVIE_SEARCH_MODE:
                return MOVIE_SEARCH_MODE;
            case WIKI_SEARCH_MODE:
                return WIKI_SEARCH_MODE;
            case PIXIV_SEARCH_MODE:
                return PIXIV_SEARCH_MODE;
            default:
                return DISABLE;
        }
    }

}
