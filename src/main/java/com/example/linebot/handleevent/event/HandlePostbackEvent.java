package com.example.linebot.handleevent.event;

import com.example.linebot.database.DatabaseConnection;
import com.example.linebot.database.Frag;
import com.example.linebot.database.IFragCollection;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.PostbackEvent;

class HandlePostbackEvent extends AbstractHandleEvent {

    public HandlePostbackEvent() {
    }

    @Override
    public void handle(final Event event) {
        System.out.println("ポストバックイベント");
        final String replyToken = ((PostbackEvent) event).getReplyToken();
        final String userId = event.getSource().getUserId();
        final String userName = getDisplayName(event);
        final String lineId = getId(event);
        final String postBackData = ((PostbackEvent) event).getPostbackContent().getData();
        final DatabaseConnection dc = new DatabaseConnection();
        try {
            if (((PostbackEvent) event).getPostbackContent().getParams() != null) {
                final String dateString = ((PostbackEvent) event).getPostbackContent().getParams().get("date");
                this.replyText(replyToken, userName + "さんが日付を送信しました" + "\n" + "[" + dateString + "]");

            }

            if (postBackData.equals("SEARCH")) {
                if (!dc.checkRegistration(lineId)) {
                    this.replyText(replyToken, "このグループでは選択した操作が許可されていません\n管理者に確認してください");
                    return;
                }
                dc.changeMode(new Frag(IFragCollection.SEARCH_MODE), userId, lineId);
                this.replyText(replyToken, "[" + userName + "]検索キーワードを入力してください");
                return;
            }

            if (postBackData.equals("IMAGE_SEARCH")) {
                if (!dc.checkRegistration(lineId)) {
                    this.replyText(replyToken, "このグループでは選択した操作が許可されていません\n管理者に確認してください");
                    return;
                }
                dc.changeMode(new Frag(IFragCollection.IMAGE_SEARCH_MODE), userId, lineId);
                this.replyText(replyToken, "[" + userName + "]画像検索キーワードを入力してください");
                return;
            }

            if (postBackData.equals("PRODUCT_SEARCH")) {
                dc.changeMode(new Frag(IFragCollection.PRODUCT_SEARCH_MODE), userId, lineId);
                this.replyText(replyToken, "[" + userName + "]amazon検索キーワードを入力してください");
                return;
            }

            if (postBackData.equals("MOVIE_SEARCH")) {
                if (!dc.checkRegistration(lineId)) {
                    this.replyText(replyToken, "このグループでは選択した操作が許可されていません\n管理者に確認してください");
                    return;
                }
                dc.changeMode(new Frag(IFragCollection.MOVIE_SEARCH_MODE), userId, lineId);
                this.replyText(replyToken, "[" + userName + "]youtube検索キーワードを入力してください");
                return;
            }

            if (postBackData.equals("WIKI_SEARCH")) {
                dc.changeMode(new Frag(IFragCollection.WIKI_SEARCH_MODE), userId, lineId);
                this.replyText(replyToken, "[" + userName + "]wikipedia検索キーワードを入力してください");
                return;
            }

            if (postBackData.equals("PIXIV_SEARCH")) {
                dc.changeMode(new Frag(IFragCollection.PIXIV_SEARCH_MODE), userId, lineId);
                this.replyText(replyToken, "[" + userName + "]pixiv百科事典検索キーワードを入力してください");
                return;
            }

            if (postBackData.equals("CANCEL")) {
                if (dc.checkMode(userId, lineId) == IFragCollection.DISABLE) {
                    return;
                }
                dc.changeMode(new Frag(IFragCollection.DISABLE), userId, lineId);
                this.replyText(replyToken, "[" + userName + "]検索を中止しました");
                return;
            }
            if (postBackData.equals("REPEAT")) {
                searchMenuCarousel(replyToken);
                return;
            }

            // if (postBackData.equals("SHARE_DRIVE")) {
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
            // return;
            // }

            // if (userId.equals("Uf669f8fa7e61ce402e13aae0df9f5a59")) {

            // if (postBackData.equals("STOP_BKUP")) {
            // dc.changeBackup(false, getId(event));
            // this.replyText(replyToken, "このトークルームでバックアップを無効にしました。");
            // return;
            // }
            // if (postBackData.equals("START_BKUP")) {
            // new AutoBackup(lineId, dc).checkFolderExists(getFolderName(event));
            // dc.changeBackup(true, lineId);
            // this.replyText(replyToken, "このトークルームでバックアップを有効にしました。");
            // return;
            // }
            // }

            // if (postBackData.equals("NEXT_IMAGE")) {
            // dc.saveImageCount(userId, lineId);
            // final String text = dc.getImageSearchWord(userId, lineId);
            // final ArrayList<String> imageUrlList = new
            // GoogleCustomSearch(dc.getImageCount(userId, lineId),
            // "searchType=image", "imgSize=xxlarge").wallpaper().getUrl(text);
            // if (imageUrlList.isEmpty()) {
            // this.replyText(replyToken, "画像を取得できませんでした");
            // return;
            // }
            // final ArrayList<Bubble> bubbleList = new ArrayList<>();
            // for (String url : imageUrlList) {
            // bubbleList.add(new PreviewImageFlex().createBubble(new URI(url), new
            // URI(url)));
            // }
            // final FlexMessage customFlexMessage = new FlexMessage("画像一覧",
            // Carousel.builder().contents(bubbleList).build());

            // ArrayList<CarouselColumn> columnList = new ArrayList<>();
            // columnList.add(new CarouselColumn(createUri("/static/images/next_icon.png"),
            // "#000000", "次の画像",
            // "次の画像を送信します", null, Arrays.asList(new PostbackAction("次の画像を表示",
            // "NEXT_IMAGE"))));
            // columnList.add(new
            // CarouselColumn(createUri("/static/images/repeat_search_icon.png"), "#000000",
            // "[壁紙検索]再検索", "再検索を実行します", null,
            // Arrays.asList(new PostbackAction("再検索", "PINTEREST_SEARCH"))));

            // columnList.add(new
            // CarouselColumn(createUri("/static/images/search_menu_icon.png"), "#000000",
            // "検索メニューを表示", "検索メニューを表示します", null, Arrays.asList(new PostbackAction("表示",
            // "REPEAT"))));
            // CarouselTemplate ct =
            // CarouselTemplate.builder().imageSize("contain").columns(columnList).build();
            // TemplateMessage tm = new TemplateMessage("壁紙検索結果", ct);
            // this.reply(replyToken, Arrays.asList(tm, customFlexMessage));
            // return;
            // }
        } catch (Exception e) {
            this.replyText(replyToken, "例外がスローされました :" + "\n" + e.toString());
            dc.changeMode(new Frag(IFragCollection.DISABLE), userId, lineId);
        } finally {
            dc.closeDB();
        }
    }

}
