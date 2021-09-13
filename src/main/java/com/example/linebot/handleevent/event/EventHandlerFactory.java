package com.example.linebot.handleevent.event;

public class EventHandlerFactory {

    public static AbstractHandleEvent createFollowEventHandler(){
        return new HandleFollowEvent();
    }

    public static AbstractHandleEvent createPostbackHandler(){
        return new HandlePostbackEvent();
    }

    public static AbstractHandleEvent createJoinEventHandler(){
        return new HandleJoinEvent();
    }

    public static AbstractHandleEvent createMemberJoinedEventHandler(){
        return new HandleMemberJoinedEvent();
    }

    public static AbstractHandleEvent createLeftEventHandler(){
        return new HandleLeftEvent();
    }

    public static AbstractHandleEvent createUnsendEventHandler(){
        return new HandleUnsendEvent();
    }
    
}
