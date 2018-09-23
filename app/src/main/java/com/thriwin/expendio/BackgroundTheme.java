package com.thriwin.expendio;

enum BackgroundTheme {
    WOOD,
    MOUNTAIN,
    STARWARS,
    NATURE,
    CITY,
    CARS, BIKE, LOVE;

    public int getResurceId() {
        int bgTheme = R.mipmap.mountain;
        switch (this) {
            case WOOD:
                bgTheme = R.mipmap.wood_bg;
                break;
            case MOUNTAIN:
                bgTheme = R.mipmap.mountain;
                break;
            case STARWARS:
                bgTheme = R.mipmap.star_wars;
                break;
            case NATURE:
                bgTheme = R.mipmap.nature;
                break;

            case CITY:
                bgTheme = R.mipmap.city;
                break;
            case CARS:
                bgTheme = R.mipmap.cars;
                break;
            case BIKE:
                bgTheme = R.mipmap.bike;
                break;
            case LOVE:
                bgTheme = R.mipmap.love;
                break;

        }
        return bgTheme;
    }

    public int getSmallResurceId() {
        int bgTheme = R.mipmap.small_mountain;
        switch (this) {
            case WOOD:
                bgTheme = R.mipmap.small_wood_bg;
                break;
            case MOUNTAIN:
                bgTheme = R.mipmap.small_mountain;
                break;
            case STARWARS:
                bgTheme = R.mipmap.small_star_wars;
                break;
            case NATURE:
                bgTheme = R.mipmap.small_nature;
                break;
            case LOVE:
                bgTheme = R.mipmap.small_love;
                break;

            case CITY:
                bgTheme = R.mipmap.small_city;
                break;
            case CARS:
                bgTheme = R.mipmap.small_cars;
                break;
            case BIKE:
                bgTheme = R.mipmap.small_bike;
                break;

        }
        return bgTheme;
    }
}
