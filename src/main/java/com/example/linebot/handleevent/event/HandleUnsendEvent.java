package com.example.linebot.handleevent.event;

import com.linecorp.bot.model.event.Event;

public class HandleUnsendEvent extends AbstractHandleEvent{

    @Override
    public void handle(Event event) {
        System.out.println("送信取消イベント");
    }
    
}
