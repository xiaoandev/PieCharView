package com.example.piecharview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.piecharview.R;
import com.example.piecharview.bean.Pie;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义View————绘制扇形，并区分点击区域，实现不同区域点击效果
 *
 * @author uidq2429
 * @since 2020.12.10
 */

public class PieCharView extends View {

    private final static String TAG = "PieCharView";

    /**
     * 设置中心文字默认值
     */
    private final static String DEFAULT_CENTER_TEXT = "";
    /**
     * 设置内圆半径默认值
     */
    private final static float DEFAULT_RADIUS = 50;
    /**
     * 设置圆环半径默认值
     */
    private final static float DEFAULT_ROUND_WIDTH = 100;
    /**
     * 设置字体默认颜色
     */
    private final static int DEFAULT_TEXT_COLOR = Color.RED;
    /**
     * 设置扇形区域的默认颜色
     */
    private final static int DEFAULT_MODE_COLOR = Color.GRAY;
    /**
     * 设置扇形区域点击后的默认颜色
     */
    private final static int DEFAULT_TOUCH_MODE_COLOR = Color.BLUE;
    /**
     * 设置默认间隔线颜色
     */
    private final static int DEFAULT_LINE_COLOR = Color.WHITE;

    private final static int DEFAULT_LINE_WIDTH = 2;
    /**
     * 设置字体默认大小
     */
    private final static int DEFAULT_TEXT_SIZE = 15;
    /**
     * 存储需要绘制的文字的位置信息
     */
    private List<PointF> textList;
    /**
     * 存储需要绘制的间隔线起点和终点的位置信息
     */
    private List<PointF[]> lineList;
    /**
     * 判断是否显示扇区文字信息
     */
    private boolean isShowOutText = true;
    /**
     * 判断是否显示中心文字信息
     */
    private boolean isShowInText = true;
    /**
     * 判断是否显示扇形区域之间的间隔线
     */
    private boolean isShowIntervalLine = true;
    /**
     * 扇区文字大小
     */
    private int textOutSize;
    /**
     * 中心文字大小
     */
    private int textInSize;
    /**
     * 扇区文字颜色
     */
    private int textOutColor;
    /**
     * 中心文字颜色
     */
    private int textInColor;
    /**
     * 圆环宽度
     */
    private float roundWidth;
    /**
     * 内圆半径
     */
    private float stillRadius;
    /**
     * 中心文字
     */
    private String centerText;
    /**
     * 圆环 X 轴上的偏移量，大于 0 表示向 X 轴正方向移动，屏幕中显示为向右移动
     */
    private float offsetX;
    /**
     * 圆环 Y 轴上的偏移量，大于 0 表示向 Y 轴正方向移动，屏幕中显示为向下移动
     */
    private float offsetY;
    /**
     * 当前区域
     */
    private int currentMode;
    /**
     * 点击区域
     */
    private int touchMode;
    /**
     * 外圆中心圆点 X 坐标
     */
    private float centerX;
    /**
     * 外圆中心圆点 Y 坐标
     */
    private float centerY;
    /**
     * 文本中心位置半径
     */
    private float textRadius;
    /**
     * 显示文本的角度，相对外圆中心点而言
     */
    private float textAngle;
    /**
     * 设置未点击的扇形区域的颜色
     */
    private int unTouchModeColor;
    /**
     * 设置点击的扇形区域的颜色
     */
    private int touchModeColor;
    /**
     * 间隔线颜色
     */
    private int lineColor;
    /**
     * 间隔线宽度
     */
    private int lineWidth;

    private Paint piePaint, linePaint;
    private RectF pieInRectF, pieOutRectF, pieInTouchRectF, pieOutTouchRectF;
    private List<Pie> mList;
    private int[] basePieColors = new int[]{Color.parseColor("#FDA890"), Color.parseColor("#ABD1FD")
            , Color.parseColor("#FEC976"), Color.parseColor("#4ab3fd"), Color.parseColor("#ffc100"), Color.parseColor("#FEC976")};
    private float downX, downY;

    public PieCharView(Context context) {
        this(context, null);
    }

    public PieCharView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PieCharView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        init();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PieCharView);
        if (typedArray != null) {
            //初始化属性值
            roundWidth = typedArray.getDimension(R.styleable.PieCharView_roundWidth, DEFAULT_ROUND_WIDTH);
            stillRadius = typedArray.getDimension(R.styleable.PieCharView_stillRadius, DEFAULT_RADIUS);
            textOutSize = (int) typedArray.getDimension(R.styleable.PieCharView_textOutSize, DEFAULT_TEXT_SIZE);
            textOutColor = (int) typedArray.getDimension(R.styleable.PieCharView_textOutColor, DEFAULT_TEXT_COLOR);
            textInSize = (int) typedArray.getDimension(R.styleable.PieCharView_textInSize, DEFAULT_TEXT_SIZE);
            textInColor = (int) typedArray.getDimension(R.styleable.PieCharView_textInColor, DEFAULT_TEXT_COLOR);
            lineColor = (int) typedArray.getDimension(R.styleable.PieCharView_lineColor, DEFAULT_LINE_COLOR);
            lineWidth = (int) typedArray.getDimension(R.styleable.PieCharView_lineWidth, DEFAULT_LINE_WIDTH);
            touchModeColor = (int) typedArray.getDimension(R.styleable.PieCharView_touchMode, DEFAULT_TOUCH_MODE_COLOR);
            unTouchModeColor = (int) typedArray.getDimension(R.styleable.PieCharView_unTouchMode, DEFAULT_MODE_COLOR);
            typedArray.recycle(); //回收TypedArray
        }
    }

    private void init() {
        this.piePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        this.linePaint = new Paint();
        this.linePaint.setAntiAlias(true);
        this.linePaint.setDither(true);
        this.linePaint.setStyle(Paint.Style.STROKE);

        this.pieInRectF = new RectF();
        this.pieOutRectF = new RectF();
        this.pieOutTouchRectF = new RectF();
        this.pieInTouchRectF = new RectF();

        this.mList = new ArrayList<>();
        this.textList = new ArrayList<>();
        this.lineList = new ArrayList<>();

        this.centerText = DEFAULT_CENTER_TEXT;
        this.offsetX = 0;
        this.offsetY = 0;
        this.currentMode = 0;
        this.touchMode = 0;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //设置内圆外接矩形
        pieInRectF.left = roundWidth + offsetX;
        pieInRectF.top = roundWidth + offsetY;
        pieInRectF.right = roundWidth + 2 * stillRadius + offsetX;
        pieInRectF.bottom = roundWidth + 2 * stillRadius + offsetY;

        //设置外圆外接矩形
        pieOutRectF.left = pieInRectF.left - roundWidth;
        pieOutRectF.top = pieInRectF.top - roundWidth;
        pieOutRectF.right = pieInRectF.right + roundWidth;
        pieOutRectF.bottom = pieInRectF.bottom + roundWidth;

        centerX = pieOutRectF.centerX(); //外圆中心点 x 坐标
        centerY = pieOutRectF.centerY(); //外圆中心点 y 坐标
        textRadius = stillRadius + roundWidth / 2; //计算显示文字位置的半径
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        textList.clear();
        lineList.clear();
        textList = new ArrayList<>();
        lineList = new ArrayList<>();
        if (mList != null) {
            drawPie(canvas); //绘制饼图
            drawText(canvas); //绘制文字
            drawIntervalLine(canvas); //绘制间隔线
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            //按下
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                currentMode = 0;
                for (Pie pie : mList) { //循环判断点击事件位置在哪个区域
                    pie.setTouch(false);
                    if (pie.isInRegion(downX, downY)) {
                        pie.setTouch(true);
                        touchMode = currentMode;
                        Log.d(TAG, "Pie is " + pie.getContent());
                        invalidate(); //请求重新draw
                    }
                    currentMode += 1;
                }
                break;
        }
        return true;
    }

    /**
     * 绘制扇形区域之间的间隔线
     * @param canvas
     */
    private void drawIntervalLine(Canvas canvas) {
        if (isShowIntervalLine) {
            float lineStartAngle = -90;
            for (int i = 0; i < mList.size(); i++) {
                //间隔线起点
                PointF lineStartPoint = new PointF();
                lineStartPoint.x = (float) (centerX + stillRadius * Math.cos(Math.toRadians(lineStartAngle)));
                lineStartPoint.y = (float) (centerY + stillRadius * Math.sin(Math.toRadians(lineStartAngle)));
                //间隔线终点
                PointF lineStopPoint = new PointF();
                lineStopPoint.x = (float) (centerX + (roundWidth + stillRadius)
                                        * Math.cos(Math.toRadians(lineStartAngle)));
                lineStopPoint.y = (float) (centerY + (roundWidth + stillRadius)
                                        * Math.sin(Math.toRadians(lineStartAngle)));
                lineList.add(new PointF[]{lineStartPoint, lineStopPoint});
                lineStartAngle += mList.get(i).getPercent() / getSum() * 360f;
            }
            //循环绘制间隔线
            linePaint.setStrokeWidth(lineWidth);
            linePaint.setColor(lineColor);
            for (PointF[] fp : lineList) {
                canvas.drawLine(fp[0].x, fp[0].y, fp[1].x, fp[1].y, linePaint);
            }
        }
    }
    /**
     * 绘制文字
     * @param canvas
     */
    private void drawText(Canvas canvas) {

        if (isShowOutText) {
            float textStartAngle = -90;
            for (int i = 0; i < mList.size(); i++) {
                //文字位置角度 = 起始角度 + 扇形块角度/2
                textAngle = textStartAngle + mList.get(i).getPercent() / getSum() * 360f / 2;
                PointF pointF = new PointF();
                pointF.x = (float) (centerX + textRadius * Math.cos(Math.toRadians(textAngle)));
                pointF.y = (float) (centerY + textRadius * Math.sin(Math.toRadians(textAngle)));
                textList.add(pointF);
                textStartAngle += mList.get(i).getPercent() / getSum() * 360f;
            }
            //循环绘制扇区文字
            for (int i = 0; i < textList.size(); i++) {
                //字符串尾加“\r\n”可自动换行
                TextPaint mTextPaint = new TextPaint();
                mTextPaint.setTextAlign(Paint.Align.CENTER);
                String text = mList.get(i).getContent();
                mTextPaint.setARGB(0xFF, 0, 0, 0);
                mTextPaint.setAntiAlias(true);  //消除锯齿
                mTextPaint.setTextSize(textOutSize);
                mTextPaint.setColor(textOutColor);
                //使用StaticLayout处理文字换行
                StaticLayout layout = new StaticLayout(text, mTextPaint, 300,
                        Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
                canvas.save();
                int textHeight = (int)(mTextPaint.descent() - mTextPaint.ascent());
                canvas.translate(textList.get(i).x, textList.get(i).y - textHeight); //移动当前画布的原点
                layout.draw(canvas);
                canvas.restore();   //把当前画布返回（调整）到上一个save()状态之前

            }
        }

        //绘制中间图形的文字
        if (isShowInText) {
            Paint centerTextPaint = new Paint();
            centerTextPaint.setTextAlign(Paint.Align.CENTER);
            centerTextPaint.setTextSize(textInSize);
            centerTextPaint.setColor(textInColor);
            canvas.drawText(centerText, centerX, centerY, centerTextPaint);
        }
    }

    /**
     * 绘制饼图
     * @param canvas
     */
    private void drawPie(Canvas canvas) {
        int startAngle = -90;//起始角度
        Pie item;
        //根据不同SDK版本采用不同的绘制方案，需要调整paint
        if (Build.VERSION.SDK_INT >= 19) {
            piePaint.setStrokeWidth(1);
            piePaint.setStyle(Paint.Style.FILL);
        } else {
            piePaint.setStrokeWidth(roundWidth);
            piePaint.setStyle(Paint.Style.STROKE);
        }

        for (int i = 0; i < mList.size(); i++) {
            item = mList.get(i);
            //获取单个类目所占比例
            float per = item.getPercent();
            //计算所占比例对应的角度
            int sweepAngle = (int) (per / (float) getSum() * 360);
            //设置颜色
            if (i == touchMode) {
                item.setColor(touchModeColor);
            } else {
                item.setColor(unTouchModeColor);
            }

            piePaint.setColor(item.getColor());
            //绘制弧形
            if (Build.VERSION.SDK_INT >= 19) {
                Path path = getArcPath(pieInRectF, pieOutRectF, startAngle, sweepAngle);
                canvas.drawPath(path, piePaint);
                item.setRegion(path);
            } else {
                Path path = new Path();
                path.addArc(pieInRectF, startAngle, sweepAngle);
                canvas.drawPath(path, piePaint);
            }
            //计算起始角度
            startAngle += sweepAngle;
        }
    }

    /**
     * 响应点击事件
     */
    private void doOnSpecialTypeClick() {
        Pie itemMode;
        for (int i = 0; i < mList.size(); i++) {
            itemMode = mList.get(i);
            //判断点击的坐标是否在环内
            if (itemMode.isTouch()) {
                touchMode = i;
                invalidate(); //请求重新draw
//                Toast.makeText(getContext(), itemMode.getContent(), Toast.LENGTH_SHORT).show(); //输出对应区域的Content
                Log.d(TAG, "点击了" + itemMode.getContent());
            }
        }
    }

    /**
     * 获取绘制弧度所需要的path
     *
     * @param in
     * @param out
     * @param startAngle
     * @param angle
     * @return
     */
    private Path getArcPath(RectF in, RectF out, int startAngle, int angle) {
        Path path1 = new Path();
        path1.moveTo(in.centerX(), in.centerY()); //移动画笔至内圆中心点
        path1.arcTo(in, startAngle, angle); //绘制内圆弧线
        Path path2 = new Path();
        path2.moveTo(out.centerX(), out.centerY());//移动画笔至外圆中心点
        path2.arcTo(out, startAngle, angle);//绘制外圆弧线
        Path path = new Path();
        path.op(path2, path1, Path.Op.DIFFERENCE);//除去外圆与内圆的公共部分
        return path;
    }

    /**
     * 设置饼图的数据
     *
     * @param mList
     */
    public void setPieData(List<Pie> mList) {
        if (mList == null)
            return;
        this.mList.clear();
        this.mList = mList;
        touchMode = 0;
        invalidate(); //请求重新draw
    }

    /**
     * 获取全部区域占比和
     * @return
     */
    private int getSum() {
        int sum = 0;
        for (Pie pie : mList) {
            sum += pie.getPercent();
        }
        return sum;
    }

    /**
     * 设置圆环宽度
     * @param mRoundWidth
     */
    public void setRoundWidth(float mRoundWidth) {
        roundWidth = mRoundWidth;
    }

    /**
     * 设置内圆半径
     * @param mStillRadius
     */
    public void setStillRadius(float mStillRadius) {
        stillRadius = mStillRadius;
    }

    /**
     * 设置扇区文字大小
     * @param mTextOutSize
     */
    public void setTextOutSize(int mTextOutSize) {
        textOutSize = mTextOutSize;
    }

    /**
     * 设置中心文字大小
    * @param mTextInSize
     */
    public void setTextInSize(int mTextInSize) {
        textInSize = mTextInSize;
    }

    /**
     * 设置是否显示扇形区域的文字
     * @param mShowOutText
     */
    public void setShowOutText(boolean mShowOutText) {
        isShowOutText = mShowOutText;
    }

    /**
     * 设置是否显示圆中心的文字
     * @param mShowInText
     */
    public void setShowInText(boolean mShowInText) {
        isShowInText = mShowInText;
    }

    /**
     * 设置中心文字的内容
     * @param mCenterText
     */
    public void setCenterText(String mCenterText) {
        centerText = mCenterText;
    }

    /**
     * 设置圆环 X 轴上的偏移量
     * @param mOffsetX
     */
    public void setOffsetX(float mOffsetX) {
        offsetX = mOffsetX;
    }

    /**
     * 设置圆环 Y 轴上的偏移量
     * @param mOffsetY
     */
    public void setOffsetY(float mOffsetY) {
        offsetY = mOffsetY;
    }

    /**
     * 设置扇区文字颜色
     * @param mTextOutColor
     */
    public void setTextOutColor(int mTextOutColor) {
        textOutColor = mTextOutColor;
    }

    /**
     * 设置中心文字颜色
     * @param mTextInColor
     */
    public void setTextInColor(int mTextInColor) {
        textInColor = mTextInColor;
    }

    /**
     * 设置文本显示位置的半径，默认在圆环的中间
     * @param mTextRadius
     */
    public void setTextRadius(float mTextRadius) {
        textRadius = mTextRadius;
    }

    /**
     * 设置未点击的扇形区域的颜色
     * @param mUnTouchModeColor
     */
    public void setUnTouchModeColor(int mUnTouchModeColor) {
        unTouchModeColor = mUnTouchModeColor;
    }

    /**
     * 设置点击的扇形区域的颜色
     * @param mTouchModeColor
     */
    public void setTouchModeColor(int mTouchModeColor) {
        touchModeColor = mTouchModeColor;
    }

    /**
     * 设置是否显示扇形区域之间的间隔线
     * @param mShowIntervalLine
     */
    public void setShowIntervalLine(boolean mShowIntervalLine) {
        isShowIntervalLine = mShowIntervalLine;
    }

    /**
     * 设置间隔线颜色
     * @param mLineColor
     */
    public void setLineColor(int mLineColor) {
        lineColor = mLineColor;
    }

    /**
     * 设置间隔线宽度
     * @param mLineWidth
     */
    public void setLineWidth(int mLineWidth) {
        lineWidth = mLineWidth;
    }
}
