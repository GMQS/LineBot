package com.example.linebot.handleevent.message;

import com.example.linebot.database.DatabaseConnection;
import com.example.linebot.database.Frag;
import com.example.linebot.database.IFragCollection;
import com.example.linebot.handleevent.CommonApiFunctions;
import com.example.linebot.jsoup.HTMLPerser;
import com.example.linebot.jsoup.RequestIntervalException;
import com.example.linebot.jsoup.AmazonProductValue;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.linebot.FlexMessage.AmazonPreviewFlex;
import com.example.linebot.FlexMessage.PreviewImageFlex;
import com.example.linebot.FlexMessage.TemplateMenuFlex;
import com.example.linebot.FlexMessage.WikiPreviewFlex;
import com.example.linebot.common.GoogleCustomSearch;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.FlexMessage;
import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.flex.container.Bubble;
import com.linecorp.bot.model.message.flex.container.Carousel;
import com.linecorp.bot.model.message.flex.container.Bubble.BubbleSize;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

class HandleModeText extends CommonApiFunctions {

        public HandleModeText() {
        }

        public void handle(final String replyToken, final Event event, final TextMessageContent content,
                        final DatabaseConnection dc)
                        throws IOException, URISyntaxException, SQLException, InterruptedException {
                final String text = content.getText();
                final String displayText = subStringText(text);
                final String escapeText = escapeString(text);
                final String userId = event.getSource().getUserId();
                final String lineId = getId(event);
                if (text.equals("検索中止")) {
                        return;
                }
                final int fragValue = dc.checkMode(userId, lineId);
                dc.changeMode(new Frag(IFragCollection.DISABLE), userId, lineId);
                switch (fragValue) {
                        case IFragCollection.SEARCH_MODE: {
                                ArrayList<String> urlList = new GoogleCustomSearch(1).noLimitSearch(getId(event))
                                                .getUrl(escapeText);
                                String URL = "https://www.google.com/search?q=" + escapeText;
                                FlexMessage fm = repeatSearchCarousel(
                                                createUri("/static/images/google_search_icon.png"), "google検索",
                                                displayText, URL, "SEARCH");
                                if (urlList.size() == 0) {
                                        this.reply(replyToken, fm);
                                } else {
                                        this.reply(replyToken, Arrays.asList(new TextMessage(urlList.get(0)), fm));
                                }
                                break;
                        }
                        case IFragCollection.IMAGE_SEARCH_MODE: {
                                final ArrayList<String> imageUrlList = new GoogleCustomSearch(1, "searchType=image")
                                                .noLimitSearch(getId(event)).getUrl(escapeText);
                                final ArrayList<Bubble> bubbleList = new ArrayList<>();
                                for (String url : imageUrlList) {
                                        bubbleList.add(new PreviewImageFlex().createBubble(new URI(url), new URI(url)));
                                }
                                final FlexMessage customFlexMessage = new FlexMessage("画像一覧",
                                                Carousel.builder().contents(bubbleList).build());
                                String URL = "https://www.google.com/search?q=" + escapeText
                                                + "&safe=off&sxsrf=ALeKk03t2DaxFSWgS8UnnVSZ_fdsiWwYwQ:1616424762574&source=lnms&tbm=isch&sa=X&ved=2ahUKEwi3w52-k8TvAhWVH3AKHa9gCxcQ_AUoAXoECBgQAw&biw=1920&bih=880";
                                FlexMessage fm = repeatSearchCarousel(createUri("/static/images/image_search_icon.png"),
                                                "google画像検索", displayText, URL, "IMAGE_SEARCH");
                                this.reply(replyToken, Arrays.asList(customFlexMessage, fm));
                                break;
                        }

                        case IFragCollection.WIKI_SEARCH_MODE: {
                                final String URL = "https://ja.wikipedia.org/wiki/" + escapeStringWiki(text);
                                Map<String, String> wikiDataSet = null;
                                try {
                                        wikiDataSet = new HTMLPerser(URL, "wiki").parseWiki();
                                } catch (HttpStatusException e) { // 記事が無かった
                                        final String SEARCH_URL = "https://ja.wikipedia.org/w/index.php?search="
                                                        + escapeStringWiki(text);
                                        final ArrayList<Map<String, String>> wikiDataList = new HTMLPerser(SEARCH_URL,
                                                        "wiki").parseWikiSearch();

                                        final ArrayList<Bubble> bubbleList = new ArrayList<>();
                                        for (int i = 0; i < 12 && i < wikiDataList.size(); i++) {
                                                final Map<String, String> map = wikiDataList.get(i);
                                                bubbleList.add(new WikiPreviewFlex(map).createBubble());
                                        }
                                        if (bubbleList.isEmpty()) {
                                                this.reply(replyToken, Arrays.asList(new TextMessage("記事が見つかりませんでした"),
                                                                repeatSearchCarousel(createUri(
                                                                                "/static/images/wiki_icon.png"),
                                                                                "wikipedia検索", displayText, SEARCH_URL,
                                                                                "WIKI_SEARCH")));
                                                return;
                                        }

                                        final FlexMessage customFlexMessage = new FlexMessage("wikipedia検索候補",
                                                        Carousel.builder().contents(bubbleList).build());

                                        this.reply(replyToken, Arrays.asList(new TextMessage("記事が見つからなかったため候補記事を表示します"),
                                                        customFlexMessage,
                                                        repeatSearchCarousel(createUri("/static/images/wiki_icon.png"),
                                                                        "wikipedia検索", displayText, SEARCH_URL,
                                                                        "WIKI_SEARCH")));
                                        return;
                                } catch (RequestIntervalException e) { // 接続インターバルのタイムアウト時間を超過した
                                        this.replyText(replyToken, "接続がタイムアウトしました" + "\n" + "時間を置いてから再度試してください");
                                }

                                final String imageUrl = wikiDataSet.get("image");
                                String headingText = wikiDataSet.get("text");

                                if (headingText.equals("")) {
                                        headingText = "説明文がありません";
                                }
                                if (imageUrl.equals("")) {
                                        // TemplateMessage tm = repeatSearchCarousel(
                                        // createUri("/static/images/wiki_icon.png"), "wikipedia検索",
                                        // displayText, URL, "WIKI_SEARCH");
                                        FlexMessage fm = repeatSearchCarousel(createUri("/static/images/wiki_icon.png"),
                                                        "wikipedia検索", displayText, URL, "WIKI_SEARCH");
                                        this.reply(replyToken, Arrays.asList(new TextMessage(headingText), fm));
                                        break;
                                }
                                final URI thumbImage = URI.create(imageUrl);
                                // TemplateMessage tm = repeatSearchCarousel(thumbImage, "wikipedia検索",
                                // displayText, URL,
                                // "WIKI_SEARCH");
                                FlexMessage fm = repeatSearchCarousel(thumbImage, "wikipedia検索", displayText, URL,
                                                "WIKI_SEARCH");
                                this.reply(replyToken, Arrays.asList(new ImageMessage(thumbImage, thumbImage),
                                                new TextMessage(headingText), fm));
                                break;

                        }

                        case IFragCollection.PIXIV_SEARCH_MODE: {
                                final String URL = "https://dic.pixiv.net/a/" + escapeText;
                                Map<String, String> pixivEncyDataSet = null;
                                try {
                                        pixivEncyDataSet = new HTMLPerser(URL, "pixiv").parsePixivEncy();
                                } catch (RequestIntervalException e) { // 接続インターバルのタイムアウト時間を超過した
                                        this.replyText(replyToken, "接続がタイムアウトしました" + "\n" + "時間を置いてから再度試してください");
                                }

                                final String imageUrl = pixivEncyDataSet.get("image");
                                String headingText = pixivEncyDataSet.get("summary");

                                if (headingText.equals("")) {
                                        final String SEARCH_URL = "https://dic.pixiv.net/search?query=" + escapeText;
                                        final ArrayList<Map<String, String>> pixivEncyDataList = new HTMLPerser(
                                                        SEARCH_URL, "pixiv").parsePixivEncySearch();

                                        final ArrayList<Bubble> bubbleList = new ArrayList<>();
                                        for (int i = 0; i < 12 && i < pixivEncyDataList.size(); i++) {
                                                final Map<String, String> map = pixivEncyDataList.get(i);
                                                bubbleList.add(new TemplateMenuFlex(map, "リンクを開く")
                                                                .createBubble(BubbleSize.KILO));
                                        }
                                        if (bubbleList.isEmpty()) {
                                                this.reply(replyToken, Arrays.asList(new TextMessage("記事が見つかりませんでした"),
                                                                repeatSearchCarousel(createUri(
                                                                                "/static/images/pixiv_ency_icon.png"),
                                                                                "pixiv百科事典検索", displayText, SEARCH_URL,
                                                                                "PIXIV_SEARCH")));
                                                return;
                                        }

                                        final FlexMessage customFlexMessage = new FlexMessage("pixiv百科事典検索候補",
                                                        Carousel.builder().contents(bubbleList).build());

                                        this.reply(replyToken, Arrays.asList(new TextMessage("記事が見つからなかったため候補記事を表示します"),
                                                        customFlexMessage,
                                                        repeatSearchCarousel(
                                                                        createUri("/static/images/pixiv_ency_icon.png"),
                                                                        "pixiv百科事典検索", displayText, SEARCH_URL,
                                                                        "PIXIV_SEARCH")));
                                        return;

                                }
                                if (imageUrl.equals("")) {
                                        // TemplateMessage tm = repeatSearchCarousel(
                                        // createUri("/static/images/pixiv_ency_icon.png"), "pixiv百科辞典検索",
                                        // displayText, URL, "PIXIV_SEARCH");
                                        FlexMessage fm = repeatSearchCarousel(
                                                        createUri("/static/images/pixiv_ency_icon.png"), "pixiv百科辞典検索",
                                                        displayText, URL, "PIXIV_SEARCH");
                                        this.reply(replyToken, Arrays.asList(new TextMessage(headingText), fm));
                                        break;
                                }
                                final URI thumbImage = URI.create(imageUrl);
                                // TemplateMessage tm = repeatSearchCarousel(thumbImage, "pixiv百科辞典検索",
                                // displayText, URL,
                                // "PIXIV_SEARCH");
                                FlexMessage fm = repeatSearchCarousel(thumbImage, "pixiv百科辞典検索", displayText, URL,
                                                "PIXIV_SEARCH");
                                this.reply(replyToken, Arrays.asList(new ImageMessage(thumbImage, thumbImage),
                                                new TextMessage(headingText), fm));

                                break;
                        }

                        case IFragCollection.PRODUCT_SEARCH_MODE: {
                                ArrayList<Bubble> bubbleList = new ArrayList<>();
                                final String URL = "https://www.amazon.co.jp/s?k=" + escapeText;
                                ArrayList<AmazonProductValue> productValueList = new HTMLPerser(URL, "amazon")
                                                .parseAmazon();

                                final URI starIcon = createUri("/static/images/star.png");
                                ArrayList<Message> flexList = new ArrayList<>();
                                final int LIMIT = 12; // 12の倍数指定 MAX48 : API仕様上1列12バブルが限度のため
                                for (int i = 1; i <= LIMIT && i <= productValueList.size(); i++) {
                                        System.out.println("バブル追加 :" + i + "個目");
                                        bubbleList.add(new AmazonPreviewFlex(productValueList.get(i - 1), starIcon)
                                                        .createBubble());
                                        if (i % 12 == 0) {
                                                System.out.println("12の倍数 ブロック i :" + i);
                                                System.out.println("フレックス1列追加");
                                                flexList.add(new FlexMessage("画像一覧",
                                                                Carousel.builder().contents(bubbleList).build()));
                                                bubbleList = new ArrayList<>();
                                                continue;
                                        }
                                        if (i == productValueList.size() || i == LIMIT) {
                                                System.out.println("端数ブロック i :" + i);
                                                System.out.println("フレックス1列追加");
                                                flexList.add(new FlexMessage("画像一覧",
                                                                Carousel.builder().contents(bubbleList).build()));
                                                bubbleList = new ArrayList<>();
                                                break;
                                        }
                                }

                                flexList.add(repeatSearchCarousel(createUri("/static/images/amazon_icon.png"),
                                                "amazon検索", displayText, URL, "PRODUCT_SEARCH"));
                                this.reply(replyToken, flexList);

                                break;
                        }

                        case IFragCollection.MOVIE_SEARCH_MODE: {
                                String URL = "https://www.youtube.com/results?search_query=" + escapeText;
                                final ArrayList<String> urlList = new GoogleCustomSearch(1).youtube()
                                                .getUrl(escapeText);
                                final Pattern pattern = Pattern.compile("^.*?(?:v|list)=(.*?)(?:&|$)");
                                final ArrayList<Bubble> bubbleList = new ArrayList<>();

                                for (int i = 0; ((i < 12) && (urlList.size() > i)); i++) {

                                        Document document = Jsoup.connect(urlList.get(i)).get();
                                        String extStr = document.select("head title").text();
                                        String title;
                                        if (extStr.length() == 0) {
                                                title = escapeText;
                                        } else {
                                                title = extStr.substring(0, extStr.length() - 10);
                                        }
                                        if (title.length() > 60) {
                                                title = title.substring(0, 56) + "...";
                                        }

                                        Matcher matcher = pattern.matcher(urlList.get(i));
                                        String id = null;
                                        URI thumbnail = null;
                                        if (matcher != null) {
                                                if (matcher.matches()) {
                                                        id = matcher.group(1);
                                                }
                                        }
                                        if (id == null) {
                                                thumbnail = createUri("/static/images/youtube_icon.png");
                                        } else {
                                                thumbnail = new URI("https://img.youtube.com/vi/" + id + "/0.jpg");
                                        }

                                        bubbleList.add(new TemplateMenuFlex(thumbnail, "youtube検索結果[" + (i + 1) + "]",
                                                        title, new URIAction("動画を表示", new URI(urlList.get(i)), null))
                                                                        .createBubble(BubbleSize.KILO));
                                }

                                FlexMessage resultFlexMessage = new FlexMessage("youtube動画検索結果",
                                Carousel.builder().contents(bubbleList).build());

                                FlexMessage repeatFlexMessage = repeatSearchCarousel(createUri("/static/images/youtube_icon.png"),
                                                "youtube動画検索", displayText, URL, "MOVIE_SEARCH");
                                this.reply(replyToken, Arrays.asList(resultFlexMessage,repeatFlexMessage));
                                break;
                        }
                }
        }

}
