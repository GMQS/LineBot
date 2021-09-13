package com.example.linebot.handleevent.event;

import com.example.linebot.handleevent.CommonApiFunctions;
import com.linecorp.bot.model.event.Event;

public abstract class AbstractHandleEvent extends CommonApiFunctions{
    public abstract void handle(final Event event);
}
