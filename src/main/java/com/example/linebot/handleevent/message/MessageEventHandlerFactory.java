package com.example.linebot.handleevent.message;

public class MessageEventHandlerFactory {

    public static AbstractHandleMessageEvent createAudioMessageHandler(){
        return new HandleAudioMessageEvent();
    }

    public static AbstractHandleMessageEvent createVideoMessageHandler(){
        return new HandleVideoMessageEvent();
    }

    public static AbstractHandleMessageEvent createTextMessageHandler(){
        return new HandleTextEvent();
    }

    public static AbstractHandleMessageEvent createFileMessageHandler(){
        return new HandleFileMessageEvent();
    }

    public static AbstractHandleMessageEvent createImageMessageHandler(){
        return new HandleImageMessageEvent();
    }

    public static AbstractHandleMessageEvent createStickerMessageHandler(){
        return new HandleStickerMessageEvent();
    }
    
}
