package com.samarth.flutter_upi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import java.util.Random;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** FlutterUpiPlugin */
public class FlutterUpiPlugin implements MethodCallHandler, PluginRegistry.ActivityResultListener {

  private Activity activity;
  private Context context;
  MethodChannel.Result _result;
  int requestCodeNumber = 1907;

  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutter_upi");

    FlutterUpiPlugin _plugin = new FlutterUpiPlugin(registrar, channel);

    registrar.addActivityResultListener(_plugin);
    channel.setMethodCallHandler(_plugin);
  }

  FlutterUpiPlugin(Registrar registrar, MethodChannel channel) {
    activity = registrar.activity();
    context = registrar.activeContext();
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {

    _result = result;

    if (call.method.equals("initiateTransaction")) {

      String app;

      if(call.argument("app") == null) app = "in.org.npci.upiapp"; else app = call.argument("app");
      String pa = call.argument("pa");
      String pn = call.argument("pn");
      String mc = call.argument("mc");
      String tr = call.argument("tr");
      String tn = call.argument("tn");
      String am = call.argument("am");
      String cu = call.argument("cu");
      String url = call.argument("url");

      try {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("upi").authority("pay");
        uriBuilder.appendQueryParameter("pa", pa);
        uriBuilder.appendQueryParameter("pn", pn);
        if(mc != null) uriBuilder.appendQueryParameter("mc", mc);
        uriBuilder.appendQueryParameter("tr", tr);
        uriBuilder.appendQueryParameter("tn", tn);
        uriBuilder.appendQueryParameter("am", am);
        uriBuilder.appendQueryParameter("cu", cu);
        uriBuilder.appendQueryParameter("url", url);

        Uri uri = uriBuilder.build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        intent.setPackage(app);

        if(appInstalledOrNot(app)) {
          activity.startActivityForResult(intent, requestCodeNumber);
          _result = result;
        } else {
          result.success("app_not_installed");
          Log.d("flutter_upi", app + " not installed on the device.");
        }
      } catch(Exception ex) {
        result.success("invalid_params") ;
      }
    } else {
      result.notImplemented();
    }
  }

  @Override
  public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCodeNumber == requestCode && _result != null) {
      if(data != null) {
        try {
          String response = data.getStringExtra("response");
          _result.success(response);
        } catch(Exception ex) {
          _result.success("null_response");
        }
      } else {
        Log.d("Result","Data = null (User canceled)");
        _result.success("user_canceled");
      }
    }

    return true;
  }

  private boolean appInstalledOrNot(String uri) {
    PackageManager pm = activity.getPackageManager();
    try {
      pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
      return true;
    } catch (PackageManager.NameNotFoundException e) {
    }

    return false;
  }
}
