package com.example.linebot.handleevent.message;

import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.MessageContent;

class HandleStickerMessageEvent extends AbstractHandleMessageEvent{

    @Override
    public void handle(MessageEvent<? extends MessageContent> messageEvent) {
        System.out.println("ステッカーメッセージイベント");
    }
    
}
