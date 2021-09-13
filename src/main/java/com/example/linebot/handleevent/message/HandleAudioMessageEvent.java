package com.example.linebot.handleevent.message;

import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.AudioMessageContent;
import com.linecorp.bot.model.event.message.ContentProvider;
import com.linecorp.bot.model.event.message.MessageContent;

class HandleAudioMessageEvent extends AbstractHandleMessageEvent {

    @Override
    public void handle(final MessageEvent<? extends MessageContent> event) {
        System.out.println("オーディオメッセージイベント");
                new HandleHeavyContent().handle(event.getReplyToken(), event.getMessage().getId(), responseBody -> {
            final ContentProvider provider = ((AudioMessageContent) event.getMessage()).getContentProvider();
            final DownloadedContent mp3;
            // final DatabaseConnection dc = new DatabaseConnection();
            // final String lineId = getId(event);
            if (provider.isExternal()) {
                mp3 = new DownloadedContent(null, provider.getOriginalContentUrl());
                // new AutoBackup(lineId, dc).uploadAudioFile(mp3.path.toString(),
                // getFolderName(event),
                // formatTimeStamp(event.getTimestamp(), "yyyy-MM-dd HHmmssS"));
            } else {
                mp3 = saveContent("mp3", responseBody);
                // new AutoBackup(lineId, dc).uploadAudioFile(mp3.path.toString(),
                // getFolderName(event),
                // formatTimeStamp(event.getTimestamp(), "yyyy-MM-dd HHmmssS"));
            }
            // reply(event.getReplyToken(), new AudioMessage(mp3.getUri(), 100));
        });
    }

}
