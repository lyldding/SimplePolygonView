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
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
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
    private static final String TAG = "SimplePolygonView";

    /**
     * 最外围半径
     */
    private int radiusMax;
    /**
     * true 显示维度区域
     */
    private boolean isShowDim;

    /**
     * the value of layer
     */
    private int layers;
    /**
     * 指定内层
     */
    private int innerLayer;
    /**
     * the value of side
     */
    private int sides;
    /**
     * 最外层宽度
     */
    private int outerStrokeWidth;

    /**
     * 正多边形各边颜色
     */
    private Paint polygonStrokePaint;
    private int polygonStrokeColor;
    /**
     * 最内层多边形填充颜色
     */
    private Paint polygonInnerFillPaint;
    private int polygonInnerFillColor;
    /**
     * 各层顶点之间连线
     */
    private Paint vertexLinePaint;
    private int vertexLinePaintColor;
    /**
     * 维度区域填充颜色
     */
    private Paint dimFillPaint;
    private int dimFillColor;
    /**
     * 维度边线颜色
     */
    private Paint dimStrokePaint;
    private int dimStrokeColor;
    /**
     * 维度顶点圆颜色填充
     */
    private Paint dimCircleFillPaint;
    private int dimCircleColorBackground;
    private int dimCircleColor;

    private Path tempPath;


    private float cornerRadius;
    private float rotation;
    private float scale;
    private int radiusMaxScale;
    private float dimCircleRadiusBackground;
    private float dimCircleRadius;

    private List<Float> maxPointListX;
    private List<Float> maxPointListY;

    private List<Float> minPointListX;
    private List<Float> minPointListY;

    private List<Float> dimPointListX;
    private List<Float> dimPointListY;

    private List<Float> mDimPercentages;
    private float centerX;
    private float centerY;

    private Context context;

    public SimplePolygonView(Context context) {
        this(context, null);
    }

    public SimplePolygonView(final Context context, @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public SimplePolygonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SimplePolygonView);
        layers = array.getInt(R.styleable.SimplePolygonView_polygon_layers, 3);
        if (layers < 1) {
            throw new IllegalArgumentException(TAG + " : polygon_layers should >= 1");
        }
        sides = array.getInt(R.styleable.SimplePolygonView_polygon_sides, 5);
        rotation = array.getInt(R.styleable.SimplePolygonView_polygon_rotation, 0);
        cornerRadius = Utils.dp2px(context, array.getInt(R.styleable.SimplePolygonView_polygon_cornerRadius, 7));
        dimCircleRadiusBackground = Utils.dp2px(context, array.getInt(R.styleable.SimplePolygonView_polygon_dimCircleRadiusBackground, 5));
        dimCircleRadius = Utils.dp2px(context, array.getInt(R.styleable.SimplePolygonView_polygon_dimCircleRadius, 4));
        scale = array.getFloat(R.styleable.SimplePolygonView_polygon_scale, 1.0f);
        radiusMax = Utils.dp2px(context, array.getInt(R.styleable.SimplePolygonView_polygon_radiusMax, 100));

        innerLayer = array.getInt(R.styleable.SimplePolygonView_polygon_innerLayer, 0);
        if (innerLayer < 0 || innerLayer > sides) {
            throw new IllegalArgumentException(TAG + " : polygon_innerLayer 0< innerLayer <= sides");
        }
        outerStrokeWidth = Utils.dp2px(context, array.getInt(R.styleable.SimplePolygonView_polygon_outerStrokeWidth, 2));

        polygonStrokeColor = array.getColor(R.styleable.SimplePolygonView_polygon_StrokeColor, Color.BLACK);
        polygonInnerFillColor = array.getColor(R.styleable.SimplePolygonView_polygon_innerFillColor, Color.LTGRAY);
        vertexLinePaintColor = array.getColor(R.styleable.SimplePolygonView_polygon_vertexLinePaintColor, Color.GRAY);
        dimFillColor = array.getColor(R.styleable.SimplePolygonView_polygon_dimFillColor, Color.parseColor("#44FFDEAD"));
        dimStrokeColor = array.getColor(R.styleable.SimplePolygonView_polygon_dimStrokeColor, Color.YELLOW);
        dimCircleColorBackground = array.getColor(R.styleable.SimplePolygonView_polygon_dimCircleColorBackground, Color.WHITE);
        dimCircleColor = array.getColor(R.styleable.SimplePolygonView_polygon_dimCircleColor, Color.RED);

        array.recycle();
        init(context);
    }

    private void init(Context context) {
        mDimPercentages = new ArrayList<>();
        maxPointListX = new ArrayList<>();
        maxPointListY = new ArrayList<>();
        minPointListX = new ArrayList<>();
        minPointListY = new ArrayList<>();
        dimPointListX = new ArrayList<>();
        dimPointListY = new ArrayList<>();

        polygonStrokePaint = new Paint(ANTI_ALIAS_FLAG);
        polygonStrokePaint.setColor(polygonStrokeColor);
        polygonStrokePaint.setStyle(Paint.Style.STROKE);

        polygonInnerFillPaint = new Paint(ANTI_ALIAS_FLAG);
        polygonInnerFillPaint.setColor(polygonInnerFillColor);
        polygonInnerFillPaint.setStyle(Paint.Style.FILL);

        vertexLinePaint = new Paint(ANTI_ALIAS_FLAG);
        vertexLinePaint.setColor(vertexLinePaintColor);
        vertexLinePaint.setStyle(Paint.Style.STROKE);
        vertexLinePaint.setStrokeWidth(1);

        dimFillPaint = new Paint(ANTI_ALIAS_FLAG);
        dimFillPaint.setColor(dimFillColor);
        dimFillPaint.setStyle(Paint.Style.FILL);

        dimStrokePaint = new Paint(ANTI_ALIAS_FLAG);
        dimStrokePaint.setColor(dimStrokeColor);
        dimStrokePaint.setStyle(Paint.Style.STROKE);
        dimStrokePaint.setStrokeWidth(outerStrokeWidth);

        dimCircleFillPaint = new Paint(ANTI_ALIAS_FLAG);
        dimCircleFillPaint.setStyle(Paint.Style.FILL);

        tempPath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        radiusMaxScale = (int) (scale * (radiusMax));
        setMeasuredDimension(computeMeasuredDimension(widthMeasureSpec, radiusMaxScale * 2), computeMeasuredDimension(heightMeasureSpec, radiusMaxScale * 2));
    }

    private int computeMeasuredDimension(int measureSpec, int defaultSize) {
        int size = defaultSize;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            size = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            size = defaultSize;
        } else if (specMode == MeasureSpec.UNSPECIFIED) {
            size = defaultSize;
        }
        return size;
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        centerX = getWidth() / 2;
        centerY = getHeight() / 2;
        radiusMaxScale -= outerStrokeWidth / 2;
        canvas.translate(centerX, centerY);
        canvas.rotate(rotation);

        computePoint();

        drawLine(canvas);
        drawPolygon(canvas);
        if (isShowDim) {
            drawDimArea(canvas);
            drawDimCircle(canvas);
        }
    }

    /**
     * 计算顶点位置
     */
    private void computePoint() {
        if (isShowDim) {
            PolygonDrawHelper.getInstance().computeDimPoint(dimPointListX, dimPointListY, mDimPercentages, radiusMaxScale, sides);
        }
        PolygonDrawHelper.getInstance().computeVertexPoint(minPointListX, minPointListY, radiusMaxScale * innerLayer / layers, sides, cornerRadius * innerLayer / layers);
        PolygonDrawHelper.getInstance().computeVertexPoint(maxPointListX, maxPointListY, radiusMaxScale, sides, cornerRadius);
    }

    /**
     * 绘制多边形
     */
    private void drawPolygon(Canvas canvas) {
        for (int i = 1; i <= layers; i++) {
            float radius = radiusMaxScale * i / layers;
            if (i == innerLayer) {
                // polygonInnerFillPaint.setPathEffect(new CornerPathEffect(cornerRadius * i / layers));
                PolygonDrawHelper.getInstance().drawPolygon(canvas, sides, centerX, centerY, radius, cornerRadius * i / layers, polygonInnerFillPaint);
            }

            polygonStrokePaint.setStrokeWidth(i != layers ? 1f : outerStrokeWidth);
            //polygonStrokePaint.setPathEffect(new CornerPathEffect(cornerRadius * i / layers));
            PolygonDrawHelper.getInstance().drawPath(canvas, sides, centerX, centerY, radius, cornerRadius * i / layers, polygonStrokePaint);
        }
    }

    /**
     * 画出从中心向各顶点的连线
     */
    private void drawLine(Canvas canvas) {
        for (int i = 0; i < sides; i++) {
            tempPath.reset();
            tempPath.moveTo(minPointListX.get(i), minPointListY.get(i));
            tempPath.lineTo(maxPointListX.get(i), maxPointListY.get(i));
            canvas.drawPath(tempPath, vertexLinePaint);
        }
    }

    /**
     * 绘制维度区域
     */
    private void drawDimArea(Canvas canvas) {
        tempPath.reset();
        for (int i = 0; i < sides; i++) {
            if (i == 0) {
                tempPath.moveTo(dimPointListX.get(i), dimPointListY.get(i));
            } else {
                tempPath.lineTo(dimPointListX.get(i), dimPointListY.get(i));
            }
        }
        tempPath.close();
        canvas.drawPath(tempPath, dimFillPaint);
        canvas.drawPath(tempPath, dimStrokePaint);
    }

    /**
     * 绘制维度顶点
     */
    private void drawDimCircle(Canvas canvas) {
        for (int i = 0; i < sides; i++) {
            dimCircleFillPaint.setColor(dimCircleColorBackground);
            canvas.drawCircle(dimPointListX.get(i), dimPointListY.get(i), dimCircleRadiusBackground, dimCircleFillPaint);
            dimCircleFillPaint.setColor(dimCircleColor);
            canvas.drawCircle(dimPointListX.get(i), dimPointListY.get(i), dimCircleRadius, dimCircleFillPaint);
        }
    }


    /**
     * @param layers 多边形层数
     */
    public void setPolygonLayers(@IntRange(from = 1) int layers) {
        this.layers = layers;
    }

    /**
     * @return 多边形边数
     */
    public int getSides() {
        return sides;
    }

    /**
     * @param sides 多边形边数
     */
    public void setPolygonSides(@IntRange(from = 3) int sides) {
        this.sides = sides;
    }

    /**
     * @param rotation 多边形旋转角度
     */
    public void setPolygonRotation(int rotation) {
        this.rotation = rotation;
    }

    /**
     * @param cornerRadius 圆角弧度半径
     */
    public void setPolygonCornerRadius(int cornerRadius) {
        this.cornerRadius = Utils.dp2px(context, cornerRadius);
    }

    /**
     * @param radius 维度圆点背景半径
     */
    public void setPolygonDimCircleRadiusBackground(int radius) {
        this.dimCircleRadiusBackground = Utils.dp2px(context, radius);
    }

    /**
     * @param radius 维度圆点半径
     */
    public void setPolygonDimCircleRadius(int radius) {
        this.dimCircleRadius = Utils.dp2px(context, radius);
    }

    /**
     * @param scale 多边形scale
     */
    public void setPolygonScale(float scale) {
        this.scale = scale;
    }

    /**
     * @param value 多边形最外围半径
     */
    public void setPolygonRadiusMax(int value) {
        this.radiusMax = Utils.dp2px(context, value);
    }

    /**
     * @param value 多边形指定内层
     */
    public void setPolygonInnerLayer(int value) {
        this.innerLayer = value;
    }

    /**
     * @param value 多边形最外层边宽度
     */
    public void setPolygonOuterStrokeWidth(int value) {
        this.outerStrokeWidth = Utils.dp2px(context, value);
    }

    /**
     * @param isShowDim true 显示维度区域
     */
    public void setPolygonShowDim(boolean isShowDim) {
        if (sides != mDimPercentages.size()) {
            throw new IllegalArgumentException(TAG + " : should  setDimPercentages() first.");
        }
        this.isShowDim = isShowDim;
    }

    /**
     * @param dimPercentages 每个维度的百分比值 0.0 - 1.0
     */
    public void setDimPercentages(List<Float> dimPercentages) {
        if (sides != dimPercentages.size()) {
            throw new IllegalArgumentException(TAG + " : sides != mDimPercentages.size() sides = " + sides + " dimPercentages.size() = " + dimPercentages.size());
        }
        for (float percentage : dimPercentages) {
            if (percentage < 0.0 || percentage > 1.0) {
                throw new IllegalArgumentException(TAG + " : percentage = " + percentage);
            }
        }
        mDimPercentages = dimPercentages;
    }

    /**
     * @param color 维度顶点圆填充色
     */
    public void setColorDimCircle(@ColorInt int color) {
        dimCircleColor = color;
    }

    /**
     * @param color 维度顶点圆背景填充色
     */
    public void setColorDimCircleBackground(@ColorInt int color) {
        dimCircleColorBackground = color;
    }

    /**
     * @param color 多边形边颜色
     */
    public void setColorPolygonStroke(@ColorInt int color) {
        polygonStrokePaint.setColor(color);
    }

    /**
     * @param color 多边形内层填充色
     */
    public void setColorPolygonFill(@ColorInt int color) {
        polygonInnerFillPaint.setColor(color);
    }

    /**
     * @param color 各层顶点连线颜色
     */
    public void setColorVertexLinePaint(@ColorInt int color) {
        vertexLinePaint.setColor(color);
    }

    /**
     * @param color 维度区域填充色
     */
    public void setColorDimFill(@ColorInt int color) {
        dimFillPaint.setColor(color);
    }

    /**
     * @param color 维度区域边颜色
     */
    public void setColorDimStroke(@ColorInt int color) {
        dimStrokePaint.setColor(color);
    }
}
