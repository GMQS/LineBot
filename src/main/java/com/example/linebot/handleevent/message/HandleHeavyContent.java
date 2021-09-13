package com.example.linebot.handleevent.message;

import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import com.example.linebot.handleevent.CommonApiFunctions;
import com.linecorp.bot.client.MessageContentResponse;
import com.linecorp.bot.model.message.TextMessage;

class HandleHeavyContent extends CommonApiFunctions{
    
    public void handle(String replyToken, String messageId,
            Consumer<MessageContentResponse> messageConsumer) {
        final MessageContentResponse response;
        try {
            response = getLineBlobClient().getMessageContent(messageId).get();
        } catch (InterruptedException | ExecutionException e) {
            reply(replyToken, new TextMessage("Cannot get image: " + e.getMessage()));
            throw new RuntimeException(e);
        }
        messageConsumer.accept(response);
    }
}
