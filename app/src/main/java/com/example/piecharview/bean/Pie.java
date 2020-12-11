package com.example.piecharview.bean;

import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;

/**
 * @author uidq2429
 * @since 2020.12.10
 */

public class Pie {
    private Region region;
    /**
     * 某个区域显示的文字内容
     */
    private String content;
    /**
     * 区域占比
     */
    private float percent;
    /**
     * 区域颜色
     */
    private int color;
    /**
     * 判断是否点击了某个区域
     */
    private boolean isTouch;

    public Pie() {
    }

    public Pie(float percent, String content) {
        this.percent = percent;
        this.content = content;
    }

    public Pie(float percent, String content, int color) {
        this.percent = percent;
        this.content = content;
        this.color = color;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    /**
     * 通过Region判断坐标落点, 将path转化成region，
     * 使用region.contains(int x,int y)来判断点击坐标是否在该区域内。
     * @param path
     */
    public void setRegion(Path path) {
        Region re = new Region();
        RectF rectF = new RectF();
        path.computeBounds(rectF, true);
        re.setPath(path, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
        this.region = re;
    }

    public boolean isInRegion(float x, float y) {
        return region != null && region.contains((int)x, (int)y);
    }

    public boolean isTouch() {
        return isTouch;
    }

    public void setTouch(boolean touch) {
        isTouch = touch;
    }
}