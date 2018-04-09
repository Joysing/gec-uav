package cc.joysing.uav.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import cc.joysing.uav.R;

/**
 * Created by kqw on 2016/8/30.
 * ҡ�˿ؼ�
 */
public class RockerView extends View {
    private static final String TAG = "RockerView";

    private static final int DEFAULT_SIZE = 400;
    private static final int DEFAULT_ROCKER_RADIUS = DEFAULT_SIZE / 8;

    private Paint mAreaBackgroundPaint;
    private Paint mRockerPaint;

    private Point mRockerPosition;
    private Point mCenterPoint;

    private int mAreaRadius;
    private int mRockerRadius;

    private CallBackMode mCallBackMode = CallBackMode.CALL_BACK_MODE_MOVE;
    private OnAngleChangeListener mOnAngleChangeListener;
    private OnShakeListener mOnShakeListener;

    private DirectionMode mDirectionMode;
    private Direction tempDirection = Direction.DIRECTION_CENTER;
    // �Ƕ�
    private static final double ANGLE_0 = 0;
    private static final double ANGLE_360 = 360;

    // 360��ƽ��4�ݵı�Ե�Ƕ�(��ת45��)
    private static final double ANGLE_ROTATE45_4D_OF_0P = 45;
    private static final double ANGLE_ROTATE45_4D_OF_1P = 135;
    private static final double ANGLE_ROTATE45_4D_OF_2P = 225;
    private static final double ANGLE_ROTATE45_4D_OF_3P = 315;


    // ҡ�˿��ƶ����򱳾�
    private static final int AREA_BACKGROUND_MODE_PIC = 0;
    private static final int AREA_BACKGROUND_MODE_COLOR = 1;
    private static final int AREA_BACKGROUND_MODE_XML = 2;
    private static final int AREA_BACKGROUND_MODE_DEFAULT = 3;
    private int mAreaBackgroundMode = AREA_BACKGROUND_MODE_DEFAULT;
    private Bitmap mAreaBitmap;
    private int mAreaColor;
    // ҡ�˱���
    private static final int ROCKER_BACKGROUND_MODE_PIC = 4;
    private static final int ROCKER_BACKGROUND_MODE_COLOR = 5;
    private static final int ROCKER_BACKGROUND_MODE_XML = 6;
    private static final int ROCKER_BACKGROUND_MODE_DEFAULT = 7;
    private int mRockerBackgroundMode = ROCKER_BACKGROUND_MODE_DEFAULT;
    private Bitmap mRockerBitmap;
    private int mRockerColor;


    public RockerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // ��ȡ�Զ�������
        initAttribute(context, attrs);

        if (isInEditMode()) {
            //Log.i(TAG, "RockerView: isInEditMode");
        }

        // �ƶ����򻭱�
        mAreaBackgroundPaint = new Paint();
        mAreaBackgroundPaint.setAntiAlias(true);

        // ҡ�˻���
        mRockerPaint = new Paint();
        mRockerPaint.setAntiAlias(true);

        // ���ĵ�
        mCenterPoint = new Point();
        // ҡ��λ��
        mRockerPosition = new Point();
    }

    /**
     * ��ȡ����
     *
     * @param context context
     * @param attrs   attrs
     */
    private void initAttribute(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RockerView);

        // ���ƶ����򱳾�
        Drawable areaBackground = typedArray.getDrawable(R.styleable.RockerView_areaBackground);
        if (null != areaBackground) {
            // �����˱���
            if (areaBackground instanceof BitmapDrawable) {
                // ������һ��ͼƬ
                mAreaBitmap = ((BitmapDrawable) areaBackground).getBitmap();
                mAreaBackgroundMode = AREA_BACKGROUND_MODE_PIC;
            } else if (areaBackground instanceof GradientDrawable) {
                // XML
                mAreaBitmap = drawable2Bitmap(areaBackground);
                mAreaBackgroundMode = AREA_BACKGROUND_MODE_XML;
            } else if (areaBackground instanceof ColorDrawable) {
                // ɫֵ
                mAreaColor = ((ColorDrawable) areaBackground).getColor();
                mAreaBackgroundMode = AREA_BACKGROUND_MODE_COLOR;
            } else {
                // ������ʽ
                mAreaBackgroundMode = AREA_BACKGROUND_MODE_DEFAULT;
            }
        } else {
            // û�����ñ���
            mAreaBackgroundMode = AREA_BACKGROUND_MODE_DEFAULT;
        }
        // ҡ�˱���
        Drawable rockerBackground = typedArray.getDrawable(R.styleable.RockerView_rockerBackground);
        if (null != rockerBackground) {
            // ������ҡ�˱���
            if (rockerBackground instanceof BitmapDrawable) {
                // ͼƬ
                mRockerBitmap = ((BitmapDrawable) rockerBackground).getBitmap();
                mRockerBackgroundMode = ROCKER_BACKGROUND_MODE_PIC;
            } else if (rockerBackground instanceof GradientDrawable) {
                // XML
                mRockerBitmap = drawable2Bitmap(rockerBackground);
                mRockerBackgroundMode = ROCKER_BACKGROUND_MODE_XML;
            } else if (rockerBackground instanceof ColorDrawable) {
                // ɫֵ
                mRockerColor = ((ColorDrawable) rockerBackground).getColor();
                mRockerBackgroundMode = ROCKER_BACKGROUND_MODE_COLOR;
            } else {
                // ������ʽ
                mRockerBackgroundMode = ROCKER_BACKGROUND_MODE_DEFAULT;
            }
        } else {
            // û������ҡ�˱���
            mRockerBackgroundMode = ROCKER_BACKGROUND_MODE_DEFAULT;
        }

        // ҡ�˰뾶
        mRockerRadius = typedArray.getDimensionPixelOffset(R.styleable.RockerView_rockerRadius, DEFAULT_ROCKER_RADIUS);

//        Log.i(TAG, "initAttribute: mAreaBackground = " + areaBackground + "   mRockerBackground = " + rockerBackground + "  mRockerRadius = " + mRockerRadius);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureWidth, measureHeight;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            // �����ֵ��match_parent
            measureWidth = widthSize;
        } else {
            // wrap_content
            measureWidth = DEFAULT_SIZE;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            measureHeight = heightSize;
        } else {
            measureHeight = DEFAULT_SIZE;
        }
//        Log.i(TAG, "onMeasure: --------------------------------------");
//        Log.i(TAG, "onMeasure: widthMeasureSpec = " + widthMeasureSpec + " heightMeasureSpec = " + heightMeasureSpec);
//        Log.i(TAG, "onMeasure: widthMode = " + widthMode + "  measureWidth = " + widthSize);
//        Log.i(TAG, "onMeasure: heightMode = " + heightMode + "  measureHeight = " + widthSize);
//        Log.i(TAG, "onMeasure: measureWidth = " + measureWidth + " measureHeight = " + measureHeight);
        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();

        int cx = measuredWidth / 2;
        int cy = measuredHeight / 2;
        // ���ĵ�
        mCenterPoint.set(cx, cy);
        // ���ƶ�����İ뾶
        mAreaRadius = (measuredWidth <= measuredHeight) ? cx : cy;

        // ҡ��λ��
        if (0 == mRockerPosition.x || 0 == mRockerPosition.y) {
            mRockerPosition.set(mCenterPoint.x, mCenterPoint.y);
        }

        // �����ƶ�����
        if (AREA_BACKGROUND_MODE_PIC == mAreaBackgroundMode || AREA_BACKGROUND_MODE_XML == mAreaBackgroundMode) {
            // ͼƬ
            Rect src = new Rect(0, 0, mAreaBitmap.getWidth(), mAreaBitmap.getHeight());
            Rect dst = new Rect(mCenterPoint.x - mAreaRadius, mCenterPoint.y - mAreaRadius, mCenterPoint.x + mAreaRadius, mCenterPoint.y + mAreaRadius);
            canvas.drawBitmap(mAreaBitmap, src, dst, mAreaBackgroundPaint);
        } else if (AREA_BACKGROUND_MODE_COLOR == mAreaBackgroundMode) {
            // ɫֵ
            mAreaBackgroundPaint.setColor(mAreaColor);
            canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, mAreaRadius, mAreaBackgroundPaint);
        } else {
            // ��������δ����
            mAreaBackgroundPaint.setColor(Color.GRAY);
            canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, mAreaRadius, mAreaBackgroundPaint);
        }

        // ��ҡ��
        if (ROCKER_BACKGROUND_MODE_PIC == mRockerBackgroundMode || ROCKER_BACKGROUND_MODE_XML == mRockerBackgroundMode) {
            // ͼƬ
            Rect src = new Rect(0, 0, mRockerBitmap.getWidth(), mRockerBitmap.getHeight());
            Rect dst = new Rect(mRockerPosition.x - mRockerRadius, mRockerPosition.y - mRockerRadius, mRockerPosition.x + mRockerRadius, mRockerPosition.y + mRockerRadius);
            canvas.drawBitmap(mRockerBitmap, src, dst, mRockerPaint);
        } else if (ROCKER_BACKGROUND_MODE_COLOR == mRockerBackgroundMode) {
            // ɫֵ
            mRockerPaint.setColor(mRockerColor);
            canvas.drawCircle(mRockerPosition.x, mRockerPosition.y, mRockerRadius, mRockerPaint);
        } else {
            // ��������δ����
            mRockerPaint.setColor(Color.RED);
            canvas.drawCircle(mRockerPosition.x, mRockerPosition.y, mRockerRadius, mRockerPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:// ����
                // �ص� ��ʼ
                callBackStart();
            case MotionEvent.ACTION_MOVE:// �ƶ�
                float moveX = event.getX();
                float moveY = event.getY();
                mRockerPosition = getRockerPositionPoint(mCenterPoint, new Point((int) moveX, (int) moveY), mAreaRadius, mRockerRadius);
                moveRocker(mRockerPosition.x, mRockerPosition.y);
                break;
            case MotionEvent.ACTION_UP:// ̧��
            case MotionEvent.ACTION_CANCEL:// �Ƴ�����
                // �ص� ����
                callBackFinish();
                float upX = event.getX();
                float upY = event.getY();
                moveRocker(mCenterPoint.x, mCenterPoint.y);
                //Log.i(TAG, "onTouchEvent: ̧��λ�� : x = " + upX + " y = " + upY);
                break;
        }
        return true;
    }

    /**
     * ��ȡҡ��ʵ��Ҫ��ʾ��λ�ã��㣩
     *
     * @param centerPoint  ���ĵ�
     * @param touchPoint   ������
     * @param regionRadius ҡ�˿ɻ����뾶
     * @param rockerRadius ҡ�˰뾶
     * @return ҡ��ʵ����ʾ��λ�ã��㣩
     */
    private Point getRockerPositionPoint(Point centerPoint, Point touchPoint, float regionRadius, float rockerRadius) {
        // ������X��ľ���
        float lenX = (float) (touchPoint.x - centerPoint.x);
        // ������Y�����
        float lenY = (float) (touchPoint.y - centerPoint.y);
        // �������
        float lenXY = (float) Math.sqrt((double) (lenX * lenX + lenY * lenY));
        // ���㻡��
        double radian = Math.acos(lenX / lenXY) * (touchPoint.y < centerPoint.y ? -1 : 1);
        // ����Ƕ�
        double angle = radian2Angle(radian);

        // �ص� ���ز���
        callBack(angle);

        //Log.i(TAG, "getRockerPositionPoint: �Ƕ� :" + angle);
        if (lenXY + rockerRadius <= regionRadius) { // ����λ���ڿɻ��Χ��
            return touchPoint;
        } else { // ����λ���ڿɻ��Χ����
            // ����Ҫ��ʾ��λ��
            int showPointX = (int) (centerPoint.x + (regionRadius - rockerRadius) * Math.cos(radian));
            int showPointY = (int) (centerPoint.y + (regionRadius - rockerRadius) * Math.sin(radian));
            return new Point(showPointX, showPointY);
        }
    }

    /**
     * �ƶ�ҡ�˵�ָ��λ��
     *
     * @param x x����
     * @param y y����
     */
    private void moveRocker(float x, float y) {
        mRockerPosition.set((int) x, (int) y);
        //Log.i(TAG, "onTouchEvent: �ƶ�λ�� : x = " + mRockerPosition.x + " y = " + mRockerPosition.y);
        invalidate();
    }

    /**
     * ����ת�Ƕ�
     *
     * @param radian ����
     * @return �Ƕ�[0, 360)
     */
    private double radian2Angle(double radian) {
        double tmp = Math.round(radian / Math.PI * 180);
        return tmp >= 0 ? tmp : 360 + tmp;
    }

    /**
     * Drawable ת Bitmap
     *
     * @param drawable Drawable
     * @return Bitmap
     */
    private Bitmap drawable2Bitmap(Drawable drawable) {
        // ȡ drawable �ĳ���
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        // ȡ drawable ����ɫ��ʽ
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        // ������Ӧ bitmap
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * �ص�
     * ��ʼ
     */
    private void callBackStart() {
        tempDirection = Direction.DIRECTION_CENTER;
        if (null != mOnAngleChangeListener) {
            mOnAngleChangeListener.onStart();
        }
        if (null != mOnShakeListener) {
            mOnShakeListener.onStart();
        }
    }

    /**
     * �ص�
     * ���ز���
     *
     * @param angle ҡ���Ƕ�
     */
    private void callBack(double angle) {
        if (null != mOnAngleChangeListener) {
            mOnAngleChangeListener.angle(angle);
        }
        if (null != mOnShakeListener) {
            if (CallBackMode.CALL_BACK_MODE_MOVE == mCallBackMode) {
                switch (mDirectionMode) {
                    case DIRECTION_4:// �ĸ����� ��ת45��
                        if (ANGLE_0 <= angle && ANGLE_ROTATE45_4D_OF_0P > angle || ANGLE_ROTATE45_4D_OF_3P <= angle && ANGLE_360 > angle) {
                            // ��
                            mOnShakeListener.direction(Direction.DIRECTION_RIGHT);
                        } else if (ANGLE_ROTATE45_4D_OF_0P <= angle && ANGLE_ROTATE45_4D_OF_1P > angle) {
                            // ��
                            mOnShakeListener.direction(Direction.DIRECTION_DOWN);
                        } else if (ANGLE_ROTATE45_4D_OF_1P <= angle && ANGLE_ROTATE45_4D_OF_2P > angle) {
                            // ��
                            mOnShakeListener.direction(Direction.DIRECTION_LEFT);
                        } else if (ANGLE_ROTATE45_4D_OF_2P <= angle && ANGLE_ROTATE45_4D_OF_3P > angle) {
                            // ��
                            mOnShakeListener.direction(Direction.DIRECTION_UP);
                        }
                        break;
                    default:
                        break;
                }
            } else if (CallBackMode.CALL_BACK_MODE_STATE_CHANGE == mCallBackMode) {
                switch (mDirectionMode) {
                    case DIRECTION_4:// �ĸ����� ��ת45��
                        if ((ANGLE_0 <= angle && ANGLE_ROTATE45_4D_OF_0P > angle || ANGLE_ROTATE45_4D_OF_3P <= angle && ANGLE_360 > angle) && tempDirection != Direction.DIRECTION_RIGHT) {
                            // ��
                            tempDirection = Direction.DIRECTION_RIGHT;
                            mOnShakeListener.direction(Direction.DIRECTION_RIGHT);
                        } else if (ANGLE_ROTATE45_4D_OF_0P <= angle && ANGLE_ROTATE45_4D_OF_1P > angle && tempDirection != Direction.DIRECTION_DOWN) {
                            // ��
                            tempDirection = Direction.DIRECTION_DOWN;
                            mOnShakeListener.direction(Direction.DIRECTION_DOWN);
                        } else if (ANGLE_ROTATE45_4D_OF_1P <= angle && ANGLE_ROTATE45_4D_OF_2P > angle && tempDirection != Direction.DIRECTION_LEFT) {
                            // ��
                            tempDirection = Direction.DIRECTION_LEFT;
                            mOnShakeListener.direction(Direction.DIRECTION_LEFT);
                        } else if (ANGLE_ROTATE45_4D_OF_2P <= angle && ANGLE_ROTATE45_4D_OF_3P > angle && tempDirection != Direction.DIRECTION_UP) {
                            // ��
                            tempDirection = Direction.DIRECTION_UP;
                            mOnShakeListener.direction(Direction.DIRECTION_UP);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * �ص�
     * ����
     */
    private void callBackFinish() {
        tempDirection = Direction.DIRECTION_CENTER;
        if (null != mOnAngleChangeListener) {
            mOnAngleChangeListener.onFinish();
        }
        if (null != mOnShakeListener) {
            mOnShakeListener.onFinish();
        }
    }

    /**
     * �ص�ģʽ
     */
    public enum CallBackMode {
        // ���ƶ������̻ص�
        CALL_BACK_MODE_MOVE,
        // ֻ��״̬�仯��ʱ��Żص�
        CALL_BACK_MODE_STATE_CHANGE
    }

    /**
     * ���ûص�ģʽ
     *
     * @param mode �ص�ģʽ
     */
    public void setCallBackMode(CallBackMode mode) {
        mCallBackMode = mode;
    }

    /**
     * ҡ��֧�ּ�������
     */
    public enum DirectionMode {
        DIRECTION_4, // �ĸ����� ��ת45��
    }

    /**
     * ����
     */
    public enum Direction {
        DIRECTION_LEFT, // ��
        DIRECTION_RIGHT, // ��
        DIRECTION_UP, // ��
        DIRECTION_DOWN, // ��
        DIRECTION_CENTER // �м�
    }

    /**
     * ���ҡ��ҡ���Ƕȵļ���
     *
     * @param listener �ص��ӿ�
     */
    public void setOnAngleChangeListener(OnAngleChangeListener listener) {
        mOnAngleChangeListener = listener;
    }

    /**
     * ���ҡ���ļ���
     *
     * @param directionMode �����ķ���
     * @param listener      �ص�
     */
    public void setOnShakeListener(DirectionMode directionMode, OnShakeListener listener) {
        mDirectionMode = directionMode;
        mOnShakeListener = listener;
    }

    /**
     * ҡ����������ӿ�
     */
    public interface OnShakeListener {
        // ��ʼ
        void onStart();

        /**
         * ҡ������
         *
         * @param direction ����
         */
        void direction(Direction direction);

        // ����
        void onFinish();
    }

    /**
     * ҡ���Ƕȵļ����ӿ�
     */
    public interface OnAngleChangeListener {
        // ��ʼ
        void onStart();

        /**
         * ҡ�˽Ƕȱ仯
         *
         * @param angle �Ƕ�[0,360)
         */
        void angle(double angle);

        // ����
        void onFinish();
    }
}