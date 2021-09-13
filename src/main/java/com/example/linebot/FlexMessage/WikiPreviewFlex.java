package com.example.linebot.FlexMessage;

import java.net.URI;
import java.util.Arrays;
import java.util.Map;

import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.message.flex.component.Box;
import com.linecorp.bot.model.message.flex.component.Button;
import com.linecorp.bot.model.message.flex.component.Text;
import com.linecorp.bot.model.message.flex.component.Button.ButtonHeight;
import com.linecorp.bot.model.message.flex.component.Button.ButtonStyle;
import com.linecorp.bot.model.message.flex.component.Text.TextWeight;
import com.linecorp.bot.model.message.flex.container.Bubble;
import com.linecorp.bot.model.message.flex.container.Bubble.BubbleSize;
import com.linecorp.bot.model.message.flex.unit.FlexFontSize;
import com.linecorp.bot.model.message.flex.unit.FlexLayout;
import com.linecorp.bot.model.message.flex.unit.FlexMarginSize;

public class WikiPreviewFlex {
    private String title;
    private String main;
    private String url;

    public WikiPreviewFlex(String title, String main, String url) {
        this.title = title;
        this.main = main;
        this.url = url;
    }

    public WikiPreviewFlex(final Map<String,String> map){
        this.title = map.get("title");
        this.main = map.get("main");
        this.url = map.get("link");
    }

    public Bubble createBubble() {
        final Box bodyBlock = createBodyBlock();
        final Box footerBlock = createFooterBlock();
        return Bubble.builder().body(bodyBlock).footer(footerBlock).size(BubbleSize.KILO).build();
    }

    private Box createBodyBlock() {
        final Text headerText = Text.builder().text("[候補]").size(FlexFontSize.SM).color("#FFFFFF").build();
        final Text titleText = Text.builder().text(title).size(FlexFontSize.LG).weight(TextWeight.BOLD).margin(FlexMarginSize.MD).wrap(true).color("#FFFFFF").build();
        final Text mainText = Text.builder().text(main).size(FlexFontSize.SM).margin(FlexMarginSize.MD).wrap(true).color("#FFFFFF").build();

        return Box.builder().layout(FlexLayout.VERTICAL).contents(Arrays.asList(headerText, titleText, mainText)).backgroundColor("#212121")
                .build();
    }

    private Box createFooterBlock() {
        final Button linkOpenButton = Button.builder().style(ButtonStyle.PRIMARY).height(ButtonHeight.SMALL)
                .action(new URIAction("リンクを開く", URI.create(url), null)).build();
                return Box.builder().layout(FlexLayout.VERTICAL).spacing(FlexMarginSize.SM)
                .contents(linkOpenButton).backgroundColor("#212121").build();
    }

}
