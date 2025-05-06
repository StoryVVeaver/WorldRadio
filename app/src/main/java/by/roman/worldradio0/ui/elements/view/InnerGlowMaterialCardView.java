package by.roman.worldradio0.ui.elements.view;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;

import by.roman.worldradio0.R;

public class InnerGlowMaterialCardView extends MaterialCardView {
    private final Paint glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float blurRadius;
    private final RectF innerRect = new RectF();
    private boolean innerGlowEnabled = false;

    public InnerGlowMaterialCardView(@NonNull Context context) {
        this(context, null);
    }

    public InnerGlowMaterialCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        blurRadius = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                10,
                getResources().getDisplayMetrics()
        );

        int glowColor = ContextCompat.getColor(getContext(), R.color.red);
        glowPaint.setStyle(Paint.Style.STROKE);
        glowPaint.setStrokeWidth(
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        3,
                        getResources().getDisplayMetrics()
                )
        );
        glowPaint.setColor(glowColor);

        glowPaint.setMaskFilter(null);

        setLayerType(LAYER_TYPE_SOFTWARE, glowPaint);
    }

    @Override
    public void onDrawForeground(@NonNull Canvas canvas) {
        super.onDrawForeground(canvas);

        if (!innerGlowEnabled) return;

        float halfStroke = glowPaint.getStrokeWidth() / 2f;
        innerRect.left   = getPaddingLeft() + halfStroke;
        innerRect.top    = getPaddingTop() + halfStroke;
        innerRect.right  = getWidth() - getPaddingRight() - halfStroke;
        innerRect.bottom = getHeight() - getPaddingBottom() - halfStroke;

        float radius = getRadius();
        canvas.drawRoundRect(innerRect, radius, radius, glowPaint);
    }

    public void setInnerGlowEnabled(boolean enabled) {
        innerGlowEnabled = enabled;

        if (enabled) {
            glowPaint.setMaskFilter(new BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL));
        } else {
            glowPaint.setMaskFilter(null);
        }

        invalidate();
    }
}
