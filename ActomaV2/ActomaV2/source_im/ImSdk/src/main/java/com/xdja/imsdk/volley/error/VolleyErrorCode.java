package com.xdja.imsdk.volley.error;

/**
 * Created by xdjaxa on 2016/10/10.
 */

public class VolleyErrorCode {

    /**
     * {@link VolleyError}
     */
    public static final int VOLLEY_ERROR = 40000;

    /**
     * {@link AuthFailureError}
     */
    public static final int AUTH_FAILURE_ERROR = VOLLEY_ERROR + 1;

    /**
     * {@link MalformedURLError}
     */
    public static final int MALFORM_URL_ERROR = VOLLEY_ERROR + 2;

    /**
     * {@link NetworkError}
     */
    public static final int NETWORK_ERROR = VOLLEY_ERROR + 3;

    /**
     * {@link NoConnectionError}
     */
    public static final int NO_CONNECTION_ERROR = VOLLEY_ERROR + 4;

    /**
     * {@link ParseError}
     */
    public static final int PARSE_ERROR = VOLLEY_ERROR + 5;

    /**
     * {@link ServerError}
     */
    public static final int SERVER_ERROR = VOLLEY_ERROR + 6;

    /**
     * {@link TimeoutError}
     */
    public static final int TIMEOUT_ERROR = VOLLEY_ERROR + 7;
}
