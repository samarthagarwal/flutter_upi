#import "FlutterUpiPlugin.h"

@implementation FlutterUpiPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"flutter_upi"
            binaryMessenger:[registrar messenger]];
  FlutterUpiPlugin* instance = [[FlutterUpiPlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
  if ([@"initiateTransaction" isEqualToString:call.method]) {
    result(FlutterMethodNotImplemented);
  } else {
    result(FlutterMethodNotImplemented);
  }
}

@end
