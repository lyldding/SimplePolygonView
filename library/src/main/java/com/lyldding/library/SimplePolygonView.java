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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 * @author lyldding
 */
public class SimplePolygonView extends View {
    /**
     * radius max dp value
     */
    private int dpRadiusMax;
    /**
     * dimension max dp value
     */
    private float dpDimMax;
    /**
     * layer value
     */
    private int layers;
    /**
     * side value
     */
    private int sides;

    private PolygonDrawHelper polygonDrawHelper;
    //各层
    private Paint polygonStrokePaint;
    //各层填充
    private Paint polygonFillPaint;
    //圆心到顶点连线
    private Paint sidePaint;
    //覆盖区域
    private Paint areaFillPaint;
    //覆盖区域连线
    private Paint areaStrokePaint;
    //覆盖区顶点圆
    private Paint circleFillPaint;


    private float cornerRadius;
    private float rotation;
    private float scale;
    private float pxDimMax;
    private float pxRadiusMax;
    private float radiusCircleBottom;
    private float radiusCircleTop;

    private final Path strokePath = new Path();

    private List<Float> maxPointListX;
    private List<Float> maxPointListY;

    private List<Float> minPointListX;
    private List<Float> minPointListY;

    private List<Float> dimPointListX;
    private List<Float> dimPointListY;

    private List<Float> mDimNums;
    private List<String> mColors;

    private float centerX = 0;
    private float centerY = 0;

    public SimplePolygonView(Context context) {
        this(context, null);
    }

    public SimplePolygonView(final Context context, @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public SimplePolygonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        layers = 3;
        sides = 5;
        rotation = 0;
        cornerRadius = 20;
        radiusCircleBottom = 11;
        radiusCircleTop = 8;
        scale = 1;
        dpRadiusMax = 100;
        dpDimMax = 100;
        init(context);
    }

    private void init(Context context) {
        mDimNums = new ArrayList<>(sides);
        mColors = new ArrayList<>(sides);
        for (int index = 0; index < sides; index++) {
            mDimNums.add((float) Utils.dp2px(context, dpDimMax * 0.6f));
            mColors.add("#FF4040");
        }
        maxPointListX = new ArrayList<>(sides);
        maxPointListY = new ArrayList<>(sides);
        minPointListX = new ArrayList<>(sides);
        minPointListY = new ArrayList<>(sides);
        dimPointListX = new ArrayList<>(sides);
        dimPointListY = new ArrayList<>(sides);

        polygonDrawHelper = new PolygonDrawHelper();

        polygonStrokePaint = new Paint(ANTI_ALIAS_FLAG);
        polygonStrokePaint.setColor(Color.BLACK);
        polygonStrokePaint.setStyle(Paint.Style.STROKE);

        polygonFillPaint = new Paint(ANTI_ALIAS_FLAG);
        polygonFillPaint.setColor(Color.LTGRAY);
        polygonFillPaint.setStyle(Paint.Style.FILL);

        sidePaint = new Paint(ANTI_ALIAS_FLAG);
        sidePaint.setColor(Color.GRAY);
        sidePaint.setStyle(Paint.Style.STROKE);
        sidePaint.setStrokeWidth(1);

        areaFillPaint = new Paint(ANTI_ALIAS_FLAG);
        areaFillPaint.setColor(Color.parseColor("#44FFDEAD"));
        areaFillPaint.setStyle(Paint.Style.FILL);

        areaStrokePaint = new Paint(ANTI_ALIAS_FLAG);
        areaStrokePaint.setColor(Color.YELLOW);
        areaStrokePaint.setStyle(Paint.Style.STROKE);
        areaStrokePaint.setStrokeWidth(6);

        circleFillPaint = new Paint(ANTI_ALIAS_FLAG);
        circleFillPaint.setStyle(Paint.Style.FILL);

        pxRadiusMax = Utils.dp2px(context, dpRadiusMax);
        pxDimMax = Utils.dp2px(context, dpDimMax);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        centerX = getWidth() / 2;
        centerY = getHeight() / 2;
        drawPolygon(canvas);
        computeDimPoint();
        drawLine(canvas);
        drawDimArea(canvas);
        drawCircle(canvas);
    }

    private void drawPolygon(Canvas canvas) {
        for (int i = 1; i <= layers; i++) {
            float radius = pxRadiusMax * i / layers;
            if (i == 1) {
                polygonDrawHelper.drawPolygon(
                        canvas,
                        sides,
                        centerX,
                        centerY,
                        radius,
                        cornerRadius,
                        rotation,
                        polygonFillPaint);
                PolygonDrawHelper.computeVertexPoint(minPointListX, minPointListY, centerX, centerY, radius - cornerRadius / 2, sides);
            }
            if (i == layers) {
                PolygonDrawHelper.computeVertexPoint(maxPointListX, maxPointListY, centerX, centerY, radius - cornerRadius / 2, sides);
            }

            polygonStrokePaint.setStrokeWidth(i == layers ? 6 : 1);
            polygonDrawHelper.constructPolygonPath(
                    strokePath,
                    sides,
                    centerX,
                    centerY,
                    radius,
                    cornerRadius,
                    rotation);

            canvas.drawPath(strokePath, polygonStrokePaint);
        }
    }

    private void computeDimPoint() {
        PolygonDrawHelper.computeDimPoint(dimPointListX, dimPointListY,
                centerX,
                centerY,
                mDimNums,
                pxRadiusMax,
                pxDimMax,
                sides);
    }

    /**
     * 画出从中心向各顶点的连线
     */
    private void drawLine(Canvas canvas) {

        Path path = new Path();
        final Matrix rotationMatrix = new Matrix();
        rotationMatrix.setRotate(rotation, centerX, centerY);
        for (int i = 0; i < sides; i++) {
            path.reset();
            path.moveTo(minPointListX.get(i), minPointListY.get(i));
            path.lineTo(maxPointListX.get(i), maxPointListY.get(i));
            path.transform(rotationMatrix);
            canvas.drawPath(path, sidePaint);
        }
    }

    private void drawDimArea(Canvas canvas) {
        Path path = new Path();
        path.reset();
        Matrix rotationMatrix = new Matrix();
        rotationMatrix.setRotate(rotation, centerX, centerY);
        for (int i = 0; i < sides; i++) {
            if (i == 0) {
                path.moveTo(dimPointListX.get(i), dimPointListY.get(i));
            } else {
                path.lineTo(dimPointListX.get(i), dimPointListY.get(i));
            }
        }
        path.close();
        path.transform(rotationMatrix);
        canvas.drawPath(path, areaFillPaint);
        canvas.drawPath(path, areaStrokePaint);
    }

    private void drawCircle(Canvas canvas) {
        Matrix rotationMatrix = new Matrix();
        rotationMatrix.setRotate(rotation, centerX, centerY);
        canvas.save();
        canvas.setMatrix(rotationMatrix);
        for (int i = 0; i < sides; i++) {
            circleFillPaint.setColor(Color.WHITE);
            canvas.drawCircle(dimPointListX.get(i), dimPointListY.get(i), radiusCircleBottom, circleFillPaint);
            circleFillPaint.setColor(Color.parseColor(mColors.get(i)));
            canvas.drawCircle(dimPointListX.get(i), dimPointListY.get(i), radiusCircleTop, circleFillPaint);
        }
        canvas.restore();
    }

    public int getNumberOfSides() {
        return sides;
    }

    public void setNumberOfSides(final int numberOfSides) {
        this.sides = numberOfSides;
        invalidate();
    }

    public void setCornerRadius(final float cornerRadius) {
        this.cornerRadius = cornerRadius;
        invalidate();
    }

    public void setPolygonRotation(final float rotation) {
        this.rotation = rotation;
        invalidate();
    }

    public void setScale(final float scale) {
        this.scale = scale;
        invalidate();
    }

    public void setDimNums(List<Float> dimNums) {
        mDimNums = dimNums;
    }

    public void setDimCircleColor(List<String> colors) {
        mColors = colors;
    }
}
