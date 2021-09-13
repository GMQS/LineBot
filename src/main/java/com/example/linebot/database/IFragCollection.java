package com.example.linebot.database;

public interface IFragCollection {
    public static final int DISABLE = 0;
    public static final int IMAGE_SEARCH_MODE = 1;
    public static final int PRODUCT_SEARCH_MODE = 2;
    public static final int MOVIE_SEARCH_MODE = 3;
    public static final int SEARCH_MODE = 4;
    public static final int WIKI_SEARCH_MODE = 6;
    public static final int PIXIV_SEARCH_MODE = 7;

    public boolean isDisabled();
    public int getValue();
    
    
}
