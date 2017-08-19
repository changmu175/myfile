/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xdja.imsdk.volley;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A request dispatch queue with a thread pool of dispatchers.
 *
 * Calling {@link #add(Request)} will enqueue the given Request for dispatch,
 * resolving from either cache or network on a worker thread, and then delivering
 * a parsed response on the main thread.
 */
public class RequestQueue {

    /** Used for generating monotonically-increasing sequence numbers for requests. */
    private AtomicInteger mSequenceGenerator = new AtomicInteger();

    /**
     * The set of all requests currently being processed by this RequestQueue. A Request
     * will be in this set if it is waiting in any queue or currently being processed by
     * any dispatcher.
     */
    private final Set<Request<?>> mCurrentRequests = new HashSet<Request<?>>();

    /** The queue of requests that are actually going out to the network. */
    private final PriorityBlockingQueue<Request<?>> mNetworkQueue =
        new PriorityBlockingQueue<Request<?>>();

    /** Number of network request dispatcher threads to start. */
    private static final int DEFAULT_NETWORK_THREAD_POOL_SIZE = 4;

    /** Network interface for performing requests. */
    private final Network mNetwork;

    /** The network dispatchers. */
    private NetworkDispatcher[] mDispatchers;

    /**
     * Creates the worker pool. Processing will not begin until {@link #start()} is called.
     *
     * @param network A Network interface for performing HTTP requests
     */
    public RequestQueue(Network network) {
        mNetwork = network;
        mDispatchers = new NetworkDispatcher[DEFAULT_NETWORK_THREAD_POOL_SIZE];
    }

    public RequestQueue(Network network, int threadPollSize) {
        if (threadPollSize <= 0) {
            threadPollSize = DEFAULT_NETWORK_THREAD_POOL_SIZE;
        }
        mNetwork = network;
        mDispatchers = new NetworkDispatcher[threadPollSize];
    }

    /**
     * Starts the dispatchers in this queue.
     */
    public void start() {
        stop();

        // Create network dispatchers (and corresponding threads) up to the pool size.
        for (int i = 0; i < mDispatchers.length; i++) {
            NetworkDispatcher networkDispatcher = new NetworkDispatcher(mNetworkQueue, mNetwork);
            mDispatchers[i] = networkDispatcher;
            networkDispatcher.start();
        }
    }

    /**
     * Stops the cache and network dispatchers.
     */
    public void stop() {
        for (int i = 0; i < mDispatchers.length; i++) {
            if (mDispatchers[i] != null) {
                mDispatchers[i].quit();
            }
        }
    }
    
    public void resume() {
        for (int i = 0; i < mDispatchers.length; i++) {
            if (mDispatchers[i] != null) {
                mDispatchers[i].resumeTask();
            }
        }
    }
    
    public void pause() {
        for (int i = 0; i < mDispatchers.length; i++) {
            if (mDispatchers[i] != null) {
                mDispatchers[i].pauseTask();
            }
        }
    }

    /**
     * Gets the thread pool size.
     */
    public int getThreadPoolSize() {
        return mDispatchers.length;
    }

    /**
     * Gets a sequence number.
     */
    public int getSequenceNumber() {
        return mSequenceGenerator.incrementAndGet();
    }


    /**
     * A simple predicate or filter interface for Requests, for use by
     * {@link RequestQueue#cancelAll(RequestFilter)}.
     */
    public interface RequestFilter {
        public boolean apply(Request<?> request);
    }

    /**
     * Cancels all requests in this queue.
     */
    public void cancelAll(){
        synchronized (mCurrentRequests) {

            try {
                //遇到情况，当正在执行任务时退出
                Iterator<Request<?>> iterator = mCurrentRequests.iterator();
                while (iterator.hasNext()) {
                    Request<?> request = iterator.next();
                    if (request != null ) {
                        request.cancel();
                        iterator.remove();
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * if the request is exist in Queue.
     * @param tag
     * @param url
     * @return
     */
    public boolean isExist(Object tag, String url) {
        for (Request<?> request : mCurrentRequests) {
            if (tag != null && url != null &&
                    tag.equals(request.getTag()) &&
                    url.equals(request.getUrl())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Cancels all requests in this queue for which the given filter applies.
     * @param filter The filtering function to use
     */
    public void cancelAll(RequestFilter filter) {
        synchronized (mCurrentRequests) {

            try {
                //遇到情况，当正在执行任务时退出
                Iterator<Request<?>> iterator = mCurrentRequests.iterator();
                while (iterator.hasNext()) {
                    Request<?> request = iterator.next();
                    if (filter.apply(request)) {
                        VolleyLog.d("cancel request:" + request.getUrl() +
                                ", Tag:" + request.getTag());
                        request.cancel();
                        request.deliverCanceled();
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * Cancels all requests in this queue with the given tag. Tag must be non-null
     * and equality is by identity.
     */
    public void cancelAll(final Object tag) {
        if (tag == null) {
            throw new IllegalArgumentException("Cannot cancelAll with a null tag");
        }
        VolleyLog.d("Cancels request in this queue with the given tag " + tag);
        cancelAll(new RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                if (tag instanceof  String){
                    return tag.equals(request.getTag());
                }
                return request.getTag() == tag ;
            }
        });
    }

    /**
     * Adds a Request to the dispatch queue.
     * @param request The request to service
     * @return The passed-in request
     */
    public <T> Request<T> add(Request<T> request) {
        // Tag the request as belonging to this queue and add it to the set of current requests.
        synchronized (mCurrentRequests) {
            if (isExist(request.getTag(), request.getUrl())) {
                VolleyLog.d("Request exist in Queue. tag:" + request.getTag() +
                        ", url:" + request.getUrl());
                return request;
            }
            request.setRequestQueue(this);
            mCurrentRequests.add(request);
            VolleyLog.d("Add a Request to the current request queue " +
                    mCurrentRequests.size());
        }

        // Process requests in the order they are added.
        request.setSequence(getSequenceNumber());
        request.addMarker("add-to-queue");

        mNetworkQueue.add(request);
        VolleyLog.d("Add a Request to the dispatch queue " + mNetworkQueue.size());

        return request;
    }

    /**
     * Called from {@link Request#finish(String)}, indicating that processing of the given request
     * has finished.
     *
     * <p>Releases waiting requests for <code>request.getCacheKey()</code> if
     *      <code>request.shouldCache()</code>.</p>
     */
    <T> void finish(Request<T> request) {
        // Remove from the set of requests currently being processed.
        synchronized (mCurrentRequests) {
            mCurrentRequests.remove(request);
        }
        VolleyLog.d("Remove a Request from the current queue " +
                mCurrentRequests.size());
    }
}
