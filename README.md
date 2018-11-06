
SimplePolygonView 可以轻松构建构建M边N层的正多边形统计图，以及各维度的显示。

blog：https://blog.csdn.net/lylddingHFFW/article/details/83789078


### 效果图展示： (上传图片被压缩了不清晰)

<div align="center">
<img src="https://img-blog.csdnimg.cn/201811061606340.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2x5bGRkaW5nSEZGVw==,size_16,color_FFFFFF,t_70"
width ="40%" /></div>

gradle：

```
 allprojects {
  repositories {
   ...
   maven { url 'https://jitpack.io' }
  }
 }
 ```
 
dependency：

```
 dependencies {
         implementation 'com.github.lyldding:SimplePolygonView:Tag'
 }

```


### xml代码：

```
<!--view-->
<com.lyldding.library.SimplePolygonView
    android:id="@+id/demo_view"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:polygon_radiusMax="60" />
<!--view1-->
<com.lyldding.library.SimplePolygonView
    android:id="@+id/demo_view1"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:polygon_cornerRadius="10"
    app:polygon_layers="3"
    app:polygon_radiusMax="60"
    app:polygon_rotation="270"
    app:polygon_sides="3"
    app:polygon_vertexLinePaintColor="@color/colorAccent" />
<!--view2-->
<com.lyldding.library.SimplePolygonView
    android:id="@+id/demo_view2"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:polygon_cornerRadius="7"
    app:polygon_innerFillColor="#ffeebb"
    app:polygon_innerLayer="2"
    app:polygon_layers="4"
    app:polygon_radiusMax="60"
    app:polygon_rotation="270"
    app:polygon_sides="3"
    app:polygon_vertexLinePaintColor="@color/colorAccent" />
<!--view3-->
<com.lyldding.library.SimplePolygonView
    android:id="@+id/demo_view3"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:polygon_cornerRadius="7"
    app:polygon_dimFillColor="#33123FFF"
    app:polygon_innerFillColor="#ffeebb"
    app:polygon_innerLayer="1"
    app:polygon_layers="4"
    app:polygon_radiusMax="80"
    app:polygon_rotation="270"
    app:polygon_sides="6"
    app:polygon_vertexLinePaintColor="@color/colorAccent" />

```

### java代码：

显示维度区域时，要先设置各维度百分比。

```
SimplePolygonView dimView = findViewById(R.id.demo_view3);
int sides = dimView.getSides();
List<Float> dimPercentages = new ArrayList<>();
for (int index = 0; index < sides; index++) {
    dimPercentages.add((index + 1f) / (sides + 1f));
}
dimView.setDimPercentages(dimPercentages);
dimView.setPolygonShowDim(true);
```

### 主要思路：

 1. 计算顶点位置；
 2. 画出从中心向各顶点的连线；
 3. 绘制多边形；
 4. 绘制维度区域。

### 属性表：

```
<!--层数-->
<attr name="polygon_layers" format="integer" />
<!--边数-->
<attr name="polygon_sides" format="integer" />
<!--旋转度-->
<attr name="polygon_rotation" format="integer" />
<!--圆角半径-->
<attr name="polygon_cornerRadius" format="integer" />
<!--维度顶点圆背景半径-->
<attr name="polygon_dimCircleRadiusBackground" format="integer" />
<!--维度顶点圆半径-->
<attr name="polygon_dimCircleRadius" format="integer" />
<!--多边形伸缩-->
<attr name="polygon_scale" format="float" />
<!--最外层半径-->
<attr name="polygon_radiusMax" format="integer" />
<!--指定内层-->
<attr name="polygon_innerLayer" format="integer" />
<!--最外层边宽度-->
<attr name="polygon_outerStrokeWidth" format="integer" />

<!--层边颜色-->
<attr name="polygon_strokeColor" format="reference|color" />
<!--指定内层填充颜色-->
<attr name="polygon_innerFillColor" format="reference|color" />
<!--顶点到圆心连线颜色-->
<attr name="polygon_vertexLinePaintColor" format="reference|color" />
<!--维度区域填充色-->
<attr name="polygon_dimFillColor" format="reference|color" />
<!--维度区域边颜色-->
<attr name="polygon_dimStrokeColor" format="reference|color" />
<!--维度区域顶点背景填充色-->
<attr name="polygon_dimCircleColorBackground" format="reference|color" />
<!--维度区域顶点填充色-->
<attr name="polygon_dimCircleColor" format="reference|color" />
```

