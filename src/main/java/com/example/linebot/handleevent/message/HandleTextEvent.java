package com.example.linebot.handleevent.message;

import java.util.ArrayList;
import java.util.Arrays;

import com.example.linebot.FlexMessage.TemplateMenuFlex;
import com.example.linebot.common.TextLoader;
import com.example.linebot.database.DatabaseConnection;
import com.example.linebot.database.Frag;
import com.example.linebot.database.IFragCollection;
import com.linecorp.bot.model.action.Action;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.FlexMessage;
import com.linecorp.bot.model.message.flex.container.Bubble;
import com.linecorp.bot.model.message.flex.container.Bubble.BubbleSize;

class HandleTextEvent extends AbstractHandleMessageEvent {

    @Override
    public void handle(MessageEvent<? extends MessageContent> event) {
        TextMessageContent content = null;
        DatabaseConnection dc = null;
        String replyToken = null;
        String text = null;
        String userId = null;
        String lineId = null;
        try {
            System.out.println("テキストメッセージイベント" + "\n" + "送信者:" + getDisplayName(event) + "\n" + "テキスト:"
                    + ((TextMessageContent) event.getMessage()).getText());
            content = (TextMessageContent) event.getMessage();
            dc = new DatabaseConnection();
            replyToken = event.getReplyToken();
            text = content.getText();
            userId = event.getSource().getUserId();
            lineId = getId(event);
            new HandleModeText().handle(replyToken, event, content, dc);
            switch (text) {
                case "検索メニュー": {
                    searchMenuCarousel(replyToken);
                    break;
                }
                case "ヘルプ": {
                    this.replyText(replyToken, new TextLoader(
                            this.getClass().getClassLoader().getResourceAsStream("static/text/help_message.txt"))
                                    .load());
                    break;
                }

                case "検索": {
                    dc.changeMode(new Frag(IFragCollection.SEARCH_MODE), userId, lineId);
                    this.replyText(replyToken, "[" + getDisplayName(event) + "]検索キーワードを入力してください");
                    break;
                }

                case "画像検索": {
                    dc.changeMode(new Frag(IFragCollection.IMAGE_SEARCH_MODE), userId, lineId);
                    this.replyText(replyToken, "[" + getDisplayName(event) + "]画像検索キーワードを入力してください");
                    break;
                }

                case "動画検索": {
                    dc.changeMode(new Frag(IFragCollection.MOVIE_SEARCH_MODE), userId, lineId);
                    this.replyText(replyToken, "[" + getDisplayName(event) + "]youtube検索キーワードを入力してください");
                    break;
                }

                case "amazon検索": {
                    dc.changeMode(new Frag(IFragCollection.PRODUCT_SEARCH_MODE), userId, lineId);
                    this.replyText(replyToken, "[" + getDisplayName(event) + "]amazon検索キーワードを入力してください");
                    break;
                }

                case "wiki検索": {
                    dc.changeMode(new Frag(IFragCollection.WIKI_SEARCH_MODE), userId, lineId);
                    this.replyText(replyToken, "[" + getDisplayName(event) + "]wikipedia検索キーワードを入力してください");
                    break;
                }

                case "pixiv検索": {
                    dc.changeMode(new Frag(IFragCollection.PIXIV_SEARCH_MODE), userId, lineId);
                    this.replyText(replyToken, "[" + getDisplayName(event) + "]pixiv百科事典検索キーワードを入力してください");
                    break;
                }

                case "検索中止": {
                    if (dc.checkMode(userId, lineId) == IFragCollection.DISABLE) {
                        return;
                    }
                    dc.changeMode(new Frag(IFragCollection.DISABLE), userId, lineId);
                    this.replyText(replyToken, "[" + getDisplayName(event) + "]検索を中止しました");
                    break;
                }

                // case "バックアップ": {
                // final URI thumb = createUri("/static/images/drive_icon.png");
                // if (userId.equals("Uf669f8fa7e61ce402e13aae0df9f5a59")) {
                // ButtonsTemplate bt =
                // ButtonsTemplate.builder().thumbnailImageUrl(thumb).imageSize("contain")
                // .imageBackgroundColor("#202020").title("GoogleDrive設定")
                // .text(new AutoBackup(getId(event), dc).checkStorage())
                // .actions(Arrays.asList(new PostbackAction("バックアップを開始", "START_BKUP"),
                // new PostbackAction("バックアップを停止", "STOP_BKUP"),
                // new PostbackAction("共有リンクを発行", "SHARE_DRIVE")))
                // .build();
                // TemplateMessage templateMessage = new TemplateMessage("バックアップ設定", bt);
                // this.reply(replyToken, templateMessage);
                // break;
                // }
                // break;
                // }
                // case "リンク表示": {
                // // バックアップが有効化時のみ
                // if (dc.isBackupRoom(lineId)) {
                // final String url = new AutoBackup(getId(event),
                // dc).getSharedUrl(getFolderName(event));
                // final URI thumb = createUri("/static/images/share_icon.png");
                // ButtonsTemplate bt =
                // ButtonsTemplate.builder().thumbnailImageUrl(thumb).imageSize("contain")
                // .imageBackgroundColor("#00BFA5").title("GoogleDrive共有リンク")
                // .text(getFolderName(event) + "のバックアップファイルを表示")
                // .actions(Arrays.asList(new URIAction("リンクを開く", URI.create(url),
                // null))).build();
                // TemplateMessage templateMessage = new TemplateMessage("GoogleDrive共有リンク",
                // bt);
                // this.reply(replyToken, Arrays.asList(templateMessage, new TextMessage(url)));
                // break;
                // }
                // this.replyText(replyToken, "トークルームで自動バックアップが有効になっていないためリンクを表示できません。");
                // break;
                // }
                // case "容量確認": {
                // if (userId.equals("Uf669f8fa7e61ce402e13aae0df9f5a59")) {
                // this.replyText(replyToken, new AutoBackup(lineId, dc).checkStorage());
                // }
                // break;
                // }

                case "ID": {
                    if (!userId.equals("Uf669f8fa7e61ce402e13aae0df9f5a59")) {
                        return;
                    }
                    this.replyText(replyToken, getId(event));
                    break;
                }

                case "許可登録": {
                    if (!userId.equals("Uf669f8fa7e61ce402e13aae0df9f5a59")) {
                        return;
                    }
                    this.replyText(replyToken, dc.editRegistration(lineId, false));
                    break;
                }

                case "許可取消": {
                    if (!userId.equals("Uf669f8fa7e61ce402e13aae0df9f5a59")) {
                        return;
                    }
                    this.replyText(replyToken, dc.editRegistration(lineId, true));
                    break;
                }

                case "APEXランク募集": {
                    final Bubble bubble = new TemplateMenuFlex(createUri("/static/images/apex-hero.jpg"),"APEXランク募集","test", new ArrayList<Action>(Arrays.asList(new PostbackAction("参加","join")))).createBubble(BubbleSize.MEGA);
                    this.reply(replyToken, new FlexMessage("APEXランク募集", bubble));
                    break;
                }
            }
        } catch (Exception e) {
            this.replyText(replyToken, "例外がスローされました:" + "\n" + e.getClass().getName() + "\nメッセージ:" + e.getMessage());
            System.out.println(e.getClass().getName() + " : " + e.getMessage());
            dc.changeMode(new Frag(IFragCollection.DISABLE), userId, lineId);
        } finally {
            dc.closeDB();
        }

    }

}
