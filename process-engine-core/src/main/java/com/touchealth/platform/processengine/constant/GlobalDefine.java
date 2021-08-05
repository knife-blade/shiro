package com.touchealth.platform.processengine.constant;

public class GlobalDefine {

    public static class Jwt {
        /**
         * 数据请求返回码
         */
        public static final int RESCODE_SUCCESS = 1000;                //成功
        /**
         * Jwt
         */
        public static final String JWT_ID = "Jwt";
        public static final String JWT_SECRET = "7786df7fc3a34e26a61c034d5ec8245d";
        /**
         * jwtToken有效时间
         */
        public static final long JWT_TTL = 365 * 24 * 60 * 60 * 1000L;  //millisecond
        public static final long JWT_SERVEN_DAY_TTL = 7 * 24 * 60 * 60 * 1000L;  //millisecond
        public static final long JWT_REFRESH_INTERVAL = 55 * 60 * 1000L;  //millisecond

        /**
         * jwtToken有效刷新时间
         */
        public static final long JWT_REFRESH_TTL = 7 * 24 * 60 * 60 * 1000L;  //millisecond

        /**
         * jwtToken缓冲失效时间
         */
        public static final long JWT_PADDING_TTL = 3 * 60 * 1000L;  //millisecond
        /**
         * token前缀
         */
        public static final String TOKEN_PREFIX = "Base ";
    }
}
