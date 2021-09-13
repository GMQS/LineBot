package com.example.linebot.jsoup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.example.linebot.database.DatabaseConnection;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HTMLPerser {

    private Document document;

    public HTMLPerser(final String URL, final String id)
            throws IOException, HttpStatusException, RequestIntervalException, InterruptedException {
        DatabaseConnection dc = new DatabaseConnection();

        checkInterval(dc, id);
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("static/text/UserAgentList.txt");
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;
        final ArrayList<String> lines = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            lines.add(line);
        }
        br.close();

        Random rand = new Random();
        int index = rand.nextInt(lines.size());
        insertInterval(dc, id);
        this.document = Jsoup.connect(URL).userAgent(lines.get(index)).referrer("http://www.google.com")
                .followRedirects(true).get();
    }

    // デバッグ用
    public HTMLPerser(final InputStream is, final String baseUri) throws IOException {
        this.document = Jsoup.parse(is, "UTF-8", baseUri);
    }

    public Map<String, String> parseWiki() {
        final Map<String, String> map = new HashMap<>();
        Element element = document.select("div#mw-content-text").first();
        if (element.html().contains("曖昧さ回避のためのページ")) {
            String text = "検索ワードが複数の意味を持つため記事を特定できませんでした\n詳細は下の検索結果ボタンからリンクを開いてください";
            map.put("image", "");
            map.put("text", text);
            return map;
        }
        String image = document.select("head meta[property=og:image]").attr("content");
        String headingText = "";

        Elements contents;
        if (element.html().contains("mf-section-0")) {
            contents = element.select(".mf-section-0 > *");
        } else {
            contents = document.select(".mw-parser-output > *");
        }
        boolean appearance = false;
        for (Element elm : contents) {
            String str = elm.select("p:not(* > p, .mw-empty-elt)").text().replaceAll("\\[.*?\\]", "");
            if (str.isEmpty()) {
                if (appearance == true) {
                    break;
                }
                continue;
            }
            headingText += str + "\n\n";
            appearance = true;
        }

        // LINE API仕様で5000文字以上を送信できないためカットする
        if (headingText.length() > 5000) {
            headingText = headingText.substring(0, 4997) + "...";
        }
        map.put("image", image);
        map.put("text", headingText);
        return map;
    }

    public ArrayList<Map<String, String>> parseWikiSearch() {
        final ArrayList<Map<String, String>> list = new ArrayList<>();
        Elements elements = document.select("ul.mw-search-results li.mw-search-result");
        for (Element elm : elements) {
            final Map<String, String> map = new HashMap<>();
            map.put("title", elm.select("div.mw-search-result-heading a").attr("title"));
            map.put("main", elm.select("div.searchresult").text() + "...");
            String link = elm.select("div.mw-search-result-heading a").attr("href");
            if (link.contains("https://")) {
                map.put("link", link);
            } else {
                map.put("link", "https://ja.wikipedia.org" + link);
            }
            list.add(map);

            System.out.println("タイトル : " + elm.select("div.mw-search-result-heading a").attr("title"));
            System.out.println("本文 : " + elm.select("div.searchresult").text());
            System.out.println("リンク : " + elm.select("div.mw-search-result-heading a").attr("href"));
        }

        return list;
    }

    public ArrayList<AmazonProductValue> parseAmazon() {
        final ArrayList<AmazonProductValue> list = new ArrayList<>();
        // 検索結果から商品コンポーネントだけに絞る
        Elements elements = document.select("div[data-component-type=s-search-result]");
        for (Element elm : elements) {
            final AmazonProductValue productValue = new AmazonProductValue();

            String url = "https://www.amazon.co.jp/gp/product/" + elm.attr("data-asin");
            String image = elm.select("img.s-image").attr("src");
            String title = elm.select(".a-section h2 span").text();
            String star = elm.select("span.a-icon-alt").text().replaceAll("5つ星のうち", "");
            String starCount = elm.select("span[aria-label] span.a-size-base").text();
            String price = elm.select("span.a-price span.a-price-whole").text();
            // String shipping = elm.select(".a-spacing-top-micro
            // i.a-icon-prime").attr("aria-label")
            // + elm.select(".a-spacing-top-micro .s-align-children-center").text();
            String video = elm.select(".a-spacing-top-small a.a-text-bold").text();

            productValue.setUrl(url);
            productValue.setImage(image);
            productValue.setTitle(title);
            productValue.setStar(star);
            productValue.setStarCount(starCount);
            productValue.setPrice(price);
            // productValue.setShipping("");
            productValue.setVideo(video);

            list.add(productValue);
        }

        return list;

    }

    public Map<String, String> parsePixivEncy() {
        final Map<String, String> map = new HashMap<>();
        Element article = document.selectFirst("article#content");
        String thumbImage = article.select("header div#main-image a img").attr("src");
        System.out.println(article.select("header div#main-image a img").attr("src"));
        String hurigana = article.select("header div#content_title p.subscript").text();
        String title = article.select("header div#content_title h1#article-name").text();
        String summary = article.select("header div#content_title div.summary").text();
        map.put("image", thumbImage);
        map.put("hurigana", hurigana);
        map.put("title", title);
        map.put("summary", summary);
        return map;
    }

    public ArrayList<Map<String, String>> parsePixivEncySearch() {
        final ArrayList<Map<String, String>> list = new ArrayList<>();
        Elements articles = document.select("div#content section article");
        for (Element article : articles) {
            final Map<String, String> map = new HashMap<>();
            String link = "https://dic.pixiv.net" + article.select("div.thumb a").attr("href");
            String thumbImage = article.select("div.thumb a img").attr("src");
            String title = article.select("div.info h2").text();
            String summary = article.select("div.info p.summary").text().replaceAll("続きを読む", "");
            map.put("link", link);
            map.put("image", thumbImage);
            map.put("title", title);
            map.put("summary", summary);
            list.add(map);
        }

        return list;
    }

    private void checkInterval(final DatabaseConnection dc, final String id)
            throws InterruptedException, RequestIntervalException {
        final long startTime = System.currentTimeMillis();
        long time = 0L;
        while (dc.isInterVal(id) && time < 10000L) {
            System.out.println("接続待機中 : " + time + "ms");
            Thread.sleep(500);
            time = System.currentTimeMillis() - startTime;
        }
        if (time >= 10000L) {
            System.out.println("タイムアウト");
            throw new RequestIntervalException("接続待機タイムアウト");
        }
    }

    private void insertInterval(final DatabaseConnection dc, final String siteName) {
        final int INTERVAL = 5000;
        dc.changeInterval(siteName, true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                DatabaseConnection con = new DatabaseConnection();
                con.changeInterval(siteName, false);
                con.closeDB();
            }
        }).start();
    }

}
