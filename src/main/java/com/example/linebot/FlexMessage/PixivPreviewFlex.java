package com.example.linebot.FlexMessage;

import java.net.URI;
import java.util.Arrays;
import java.util.Map;

import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.message.flex.component.Box;
import com.linecorp.bot.model.message.flex.component.Button;
import com.linecorp.bot.model.message.flex.component.Image;
import com.linecorp.bot.model.message.flex.component.Spacer;
import com.linecorp.bot.model.message.flex.component.Text;
import com.linecorp.bot.model.message.flex.component.Button.ButtonHeight;
import com.linecorp.bot.model.message.flex.component.Button.ButtonStyle;
import com.linecorp.bot.model.message.flex.component.Image.ImageAspectMode;
import com.linecorp.bot.model.message.flex.component.Image.ImageAspectRatio;
import com.linecorp.bot.model.message.flex.component.Image.ImageSize;
import com.linecorp.bot.model.message.flex.component.Text.TextWeight;
import com.linecorp.bot.model.message.flex.container.Bubble;
import com.linecorp.bot.model.message.flex.container.Bubble.BubbleSize;
import com.linecorp.bot.model.message.flex.unit.FlexFontSize;
import com.linecorp.bot.model.message.flex.unit.FlexLayout;
import com.linecorp.bot.model.message.flex.unit.FlexMarginSize;

public class PixivPreviewFlex {

    private String url;
    private String image;
    private String title;
    private String summary;

    public PixivPreviewFlex(final Map<String,String> map){
        this.url = map.get("link");
        this.image = map.get("image");
        this.title = map.get("title");
        this.summary = map.get("summary");
    }


    public Bubble createBubble(){
        final Image heroBlock = Image.builder().url(URI.create(image)).size(ImageSize.FULL_WIDTH)
        .aspectRatio(ImageAspectRatio.R20TO13).aspectMode(ImageAspectMode.Fit).backgroundColor("#000000")
        .build();
        final Box bodyBlock = createBodyBlock();
        final Box footerBlock = createFooterBlock();
        return Bubble.builder().hero(heroBlock).body(bodyBlock).footer(footerBlock).size(BubbleSize.KILO).build();
    }

    private Box createBodyBlock() {

        final Text titleField = Text.builder().text(title).size(FlexFontSize.LG).weight(TextWeight.BOLD).wrap(true).color("#FFFFFF").build();
        final Text summaryField = Text.builder().text(summary).size(FlexFontSize.Md).wrap(true).color("#FFFFFF").build();

        return Box.builder().layout(FlexLayout.VERTICAL).contents(Arrays.asList(titleField,summaryField)).backgroundColor("#212121").build();
    }

    private Box createFooterBlock(){
        final Spacer spacer = Spacer.builder().size(FlexMarginSize.SM).build();
        final Button linkOpenButton = Button.builder().style(ButtonStyle.LINK).height(ButtonHeight.SMALL)
                .action(new URIAction("リンクを開く", URI.create(url), null)).build();
        return Box.builder().layout(FlexLayout.VERTICAL).spacing(FlexMarginSize.SM)
                .contents(Arrays.asList(spacer, linkOpenButton)).backgroundColor("#212121").build();
    }
    
}
