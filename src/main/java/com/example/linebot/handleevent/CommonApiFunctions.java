package com.example.linebot.handleevent;

import static java.util.Collections.singletonList;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.linebot.KitchenSinkApplication;
import com.example.linebot.FlexMessage.TemplateMenuFlex;
import com.google.common.io.ByteStreams;
import com.linecorp.bot.client.LineBlobClient;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.MessageContentResponse;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.action.Action;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.source.GroupSource;
import com.linecorp.bot.model.event.source.RoomSource;
import com.linecorp.bot.model.event.source.UserSource;
import com.linecorp.bot.model.group.GroupSummaryResponse;
import com.linecorp.bot.model.message.FlexMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.flex.container.Bubble;
import com.linecorp.bot.model.message.flex.container.Carousel;
import com.linecorp.bot.model.message.flex.container.Bubble.BubbleSize;
import com.linecorp.bot.model.message.template.CarouselColumn;
import com.linecorp.bot.model.message.template.CarouselTemplate;
import com.linecorp.bot.model.profile.UserProfileResponse;
import com.linecorp.bot.model.response.BotApiResponse;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonApiFunctions {

    private static final String TOKEN = "V/TRZRoJ256af/xY+g0DbCyssppR30bExU3a7OGYtidOw36Md33Tx/6DDpm0AKV6b+6GoZQg2CMEYMnR0N/UcWCeaGPCc1vf6iJl48pwjYBgdiqu24jcL7qKfNpr2dE6gukZe6XOHcgwkVEXXIOmaQdB04t89/1O/w1cDnyilFU=";

    @Autowired
    private static final LineMessagingClient lineMessagingClient = LineMessagingClient.builder(TOKEN).build();

    @Autowired
    private static final LineBlobClient lineBlobClient = LineBlobClient.builder(TOKEN).build();

    /**
     * 
     * @return
     */
    protected LineMessagingClient getLineMessagingClient() {
        return CommonApiFunctions.lineMessagingClient;
    }

    /**
     * 
     * @return
     */
    protected LineBlobClient getLineBlobClient() {
        return CommonApiFunctions.lineBlobClient;
    }

    /**
     * 
     * @param replyToken
     * @param message
     */
    protected void reply(@NonNull String replyToken, @NonNull Message message) {
        reply(replyToken, singletonList(message));
    }

    /**
     * 
     * @param replyToken
     * @param messages
     */
    protected void reply(@NonNull String replyToken, @NonNull List<Message> messages) {
        reply(replyToken, messages, false);
    }

    /**
     * 
     * @param replyToken
     * @param messages
     * @param notificationDisabled
     */
    protected void reply(@NonNull String replyToken, @NonNull List<Message> messages, boolean notificationDisabled) {
        try {
            BotApiResponse apiResponse = lineMessagingClient
                    .replyMessage(new ReplyMessage(replyToken, messages, notificationDisabled)).get();
            log.info("Sent messages: {}", apiResponse);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 
     * @param replyToken
     * @param message
     */
    protected void replyText(@NonNull String replyToken, @NonNull String message) {
        if (replyToken.isEmpty()) {
            throw new IllegalArgumentException("replyToken must not be empty");
        }
        if (message.length() > 1000) {
            message = message.substring(0, 1000 - 2) + "……";
        }
        this.reply(replyToken, new TextMessage(message));
    }

    /**
     * 
     * @param path
     * @return
     */
    protected static URI createUri(String path) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().scheme("https").path(path).build().toUri();
    }

    /**
     * 
     * @param event
     * @return トークルームID
     * @throws InterruptedException
     * @throws ExecutionException
     */
    protected String getId(Event event) {
        String id = null;
        if (event.getSource() instanceof GroupSource) {
            GroupSummaryResponse groupSummary;
            try {
                groupSummary = lineMessagingClient.getGroupSummary(((GroupSource) event.getSource()).getGroupId())
                        .get();
                System.out.println("グループID:" + groupSummary.getGroupId());
                id = groupSummary.getGroupId();
                return id;
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return id;
            }
        }
        if (event.getSource() instanceof RoomSource) {
            System.out.println("ルームID:" + ((RoomSource) event.getSource()).getRoomId());
            id = ((RoomSource) event.getSource()).getRoomId();
            return id;
        }
        if (event.getSource() instanceof UserSource) {
            System.out.println("ユーザーID:" + event.getSource().getUserId());
            id = event.getSource().getUserId();
            return id;
        }
        return id;
    }

    /**
     * 
     * @param event
     * @return
     */
    protected String getDisplayName(Event event) {
        final String userId = event.getSource().getUserId();
        String userName = null;
        try {
            if (event.getSource() instanceof GroupSource) {

                UserProfileResponse groupResponse = lineMessagingClient
                        .getGroupMemberProfile(((GroupSource) event.getSource()).getGroupId(), userId).get();
                userName = groupResponse.getDisplayName();
                return userName;
            }
            if (event.getSource() instanceof RoomSource) {

                UserProfileResponse roomResponse = lineMessagingClient
                        .getRoomMemberProfile(((RoomSource) event.getSource()).getRoomId(), userId).get();
                userName = roomResponse.getDisplayName();
                return userName;
            }
            UserProfileResponse response = lineMessagingClient.getProfile(userId).get();
            userName = response.getDisplayName();

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {

            if (userName == null) {
                userName = "名前が取得できませんでした";
            }
            if (userId == null) {
                System.out.println("User ID is NULL");
            }

        }
        return userName;
    }

    private String changeAlpFullToHalf(String str) {
        String result = null;
        if (str != null) {
            StringBuilder sb = new StringBuilder(str);
            for (int i = 0; i < sb.length(); i++) {
                int c = (int) sb.charAt(i);
                if ((c >= 0xFF21 && c <= 0xFF3A) || (c >= 0xFF41 && c <= 0xFF5A)) {
                    sb.setCharAt(i, (char) (c - 0xFEE0));
                }
            }
            result = sb.toString();
        }
        return result;
    }

    protected String escapeString(String text) {
        String str = changeAlpFullToHalf(text);
        str = str.trim().replaceAll("[^あ-んア-ンｱ-ﾝ一-龥a-zA-Z0-9-_&+ー 　]", "");
        str = str.replaceAll("[ 　&]", "+");
        return str;
    }

    protected String escapeStringWiki(String text) {
        String str = changeAlpFullToHalf(text);
        str = text.trim().replaceAll("[^あ-んア-ンｱ-ﾝ一-龥a-zA-Z0-9-_&+ー. 　]", "");
        str = text.replaceAll("[ 　&]", "_");
        return str;
    }

    protected String subStringText(String text) {
        if (text.length() > 60) {
            return text.substring(0, 56) + "...";
        }
        return text;
    }

    protected String formatTimeStamp(Instant timeStamp, final String pattern) {
        if (timeStamp == null) {
            System.out.println("Instant Object is NULL");
            return null;
        }
        return DateTimeFormatter.ofPattern(pattern).format(LocalDateTime.ofInstant(timeStamp, ZoneId.systemDefault()));
    }

    protected String getFolderName(Event event) {
        try {
            if (event.getSource() instanceof GroupSource) {
                GroupSummaryResponse groupSummary;
                groupSummary = lineMessagingClient.getGroupSummary(((GroupSource) event.getSource()).getGroupId())
                        .get();
                System.out.println("グループ名:" + groupSummary.getGroupName());
                return groupSummary.getGroupName() + "(グループ)";
            }
            if (event.getSource() instanceof RoomSource) {
                System.out.println("ルーム名(ID):" + ((RoomSource) event.getSource()).getRoomId());
                return ((RoomSource) event.getSource()).getRoomId() + "(トークルーム)";
            }
            if (event.getSource() instanceof UserSource) {
                final String userId = event.getSource().getUserId();
                UserProfileResponse response = lineMessagingClient.getProfile(userId).get();
                return response.getDisplayName() + "(個人トーク)";
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {

        }
        return "";
    }

    /**
     * 
     * @param args
     */
    protected void system(String... args) {
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        try {
            Process start = processBuilder.start();
            int i = start.waitFor();
            log.info("result: {} =>  {}", Arrays.toString(args), i);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (InterruptedException e) {
            log.info("Interrupted", e);
            Thread.currentThread().interrupt();
        }
    }

    protected TemplateMessage pinterestCarousel() {
        final ArrayList<CarouselColumn> columnList = new ArrayList<>();
        columnList.add(new CarouselColumn(createUri("/static/images/next_icon.png"), "#000000", "次の画像を送信", "次の画像を送信します",
                null, Arrays.asList(new PostbackAction("次の画像", "NEXT_IMAGE"))));

        columnList.add(new CarouselColumn(createUri("/static/images/repeat_search_icon.png"), "#000000", "[壁紙検索]再検索",
                "再検索を実行します", null, Arrays.asList(new PostbackAction("再検索", "PINTEREST_SEARCH"))));

        columnList.add(new CarouselColumn(createUri("/static/images/search_menu_icon.png"), "#000000", "検索メニューを表示",
                "検索メニューを表示します", null, Arrays.asList(new PostbackAction("表示", "REPEAT"))));
        CarouselTemplate ct = CarouselTemplate.builder().imageSize("contain").columns(columnList).build();
        return new TemplateMessage("壁紙検索結果", ct);
    }

    // protected void searchMenuCarousel(String replyToken) {
    //     CarouselTemplate ct = CarouselTemplate.builder().imageSize("contain")
    //             .columns(Arrays.asList(
    //                     new CarouselColumn(createUri("/static/images/google_search_icon.png"), "#202020", "Google検索",
    //                             "Google検索を開始します", null,
    //                             Arrays.asList(new PostbackAction("開始", "SEARCH"),
    //                                     new PostbackAction("キャンセル", "CANCEL"))),
    //                     new CarouselColumn(createUri("/static/images/image_search_icon.png"), "#202020", "画像検索",
    //                             "画像検索を開始します", null,
    //                             Arrays.asList(new PostbackAction("開始", "IMAGE_SEARCH"),
    //                                     new PostbackAction("キャンセル", "CANCEL"))),
    //                     new CarouselColumn(createUri("/static/images/youtube_icon.png"), "#202020", "youtube検索",
    //                             "youtube検索を開始します", null,
    //                             Arrays.asList(new PostbackAction("開始", "MOVIE_SEARCH"),
    //                                     new PostbackAction("キャンセル", "CANCEL"))),
    //                     new CarouselColumn(createUri("/static/images/wiki_icon.png"), "#202020", "wikipedia検索",
    //                             "wikipedia検索を開始します", null,
    //                             Arrays.asList(new PostbackAction("開始", "WIKI_SEARCH"),
    //                                     new PostbackAction("キャンセル", "CANCEL"))),
    //                     new CarouselColumn(createUri("/static/images/pixiv_ency_icon.png"), "#202020", "pixiv百科事典検索",
    //                             "pixiv百科事典検索を開始します", null,
    //                             Arrays.asList(new PostbackAction("開始", "PIXIV_SEARCH"),
    //                                     new PostbackAction("キャンセル", "CANCEL"))),
    //                     new CarouselColumn(createUri("/static/images/amazon_icon.png"), "#202020", "amazon検索",
    //                             "amazon検索を開始します", null, Arrays.asList(new PostbackAction("開始", "PRODUCT_SEARCH"),
    //                                     new PostbackAction("キャンセル", "CANCEL")))))
    //             .build();
    //     TemplateMessage tm = new TemplateMessage("検索メニュー", ct);
    //     this.reply(replyToken, tm);
    // }

    protected void searchMenuCarousel(String replyToken) {
        final ArrayList<Bubble> bubbleList = new ArrayList<>();
        bubbleList.add(new TemplateMenuFlex(createUri("/static/images/google_search_icon.png"), "google検索",
                "google検索を開始します\n(許可されたグループのみ利用できます)",
                new ArrayList<Action>(
                        Arrays.asList(new PostbackAction("開始", "SEARCH"), new PostbackAction("キャンセル", "CANCEL"))))
                                .createBubble(BubbleSize.MEGA));
        bubbleList.add(new TemplateMenuFlex(createUri("/static/images/image_search_icon.png"), "google画像検索",
                "google画像検索を開始します\n(許可されたグループのみ利用できます)",
                new ArrayList<Action>(
                        Arrays.asList(new PostbackAction("開始", "IMAGE_SEARCH"), new PostbackAction("キャンセル", "CANCEL"))))
                                .createBubble(BubbleSize.MEGA));
        bubbleList.add(new TemplateMenuFlex(createUri("/static/images/youtube_icon.png"), "youtube動画検索",
                "youtube動画検索を開始します\n(許可されたグループのみ利用できます)",
                new ArrayList<Action>(
                        Arrays.asList(new PostbackAction("開始", "MOVIE_SEARCH"), new PostbackAction("キャンセル", "CANCEL"))))
                                .createBubble(BubbleSize.MEGA));
        bubbleList.add(new TemplateMenuFlex(createUri("/static/images/wiki_icon.png"), "wikipedia検索",
                "wikipedia検索を開始します",
                new ArrayList<Action>(
                        Arrays.asList(new PostbackAction("開始", "WIKI_SEARCH"), new PostbackAction("キャンセル", "CANCEL"))))
                                .createBubble(BubbleSize.MEGA));
        bubbleList.add(new TemplateMenuFlex(createUri("/static/images/pixiv_ency_icon.png"), "pixiv百科事典検索",
                "pixiv百科事典検索を開始します",
                new ArrayList<Action>(
                        Arrays.asList(new PostbackAction("開始", "PIXIV_SEARCH"), new PostbackAction("キャンセル", "CANCEL"))))
                                .createBubble(BubbleSize.MEGA));
        bubbleList.add(new TemplateMenuFlex(createUri("/static/images/amazon_icon.png"), "amazon商品検索",
                "amazon商品検索を開始します",
                new ArrayList<Action>(
                        Arrays.asList(new PostbackAction("開始", "PRODUCT_SEARCH"), new PostbackAction("キャンセル", "CANCEL"))))
                                .createBubble(BubbleSize.MEGA));

        this.reply(replyToken, new FlexMessage("検索開始メニュー", Carousel.builder().contents(bubbleList).build()));

    }

    protected FlexMessage repeatSearchCarousel(final URI thumbImage, final String titleText, final String displayText,
            final String linkUrl, final String postbackData) {
        final ArrayList<Bubble> bubbleList = new ArrayList<>();
        bubbleList.add(new TemplateMenuFlex(thumbImage, titleText, displayText,
                new URIAction("検索結果を表示", URI.create(linkUrl), null)).createBubble(BubbleSize.KILO));
        bubbleList
                .add(new TemplateMenuFlex(createUri("/static/images/repeat_search_icon.png"), "[" + titleText + "]再検索",
                        "再検索を実行します", new PostbackAction("再検索", postbackData)).createBubble(BubbleSize.KILO));
        bubbleList.add(new TemplateMenuFlex(createUri("/static/images/search_menu_icon.png"), "検索メニューを表示",
                "検索メニューを表示します", new PostbackAction("表示", "REPEAT")).createBubble(BubbleSize.KILO));
        return new FlexMessage(titleText + "結果", Carousel.builder().contents(bubbleList).build());
    }

    /**
     * 
     * @param ext
     * @param responseBody
     * @return
     */
    protected static DownloadedContent saveContent(String ext, MessageContentResponse responseBody) {
        log.info("Got content-type: {}", responseBody);

        DownloadedContent tempFile = createTempFile(ext);
        try (OutputStream outputStream = Files.newOutputStream(tempFile.path)) {
            ByteStreams.copy(responseBody.getStream(), outputStream);
            log.info("Saved {}: {}", ext, tempFile);
            return tempFile;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * 
     * @param ext
     * @return
     */
    protected static DownloadedContent createTempFile(String ext) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HHmmssS");
        String nowStr = format.format(date);
        String fileName = nowStr + UUID.randomUUID() + '.' + ext;
        Path tempFile = KitchenSinkApplication.downloadedContentDir.resolve(fileName);
        tempFile.toFile().deleteOnExit();
        return new DownloadedContent(tempFile, createUri("/downloaded/" + tempFile.getFileName()));
    }

    @Value
    protected static class DownloadedContent {
        public Path path;
        public URI uri;
    }

}
