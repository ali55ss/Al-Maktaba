package com.technostacks.almaktaba.activity.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.technostacks.almaktaba.R;
import com.technostacks.almaktaba.activity.DocumentDetectionActivity;
import com.technostacks.almaktaba.model.MatData;

import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.imgproc.Imgproc;
import java.util.ArrayList;
import java.util.List;

import static com.technostacks.almaktaba.util.GlobalData.isAutoDetect;
import static com.technostacks.almaktaba.util.GlobalData.isFirstTimeForAuto;
import static org.opencv.core.CvType.CV_32S;
import static org.opencv.imgproc.Imgproc.approxPolyDP;


public class DrawView extends View {

    Context mContext;
    private Paint paint;
    private Path path = null;
    private MatData matData;
    Canvas canvas;
    int textX, textY;
    int counter = 0;

    public DrawView(Context context) {
        super(context);
        init(context);
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        paint = new Paint();
        paint.setColor(ContextCompat.getColor(mContext, R.color.button_start));
        paint.setStrokeWidth(6);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;

        textX = canvas.getWidth() / 2;
        textY = canvas.getHeight() / 2;

        if (path != null) {
            paint.setColor(ContextCompat.getColor(mContext, R.color.button_start));
            paint.setStrokeWidth(6);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawPath(path, paint);

            List<Point> originalPoints = new ArrayList<>();
            for (int counter = 0; counter < matData.points.size(); counter++) {
                Point point = new Point(matData.points.get(counter).x, matData.points.get(counter).y);
                point.x = point.x * matData.resizeRatio;
                point.y = point.y * matData.resizeRatio;
                originalPoints.add(point);
            }

            MatOfPoint2f thisContour2f = new MatOfPoint2f();
            MatOfPoint approxContour = new MatOfPoint();
            MatOfPoint2f approxContour2f = new MatOfPoint2f();
            thisContour2f.fromList(originalPoints);

            approxPolyDP(thisContour2f, approxContour2f, Imgproc.arcLength(thisContour2f, true) * 0.009, true);
            approxContour2f.convertTo(approxContour, CV_32S);

            MatOfPoint2f rotatedMat = new MatOfPoint2f();
            rotatedMat.fromList(originalPoints);
            RotatedRect boundingBox = Imgproc.minAreaRect(rotatedMat);

            if (matData.oriMat.width() < boundingBox.boundingRect().width + 200
                    || matData.oriMat.height() < boundingBox.boundingRect().height + 250) {
                drawTextCentered(getContext().getString(R.string.hold_still));
                DocumentDetectionActivity.isDocDetect = true;
            } else {
                drawTextCentered(getContext().getString(R.string.move_closer));
                DocumentDetectionActivity.isDocDetect = false;
            }

            thisContour2f.release();
            approxContour.release();
            approxContour2f.release();
            rotatedMat.release();
            originalPoints.clear();

        } else {

            DocumentDetectionActivity.isDocDetect = false;

            drawTextCentered(getContext().getString(R.string.nothing_found_snap_manually));

        }
    }

    public void drawTextCentered(String text) {

        if (isAutoDetect && !isFirstTimeForAuto && !TextUtils.isEmpty(text)) {
            paint.setTextSize(30);
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(2);
            int xPos = textX - (int) (paint.measureText(text) / 2);
            int yPos = (int) (textY - ((paint.descent() + paint.ascent()) / 2));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                paint.setColor(ContextCompat.getColor(mContext, R.color.overlay));
                canvas.drawRoundRect(
                        xPos - 70,
                        yPos - 50,
                        (textX + (int) (paint.measureText(text) / 2)) + 70,
                        (int) (textY + ((paint.descent() + paint.ascent()) / 2)) + 50,
                        20, 20,
                        paint);
            } else {
                paint.setColor(ContextCompat.getColor(mContext, R.color.overlay));
                canvas.drawRect(xPos - 70,
                        yPos - 50,
                        (textX + (int) (paint.measureText(text) / 2)) + 70,
                        (int) (textY + ((paint.descent() + paint.ascent()) / 2)) + 50,
                        paint);
            }
            paint.setColor(ContextCompat.getColor(mContext, R.color.white));
            canvas.drawText(text, xPos, yPos, paint);
        }
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public void setMatData(MatData matData) {
        this.matData = matData;
    }

}
