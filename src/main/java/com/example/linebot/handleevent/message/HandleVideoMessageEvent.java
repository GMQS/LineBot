package com.example.linebot.handleevent.message;

import java.util.UUID;

import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.ContentProvider;
import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.event.message.VideoMessageContent;

class HandleVideoMessageEvent extends AbstractHandleMessageEvent{

    public void handle(MessageEvent<? extends MessageContent> event){
        System.out.println("動画メッセージイベント");
        new HandleHeavyContent().handle(event.getReplyToken(), event.getMessage().getId(), responseBody -> {
            final ContentProvider provider = ((VideoMessageContent) event.getMessage()).getContentProvider();
            final DownloadedContent mp4;
            final DownloadedContent previewImg;
            // final DatabaseConnection dc = new DatabaseConnection();
            // final String lineId = getId(event);
            if (provider.isExternal()) {
                mp4 = new DownloadedContent(null, provider.getOriginalContentUrl());
                previewImg = new DownloadedContent(null, provider.getPreviewImageUrl());
                // new AutoBackup(lineId, dc).uploadVideoFile(mp4.path.toString(),
                // getFolderName(event),
                // formatTimeStamp(event.getTimestamp(), "yyyy-MM-dd HHmmssS"));
            } else {
                mp4 = saveContent("mp4", responseBody);
                previewImg = createTempFile("jpg");
                // new AutoBackup(lineId, dc).uploadVideoFile(mp4.path.toString(),
                // getFolderName(event),
                // formatTimeStamp(event.getTimestamp(), "yyyy-MM-dd HHmmssS"));
                system("convert", mp4.path + "[0]", previewImg.path.toString());
            }
            String trackingId = UUID.randomUUID().toString();
            // log.info("Sending video message with trackingId={}", trackingId);
        });
    }
    
}
