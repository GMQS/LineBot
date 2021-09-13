package com.example.linebot.handleevent.message;

import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.ContentProvider;
import com.linecorp.bot.model.event.message.ImageMessageContent;
import com.linecorp.bot.model.event.message.MessageContent;

class HandleImageMessageEvent extends AbstractHandleMessageEvent {

    @Override
    public void handle(final MessageEvent<? extends MessageContent> event) {
        System.out.println("画像メッセージイベント");
        new HandleHeavyContent().handle(event.getReplyToken(), event.getMessage().getId(), responseBody -> {
            final ContentProvider provider = ((ImageMessageContent) event.getMessage()).getContentProvider();
            final DownloadedContent jpg;
            final DownloadedContent previewImg;
            // final DatabaseConnection dc = new DatabaseConnection();
            // final String lineId = getId(event);
            if (provider.isExternal()) {
                jpg = new DownloadedContent(null, provider.getOriginalContentUrl());
                previewImg = new DownloadedContent(null, provider.getPreviewImageUrl());
                // new AutoBackup(lineId, dc).uploadImageFile(jpg.path.toString(),
                // getFolderName(event),
                // formatTimeStamp(event.getTimestamp(), "yyyy-MM-dd HHmmssS"));
            } else {
                jpg = saveContent("jpg", responseBody);
                previewImg = createTempFile("jpg");
                // new AutoBackup(lineId, dc).uploadImageFile(jpg.path.toString(),
                // getFolderName(event),
                // formatTimeStamp(event.getTimestamp(), "yyyy-MM-dd HHmmssS"));
                system("convert", "-resize", "240x", jpg.path.toString(), previewImg.path.toString());
            }
            // reply(event.getReplyToken(),new ImageMessage(jpg.getUri(), previewImg.getUri()));
        });

    }
}
