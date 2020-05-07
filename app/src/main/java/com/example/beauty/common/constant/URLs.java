package com.example.beauty.common.constant;

/**
 * @author huanlin-zhl
 * @date 2020/5/2 20:24
 */
public class URLs {

    private static final String BASE_URL = "http://192.168.0.104:8080";

    public static final String ADD_NEW_CLOTH = BASE_URL + "/cloth/add";

    public static final String ADD_NEW_SCHEDULE = BASE_URL + "/schedule/insert";

    public static final String FIND_ALL_SCHEDULE_BY_CLOTH_ID = BASE_URL + "/schedule/";

    public static final String UPDATE_SCHEDULE = BASE_URL + "/schedule/update";

    public static final String DELETE_CLOTH = BASE_URL + "/cloth/delete/";

    public static final String LOAD_IMAGE = BASE_URL + "/image/";

    public static final String GET_CLOTH = BASE_URL + "/cloth/";
}
