package com.edu.cdp.flutter.channel;

import io.flutter.Log;
import io.flutter.embedding.android.FlutterFragment;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.view.FlutterView;

public class FlutterEventChannel implements EventChannel.StreamHandler {
    public FlutterEventChannel(BinaryMessenger binaryMessenger, String channelName) {
        EventChannel eventChannel = new EventChannel(binaryMessenger,channelName);
        eventChannel.setStreamHandler(this);
    }
    private EventChannel.EventSink eventSink;


    public void sendEvent(Object event){
        if(eventSink != null){
            eventSink.success(event);
        }else{
            Log.e("CHANNEL", "===== FlutterEventChannel.eventSink 为空 需要检查一下 =====");
        }
    }

    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {
        this.eventSink = events;
        System.out.println("native:"+arguments);
    }

    @Override
    public void onCancel(Object arguments) {

    }
}
