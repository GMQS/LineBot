package com.example.linebot.handleevent.message;

import com.example.linebot.handleevent.CommonApiFunctions;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.MessageContent;
public abstract class AbstractHandleMessageEvent extends CommonApiFunctions{
    
    public abstract void handle(final MessageEvent<? extends MessageContent> messageEvent);
}
