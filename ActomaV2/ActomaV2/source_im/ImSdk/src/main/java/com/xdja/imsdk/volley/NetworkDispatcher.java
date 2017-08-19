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

import android.annotation.TargetApi;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Process;
import android.os.SystemClock;

import com.xdja.imsdk.volley.error.VolleyError;

import java.util.concurrent.BlockingQueue;

/**
 * Provides a thread for performing network dispatch from a queue of requests.
 *
 * Requests added to the specified queue are processed from the network via a
 * specified {@link Network} interface. Responses are committed to cache, if
 * eligible, using a specified {@link Cache} interface. Valid responses and
 */
public class NetworkDispatcher extends Thread {
    /** The queue of requests to service. */
    private final BlockingQueue<Request<?>> mQueue;
    /** The network interface for processing requests. */
    private final Network mNetwork;
    /** Used for telling us to die. */
    private volatile boolean mQuit = false;

    private volatile boolean mPause = false;
    private Object mPauseLock = new Object();

    /**
     * Creates a new network dispatcher thread.  You must call {@link #start()}
     * in order to begin processing.
     *
     * @param queue Queue of incoming requests for triage
     * @param network Network interface to use for performing requests
     */
    public NetworkDispatcher(BlockingQueue<Request<?>> queue, Network network) {
        mQueue = queue;
        mNetwork = network;
    }

    /**
     * Forces this dispatcher to quit immediately.  If any requests are still in
     * the queue, they are not guaranteed to be processed.
     */
    public void quit() {
        resumeTask();
        mQuit = true;
        interrupt();
    }

    public void resumeTask() {
        mPause = false;
        synchronized (mPauseLock) {
            mPauseLock.notifyAll();
        }
    }

    public void pauseTask() {
        mPause = true;
    }


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void addTrafficStatsTag(Request<?> request) {
        // Tag the request (if API >= 14)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            TrafficStats.setThreadStatsTag(request.getTrafficStatsTag());
        }
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        while (true) {
            long startTimeMs = SystemClock.elapsedRealtime();
            Request<?> request;
            try {
                if (mPause) {
                    synchronized (mPauseLock) {
                        mPauseLock.wait();
                    }
                }
                // Take a request from the queue.
                VolleyLog.d("Take a request from the queue " + mQueue.size() +
                        ", Thread:" + getName());
                request = mQueue.take();
            } catch (InterruptedException e) {
                // We may have been interrupted because it was time to quit.
                if (mQuit) {
                    return;
                }
                continue;
            }

            try {
                request.addMarker("network-queue-take");

                // If the request was cancelled already, do not perform the
                // network request.
                if (request.isCanceled()) {
                    request.finish("network-discard-cancelled");
                    continue;
                }

                addTrafficStatsTag(request);

                // Perform the network request.
                NetworkResponse networkResponse = mNetwork.performRequest(request);
                request.addMarker("network-http-complete");

                // If the server returned 304 AND we delivered a response already,
                // we're done -- don't deliver a second identical response.
                if (networkResponse.notModified && request.hasHadResponseDelivered()) {
                    request.finish("not-modified");
                    continue;
                }

                // Parse the response here on the worker thread.
                Response<?> response = request.parseNetworkResponse(networkResponse);
                request.addMarker("network-parse-complete");

                // Post the response back.
                request.markDelivered();

                deliverResponse(request, response);
            } catch (VolleyError volleyError) {

                request.finish("post-error");

                volleyError.setNetworkTimeMs(SystemClock.elapsedRealtime() - startTimeMs);
                request.deliverError(volleyError);
            } catch (Exception e) {
                VolleyLog.w(e, "Unhandled exception %s", e.toString());

                request.finish("post-error");

                VolleyError volleyError = new VolleyError(e);
                volleyError.setNetworkTimeMs(SystemClock.elapsedRealtime() - startTimeMs);
                request.deliverError(volleyError);
            }
        }
    }

    public void deliverResponse(Request request, Response response) {
        if (request.isCanceled()) {
            request.finish("canceled-at-delivery");
            request.deliverCanceled();
            return;
        }

        // Deliver a normal response or error, depending.
        if (response.isSuccess()) {
            request.deliverResponse(response.result);
        } else {
            request.deliverError(response.error);
        }

        // If this is an intermediate response, add a marker, otherwise we're done
        // and the request can be finished.
        if (response.intermediate) {
            request.addMarker("intermediate-response");
        } else {
            request.finish("done");
        }
    }
}
