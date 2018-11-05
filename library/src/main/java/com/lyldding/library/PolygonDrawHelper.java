/*
 * Copyright 2018 Stuart Kent
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.lyldding.library;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * @author lyldding
 */
public class PolygonDrawHelper {
    private Path tempPath = new Path();

    private PolygonDrawHelper() {
    }

    private static class InstanceHolder {
        private static final PolygonDrawHelper INSTANCE = new PolygonDrawHelper();
    }

    public static PolygonDrawHelper getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * 绘制多边形区域
     *
     * @param canvas
     * @param sideCount 边数
     * @param radius    半径
     * @param paint     画笔
     */
    public void drawPolygon(
            @NonNull final Canvas canvas,
            @IntRange(from = 3) final int sideCount,
            @FloatRange(from = 0, fromInclusive = false) final float radius,
            @NonNull final Paint paint) {

        constructPolygonPath(
                tempPath,
                sideCount,
                radius);

        canvas.drawPath(tempPath, paint);
    }


    /**
     * 绘制路径
     *
     * @param canvas
     * @param sideCount 边数
     * @param radius    半径
     * @param paint     画笔
     */
    public void drawPath(
            @NonNull final Canvas canvas,
            @IntRange(from = 3) final int sideCount,
            @FloatRange(from = 0, fromInclusive = false) final float radius,
            @NonNull final Paint paint) {

        constructPolygonPath(
                tempPath,
                sideCount,
                radius);

        canvas.drawPath(tempPath, paint);
    }

    /**
     * 构建路径
     *
     * @param path      路径
     * @param sideCount 边数
     * @param radius    半径
     */
    public void constructPolygonPath(
            @NonNull final Path path,
            @IntRange(from = 3) final int sideCount,
            @FloatRange(from = 0, fromInclusive = false) final float radius) {
        path.reset();
        for (int index = 0; index < sideCount; index++) {
            final double angleToCorner = index * (360.0 / sideCount);
            final float cornerX = (float) (radius * Math.cos(toRadians(angleToCorner)));
            final float cornerY = (float) (radius * Math.sin(toRadians(angleToCorner)));

            if (index == 0) {
                path.moveTo(cornerX, cornerY);
            } else {
                path.lineTo(cornerX, cornerY);
            }
        }
        path.close();
    }


    /**
     * 计算定点坐标
     *
     * @param pointListX X轴坐标
     * @param pointListY Y轴坐标
     * @param radius     半径
     * @param sideCount  边数
     */
    public void computeVertexPoint(List<Float> pointListX, List<Float> pointListY, float radius, int sideCount, float cornerRadius) {
        double realRadius = computeRealRadius(radius, cornerRadius, sideCount);
        for (int cornerNumber = 0; cornerNumber < sideCount; cornerNumber++) {
            final double angleToCorner = cornerNumber * (360.0 / sideCount);
            pointListX.add((float) (realRadius * Math.cos(toRadians(angleToCorner))));
            pointListY.add((float) (realRadius * Math.sin(toRadians(angleToCorner))));
        }
    }

    /**
     * 计算顶点的真实半径
     *
     * @param radius       半径
     * @param cornerRadius 弧度半径
     * @param sideCount    边数
     * @return 真实半径
     */
    private double computeRealRadius(float radius, float cornerRadius, int sideCount) {
        double angleToCorner = 90 - (360.0f / sideCount) / 2;
        return radius - (cornerRadius / Math.sin(toRadians(angleToCorner)) - cornerRadius);
    }

    /**
     * 计算维度坐标
     *
     * @param pointListX X轴坐标
     * @param pointListY Y轴坐标
     * @param dims       各个维度值
     * @param dimMax     维度最大值
     * @param radiusMax  半径最大值
     * @param sideCount  边数
     */
    public void computeDimPoint(List<Float> pointListX, List<Float> pointListY, List<Float> dims, float radiusMax, float dimMax, int sideCount) {
        for (int index = 0; index < sideCount; index++) {
            final double angleToCorner = index * (360.0 / sideCount);
            float radius = dims.get(index) * radiusMax / dimMax;
            pointListX.add((float) (radius * Math.cos(toRadians(angleToCorner))));
            pointListY.add((float) (radius * Math.sin(toRadians(angleToCorner))));
        }
    }


    private static double toRadians(final double degrees) {
        return 2 * Math.PI * degrees / 360;
    }

}
