package com.example.linebot.handleevent.event;

import java.util.List;
import java.util.stream.Collectors;

import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MemberLeftEvent;
import com.linecorp.bot.model.event.source.Source;

public class HandleLeftEvent extends AbstractHandleEvent {

    @Override
    public void handle(Event event) {
        System.out.println("メンバー退出イベント:" + ((MemberLeftEvent) event).getLeft().getMembers().stream()
                .map(Source::getUserId).collect(Collectors.joining(",")));
        final List<Source> leftMemberlist = ((MemberLeftEvent) event).getLeft().getMembers();
        for (Source source : leftMemberlist) {
            if (source.getUserId().equals("Uf669f8fa7e61ce402e13aae0df9f5a59")) {
            }
        }
    }

}
