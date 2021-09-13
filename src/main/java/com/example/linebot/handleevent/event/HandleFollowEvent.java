package com.example.linebot.handleevent.event;

import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.FollowEvent;

public class HandleFollowEvent extends AbstractHandleEvent{

    @Override
    public void handle(Event event) {
        System.out.println("フォローイベント");
        this.replyText(((FollowEvent) event).getReplyToken(), "友だち追加ありがとうございます。" + "\n" +
        "こちらのbotではトークルームで使用できる便利な機能を提供しています。" + "\n"
        + "トークルームで[ヘルプ]と入力して頂くと操作説明を表示します。");
    }
    
}
