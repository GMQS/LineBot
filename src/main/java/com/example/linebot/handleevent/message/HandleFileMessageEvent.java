package com.example.linebot.handleevent.message;

import java.util.concurrent.ExecutionException;

import com.linecorp.bot.client.MessageContentResponse;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.FileMessageContent;
import com.linecorp.bot.model.event.message.MessageContent;

class HandleFileMessageEvent extends AbstractHandleMessageEvent{

    @Override
    public void handle(final MessageEvent<? extends MessageContent> event){
        System.out.println("ファイルメッセージイベント");
        MessageContentResponse response = null;
        DownloadedContent content = null;
        // final DatabaseConnection dc = new DatabaseConnection();
        // final String lineId = getId(event);
        try {
            response = getLineBlobClient().getMessageContent(event.getMessage().getId()).get();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        content = saveContent(((FileMessageContent) event.getMessage()).getFileName(), response);
        // new AutoBackup(lineId, dc).uploadFile(content.path.toString(),
        // event.getMessage().getFileName(),
        // getFolderName(event), formatTimeStamp(event.getTimestamp(), "yyyy-MM-dd
        // HHmmssS"));
    }
    
}
