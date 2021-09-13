package com.example.linebot.FlexMessage;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import com.linecorp.bot.model.action.Action;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.message.flex.component.Box;
import com.linecorp.bot.model.message.flex.component.Button;
import com.linecorp.bot.model.message.flex.component.FlexComponent;
import com.linecorp.bot.model.message.flex.component.Image;
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

public class TemplateMenuFlex {

    private URI image;
    private String title;
    private String summary;
    private ArrayList<Action> actionList;

    /**
     * 静的バブルを生成する時に使うコンストラクタ ボタン複数用
     * 
     * @param image
     * @param title
     * @param summary
     * @param actionList ボタンに割り当てるAction型のアレイリスト. Postback or URI.
     */
    public TemplateMenuFlex(URI image, String title, String summary, ArrayList<Action> actionList) {
        this.image = image;
        this.title = title;
        this.summary = summary;
        this.actionList = actionList;
    }

    /**
     * 静的バブルを生成する時に使うコンストラクタ ボタン１つ用
     * 
     * @param image
     * @param title
     * @param summary
     * @param action  ボタンに割り当てるAction型の変数.
     */
    public TemplateMenuFlex(URI image, String title, String summary, Action action) {
        this.image = image;
        this.title = title;
        this.summary = summary;
        this.actionList = new ArrayList<>();
        this.actionList.add(action);
    }

    /**
     * 動的バブルを生成する時に使うコンストラクタ ボタン１つ用
     * 
     * @param dataSet    HTMLパーサから受け取ったkey,valueペアの値を格納しているMap.
     * @param buttonText ボタンに表示するテキスト.
     */
    public TemplateMenuFlex(Map<String, String> dataSet, final String buttonText) {
        this.image = URI.create(dataSet.get("image"));
        this.title = dataSet.get("title");
        this.summary = dataSet.get("summary");
        this.actionList = new ArrayList<>();
        this.actionList.add(new URIAction(buttonText, URI.create(dataSet.get("link")), null));
    }

    public Bubble createBubble(BubbleSize size) {
        final Image heroBlock = Image.builder().url(image).size(ImageSize.FULL_WIDTH)
                .aspectRatio(ImageAspectRatio.R20TO13).aspectMode(ImageAspectMode.Fit).backgroundColor("#000000")
                .build();
        final Box bodyBlock = createBodyBlock();
        final Box footerBlock = createFooterBlock();
        return Bubble.builder().hero(heroBlock).body(bodyBlock).footer(footerBlock).size(size).build();
    }

    private Box createBodyBlock() {

        final Text titleField = Text.builder().text(title).size(FlexFontSize.LG).weight(TextWeight.BOLD).wrap(true)
                .color("#FFFFFF").build();
        final Text summaryField = Text.builder().text(summary).size(FlexFontSize.Md).wrap(true).color("#FFFFFF")
                .margin(FlexMarginSize.MD).build();
        return Box.builder().layout(FlexLayout.VERTICAL).contents(Arrays.asList(titleField, summaryField))
                .backgroundColor("#212121").build();
    }

    private Box createFooterBlock() {
        final ArrayList<FlexComponent> list = new ArrayList<>();
        for (Action action : actionList) {

            if (action instanceof PostbackAction) {
                if (((PostbackAction) action).getLabel().equals("キャンセル")) {
                list.add(Button.builder().style(ButtonStyle.PRIMARY).height(ButtonHeight.SMALL).action(action)
                .margin(FlexMarginSize.MD).color("#FF0000").build());
                continue;
                }
                // if (action.equals(actionList.get(actionList.size() - 1))) {
                //     list.add(Button.builder().style(ButtonStyle.PRIMARY).height(ButtonHeight.SMALL).action(action)
                //             .margin(FlexMarginSize.MD).color("#FF0000").build());
                //     continue;
                // }
                list.add(Button.builder().style(ButtonStyle.PRIMARY).height(ButtonHeight.SMALL).action(action).build());
                continue;
            }
            if (action instanceof URIAction) {
                list.add(Button.builder().style(ButtonStyle.PRIMARY).height(ButtonHeight.SMALL).action(action).build());
                continue;
            }
        }
        return Box.builder().layout(FlexLayout.HORIZONTAL).spacing(FlexMarginSize.SM).contents(list)
                .backgroundColor("#212121").build();
    }

}
