package org.bspb.smartbirds.pro.ui.map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.location.Location;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.text.NumberFormat;

/**
 * Created by dani on 04.08.16.
 */
public class ScaleBar extends ImageView {
    float mXOffset = 10;
    float mYOffset = 10;
    float mLineWidth = 3;
    int mTextSize = 25;

    boolean mIsImperial = false;
    boolean mIsNautical = false;

    boolean mIsLatitudeBar = true;
    boolean mIsLongitudeBar = true;

    private GoogleMap mMap;

    float mXdpi;
    float mYdpi;
    float mScreenWidth;

    private Canvas mCanvas;
    private Paint textPaint;
    private Paint barPaint;

    public ScaleBar(Context context, GoogleMap map) {
        super(context);

        barPaint = new Paint();
        barPaint.setColor(Color.BLACK);
        barPaint.setAntiAlias(true);
        barPaint.setStrokeWidth(mLineWidth);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(mTextSize);

        mMap = map;

        mXdpi = context.getResources().getDisplayMetrics().xdpi;
        mYdpi = context.getResources().getDisplayMetrics().ydpi;
        mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if(mCanvas != null) {
                    invalidate();
                }
            }
        });
    }

    @Override
    public void onDraw(Canvas canvas) {
        mCanvas = canvas;
        canvas.save();

        drawScaleBarPicture(canvas);

        canvas.restore();
    }

    private void drawScaleBarPicture(Canvas canvas) {
        // We want the scale bar to be as long as the closest round-number miles/kilometers
        // to 1-inch at the latitude at the current center of the screen.

        Projection projection = mMap.getProjection();

        if (projection == null) {
            return;
        }


        drawXMetric(canvas, textPaint, barPaint);
    }

    private void drawXMetric(Canvas canvas, Paint textPaint, Paint barPaint) {
        Projection projection = mMap.getProjection();

        if (projection != null) {
            LatLng p1 = projection.fromScreenLocation(new Point((int) ((getWidth() / 2) - (mXdpi / 2)), getHeight() / 2));
            LatLng p2 = projection.fromScreenLocation(new Point((int) ((getWidth() / 2) + (mXdpi / 2)), getHeight() / 2));

            Location locationP1 = new Location("ScaleBar location p1");
            Location locationP2 = new Location("ScaleBar location p2");

            locationP1.setLatitude(p1.latitude);
            locationP2.setLatitude(p2.latitude);
            locationP1.setLongitude(p1.longitude);
            locationP2.setLongitude(p2.longitude);

            float xMetersPerInch = locationP1.distanceTo(locationP2);

            if (mIsLatitudeBar) {
                String xMsg = scaleBarLengthText(xMetersPerInch, mIsImperial, mIsNautical);
                Rect xTextRect = new Rect();
                textPaint.getTextBounds(xMsg, 0, xMsg.length(), xTextRect);

                int textSpacing = (int) (xTextRect.height() / 5.0);

                float left = mScreenWidth/2 - mXdpi/2;
                float right = mScreenWidth/2 + mXdpi/2;

                canvas.drawRect(left, mYOffset, right, mYOffset + mLineWidth, barPaint);
                canvas.drawRect(right, mYOffset, right + mLineWidth, mYOffset +
                        xTextRect.height() + mLineWidth + textSpacing, barPaint);
                canvas.drawRect(left - mLineWidth, mYOffset, left, mYOffset +
                        xTextRect.height() + mLineWidth +   textSpacing, barPaint);

                if (!mIsLongitudeBar) {
                    canvas.drawRect(left, mYOffset, left + mLineWidth, mYOffset +
                            xTextRect.height() + mLineWidth + textSpacing, barPaint);
                }
                canvas.drawText(xMsg, (left + mXdpi / 2 - xTextRect.width() / 2),
                        (mYOffset + xTextRect.height() + mLineWidth     + textSpacing), textPaint);
            }
        }
    }

    private void drawYMetric(Canvas canvas, Paint textPaint, Paint barPaint) {
        Projection projection = mMap.getProjection();

        if (projection != null) {
            Location locationP1 = new Location("ScaleBar location p1");
            Location locationP2 = new Location("ScaleBar location p2");

            LatLng p1 = projection.fromScreenLocation(new Point(getWidth() / 2,
                    (int) ((getHeight() / 2) - (mYdpi / 2))));
            LatLng p2 = projection.fromScreenLocation(new Point(getWidth() / 2,
                    (int) ((getHeight() / 2) + (mYdpi / 2))));

            locationP1.setLatitude(p1.latitude);
            locationP2.setLatitude(p2.latitude);
            locationP1.setLongitude(p1.longitude);
            locationP2.setLongitude(p2.longitude);

            float yMetersPerInch = locationP1.distanceTo(locationP2);

            if (mIsLongitudeBar) {
                String yMsg = scaleBarLengthText(yMetersPerInch, mIsImperial, mIsNautical);
                Rect yTextRect = new Rect();
                textPaint.getTextBounds(yMsg, 0, yMsg.length(), yTextRect);

                int textSpacing = (int) (yTextRect.height() / 5.0);

                canvas.drawRect(mXOffset, mYOffset, mXOffset + mLineWidth, mYOffset + mYdpi, barPaint);
                canvas.drawRect(mXOffset, mYOffset + mYdpi, mXOffset + yTextRect.height() +
                        mLineWidth + textSpacing, mYOffset + mYdpi + mLineWidth, barPaint);

                if (!mIsLatitudeBar) {
                    canvas.drawRect(mXOffset, mYOffset, mXOffset + yTextRect.height() +
                            mLineWidth + textSpacing, mYOffset + mLineWidth, barPaint);
                }

                float x = mXOffset + yTextRect.height() + mLineWidth + textSpacing;
                float y = mYOffset + mYdpi / 2 + yTextRect.width() / 2;

                canvas.rotate(-90, x, y);
                canvas.drawText(yMsg, x, y + textSpacing, textPaint);
            }
        }
    }

    private String scaleBarLengthText(float meters, boolean imperial, boolean nautical) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(1);
        nf.setGroupingUsed(false);

        if (meters >= 1000) {
            return (nf.format((meters / 1000))) + " km";
        } else if (meters > 100) {
            return (nf.format((meters / 100.0) / 10.0)) + " km";
        } else {
            nf.setMaximumFractionDigits(0);
            return nf.format(meters) + " m";
        }

//        if (this.mIsImperial) {
//            if (meters >= 1609.344) {
//                return (meters / 1609.344) + "mi";
//            } else if (meters >= 1609.344/10) {
//                return ((meters / 160.9344) / 10.0) + "mi";
//            } else {
//                return (meters * 3.2808399) + "ft";
//            }
//        } else if (this.mIsNautical) {
//            if (meters >= 1852) {
//                return ((meters / 1852)) + "nm";
//            } else if (meters >= 1852/10) {
//                return (((meters / 185.2)) / 10.0) + "nm";
//            } else {
//                return ((meters * 3.2808399)) + "ft";
//            }
//        } else {
//            if (meters >= 1000) {
//                return ((meters / 1000)) + "km";
//            } else if (meters > 100) {
//                return ((meters / 100.0) / 10.0) + "km";
//            } else {
//                return meters + "m";
//            }
//        }
    }

    public float getViewHeight() {
        String testMsg = "TEST";
        Rect xTextRect = new Rect();
        textPaint.getTextBounds(testMsg, 0, testMsg.length(), xTextRect);
        int textSpacing = (int) (xTextRect.height() / 5.0);
        return xTextRect.height() + mLineWidth + textSpacing + 2*mYOffset;
    }
}
