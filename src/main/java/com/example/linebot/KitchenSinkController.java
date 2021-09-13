/*
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.example.linebot;

import com.example.linebot.handleevent.event.AbstractHandleEvent;
import com.example.linebot.handleevent.event.EventHandlerFactory;
import com.example.linebot.handleevent.message.AbstractHandleMessageEvent;
import com.example.linebot.handleevent.message.MessageEventHandlerFactory;
import com.linecorp.bot.model.event.BeaconEvent;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.JoinEvent;
import com.linecorp.bot.model.event.MemberJoinedEvent;
import com.linecorp.bot.model.event.MemberLeftEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.UnfollowEvent;
import com.linecorp.bot.model.event.UnknownEvent;
import com.linecorp.bot.model.event.UnsendEvent;
import com.linecorp.bot.model.event.VideoPlayCompleteEvent;
import com.linecorp.bot.model.event.message.AudioMessageContent;
import com.linecorp.bot.model.event.message.FileMessageContent;
import com.linecorp.bot.model.event.message.ImageMessageContent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.StickerMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.message.VideoMessageContent;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@LineMessageHandler
public class KitchenSinkController {
    
    private AbstractHandleEvent postbackHandler,followHandler,joinHandler,leftHandler,memberJoinedHandler,unsendHandler;
    private AbstractHandleMessageEvent textHandler, stickerHandler, imageHandler, audioHandler, videoHandler,
            fileHandler;

    public KitchenSinkController() {
        this.textHandler = MessageEventHandlerFactory.createTextMessageHandler();
        this.stickerHandler = MessageEventHandlerFactory.createStickerMessageHandler();
        this.imageHandler = MessageEventHandlerFactory.createImageMessageHandler();
        this.audioHandler = MessageEventHandlerFactory.createAudioMessageHandler();
        this.videoHandler = MessageEventHandlerFactory.createVideoMessageHandler();
        this.fileHandler = MessageEventHandlerFactory.createFileMessageHandler();
        this.followHandler = EventHandlerFactory.createFollowEventHandler();
        this.postbackHandler = EventHandlerFactory.createPostbackHandler();
        this.joinHandler = EventHandlerFactory.createJoinEventHandler();
        this.leftHandler = EventHandlerFactory.createLeftEventHandler();
        this.memberJoinedHandler = EventHandlerFactory.createMemberJoinedEventHandler();
        this.unsendHandler = EventHandlerFactory.createUnsendEventHandler();
    }

    @EventMapping
    public void handleTextMessageEvent(MessageEvent<TextMessageContent> event) throws InterruptedException {
        textHandler.handle(event);
    }

    @EventMapping
    public void handleStickerMessageEvent(MessageEvent<StickerMessageContent> event) {
        stickerHandler.handle(event);
    }

    @EventMapping
    public void handleLocationMessageEvent(MessageEvent<LocationMessageContent> event) {
        // LocationMessageContent locationMessage = event.getMessage();
        // reply(event.getReplyToken(), new LocationMessage(locationMessage.getTitle(),
        // locationMessage.getAddress(),
        // locationMessage.getLatitude(), locationMessage.getLongitude()));
    }

    @EventMapping
    public void handleImageMessageEvent(MessageEvent<ImageMessageContent> event) {
        imageHandler.handle(event);
    }

    @EventMapping
    public void handleAudioMessageEvent(MessageEvent<AudioMessageContent> event) {
        audioHandler.handle(event);
    }

    @EventMapping
    public void handleVideoMessageEvent(MessageEvent<VideoMessageContent> event) {
        videoHandler.handle(event);
    }

    @EventMapping
    public void handleVideoPlayCompleteEvent(VideoPlayCompleteEvent event) {
        log.info("Got video play complete: tracking id={}", event.getVideoPlayComplete().getTrackingId());
    }

    @EventMapping
    public void handleFileMessageEvent(MessageEvent<FileMessageContent> event) {
        fileHandler.handle(event);
    }

    @EventMapping
    public void handleUnfollowEvent(UnfollowEvent event) {
        log.info("unfollowed this bot: {}", event);
    }

    @EventMapping
    public void handleUnknownEvent(UnknownEvent event) {
        log.info("Got an unknown event!!!!! : {}", event);
    }

    @EventMapping
    public void handleFollowEvent(FollowEvent event) {
        followHandler.handle(event);
    }

    @EventMapping
    public void handleJoinEvent(JoinEvent event) {
        joinHandler.handle(event);
    }

    @EventMapping
    public void handlePostbackEvent(PostbackEvent event) {
        postbackHandler.handle(event);
    }

    @EventMapping
    public void handleBeaconEvent(BeaconEvent event) {
        // String replyToken = event.getReplyToken();
        // this.replyText(replyToken, "Got beacon message " +
        // event.getBeacon().getHwid());
    }

    @EventMapping
    public void handleMemberJoined(MemberJoinedEvent event) {
        memberJoinedHandler.handle(event);
    }

    @EventMapping
    public void handleMemberLeft(MemberLeftEvent event) {
        leftHandler.handle(event);
    }

    @EventMapping
    public void handleUnsendEvent(UnsendEvent event) {
        unsendHandler.handle(event);
    }

    @EventMapping
    public void handleOtherEvent(Event event) {
        log.info("Received message(Ignored): {}", event);
    }
}
