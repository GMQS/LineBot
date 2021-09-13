package com.example.linebot.handleevent.event;

import java.util.stream.Collectors;

import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MemberJoinedEvent;
import com.linecorp.bot.model.event.source.Source;

public class HandleMemberJoinedEvent extends AbstractHandleEvent{

    @Override
    public void handle(Event event) {
                String replyToken = ((MemberJoinedEvent) event).getReplyToken();
        this.replyText(replyToken, "Got memberJoined message "
        +
        ((MemberJoinedEvent) event).getJoined().getMembers().stream().map(Source::getUserId).collect(Collectors.joining(",")));
    }
    
}
