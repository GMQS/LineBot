package com.example.linebot.FlexMessage;

import java.net.URI;

import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.message.flex.component.Image;
import com.linecorp.bot.model.message.flex.component.Image.ImageAspectMode;
import com.linecorp.bot.model.message.flex.component.Image.ImageAspectRatio;
import com.linecorp.bot.model.message.flex.component.Image.ImageSize;
import com.linecorp.bot.model.message.flex.container.Bubble;
import com.linecorp.bot.model.message.flex.container.Bubble.BubbleSize;

public class PreviewImageFlex{
    public Bubble createBubble(final URI imageUri,final URI uriAction) {
        final Image heroBlock = Image.builder()
                .url(imageUri)
                .size(ImageSize.FULL_WIDTH).aspectRatio(ImageAspectRatio.R1TO1).aspectMode(ImageAspectMode.Fit)
                .backgroundColor("#000000")
                .action(new URIAction("label",uriAction, null)).build();
        return Bubble.builder().hero(heroBlock).size(BubbleSize.KILO).build();
    }

}
