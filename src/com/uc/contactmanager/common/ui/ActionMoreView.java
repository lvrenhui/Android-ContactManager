package com.uc.contactmanager.common.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import com.uc.contactmanager.R;
import com.uc.contactmanager.common.tool.L;
import com.uc.contactmanager.common.tool.Util;

public class ActionMoreView extends ImageView {

    private static final int RADIUS_DOT_DP = 4;
    private static final int PADDING_DOT_RIGHT_DP = 10;
    private static final int PADDING_DOT_TOP_DP = 8;

    private static Paint sCirclePaint;

    private Context mContext;
    private int mDotRadiusPx;
    private int mDotPaddingRight;
    private int mDotPaddingTop;

    private boolean mClientHasNewVersion;
    private int mUpgradeAppCount;

    /**
     * 是否需要显示小红点
     */
    private boolean mIsNeedShowRedIcon = true;

    public ActionMoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ActionMoreView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    private void init() {
        mContext = getContext();

        //calculate dot radius
        float density = Util.getScreenDensity(mContext);
        mDotRadiusPx = (int) (RADIUS_DOT_DP * density);
        mDotPaddingRight = (int) (PADDING_DOT_RIGHT_DP * density);
        mDotPaddingTop = (int) (PADDING_DOT_TOP_DP * density);

        if (sCirclePaint == null) {
            sCirclePaint = new Paint();
            sCirclePaint.setAntiAlias(true);
            sCirclePaint.setColor(getResources().getColor(R.color.red_icon_color));
        }

        //set src and background
        setScaleType(ScaleType.CENTER_INSIDE);
        setImageResource(R.drawable.btn_header_bar_more_selector);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //check if session has data
        String clicked = "true";
        boolean showRedIcon = false;
        if (!"true".equals(clicked)) {
            showRedIcon = mClientHasNewVersion || mUpgradeAppCount > 0;
        }
        L.d("ActionMoreView#onDraw IsNeedShowRedIcon:" + mIsNeedShowRedIcon);
        L.d("ActionMoreView#onPostExecute hasNewVersion:" + mClientHasNewVersion + ", upgradeCount:" + mUpgradeAppCount + ", showRedIcon:" + showRedIcon);
        if (showRedIcon && mIsNeedShowRedIcon) {
            canvas.drawCircle(getWidth() - mDotRadiusPx - mDotPaddingRight, mDotRadiusPx + mDotPaddingTop, mDotRadiusPx, sCirclePaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (isEnabled()) {
                clicked();
            }
        }
        return super.onTouchEvent(event);
    }

    public void clicked() {
        invalidate();
    }

    /**
     * 设置是否需要显示小红点（如他人主页目前暂时不需要显示小红点）
     *
     * @param isNeedShow
     */
    public void isNeedShowRedIcon(boolean isNeedShow) {
        this.mIsNeedShowRedIcon = isNeedShow;
    }

}
