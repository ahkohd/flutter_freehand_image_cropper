import 'dart:async';
import 'package:flutter/services.dart';


enum CropResult {
  SUCCESS,
  ERROR,
}

typedef Future<void> MessageHandler(CropResult action, bool status,
    {String error});

class FlutterFreehandImageCropper {
  static const MethodChannel _channel =
      const MethodChannel('flutter_freehand_image_cropper');

  MessageHandler _cropListener;

 void cropImage(
      String filePath,
      String targetPath,
      {
        bool addBorder = false,
        int borderWidth = 100,
        String borderColor = "#ffffff",
        String paintStrokeColor = "#00ff00",
        int paintStrokeWidth = 10,
        int outputQuality = 100,
        bool showMsgToast = true,
        String customToastMsg,
        MessageHandler listener,
      }) async {
    _cropListener = listener;
    _channel.setMethodCallHandler(_handleMethod);
    _channel.invokeMethod("cropImage", <String, Object> {
        "filePath": filePath,
        "targetPath": targetPath,
        "addBorder": addBorder,
        "borderWidth": borderWidth,
        "borderColor": borderColor,
        "paintStrokeColor": paintStrokeColor,
        "paintStrokeWidth": paintStrokeWidth,
        "outputQuality": outputQuality,
        "customToastMsg": customToastMsg,
        "showMsgToast": showMsgToast
      });
    }

  Future<dynamic> _handleMethod(MethodCall call) async {
    print("masg");
    switch (call.method) {
      case "onSuccess":
        String action = call.arguments['action'];
        bool result = call.arguments['result'];
        switch (action) {
          case 'success':
            _cropListener(CropResult.SUCCESS, result);
            break;
          case 'error':
            _cropListener(CropResult.ERROR, result);
            break;
        }
        return null;
      case "onError":
        bool result = call.arguments['result'];
        String error = call.arguments['error'] ?? null;
        _cropListener(CropResult.ERROR, result, error: error);
        return null;
      default:
        throw UnsupportedError("Unrecognized activity handler");
    }
  }
}
