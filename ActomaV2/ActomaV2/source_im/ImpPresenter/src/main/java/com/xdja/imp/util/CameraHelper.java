/*
 * Copyright (C) 2013 The Android Open Source Project
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

package com.xdja.imp.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.hardware.Camera;
import android.os.Build;

import java.util.List;

/**
 * 项目名称：短视频             <br>
 * 类描述  ：短视频摄像头管理类     <br>
 * 创建时间：2017/1/28        <br>
 * 修改记录：                 <br>
 *
 * @author jyg@xdja.com   <br>
 */
@SuppressWarnings("deprecation")
@SuppressLint("Deprecation")
public class CameraHelper {

    /**
     * Iterate over supported camera video sizes to see which one best fits the
     * dimensions of the given view while maintaining the aspect ratio. If none can,
     * be lenient with the aspect ratio.
     *
     * @param supportedVideoSizes Supported camera video sizes.
     * @param previewSizes Supported camera preview sizes.
     * @param w     The width of the view.
     * @param h     The height of the view.
     * @return Best match camera video size to fit in the view.
     */
    public static Camera.Size getOptimalVideoSize(List<Camera.Size> supportedVideoSizes,
            List<Camera.Size> previewSizes, int w, int h) {
        // Use a very small tolerance because we want an exact match.
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;

        // Supported video sizes list might be null, it means that we are allowed to use the preview
        // sizes
        //noinspection deprecation,deprecation
        @SuppressWarnings("deprecation") List<Camera.Size> videoSizes = previewSizes;
        //noinspection deprecation,deprecation
        @SuppressWarnings("deprecation") Camera.Size optimalSize = null;

        // Start with max value and refine as we iterate over available video sizes. This is the
        // minimum difference between view and camera height.
        double minDiff = Double.MAX_VALUE;

        // Try to find a video size that matches aspect ratio and the target view size.
        // Iterate over all available sizes and pick the largest size that can fit in the view and
        // still maintain the aspect ratio.
        //noinspection deprecation,deprecation,deprecation,deprecation,deprecation,deprecation
        for (Camera.Size size : videoSizes) {
            double ratio = (double) size.height / size.width;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.width - h) < minDiff && supportedVideoSizes.contains(size)) {
                optimalSize = size;
                minDiff = Math.abs(size.width - h);
            }
        }

        // Cannot find video size that matches the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            //noinspection deprecation,deprecation,deprecation,deprecation,deprecation,deprecation
            for (Camera.Size size : videoSizes) {
                if (Math.abs(size.width - h) < minDiff && supportedVideoSizes.contains(size)) {
                    optimalSize = size;
                    minDiff = Math.abs(size.width - h);
                }
            }
        }
        return optimalSize;
    }


    /**
     * @return the default rear/back facing camera on the device. Returns null if camera is not
     * available.
     */
    public static int getDefaultBackFacingCameraInstance() {
        //noinspection deprecation,deprecation,deprecation,deprecation,deprecation,deprecation
        return getDefaultCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
    }

    /**
     * @return the default front facing camera on the device. Returns null if camera is not
     * available.
     */
    public static int getDefaultFrontFacingCameraInstance() {
        //noinspection deprecation,deprecation,deprecation,deprecation,deprecation,deprecation
        return getDefaultCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
    }


    /**
     *
     * @param position Physical position of the camera i.e Camera.CameraInfo.CAMERA_FACING_FRONT
     *                 or Camera.CameraInfo.CAMERA_FACING_BACK.
     * @return the default camera on the device. Returns null if camera is not available.
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private static int getDefaultCamera(int position) {
        // Find the total number of cameras available
        //noinspection deprecation
        @SuppressWarnings("deprecation") int  mNumberOfCameras = Camera.getNumberOfCameras();

        // Find the ID of the back-facing ("default") camera
        //noinspection deprecation,deprecation,deprecation,deprecation
        @SuppressWarnings("deprecation") Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < mNumberOfCameras; i++) {
            //noinspection deprecation,deprecation,deprecation
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == position) {
                return i;
            }
        }

        return 0;
    }


    /**
     * 设置相机对焦模式
     *
     * @param focusMode 对焦模式
     * @param camera 照相机
     */
    public static void setCameraFocusMode(String focusMode, Camera camera) {
        //noinspection deprecation,deprecation
        @SuppressWarnings("deprecation") Camera.Parameters parameters = camera.getParameters();
        List<String> sfm = parameters.getSupportedFocusModes();
        if (sfm.contains(focusMode)) {
            parameters.setFocusMode(focusMode);
        }
        camera.setParameters(parameters);
    }

}
