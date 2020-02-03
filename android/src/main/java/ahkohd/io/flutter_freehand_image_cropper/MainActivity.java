package ahkohd.io.flutter_freehand_image_cropper;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Region;
import android.graphics.Rect;
import android.graphics.Path;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import android.widget.Toast;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;



public class MainActivity extends Activity {
    private Bitmap bitmap;
    private FreeHandCropView freeHandCropView;
    private FreeHandConfig config;

    public static final String EXTRA_ACTION = "action";
    public static final String EXTRA_RESULT = "result";
    public static final String EXTRA_ERROR = "error";
    public static final String ACTION_RESULT = "flutter_freehand_image_cropper/result";
    public static final String ACTION_ERROR = "flutter_freehand_image_cropper/error";

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /// Read configs extras.
        Bundle args = getIntent().getExtras();
        String filePath = getIntent().getStringExtra("filePath");
        String targetPath = getIntent().getStringExtra("targetPath");
        boolean addBorder = args.getBoolean("addBorder", false);
        int borderWidth = args.getInt("borderWidth", 100);
        int borderColor = args.getInt("borderColor", 0xffffffff);
        int paintStrokeColor = args.getInt("paintStrokeColor", 0xff00ff00);
        int paintStrokeWidth = args.getInt("paintStrokeWidth", 10);
        int outputQuality = args.getInt("outputQuality", 100);
        config = new FreeHandConfig(
                filePath,
                targetPath,
                addBorder,
                borderWidth,
                borderColor,
                paintStrokeColor,
                paintStrokeWidth,
                outputQuality
        );

        try {
            /// Load the image.
            loadImage(filePath);
            addFreeHandCropView();
        } catch (Exception e) {
            showToast("Ops! Unable to read picture. 😓");
            finish();
        }

        if(args.getBoolean("showMsgToast", true)) {
            showToast(args.getString("customToastMsg", "Use your finger to trace area to crop. 👆"));
        }
    }

    private void loadImage(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        bitmap = BitmapFactory.decodeFile(filePath, options);
    }

    private void addFreeHandCropView() {
        LinearLayout container = (LinearLayout) findViewById(R.id.container);
        freeHandCropView = new FreeHandCropView(this, bitmap, config);
        container.addView(freeHandCropView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    public void cropImage(List<Point> points) {
        Bitmap fullScreenBitmap =
                Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());

        Canvas canvas = new Canvas(fullScreenBitmap);

        Path path = new Path();
        for (int i = 0; i < points.size(); i++) {
            path.lineTo(points.get(i).x, points.get(i).y);
        }

        /// Cut out the selected portion of the image.
        Paint paint = new Paint();
        canvas.drawPath(path, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, paint);

        if(!config.addBorder) {

            /// Create a bitmap with just the cropped area.
            Region region = new Region();
            Region clip = new Region(0, 0, fullScreenBitmap.getWidth(), fullScreenBitmap.getHeight());
            region.setPath(path, clip);
            Rect bounds = region.getBounds();
            Bitmap croppedBitmap =
                    Bitmap.createBitmap(fullScreenBitmap, bounds.left, bounds.top,
                            bounds.width(), bounds.height());
            saveToPNG(croppedBitmap);
        } else {

            /// Frame the cut out portion.
            paint.setColor(config.borderColor);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(config.borderWidth);
            canvas.drawPath(path, paint);

            /// Create a bitmap with just the cropped area.
            Region region = new Region();
            Region clip = new Region(0, 0, fullScreenBitmap.getWidth(), fullScreenBitmap.getHeight());
            region.setPath(path, clip);
            Rect sourceBounds = region.getBounds();
            Rect destBounds =
                    new Rect(config.borderWidth, config.borderWidth, sourceBounds.width() + config.borderWidth,
                            sourceBounds.height() + config.borderWidth);
            Bitmap croppedBitmap =
                    Bitmap.createBitmap(sourceBounds.width() + config.borderWidth * 2,
                            sourceBounds.height() + config.borderWidth * 2, bitmap.getConfig());
            canvas.setBitmap(croppedBitmap);
            canvas.drawBitmap(fullScreenBitmap, sourceBounds, destBounds, null);
            saveToPNG(croppedBitmap);
        }
    }

    public void saveToPNG(Bitmap image)
    {
        try (FileOutputStream out = new FileOutputStream(config.targetPath)) {
            image.compress(Bitmap.CompressFormat.PNG, config.outputQuality, out);
            broadcastResult(ACTION_RESULT, "SAVED", true,
                    null);
        } catch (IOException e) {
            String msg = "Unable to save cropped image. 😓";
            broadcastResult(ACTION_ERROR, "ERROR", false,
                    msg);
            e.printStackTrace();
            showToast(msg);
        }
        finish();
    }

    private void showToast(String msg) {
        Toast toast = Toast.makeText(getApplicationContext(), msg ,Toast.LENGTH_SHORT);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }


    private void broadcastResult(String intentAction, String action, Boolean result, String error){
        Intent flutterIntent = new Intent(intentAction);
        flutterIntent.putExtra(EXTRA_ACTION, action);
        flutterIntent.putExtra(EXTRA_RESULT, result);
        flutterIntent.putExtra(EXTRA_ERROR, error);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(flutterIntent);
    }

}