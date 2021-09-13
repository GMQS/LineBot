package com.example.linebot.FlexMessage;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

import com.example.linebot.jsoup.AmazonProductValue;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.message.flex.component.Box;
import com.linecorp.bot.model.message.flex.component.Button;
import com.linecorp.bot.model.message.flex.component.FlexComponent;
import com.linecorp.bot.model.message.flex.component.Icon;
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

public class AmazonPreviewFlex {
    private String url;
    private String image;
    private String title;
    private String star;
    private String starCount;
    private String price;
    private String video;
    private URI starIcon;

    public AmazonPreviewFlex(final AmazonProductValue productValue, final URI starIcon) {
        this.url = productValue.getUrl();
        this.image = productValue.getImage();
        this.title = productValue.getTitle();
        this.price = productValue.getPrice();
        this.star = productValue.getStar();
        this.starCount = productValue.getStarCount();
        this.video = productValue.getVideo();
        this.starIcon = starIcon;
    }

    public Bubble createBubble() {
        if (image.equals("")) {
            image = "https://media.discordapp.net/attachments/612397743687860237/839893182698881035/image_broken.png?width=676&height=676";
        }
        // デバッグ
        //image = "https://media.discordapp.net/attachments/612397743687860237/839893182698881035/image_broken.png?width=676&height=676";

        final Image heroBlock = Image.builder().url(URI.create(image)).size(ImageSize.FULL_WIDTH)
                .aspectRatio(ImageAspectRatio.R20TO13).aspectMode(ImageAspectMode.Fit).backgroundColor("#000000")
                .build();
        final Box bodyBlock = createBodyBlock();
        final Box footerBlock = createFooterBlock();
        return Bubble.builder().hero(heroBlock).body(bodyBlock).footer(footerBlock).size(BubbleSize.KILO).build();
    }

    private Box createBodyBlock() {
        ArrayList<FlexComponent> list = new ArrayList<>();
        if (title.equals("")) {
            title = "-";
        }

        list.add(Text.builder().text(title).size(FlexFontSize.Md).wrap(true).maxLines(3).color("#FFFFFF").build());

        if (!price.equals("")) {
            list.add(Text.builder().text(price).weight(TextWeight.BOLD).size(FlexFontSize.XL).margin(FlexMarginSize.MD).color("#FF6347").build());
        }

        list.add(createReviewBox());

        if (!video.equals("")) {
            list.add(Text.builder().text(video).weight(TextWeight.BOLD).size(FlexFontSize.LG).margin(FlexMarginSize.MD).color("#00B8D4").build());
        }

        return Box.builder().layout(FlexLayout.VERTICAL).contents(list).backgroundColor("#212121").build();
    }

    private Box createFooterBlock() {
        final Spacer spacer = Spacer.builder().size(FlexMarginSize.SM).build();
        final Button linkOpenButton = Button.builder().style(ButtonStyle.PRIMARY).height(ButtonHeight.SMALL)
                .action(new URIAction("商品ページ", URI.create(url), null)).build();
        return Box.builder().layout(FlexLayout.VERTICAL).spacing(FlexMarginSize.SM)
                .contents(Arrays.asList(spacer, linkOpenButton)).backgroundColor("#212121").build();
    }

    private Box createReviewBox() {
        ArrayList<FlexComponent> list = new ArrayList<>();
        list.add(Icon.builder().size(FlexFontSize.Md).url(starIcon).build());
        if (star.equals("")) {
            star = "評価無し";
        }
        list.add(Text.builder().text(star).size(FlexFontSize.Md).color("#FFFFFF").margin(FlexMarginSize.SM).flex(0)
                .build());

        if (!starCount.equals("")) {
            list.add(Text.builder().text("(" + starCount + ")").size(FlexFontSize.SM).color("#E297E1")
                    .margin(FlexMarginSize.SM).flex(0).build());
        }

        return Box.builder().layout(FlexLayout.BASELINE).margin(FlexMarginSize.MD).contents(list).backgroundColor("#212121").build();
    }

}
