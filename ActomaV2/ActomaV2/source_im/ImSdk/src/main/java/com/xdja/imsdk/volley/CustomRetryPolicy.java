package com.xdja.imsdk.volley;

import com.xdja.imsdk.volley.error.VolleyError;

/**
 * 自定义网络超时策略
 * Created by xdjaxa on 2016/5/18.
 */
public class CustomRetryPolicy implements RetryPolicy{

    /** The max timeout milliseconds*/
    private final int mInitialTimeoutMs;

    /** The current timeout milliseconds*/
    private int mCurrentTimeoutMs;

    /** The current retry count. */
    private int mCurrentRetryCount;

    /** The total eclipse time milliseconds*/
    private int mEclipseTimeoutMs;

    /** The default socket timeout in milliseconds */
    public static final int DEFAULT_TIMEOUT_MS = 5000;

    /** The default number of retries */
    public static final int DEFAULT_MAX_RETRIES = 1;

    /**
     * Constructs a new retry policy using the default timeouts.
     */
    public CustomRetryPolicy() {
        this(DEFAULT_TIMEOUT_MS * DEFAULT_MAX_RETRIES, DEFAULT_TIMEOUT_MS);
    }

    /**
     * Constructs a new retry policy.
     * @param initialTimeoutMs The initial timeout for the policy.
     * @param currentTimeoutMs The current timeout for the policy.
     */
    public CustomRetryPolicy(int initialTimeoutMs, int currentTimeoutMs) {
        if (currentTimeoutMs <= 0 || initialTimeoutMs <= 0){
            currentTimeoutMs = DEFAULT_TIMEOUT_MS;
            initialTimeoutMs = DEFAULT_TIMEOUT_MS;
        }
        mInitialTimeoutMs = initialTimeoutMs;
        mCurrentTimeoutMs = currentTimeoutMs;
    }

    /**
     * Returns the current timeout.
     */
    @Override
    public int getCurrentTimeout() {
        return mCurrentTimeoutMs;
    }

    /**
     * Returns the current retry count.
     */
    @Override
    public int getCurrentRetryCount() {
        return mCurrentRetryCount;
    }

    /**
     * Prepares for the next retry by applying a backoff to the timeout.
     * @param error The error code of the last attempt.
     */
    @Override
    public void retry(VolleyError error, long expendTimeMs) throws VolleyError {
        mCurrentRetryCount++;
        mEclipseTimeoutMs += expendTimeMs;
        //mCurrentTimeoutMs += (mCurrentTimeoutMs * mBackoffMultiplier);
        VolleyLog.d("init timeout " + mInitialTimeoutMs +
                " ms, currently elapsed time " + mEclipseTimeoutMs + " ms and retry count " + mCurrentRetryCount);
        if (!hasAttemptRemaining()) {
            throw error;
        }
    }

    @Override
    public int getElapsedTimeTimeoutMs() {
        return mEclipseTimeoutMs;
    }

    public int getInitialTimeoutMs() {
        return mInitialTimeoutMs;
    }

    /**
     * Returns true if this policy has attempts remaining, false otherwise.
     */
    @Override
    public boolean hasAttemptRemaining() {
     
        return mEclipseTimeoutMs < mInitialTimeoutMs;
    }
}
