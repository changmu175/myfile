/*
 * Copyright (C) 2012 The Android Open Source Project
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

package com.xdja.imsdk.volley.toolbox;

import android.os.Build;

import com.xdja.http.impl.client.HttpClients;
import com.xdja.imsdk.volley.Network;
import com.xdja.imsdk.volley.RequestQueue;
import com.xdja.imsdk.volley.stack.HttpClientStack;
import com.xdja.imsdk.volley.stack.HttpStack;
import com.xdja.imsdk.volley.stack.HurlStack;

public class Volley {

    /**
     * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
     *
     * @param stack An {@link HttpStack} to use for the network, or null for default.
     * @return A started {@link RequestQueue} instance.
     */
    public static RequestQueue newRequestQueue(HttpStack stack/*, RequestQueue queue*/) {
        if (stack == null) {
            if (Build.VERSION.SDK_INT >= 9) {
                stack = new HurlStack();
            } else {
                // Prior to Gingerbread, HttpUrlConnection was unreliable.
                // See: http://android-developers.blogspot.com/2011/09/androids-http-clients.html
                //stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
                stack = new HttpClientStack(HttpClients.createDefault());
            }
        }

        Network network = new BasicNetwork(stack);
        RequestQueue queue = new RequestQueue(network);
        queue.start();

        return queue;
    }

    /**
     * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
     *
     * @return A started {@link RequestQueue} instance.
     */
    public static RequestQueue newRequestQueue() {
        return newRequestQueue(null);
    }
}
