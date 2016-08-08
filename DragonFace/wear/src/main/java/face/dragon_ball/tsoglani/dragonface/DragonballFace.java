

package face.dragon_ball.tsoglani.dragonface;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.format.Time;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.WindowInsets;
import android.view.WindowManager;

import com.github.kleinerhacker.android.gif.Gif;
import com.github.kleinerhacker.android.gif.GifFactory;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DragonballFace extends CanvasWatchFaceService {
    protected static boolean isChangingBackgoundByTouch,isBatteryVisible;
    protected static boolean isChangingAnimationByTouch;
    protected static boolean is24HourType = true;
    protected static boolean isDateVisible = false;
    Gif myGif;
    float mainScaleX, mainScaleY;
    Bitmap originalBitmap;
    Bitmap resizedBitmap;
    ArrayList<Bitmap> listOfAnimationImages= new ArrayList<Bitmap>();
    ArrayList<String> listOfAnimationImagesLocation= new ArrayList<String>();
    Bitmap []animationBitmaps;
    int animationCounter = 0,animationNumber=0;
    int previousAnimationCounter=animationCounter,previousAnimationNumber;

    protected static boolean isEnableAnimation = true;

    private static final Typeface NORMAL_TYPEFACE =
            Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);

    /**
     * Update rate in milliseconds for interactive mode. We update once a second since seconds are
     * displayed in interactive mode.
     */
//    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);

    /**
     * Handler message id for updating the time periodically in interactive mode.
     */
    private static final int MSG_UPDATE_TIME = 0;

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private static class EngineHandler extends Handler {
        private final WeakReference<Engine> mWeakReference;

        public EngineHandler(DragonballFace.Engine reference) {
            mWeakReference = new WeakReference<>(reference);
        }


        @Override
        public void handleMessage(Message msg) {
            DragonballFace.Engine engine = mWeakReference.get();
            if (engine != null) {
                switch (msg.what) {
                    case MSG_UPDATE_TIME:
                        engine.handleUpdateTimeMessage();
                        break;
                }
            }
        }
    }

    private class Engine extends CanvasWatchFaceService.Engine {
        final Handler mUpdateTimeHandler = new EngineHandler(this);
        boolean mRegisteredTimeZoneReceiver = false;
        //        Paint mBackgroundPaint;
        Paint mTextPaint;
        boolean mAmbient;
        Time mTime;
        final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mTime.clear(intent.getStringExtra("time-zone"));
                mTime.setToNow();
            }
        };
        int mTapCount;

        float mXOffset;
        float mYOffset;
        private Bitmap one_amb_bitmap;
        private Bitmap zero_amb_bitmap;
        private Bitmap two_amb_bitmap;
        private Bitmap three_amb_bitmap;
        private Bitmap four_amb_bitmap;
        private Bitmap five_amb_bitmap;
        private Bitmap six_amb_bitmap;
        private Bitmap seven_amb_bitmap;
        private Bitmap eight_amb_bitmap;
        private Bitmap nine_amb_bitmap;
        private Bitmap scaledZero_amb_bitmap;
        private Bitmap scaledOne_amb_bitmap;
        private Bitmap scaledTwo_amb_bitmap;
        private Bitmap scaledThree_amb_bitmap;
        private Bitmap scaledFour_amb_bitmap;
        private Bitmap mScaledFive_amb_bitmap;
        private Bitmap scaledEight_amb_bitmap;
        private Bitmap scaledNine_amb_bitmap;
        private Bitmap scaledSix_amb_bitmap;
        private Bitmap scaledSeven_amb_bitmap;


        ArrayList<Integer> backgroundList = new ArrayList<>();
        ArrayList<Integer> backgroundList_abc = new ArrayList<>();

        private Bitmap batteryBitmap,batteryScaledBitmap,batteryBitmap_abc,batteryScaledBitmap_abc;

        private Bitmap zero_bitmap;
        private Bitmap one_bitmap;
        private Bitmap two_bitmap;
        private Bitmap three_bitmap;
        private Bitmap four_bitmap;
        private Bitmap five_bitmap;
        private Bitmap six_bitmap;
        private Bitmap seven_bitmap;
        private Bitmap eight_bitmap;
        private Bitmap nine_bitmap;
        //        private Bitmap seconds_bitmap, seconds_bitmap_abc;
        private Bitmap scaledZero_bitmap;
        private Bitmap scaledOne_bitmap;
        private Bitmap scaledTwo_bitmap;
        private Bitmap scaledThree_bitmap;
        private Bitmap scaledFour_bitmap;
        private Bitmap scaledFive_bitmap;
        private Bitmap scaledSix_bitmap;
        private Bitmap scaledSeven_bitmap;
        private Bitmap scaledEight_bitmap;
        private Bitmap scaledNine_bitmap;
        private Bitmap blockScaledBitmap;
        private Bitmap blockBitmap, blockBitmap_abc, blockBitmap_abc_Scalled;
//        private Bitmap secondsBlockBitmapScalled, secondsBlockBitmapScalled_abc;


//        private Bitmap date_bitmap, date_amb_bitmap, dateBitmap_abc_Scalled,dateBitmap_Scalled;

        float blockStartX = 20;
        float blockStartY = 30;
        float numberStartY = 30;
        private Movie mMovie;
        java.io.InputStream is;
        Paint paint = new Paint();

        /**
         * Whether the display supports fewer bits for each color in ambient mode. When true, we
         * disable anti-aliasing in ambient mode.
         */
        boolean mLowBitAmbient;
        private Bitmap backgroundBitmap, backgroundBitmapScaled,backgroundBitmap_abc,backgroundBitmapScaled_abc;
        private ArrayList<Integer> animationList = new ArrayList<Integer>();
        PowerManager.WakeLock wl;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);
            initAnimImageList();
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
            isChangingBackgoundByTouch = Settings.getSharedPref(getApplicationContext(), Settings.CHANGE_BACKGROUND_ON_CLICK, true);
            isChangingAnimationByTouch= Settings.getSharedPref(getApplicationContext(), Settings.CHANGE_ANIMATION_ON_CLICK, false);
            is24HourType = Settings.getSharedPref(getApplicationContext(), Settings.HOUR_TYPE, true);
            isBatteryVisible=Settings.getSharedPref(getApplicationContext(),Settings.ENABLE_BATTERY, false);

            isDateVisible = Settings.getSharedPref(getApplicationContext(), Settings.DATE_TYPE, false);
            isEnableAnimation = Settings.getSharedPref(getApplicationContext(), Settings.ENABLE_ANIMATION, true);
            setWatchFaceStyle(new WatchFaceStyle.Builder(DragonballFace.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_VARIABLE)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .setAcceptsTapEvents(true)
                    .build());
            Resources resources = DragonballFace.this.getResources();
//            mYOffset = resources.getDimension(R.dimen.digital_y_offset);
//            mBackgroundPaint = new Paint();
//            mBackgroundPaint.setColor(resources.getColor(R.color.background));

            mTextPaint = new Paint();
            mTextPaint = createTextPaint(resources.getColor(R.color.Teal));

            mTime = new Time();
            initBackgroundList();
            initBitmaps();
            initScalledBitmaps();
            initValues();
            initAnimationList();
            if (myGif == null)
                loadAnimation(animationList.get(0));
        }


        private void wakeLock() {
try {
    wl.acquire();
}catch (Exception e){
    e.printStackTrace();
}

        }

        private void wakeUnlock() {
            try{
            wl.release();
        }catch (Exception e){
            e.printStackTrace();
        }

        }


        private void initAnimationList() {
            animationList.add(R.raw.one);
            animationList.add(R.raw.two);
            animationList.add(R.raw.three);
            animationList.add(R.raw.four);
            animationList.add(R.raw.five);
            animationList.add(R.raw.six);
            animationList.add(R.raw.seven);
            animationList.add(R.raw.eight);
            animationList.add(R.raw.nine);
            animationList.add(R.raw.ten);
            animationList.add(R.raw.eleven);
            animationList.add(R.raw.twelve);
            animationList.add(R.raw.thirteen);
            animationList.add(R.raw.fourteen);
            animationList.add(R.raw.fifteen);

            animationList.add(R.raw.sixteen);
            animationList.add(R.raw.seventeen);
            animationList.add(R.raw.eighteen);
            animationList.add(R.raw.nineteen);
            animationList.add(R.raw.twenty);
            animationList.add(R.raw.twentyone);
//            animationList.add(R.raw.twentytwo);
//            animationList.add(R.raw.twentythree);
//            animationList.add(R.raw.twentyfour);
//            animationList.add(R.raw.twentyfive);
//            animationList.add(R.raw.twentysix);
//            animationList.add(R.raw.twentyseven);
//            animationList.add(R.raw.twentyeight);
//            animationList.add(R.raw.twentynine);
////            animationList.add(R.raw.thirty);
//
//
//
//
//            animationList.add(R.raw.thirtyone);
//            animationList.add(R.raw.thirtytwo);
//            animationList.add(R.raw.thirtythree);
//            animationList.add(R.raw.thirtyfour);
//            animationList.add(R.raw.thirtyfive);
//            animationList.add(R.raw.thirtysix);
//            animationList.add(R.raw.thirtyseven);
//            animationList.add(R.raw.thirtyeight);
//            animationList.add(R.raw.thirtynine);
//            animationList.add(R.raw.forty);
//            animationList.add(R.raw.fourtyone);
//            animationList.add(R.raw.fourtytwo);
//            animationList.add(R.raw.fourtythree);
//            animationList.add(R.raw.fourtyfour);
//            animationList.add(R.raw.fourtyfive);
//            animationList.add(R.raw.fourtysix);
//            animationList.add(R.raw.fourtyseven);
//            animationList.add(R.raw.fourtyeight);
//            animationList.add(R.raw.fourtynine);
//            animationList.add(R.raw.fifty);

        }

        boolean isLoaded = true;




        private void loadAnimation(final int id) {
            isLoaded = false;
            new Thread() {
                @Override
                public void run() {
                    try {
                        if (animationNumber  >= animationList.size()) {
                            animationNumber = 0;
                        }
                        myGif = GifFactory.decodeResource(getResources(), id);

                        myGif = Gif.createScaledGif(myGif, backgroundBitmap.getWidth(), backgroundBitmap.getHeight(), true);
                        animationBitmaps= new Bitmap[myGif.getFrames().length];
                        for (int i=0;i<animationBitmaps.length;i++){
                            animationBitmaps[i]=myGif.getFrames()[i].getImage();
                        }
                        isLoaded = true;
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        Log.e("error on", "image " + animationNumber);
                        if (animationNumber + 1 >= animationList.size()) {
                            animationNumber = 0;
                        }
                        animationNumber++;
                        loadAnimation(animationList.get(animationNumber));
                    }


                }
            }.start();


        }


        boolean isAnimationActivate = false;

        private void enableAnimation() {
            if (isAnimationActivate || !shouldTimerBeRunning() || !isLoaded) {
                return;
            }
            wakeLock();
            isAnimationActivate = true;
        }

        private void initAnimImageList(){


//
//

            listOfAnimationImages.add( createTrimmedBitmap(getScaledBitmap3(BitmapFactory.decodeResource(getResources(), R.drawable.one))));
            listOfAnimationImages.add( createTrimmedBitmap(getScaledBitmap3(BitmapFactory.decodeResource(getResources(), R.drawable.two))));
            listOfAnimationImages.add( createTrimmedBitmap(getScaledBitmap3(BitmapFactory.decodeResource(getResources(), R.drawable.three))));
            listOfAnimationImages.add( createTrimmedBitmap(getScaledBitmap3(BitmapFactory.decodeResource(getResources(), R.drawable.four))));
            listOfAnimationImages.add( createTrimmedBitmap(getScaledBitmap3(BitmapFactory.decodeResource(getResources(), R.drawable.five))));
            listOfAnimationImages.add( createTrimmedBitmap(getScaledBitmap3(BitmapFactory.decodeResource(getResources(), R.drawable.six))));
            listOfAnimationImages.add( createTrimmedBitmap(getScaledBitmap3(BitmapFactory.decodeResource(getResources(), R.drawable.seven))));
            listOfAnimationImages.add( createTrimmedBitmap(getScaledBitmap3(BitmapFactory.decodeResource(getResources(), R.drawable.eight))));
            listOfAnimationImages.add( createTrimmedBitmap(getScaledBitmap3(BitmapFactory.decodeResource(getResources(), R.drawable.nine))));
            listOfAnimationImages.add( createTrimmedBitmap(getScaledBitmap3(BitmapFactory.decodeResource(getResources(), R.drawable.ten))));
            listOfAnimationImages.add( createTrimmedBitmap(getScaledBitmap3(BitmapFactory.decodeResource(getResources(), R.drawable.eleven))));
            listOfAnimationImages.add( createTrimmedBitmap(getScaledBitmap3(BitmapFactory.decodeResource(getResources(), R.drawable.twelve))));
            listOfAnimationImages.add( createTrimmedBitmap(getScaledBitmap3(BitmapFactory.decodeResource(getResources(), R.drawable.thirteen))));
            listOfAnimationImages.add( createTrimmedBitmap(getScaledBitmap3(BitmapFactory.decodeResource(getResources(), R.drawable.fourteen))));
            listOfAnimationImages.add( createTrimmedBitmap(getScaledBitmap3(BitmapFactory.decodeResource(getResources(), R.drawable.fifteen))));
            listOfAnimationImages.add( createTrimmedBitmap(getScaledBitmap3(BitmapFactory.decodeResource(getResources(), R.drawable.sixteen))));
            listOfAnimationImages.add( createTrimmedBitmap(getScaledBitmap3(BitmapFactory.decodeResource(getResources(), R.drawable.seventeen))));
            listOfAnimationImages.add( createTrimmedBitmap(getScaledBitmap3(BitmapFactory.decodeResource(getResources(), R.drawable.eighteen))));
            listOfAnimationImages.add( createTrimmedBitmap(getScaledBitmap3(BitmapFactory.decodeResource(getResources(), R.drawable.nineteen))));
            listOfAnimationImages.add( createTrimmedBitmap(getScaledBitmap3(BitmapFactory.decodeResource(getResources(), R.drawable.twenty))));
            listOfAnimationImages.add( createTrimmedBitmap(getScaledBitmap3(BitmapFactory.decodeResource(getResources(), R.drawable.twentyone))));




        }
        Bitmap getScaledBitmap3(Bitmap bitmap) {

            WindowManager window = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            Display display = window.getDefaultDisplay();
            int width = display.getWidth();
            int height = display.getHeight();

            return Bitmap.createScaledBitmap(bitmap,width,height, true);
        }

        private void playAnim(final Canvas canvas) {

            if (!isEnableAnimation) {
                if (animationCounter != 0) {
                    animationCounter = 0;
                }
                if (isAnimationActivate)
                    isAnimationActivate = false;
                previousAnimationCounter=animationCounter;
                return;
            }
//                    updateBrightness(255);
            if ((animationBitmaps == null) || !isLoaded) {
                return;
            }

            if (isAnimationActivate) {
                animationCounter++;
//                paint = null;
            }
            if (animationCounter >=animationBitmaps.length) {
//                animationCounter = 0;
                animationCounter = animationBitmaps.length - 1;

//                changeAnimation();
                fadeOut();


            }
//            new Thread(){
//                @Override
//                public void run() {
//                    if(previousAnimationCounter!=animationCounter||originalBitmap==null||resizedBitmap==null) {
//                        originalBitmap = new BitmapDrawable(getResources(), myGif.getFrames()[animationCounter].getImage());
//                        resizedBitmap=getScaledBitmap(originalBitmap.getBitmap());
//                        Log.e("eeeeeeeeeeeeeee","goes here");
//                        previousAnimationCounter=animationCounter;
//                    }
//                }
//            }.start();

            if((animationCounter!=0)) {
                if(previousAnimationNumber!=animationNumber||previousAnimationCounter!=animationCounter||originalBitmap ==null||resizedBitmap ==null) {

                    new Thread(){
                        @Override
                        public void run() {


//


//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//      Bitmap b=  Bitmap.createScaledBitmap(MyWatchFace.originalBitmap, newWidth, newHeight, true);
//        b.compress(Bitmap.CompressFormat.WEBP,10,out);

                            originalBitmap = animationBitmaps[animationCounter];
                            resizedBitmap =getScaledBitmap(originalBitmap);




                        }
                    }.start();

//            startService(new Intent(getApplicationContext(),MyService.class));
                }
                drawAnim(canvas,false);
            }else{
                if((animationCounter==0))

                    drawAnim(canvas,true);
            }

//            canvas.drawRect(animationLeft, animationTop,animationLeft+resizedBitmap.getWidth(),animationTop+resizedBitmap.getHeight(),paint);


        }

        private void drawAnim(Canvas canvas,boolean playFirstImage){
            previousAnimationCounter=animationCounter;
            previousAnimationNumber=animationNumber;
            if(!playFirstImage&&resizedBitmap ==null) {
                canvas.drawBitmap(listOfAnimationImages.get(animationNumber),Integer.parseInt(listOfAnimationImagesLocation.get(animationNumber).split(",,")[0]), Integer.parseInt(listOfAnimationImagesLocation.get(animationNumber).split(",,")[1]), null);

                return;
            }
            if(animationCounter!=0){
                animationTop=0;
                animationLeft=0;
            }


            if(playFirstImage){
//                if(previousAnimationNumber!=animationNumber||MyWatchFace.previousAnimationCounter!=MyWatchFace.animationCounter||MyWatchFace.originalBitmap ==null||MyWatchFace.resizedBitmap ==null) {
////                    MyWatchFace.resizedBitmap =listOfAnimationImages.get(animationNumber);
//                Log.e("play ", "true   ");
//                }
                canvas.drawBitmap(listOfAnimationImages.get(animationNumber),Integer.parseInt(listOfAnimationImagesLocation.get(animationNumber).split(",,")[0]), Integer.parseInt(listOfAnimationImagesLocation.get(animationNumber).split(",,")[1]), null);

            }
            else {
//                Log.e("play ", "false   ");
                canvas.drawBitmap(resizedBitmap, animationLeft, animationTop, null);

            }
        }

        int animationTop,animationLeft;
        public  Bitmap createTrimmedBitmap(Bitmap original) {


            float sampleClopSize=50;
            float trimSizeX=original.getWidth()/sampleClopSize;
            float trimSizeY=original.getHeight()/sampleClopSize;


//            int[] pix = new int[bmp.getHeight()*bmp.getWidth()];
//            bmp.getPixels(pix, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
//int [][]pixels= new int[bmp.getWidth()][bmp.getHeight()];
//            for(int i =0; i<bmp.getWidth();i++) {
//               for (int j =0; j<bmp.getHeight();j++){
//                   pixels[i][j]=pix[i*bmp.getHeight()+j];
//                   Log.e("", "pixel"+i*bmp.getHeight()+j+"b   - " +pixels[i][j]);
//               }
//
//            }
            Bitmap bmp=  Bitmap.createScaledBitmap(original, (int)sampleClopSize, (int)sampleClopSize, true);

            int minX = bmp.getWidth();
            int minY = bmp.getHeight();
            int maxX = -1;
            int maxY = -1;



//
//            int[] pix = new int[bmp.getHeight()*bmp.getWidth()];
//            bmp.getPixels(pix, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
//int [][]pixels= new int[bmp.getWidth()][bmp.getHeight()];
//            for(int i =0; i<bmp.getWidth();i++) {
//               for (int j =0; j<bmp.getHeight();j++){
//                   pixels[i][j]=pix[i*bmp.getHeight()+j];
//                   Log.e("", "pixel"+i*bmp.getHeight()+j+"b   - " +pixels[i][j]);
//               }
//
//            }


//            for(int y = 0; y < bmp.getHeight(); y++)
//            {
//                for(int x = 0; x < bmp.getWidth(); x++)
//                {
//                    int alpha = (bmp.getPixel(x, y) >> 24) & 255;
//                    if(alpha > 0)   // pixel is not 100% transparent
//                    {
//                        if(x < minX)
//                            minX = x;
//                        if(x > maxX)
//                            maxX = x;
//                        if(y < minY)
//                            minY = y;
//                        if(y > maxY)
//                            maxY = y;
//                    }
//                }
//            }

            minX=getMinX(bmp);
            maxX=getMaxX(bmp);
            minY=getMinY(bmp);
            maxY=getMaxY(bmp);
            if((maxX < minX) || (maxY < minY))
                return null; // Bitmap is entirely transparent
            animationTop=(int)(minY*trimSizeY);
            animationLeft=(int)(minX*trimSizeX);
            listOfAnimationImagesLocation.add(animationLeft+",,"+animationTop);
            // crop bitmap to non-transparent area and return:
            return Bitmap.createBitmap(original, (int)(minX*trimSizeX), (int)(minY*trimSizeY), (int)((maxX - minX+ 1)*trimSizeX ), (int)((maxY - minY+ 1) *trimSizeY));




        }

        private int getMinX(Bitmap bmp){
            int minX=bmp.getWidth();

            for(int y = 0; y < bmp.getHeight(); y++)
            {
                for(int x = 0; x <minX; x++)
                {
                    int alpha = (bmp.getPixel(x, y) >> 24) & 255;
                    if(alpha > 0)   // pixel is not 100% transparent
                    {
                        if(x < minX){
                            minX = x;
                            break;}

                    }
                }
            }

//            if(minx==bit.getWidth()){
//                minx=0;
//            }
            return minX;

        }


        private int getMaxX(Bitmap bmp){
            int maxX=0;

            for(int y = 0; y < bmp.getHeight(); y++)
            {
                for(int x = bmp.getWidth()-1; x >maxX; x--)
                {
                    int alpha = (bmp.getPixel(x, y) >> 24) & 255;
                    if(alpha > 0)   // pixel is not 100% transparent
                    {
                        if(x > maxX) {
                            maxX = x;

                            break;}

                    }
                }
            }

//            if(minx==bit.getWidth()){
//                minx=0;
//            }
            return maxX;

        }
        private int getMaxY(Bitmap bmp){
            int maxY=0;

            for(int x = 0; x < bmp.getHeight(); x++)
            {
                for(int y = bmp.getWidth()-1;y >maxY; y--)
                {
                    int alpha = (bmp.getPixel(x, y) >> 24) & 255;
                    if(alpha > 0)   // pixel is not 100% transparent
                    {
                        if(y > maxY){
                            maxY = y;

                            break;}

                    }
                }
            }

//            if(minx==bit.getWidth()){
//                minx=0;
//            }
            return maxY;

        }

        private int getMinY(Bitmap bmp){
            int minY=bmp.getWidth();

            for(int x = 0; x < bmp.getHeight(); x++)
            {
                for(int y = 0; y <minY; y++)
                {
                    int alpha = (bmp.getPixel(x, y) >> 24) & 255;
                    if(alpha > 0)   // pixel is not 100% transparent
                    {
                        if(y < minY){
                            minY = y;
                            break;}

                    }
                }
            }

//            if(minx==bit.getWidth()){
//                minx=0;
//            }
            return minY;

        }

        private void updateBrightness(int brightness) {
            try {
                if (brightness < 0)
                    brightness = 0;
                else if (brightness > 255)
                    brightness = 255;
                android.provider.Settings.System.putInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE, android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                if (!mAmbient)
                    android.provider.Settings.System.putInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, brightness);
                else
                    android.provider.Settings.System.putInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, brightness);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void fadeOut() {

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
//                    if (isHourChanged) {
//                        int alpha = 250;
//                        while (alpha > 0 && !isInAmbientMode()) {
//                            try {
//                                Thread.sleep(10);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            alpha -= 10;
//                            paint.setAlpha(alpha);
//
//                        }
//                        alpha = 0;
//                        paint.setAlpha(alpha);
//
//                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {

                    if (shouldTimerBeRunning()) {
                        animationCounter = 0;


                        // set next animation
                        if (isHourChanged) {
//                                    changeAnimation();
                            isHourChanged = false;
                        }


                    }

                    wakeUnlock();
                    if (shouldTimerBeRunning()){
                        isAnimationActivate=false;
                        fadeIn();}

                    super.onPostExecute(aVoid);
                }
            }.execute();
//            new Thread() {
//                @Override
//                public void run() { }
//            }.start();
        }

        private void fadeIn() {

            new Thread() {
                @Override
                public void run() {
//                    int alpha = 0;
////                    while (alpha < 250 && !isInAmbientMode()) {
////                        try {
////                            sleep(10);
////                        } catch (InterruptedException e) {
////                            e.printStackTrace();
////                        }
////                        alpha += 10;
////                        paint.setAlpha(alpha);
////
////                    }
//                    alpha = 255;
//                    paint.setAlpha(alpha);
                    animationCounter = 0;

                }
            }.start();
        }


        private void changeAnimation() {
            if (isAnimationActivate||!isLoaded) {
                return;
            }
            animationNumber++;
            if (animationNumber >= animationList.size()) {
                animationNumber = 0;
            }
            loadAnimation(animationList.get(animationNumber));
        }

//        public Bitmap drawableToBitmap(Drawable drawable) {
//            if (drawable instanceof BitmapDrawable) {
//                return ((BitmapDrawable) drawable).getBitmap();
//            }
//
//            int width = drawable.getIntrinsicWidth();
//            width = width > 0 ? width : 1;
//            int height = drawable.getIntrinsicHeight();
//            height = height > 0 ? height : 1;
//
//            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(bitmap);
//            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
//            drawable.draw(canvas);
//
//            return bitmap;
//        }

        private void initBackgroundList() {
            backgroundList.removeAll(backgroundList);
            backgroundList_abc.removeAll(backgroundList_abc);

            backgroundList.add(R.drawable.bg1);
            backgroundList.add(R.drawable.bg2);
//            backgroundList.add(R.drawable.bg3);
//            backgroundList.add(R.drawable.bg4);
//            backgroundList.add(R.drawable.bg5);
            backgroundList.add(R.drawable.bg6);
            backgroundList.add(R.drawable.bg7);
            backgroundList.add(R.drawable.bg8);
            backgroundList.add(R.drawable.bg9);
//            backgroundList.add(R.drawable.bg10);
//            backgroundList.add(R.drawable.bg11);
//            backgroundList.add(R.drawable.bg12);
//            backgroundList.add(R.drawable.bg13);

//            backgroundList.add(R.drawable.bg7);
//            backgroundList.add(R.drawable.bg8);
//            backgroundList.add(R.drawable.bg9);
//            backgroundList.add(R.drawable.bg10);
//            backgroundList.add(R.drawable.bg11);


            backgroundList_abc.add(R.drawable.bg1_abc);
            backgroundList_abc.add(R.drawable.bg2_abc);
//            backgroundList_abc.add(R.drawable.bg3_abc);
//            backgroundList_abc.add(R.drawable.bg4_abc);
//            backgroundList_abc.add(R.drawable.bg5_abc);
            backgroundList_abc.add(R.drawable.bg6_abc);


            backgroundList_abc.add(R.drawable.bg7_abc);
            backgroundList_abc.add(R.drawable.bg8_abc);
            backgroundList_abc.add(R.drawable.bg9_abc);
//            backgroundList_abc.add(R.drawable.bg10_abc);
//            backgroundList_abc.add(R.drawable.bg11_abc);
//            backgroundList_abc.add(R.drawable.bg12_abc);
//            backgroundList_abc.add(R.drawable.bg13_abc);
//            backgroundList_abc.add(R.drawable.bg7_abc);
//            backgroundList_abc.add(R.drawable.bg8_abc);
//            backgroundList_abc.add(R.drawable.bg9_abc);
//            backgroundList_abc.add(R.drawable.bg10_abc);
//            backgroundList_abc.add(R.drawable.bg11_abc);

        }

        private void initBitmaps() {
            backgroundBitmap_abc = ((BitmapDrawable) getDrawable(backgroundList_abc.get(0))).getBitmap();

            backgroundBitmap = ((BitmapDrawable) getDrawable(backgroundList.get(0))).getBitmap();

            Resources resources = getResources();

            zero_bitmap = ((BitmapDrawable) resources.getDrawable(R.drawable.number0, null)).getBitmap();
            one_bitmap = ((BitmapDrawable) resources.getDrawable(R.drawable.number1, null)).getBitmap();
            two_bitmap = ((BitmapDrawable) resources.getDrawable(R.drawable.number2, null)).getBitmap();
            three_bitmap = ((BitmapDrawable) resources.getDrawable(R.drawable.number3, null)).getBitmap();
            four_bitmap = ((BitmapDrawable) resources.getDrawable(R.drawable.number4, null)).getBitmap();
            five_bitmap = ((BitmapDrawable) resources.getDrawable(R.drawable.number5, null)).getBitmap();
            six_bitmap = ((BitmapDrawable) resources.getDrawable(R.drawable.number6, null)).getBitmap();
            seven_bitmap = ((BitmapDrawable) resources.getDrawable(R.drawable.number7, null)).getBitmap();
            eight_bitmap = ((BitmapDrawable) resources.getDrawable(R.drawable.number8, null)).getBitmap();
            nine_bitmap = ((BitmapDrawable) resources.getDrawable(R.drawable.number9, null)).getBitmap();
            blockBitmap = ((BitmapDrawable) resources.getDrawable(R.drawable.brick, null)).getBitmap();

            batteryBitmap = ((BitmapDrawable) resources.getDrawable(R.drawable.battery_2, null)).getBitmap();
            batteryBitmap_abc = ((BitmapDrawable) resources.getDrawable(R.drawable.battery_2_abc, null)).getBitmap();

//            seconds_bitmap = ((BitmapDrawable) resources.getDrawable(R.drawable.sec, null)).getBitmap();


            zero_amb_bitmap = ((BitmapDrawable) resources.getDrawable(R.drawable.number0_abc, null)).getBitmap();
            one_amb_bitmap = ((BitmapDrawable) resources.getDrawable(R.drawable.number1_abc, null)).getBitmap();
            two_amb_bitmap = ((BitmapDrawable) resources.getDrawable(R.drawable.number2_abc, null)).getBitmap();
            three_amb_bitmap = ((BitmapDrawable) resources.getDrawable(R.drawable.number3_abc, null)).getBitmap();
            four_amb_bitmap = ((BitmapDrawable) resources.getDrawable(R.drawable.number4_abc, null)).getBitmap();
            five_amb_bitmap = ((BitmapDrawable) resources.getDrawable(R.drawable.number5_abc, null)).getBitmap();
            six_amb_bitmap = ((BitmapDrawable) resources.getDrawable(R.drawable.number6_abc, null)).getBitmap();
            seven_amb_bitmap = ((BitmapDrawable) resources.getDrawable(R.drawable.number7_abc, null)).getBitmap();
            eight_amb_bitmap = ((BitmapDrawable) resources.getDrawable(R.drawable.number8_abc, null)).getBitmap();
            nine_amb_bitmap = ((BitmapDrawable) resources.getDrawable(R.drawable.number9_abc, null)).getBitmap();
            blockBitmap_abc = ((BitmapDrawable) resources.getDrawable(R.drawable.brick_abc, null)).getBitmap();
//            seconds_bitmap_abc = ((BitmapDrawable) resources.getDrawable(R.drawable.sec_abc, null)).getBitmap();
//            date_amb_bitmap = ((BitmapDrawable) resources.getDrawable(R.drawable.date_abc, null)).getBitmap();


//            zero_amb_bitmap = ((BitmapDrawable) resources.getDrawable(R.drawable.amb_number0, null)).getBitmap();
//            one_amb_bitmap = ((BitmapDrawable) resources.getDrawable(R.drawable.amb_number1, null)).getBitmap();
//            two_amb_bitmap = ((BitmapDrawable) resources.getDrawable(R.drawable.amb_number2, null)).getBitmap();
//            three_amb_bitmap = ((BitmapDrawable) resources.getDrawable(R.drawable.amb_number3, null)).getBitmap();
//            four_amb_bitmap = ((BitmapDrawable) resources.getDrawable(R.drawable.amb_number4, null)).getBitmap();
//            five_amb_bitmap = ((BitmapDrawable) resources.getDrawable(R.drawable.amb_number5, null)).getBitmap();
//            six_amb_bitmap = ((BitmapDrawable) resources.getDrawable(R.drawable.amb_number6, null)).getBitmap();
//            seven_amb_bitmap = ((BitmapDrawable) resources.getDrawable(R.drawable.amb_number7, null)).getBitmap();
//            eight_amb_bitmap = ((BitmapDrawable) resources.getDrawable(R.drawable.amb_number8, null)).getBitmap();
//            nine_amb_bitmap = ((BitmapDrawable) resources.getDrawable(R.drawable.amb_number9, null)).getBitmap();


        }

        private void initScalledBitmaps() {
            WindowManager window = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            Display display = window.getDefaultDisplay();
            int width = display.getWidth();
            int height = display.getHeight();
            mainScaleX = ((float) width) / ((float) backgroundBitmap.getWidth());
            mainScaleY = ((float) height) / ((float) backgroundBitmap.getHeight());

            backgroundBitmapScaled = Bitmap.createScaledBitmap(backgroundBitmap, (int) (((float) backgroundBitmap.getWidth()) * mainScaleX), (int) (((float) backgroundBitmap.getHeight()) * mainScaleY), true);
            backgroundBitmapScaled_abc= getScaledBitmap(backgroundBitmap_abc);
            scaledZero_amb_bitmap = getScaledBitmap(zero_amb_bitmap);
            scaledOne_amb_bitmap = getScaledBitmap(one_amb_bitmap);
            scaledTwo_amb_bitmap = getScaledBitmap(two_amb_bitmap);
            scaledThree_amb_bitmap = getScaledBitmap(three_amb_bitmap);
            scaledFour_amb_bitmap = getScaledBitmap(four_amb_bitmap);
            mScaledFive_amb_bitmap = getScaledBitmap(five_amb_bitmap);
            scaledSix_amb_bitmap = getScaledBitmap(six_amb_bitmap);
            scaledSeven_amb_bitmap = getScaledBitmap(seven_amb_bitmap);
            scaledEight_amb_bitmap = getScaledBitmap(eight_amb_bitmap);
            scaledNine_amb_bitmap = getScaledBitmap(nine_amb_bitmap);
            batteryScaledBitmap = getScaledBitmap(batteryBitmap);
            batteryScaledBitmap_abc = getScaledBitmap(batteryBitmap_abc);
            blockBitmap_abc_Scalled = getScaledBitmap(blockBitmap_abc);
//            dateBitmap_Scalled = getScaledBitmap(date_bitmap);
//            dateBitmap_abc_Scalled = getScaledBitmap(date_amb_bitmap);
//            secondsBlockBitmapScalled_abc = getScaledBitmap(seconds_bitmap_abc);


            scaledZero_bitmap = getScaledBitmap(zero_bitmap);
            scaledOne_bitmap = getScaledBitmap(one_bitmap);
            scaledTwo_bitmap = getScaledBitmap(two_bitmap);
            scaledThree_bitmap = getScaledBitmap(three_bitmap);
            scaledFour_bitmap = getScaledBitmap(four_bitmap);
            scaledFive_bitmap = getScaledBitmap(five_bitmap);
            scaledSix_bitmap = getScaledBitmap(six_bitmap);
            scaledSeven_bitmap = getScaledBitmap(seven_bitmap);
            scaledEight_bitmap = getScaledBitmap(eight_bitmap);
            scaledNine_bitmap = getScaledBitmap(nine_bitmap);
            blockScaledBitmap = getScaledBitmap(blockBitmap);
//            secondsBlockBitmapScalled = getScaledBitmap(seconds_bitmap);
        }

        private void initValues() {
            WindowManager window = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            Display display = window.getDefaultDisplay();
            int width = display.getWidth();
            int height = display.getHeight();
            blockStartY = 50f * mainScaleX;
            blockStartX = (width / 2) - blockScaledBitmap.getWidth();
            Bitmap num1Bitmap = getTimeBitmap(0);

            numberStartY = blockStartY + blockScaledBitmap.getHeight() /2-num1Bitmap.getHeight()/2;
        }

//        @Override
//        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//            super.onSurfaceChanged(holder, format, width, height);
//
//        }

        private Bitmap getScaledBitmap(Bitmap bitmap) {
            return Bitmap.createScaledBitmap(bitmap, (int) (((float) bitmap.getWidth()) * mainScaleX), (int) (((float) bitmap.getHeight()) * mainScaleY), true);
        }

        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            super.onDestroy();
        }

        private Paint createTextPaint(int textColor) {
            Paint paint = new Paint();
            paint.setColor(textColor);
            paint.setTypeface(NORMAL_TYPEFACE);
            paint.setAntiAlias(true);
            return paint;
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {
                registerReceiver();

                // Update time zone in case it changed while we weren't visible.
                mTime.clear(TimeZone.getDefault().getID());
                mTime.setToNow();
            } else {
                unregisterReceiver();
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            DragonballFace.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            DragonballFace.this.unregisterReceiver(mTimeZoneReceiver);
        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            super.onApplyWindowInsets(insets);

            // Load resources that have alternate values for round watches.
            Resources resources = DragonballFace.this.getResources();
            boolean isRound = insets.isRound();
            mXOffset = resources.getDimension(isRound
                    ? R.dimen.digital_x_offset_round : R.dimen.digital_x_offset);
            float textSize = resources.getDimension(isRound
                    ? R.dimen.digital_text_size_round : R.dimen.digital_text_size);

            mTextPaint.setFakeBoldText(true);
            mTextPaint.setTextSize(textSize);
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            if (mAmbient != inAmbientMode) {
                mAmbient = inAmbientMode;
                if (mLowBitAmbient) {
                    mTextPaint.setAntiAlias(!inAmbientMode);
                }
                invalidate();
            }

            if (inAmbientMode) {
                setAmbienceBackground(backgroundImageCounter);
            } else {
                setNonAmbienceBackground(backgroundImageCounter);
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }


        long touchTime = 0;
        final long maxTouchTime = 150;

        /**
         * Captures tap event (and tap type) and toggles the background color if the user finishes
         * a tap.
         */
        @Override
        public void onTapCommand(int tapType, int x, int y, long eventTime) {
//            Resources resources = DragonballFace.this.getResources();
//            switch (tapType) {
//                case TAP_TYPE_TOUCH:
//                    // The user has started touching the screen.
//                    break;
//                case TAP_TYPE_TOUCH_CANCEL:
//                    // The user has started a different gesture or otherwise cancelled the tap.
//                    break;
//                case TAP_TYPE_TAP:
//                    // The user has completed the tap gesture.
//                    mTapCount++;
////                    mBackgroundPaint.setColor(resources.getColor(mTapCount % 2 == 0 ?
////                            R.color.background : R.color.background2));
////                    changeStage();
//
////                    isUpdateTime = true;
//                    enableAnimation();
////                    timeTolayAnimaton = true;
//                    break;
//            }
//            invalidate();


            if (tapType == TAP_TYPE_TOUCH) {
                touchTime = eventTime;
                Log.e("TAP_TYPE_TOUCH", "TAP_TYPE_TOUCH");
            }
            if (tapType == TAP_TYPE_TAP) {
                touchTime = eventTime - touchTime;
                if (touchTime < maxTouchTime) {
                    if (isChangingBackgoundByTouch) {
                        changeStage();
                    }
                    else   if(isChangingAnimationByTouch) {
//                        enableAnimation();
                        changeAnimation();
                        Log.e("changeAnimation", "" + touchTime);

                    }
                }
                Log.e("TAP_TYPE_TAP", "" + touchTime);

                touchTime = 0;
            }

        }


        int backgroundImageCounter = 0;

        private void changeStage() {
            backgroundImageCounter++;
            if (backgroundImageCounter >= backgroundList.size()) {
                backgroundImageCounter = 0;
            }

            if (shouldTimerBeRunning()) {
                setNonAmbienceBackground(backgroundImageCounter);
            } else {
                setAmbienceBackground(backgroundImageCounter);
            }

        }

        private void setNonAmbienceBackground(int id) {

            backgroundBitmap = ((BitmapDrawable) getDrawable(backgroundList.get(backgroundImageCounter))).getBitmap();

            backgroundBitmapScaled = Bitmap.createScaledBitmap(backgroundBitmap, (int) (((float) backgroundBitmap.getWidth()) * mainScaleX), (int) (((float) backgroundBitmap.getHeight()) * mainScaleY), true);

        }

        private void setAmbienceBackground(int id) {

            backgroundBitmap = ((BitmapDrawable) getDrawable(backgroundList_abc.get(id))).getBitmap();

            backgroundBitmapScaled = Bitmap.createScaledBitmap(backgroundBitmap, (int) (((float) backgroundBitmap.getWidth()) * mainScaleX), (int) (((float) backgroundBitmap.getHeight()) * mainScaleY), true);

        }

        boolean isHourChanged = false;
        private int previousHour = -1, previousMinute = -1;
        boolean previousIs24HourType=is24HourType;

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            // Draw the background.
            Paint paint= new Paint();

            WindowManager window = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            Display display = window.getDefaultDisplay();
            int width = display.getWidth();
            int height = display.getHeight();

//                    if (isInAmbientMode()){
//                        updateBrightness(100);
//                    }else{
//                        updateBrightness(255);
//                    }
//                canvas.drawRect(0, 0, bounds.width(), bounds.height(), mBackgroundPaint);
            canvas.drawBitmap(shouldTimerBeRunning()?backgroundBitmapScaled:backgroundBitmapScaled_abc, 0, 0, null);


            // Draw H:MM in ambient mode or H:MM:SS in interactive mode.
            mTime.setToNow();
//            String text = mAmbient
//                    ? String.format("%d:%02d", mTime.hour, mTime.minute)
//                    : String.format("%d:%02d:%02d", mTime.hour, mTime.minute, mTime.second);
//            canvas.drawText(text, mXOffset, mYOffset, mTextPaint);
            int tempHour = mTime.hour, tempMinute = mTime.minute, timeTextOneByOne = 0;
            float numberHourX1, numberHourX2, numberMinuteX1, numberMinuteX2;
            if (previousMinute == -1) {
                previousMinute = tempMinute;
            }
            String hourExtra=null;
            Date date = new Date();
            if (!is24HourType) {
                hourExtra=(tempHour<12)?"AM":"PM";
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");


                String newTimeFormat = sdf.format(date);

                try {
                    String newHourFormat = newTimeFormat.split(":")[0];
                    tempHour = Integer.parseInt(newHourFormat);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if(previousIs24HourType!=is24HourType){
                previousHour=tempHour;
            }


            if (tempHour < 10) {
                timeTextOneByOne = 0;

            } else {
                timeTextOneByOne = tempHour / 10;
            }

            if (previousHour == -1) {
                previousHour = tempHour;
            } else if (previousHour != tempHour) {
//                        if (!isChangingBackgoundByTouch)
//                            changeStage();

                isHourChanged = true;
                changeAnimation();
            }


            canvas.drawBitmap(!shouldTimerBeRunning() ? blockBitmap_abc_Scalled : blockScaledBitmap, width/2-blockScaledBitmap.getWidth()-10, blockStartY, null);
            canvas.drawBitmap(!shouldTimerBeRunning() ? blockBitmap_abc_Scalled : blockScaledBitmap, width/2+10, blockStartY, null);

            if(shouldTimerBeRunning()) {// draw energy
//                Paint transPaint= new Paint();
//                transPaint.setColor(getResources().getColor(R.color.transparent_black_percent_75));
//                canvas.drawRect(width/2-blockScaledBitmap.getWidth()-10, blockStartY,width/2-blockScaledBitmap.getWidth()-10+ blockScaledBitmap.getWidth(), blockStartY+blockScaledBitmap.getHeight(), transPaint);
//                canvas.drawRect(width/2+10, blockStartY,width/2+10+ blockScaledBitmap.getWidth(), blockStartY+blockScaledBitmap.getHeight(), transPaint);
//
//
//                Paint redPaint= new Paint();
//                redPaint.setColor(getResources().getColor(R.color.dark_red));
//                Paint greenPaint= new Paint();
//                greenPaint.setColor(getResources().getColor(R.color.ForestGreen));
//                float hourX=width / 2 - blockScaledBitmap.getWidth() - 10;
//
//                float hourTotalEndWidth=width / 2 - blockScaledBitmap.getWidth() - 10+ blockScaledBitmap.getWidth();
//                float hourDistanse=hourTotalEndWidth-hourX;

                int usedTempHour=tempHour;
                if(!is24HourType&&hourExtra.equals("PM")){
                    usedTempHour+=12;
                }
//                float distanseGreen=hourDistanse*(1-usedTempHour/23.0f);
//                canvas.drawRect(hourX,  blockStartY+5*blockScaledBitmap.getHeight()/6,hourX+distanseGreen, blockStartY+blockScaledBitmap.getHeight(), greenPaint);
//                canvas.drawRect(hourX+distanseGreen,  blockStartY+5*blockScaledBitmap.getHeight()/6, hourTotalEndWidth, blockStartY+blockScaledBitmap.getHeight(), redPaint);
//
//
//                float minX=width/2+10;
//
//                float minTotalEndWidth=width/2+10+ blockScaledBitmap.getWidth();
//                float minDistanse=minTotalEndWidth-minX;
//
//                float distanseMinGreen=minDistanse*(1-tempMinute/59.0f);
//
//                canvas.drawRect(minX,  blockStartY+5*blockScaledBitmap.getHeight()/6,minX+distanseMinGreen, blockStartY+blockScaledBitmap.getHeight(), greenPaint);
//                canvas.drawRect(minX+distanseMinGreen,  blockStartY+5*blockScaledBitmap.getHeight()/6, minTotalEndWidth, blockStartY+blockScaledBitmap.getHeight(), redPaint);

            }
            if (shouldTimerBeRunning()) {
                if (previousMinute != tempMinute) {

                    enableAnimation();
                }
//                if (timeTolayAnimaton) {
                if (isEnableAnimation) {
                    playAnim(canvas);
                }

//                }
            } else {
                if (isEnableAnimation&&myGif!=null) {

//                    final BitmapDrawable myDrawable = new BitmapDrawable(getResources(), myGif.getFrames()[animationCounter].getImage());
//                    canvas.drawBitmap(getAmbienceGifImage(getScaledBitmap(myDrawable.getBitmap())), 0, 0, paint);

                    canvas.drawBitmap(getAmbienceGifImage(listOfAnimationImages.get(animationNumber)),Integer.parseInt(listOfAnimationImagesLocation.get(animationNumber).split(",,")[0]), Integer.parseInt(listOfAnimationImagesLocation.get(animationNumber).split(",,")[1]), null);

                }
            }

            Bitmap num1Bitmap = getTimeBitmap(timeTextOneByOne);

            numberHourX1 = blockStartX + blockScaledBitmap.getWidth() / 10-10;


            canvas.drawBitmap(num1Bitmap, numberHourX1, numberStartY, null);


            if (tempHour < 10) {
                timeTextOneByOne = tempHour;
            } else {
                timeTextOneByOne = tempHour - ((tempHour / 10) * 10);
            }



            numberHourX2 = numberHourX1 + num1Bitmap.getWidth();
            Bitmap num2Bitmap = getTimeBitmap(timeTextOneByOne);


            canvas.drawBitmap(num2Bitmap, numberHourX2, numberStartY, null);



            if (tempMinute < 10) {
                timeTextOneByOne = 0;
            } else {
                timeTextOneByOne = tempMinute / 10;
            }
            Bitmap num3Bitmap = getTimeBitmap(timeTextOneByOne);

            numberMinuteX1 = blockStartX + blockScaledBitmap.getWidth() + 5 + blockScaledBitmap.getWidth() / 10+10;

            canvas.drawBitmap(num3Bitmap, numberMinuteX1, numberStartY, null);
            if (tempMinute < 10) {
                timeTextOneByOne = tempMinute;
            } else {
                timeTextOneByOne = tempMinute - ((tempMinute / 10) * 10);
            }

            Bitmap num4Bitmap = getTimeBitmap(timeTextOneByOne);
            numberMinuteX2 = numberMinuteX1 + num3Bitmap.getWidth();
            canvas.drawBitmap(num4Bitmap, numberMinuteX2, numberStartY, null);

            if( isDateVisible) {
                Calendar c = Calendar.getInstance();
                Paint paint2= new Paint();
                paint2.setColor(!shouldTimerBeRunning()?getResources().getColor(R.color.DarkGray):getResources().getColor(R.color.Orange));

                String formattedDate =  c.get(Calendar.DAY_OF_MONTH)+"/"+ c.get(Calendar.MONTH)+"/"+ Integer.toString(c.get(Calendar.YEAR)).substring(Integer.toString(c.get(Calendar.YEAR)).length()-2);

                paint2.setTextSize( 23);
                paint2.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD_ITALIC));

                Paint p=new Paint();
                p.setColor(getResources().getColor(R.color.transparent_black_percent_40));

                canvas.drawRect( (int)(numberHourX1/3-5),blockScaledBitmap.getHeight()+blockStartY,numberHourX1/3+num1Bitmap.getWidth()/10+(formattedDate.length()-2)*convertToPx(9)+5,blockScaledBitmap.getHeight()+blockStartY+30,p);
                canvas.drawText(formattedDate, (int)(numberHourX1/3),blockScaledBitmap.getHeight()+blockStartY+23, paint2);
            }

            if(!is24HourType){
                paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                paint.setTextSize(23);
                paint.setColor(!shouldTimerBeRunning()?getResources().getColor(R.color.white):getResources().getColor(R.color.gray_dark));

                canvas.drawText(hourExtra, (int)(width/2+3*blockScaledBitmap.getWidth()/7.0),num1Bitmap.getHeight()+numberStartY+25, paint);

            }


            if(isBatteryVisible){
                Paint bp= new Paint();
                bp.setTextSize(17);
                bp.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD_ITALIC));
                canvas.drawBitmap((!shouldTimerBeRunning())?batteryScaledBitmap_abc:batteryScaledBitmap,width-numberHourX1/3-batteryScaledBitmap_abc.getWidth(),blockStartY+blockScaledBitmap.getHeight(),null);
                IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                Intent batteryStatus = getApplicationContext().registerReceiver(null, iFilter);
                if(!shouldTimerBeRunning()){
                    bp.setColor(getResources().getColor(R.color.gray_dark));
                }

                canvas.drawText(Integer.toString( batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1))+"%",width-numberHourX1/3-batteryScaledBitmap_abc.getWidth()+batteryScaledBitmap.getWidth()/4,(blockStartY+blockScaledBitmap.getHeight()+2*batteryScaledBitmap.getHeight()/3.0f),bp);
            }



//            canvas.drawBitmap(isInAmbientMode() ? secondsBlockBitmapScalled_abc : secondsBlockBitmapScalled, secX, secY, null);
            if (shouldTimerBeRunning()) {
                Paint secontPaint = new Paint();
                secontPaint.setFakeBoldText(true);
                secontPaint.setColor(getResources().getColor(R.color.dark_red));
                float secX = width / 2 -30, secY = blockScaledBitmap.getHeight()+blockStartY+33;
                Paint backgroundSecPaint=new Paint();
                backgroundSecPaint.setColor(getResources().getColor(R.color.transparent_white_percent_50));
                secontPaint.setTextSize(50);
                canvas.drawOval(new RectF(secX-10 ,10+secY,secX+70,secY-50), backgroundSecPaint);

                secontPaint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));





//                Paint backgroundSecPaint=new Paint();
//                backgroundSecPaint.setColor(getResources().getColor(R.color.transparent_black_percent_50));
//                canvas.drawRect( secX-5 ,5+secY,secX+45,secY-28, backgroundSecPaint);

                canvas.drawText(mTime.second >= 10 ? Integer.toString(mTime.second) : "0" + Integer.toString(mTime.second), secX ,secY, secontPaint);

            }
//            canvas.drawRect(cardBounds, mainPaint);


//            else {
//                animationCounter = 0;
//            }
            previousIs24HourType=is24HourType;
            previousHour = tempHour;
            previousMinute = tempMinute;
        }

        public int convertToPx(int dp) {
            // Get the screen's density scale
            final float scale = getResources().getDisplayMetrics().density;
            // Convert the dps to pixels, based on density scale
            return (int) (dp * scale + 0.5f);
        }

        private Bitmap getAmbienceGifImage(Bitmap src) {
            // constant factors
            final double GS_RED = 0.299;
            final double GS_GREEN = 0.587;
            final double GS_BLUE = 0.114;

            // create output bitmap
            Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
            // pixel information
            int A, R, G, B;
            int pixel;

            // get image size
            int width = src.getWidth();
            int height = src.getHeight();

            // scan through every single pixel
            for(int x = 0; x < width; ++x) {
                for(int y = 0; y < height; ++y) {
                    // get one pixel color
                    pixel = src.getPixel(x, y);
                    // retrieve color of all channels
                    A = Color.alpha(pixel);
                    R = Color.red(pixel);
                    G = Color.green(pixel);
                    B = Color.blue(pixel);
                    // take conversion up to one single value
                    R = G = B = (int)(GS_RED * R + GS_GREEN * G + GS_BLUE * B);
                    // set new pixel color to output bitmap
                    bmOut.setPixel(x, y, Color.argb(A, R, G, B));
                }
            }

            // return final image
            return bmOut;
        }


        public Bitmap getTimeBitmap(int number) {
            switch (number) {
                case 0:
                    return !shouldTimerBeRunning() ? scaledZero_amb_bitmap : scaledZero_bitmap;
                case 1:
                    return !shouldTimerBeRunning() ? scaledOne_amb_bitmap : scaledOne_bitmap;
                case 2:
                    return !shouldTimerBeRunning() ? scaledTwo_amb_bitmap : scaledTwo_bitmap;
                case 3:
                    return !shouldTimerBeRunning() ? scaledThree_amb_bitmap : scaledThree_bitmap;
                case 4:
                    return !shouldTimerBeRunning() ? scaledFour_amb_bitmap : scaledFour_bitmap;
                case 5:
                    return !shouldTimerBeRunning() ? mScaledFive_amb_bitmap : scaledFive_bitmap;
                case 6:
                    return !shouldTimerBeRunning() ? scaledSix_amb_bitmap : scaledSix_bitmap;
                case 7:
                    return !shouldTimerBeRunning() ? scaledSeven_amb_bitmap : scaledSeven_bitmap;
                case 8:
                    return !shouldTimerBeRunning() ? scaledEight_amb_bitmap : scaledEight_bitmap;
                case 9:
                    return !shouldTimerBeRunning() ? scaledNine_amb_bitmap : scaledNine_bitmap;
                default:
                    if (!shouldTimerBeRunning())
                        return scaledZero_amb_bitmap;
                    else
                        return scaledZero_bitmap;
            }
        }

        /**
         * Starts the {@link #mUpdateTimeHandler} timer if it should be running and isn't currently
         * or stops it if it shouldn't be running but currently is.
         */
        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        /**
         * Returns whether the {@link #mUpdateTimeHandler} timer should be running. The timer should
         * only run when we're visible and in interactive mode.
         */
        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }

        /**
         * Handle updating the time periodically in interactive mode.
         */
        private void handleUpdateTimeMessage() {
            invalidate();
            if (shouldTimerBeRunning()) {
                long timeMs = System.currentTimeMillis();
                long delayMs = 120;
                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
            }
        }
    }
}