import 'dart:io';

import 'package:flutter/material.dart';

import 'package:flutter/services.dart';
import 'package:flutter_freehand_image_cropper/flutter_freehand_image_cropper.dart';
import 'package:image_picker/image_picker.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {

  @override
  void initState() {
    super.initState();
    initPlatformState();
    print("sas");
  }

  String imgPath;

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Column(
          children: <Widget>[
            imgPath != null ? Image.file( File(
                imgPath
            )) : Container(),
        Center(
        child: MaterialButton(
          color: Colors.blue,
          elevation: 2,
          child: Text("Crop Picture!", style: TextStyle(color: Colors.white)),
          onPressed: () async {
            var image = await ImagePicker.pickImage(source: ImageSource.gallery);
            FlutterFreehandImageCropper()
                .cropImage(
                image.absolute.path,
                "${image.absolute.path}.test.png",
                addBorder: true,
                borderWidth: 20,
              listener: _listener,
            );
            setState(() {
              imgPath = "${image.absolute.path}.test.png";
            });
          },
        )
        )]),
        ),
      );
  }

  Future<String> loadAsset(String path) async {
    return await rootBundle.loadString(path);
  }

  Future<void> _listener(CropResult action, bool result, {String error}) async {
    // Do what you must here
  }
}
