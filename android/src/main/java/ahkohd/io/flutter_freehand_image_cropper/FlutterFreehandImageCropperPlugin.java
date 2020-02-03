package ahkohd.io.flutter_freehand_image_cropper;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

/** FlutterFreehandImageCropperPlugin */
public class FlutterFreehandImageCropperPlugin implements MethodCallHandler, PluginRegistry.ActivityResultListener, FlutterPlugin {

  Activity activity;
  Result result;
  MethodChannel channel;
  Context context;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPlugin.FlutterPluginBinding flutterPluginBinding) {
    final MethodChannel channel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "flutter_freehand_image_cropper");
    FlutterFreehandImageCropperPlugin plugin =  new FlutterFreehandImageCropperPlugin();
    channel.setMethodCallHandler(plugin);
    plugin.setContext(flutterPluginBinding.getApplicationContext());
  }

  public static void registerWith(Registrar registrar) {
    Log.e("DEBUG_NAME", " registerWith");
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutter_freehand_image_cropper");
    channel.setMethodCallHandler(new FlutterFreehandImageCropperPlugin());
  }

  void setContext(Context value) {
    this.context = value;
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("cropImage")) {
      cropImage(call);
    } else {
      result.notImplemented();
    }
  }

  void cropImage(MethodCall call) {


    String filePath = call.argument("filePath");
    String targetPath = call.argument("targetPath");
    boolean addBorder = call.argument("addBorder");
    int borderWidth = call.argument("borderWidth");
    int borderColor = hexStringToHexCode(call.argument("borderColor").toString());
    int paintStrokeColor = hexStringToHexCode(call.argument("paintStrokeColor").toString());
    int paintStrokeWidth = call.argument("paintStrokeWidth");
    int outputQuality = call.argument("outputQuality");
    String customToastMsg = call.argument("customToastMsg");
    boolean showMsgToast = call.argument("showMsgToast");

    Intent cropIntent = new Intent(this.context, MainActivity.class);
    cropIntent.putExtra("filePath", filePath);
    cropIntent.putExtra("targetPath", targetPath);
    cropIntent.putExtra("addBorder", addBorder);
    cropIntent.putExtra("borderWidth", borderWidth);
    cropIntent.putExtra("borderColor", borderColor);
    cropIntent.putExtra("paintStrokeColor", paintStrokeColor);
    cropIntent.putExtra("paintStrokeWidth", paintStrokeWidth);
    cropIntent.putExtra("outputQuality", outputQuality);
    cropIntent.putExtra("customToastMsg", customToastMsg);
    cropIntent.putExtra("showMsgToast", showMsgToast);
    this.context.startActivity(cropIntent);

  }

  private int hexStringToHexCode(String hexString) {
    int r =  Integer.valueOf( hexString.substring( 1, 3 ), 16 );
    int g =  Integer.valueOf( hexString.substring( 3, 5 ), 16 );
    int b =  Integer.valueOf( hexString.substring( 5, 7 ), 16 );
    return getIntFromColor(r, g, b);
  }



  private int getIntFromColor(int Red, int Green, int Blue){
    Red = (Red << 16) & 0x00FF0000;
    Green = (Green << 8) & 0x0000FF00;
    Blue = Blue & 0x000000FF;
    return 0xFF000000 | Red | Green | Blue;
  }


  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
//    Log.e("DETT", "DETTTTTTTT");
  }

  @Override
  public boolean onActivityResult(int requestCode, int resultCode, Intent i) {
    Log.e("2face", "hello world!");
//    if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
//    }
    return false;
  }

}

