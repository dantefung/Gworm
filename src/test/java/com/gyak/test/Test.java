package com.gyak.test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.gyak.gworm.GwormAction;
import com.gyak.gworm.GwormBox;
import com.gyak.gworm.GwormCoordinate;

import com.gyak.proterty.RequestProperties;
import com.gyak.url.HasUrl;
import com.gyak.url.UrlGeneration;

import java.io.FileNotFoundException;

/**
 * Created by Guiyanakuang
 * on 2015-05-31.
 */
public class Test {

    private final String NAME = "JDBook";
    private final String URL_ID = "Book";
    private final String REQUEST_FILE = "request.properties";
    private final String WORM_CONFIG = "jd.json";

    private final String BOOK_LIST = "bookList";
    private final String BOOK_NAME = "bookName";
    private final String BOOK_PAGE = "bookPage";
    private final String BOOK_AUTHOR = "bookAuthor";
    private final String BOOK_COMMENT = "bookComment";

    private int concurrency = 20;

    public void test() throws FileNotFoundException {
        GwormBox gwormBox = GwormBox.getInstance();
        RequestProperties rp = RequestProperties.getInstance();
        rp.initProperties(ClassLoader.getSystemResourceAsStream(REQUEST_FILE));
        gwormBox.addGworm(WORM_CONFIG, NAME);
        UrlGeneration jd = new JdUrlGeneration();
        GwormCoordinate coordinate = new GwormCoordinate(NAME, URL_ID);
        GwormAction ga = new GwormAction(concurrency, jd, coordinate) {

            @Override
            public void action(JsonObject json, Object bindObj) {
                JsonArray jsonArray = json.get(BOOK_LIST).getAsJsonArray();
                for (int i=0;i<jsonArray.size();i++) {
                    JsonObject obj = jsonArray.get(i).getAsJsonObject();
                    String bookName = obj.get(BOOK_NAME).getAsString();
                    String bookPage = obj.get(BOOK_PAGE).getAsString();
                    String bookAuthor = obj.get(BOOK_AUTHOR).getAsString();
                    String bookComment = obj.get(BOOK_COMMENT).getAsString();
                    System.out.println("书名：" + bookName);
                    System.out.println("购买链接：" + bookPage);
                    System.out.println("作者：" + bookAuthor);
                    System.out.println("评论数：" + bookComment);
                    System.out.println("￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣");
                }
            }

        };

        ga.work();
    }

    public static void main(String[] args) {
        try {
            new Test().test();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试事例
     * JD（京东）小说->中国当代小说->人民文学出版社
     * url生成器
     */
    class JdUrlGeneration implements UrlGeneration {

        private int currentPage = 1;
        private final String page = "http://list.jd.com/list.html?cat=1713,3258,3297&ev=publishers_%E4%BA%BA%E6%B0%91%E6%96%87%E5%AD%A6%E5%87%BA%E7%89%88%E7%A4%BE@&area=1,72,4137&delivery=0&stock=0&sort=sort_totalsales15_desc&JL=6_0_0&page=";

        @Override
        public int getStart() {
            return 1;
        }

        @Override
        public int getEnd() {
            return 20;
        }

        @Override
        public void next() {
            currentPage ++;
        }

        @Override
        public HasUrl getCurrentbindObj() {
            return new HasUrl() {
                @Override
                public String getUrl() {
                    return page + currentPage;
                }
            };
        }

    }

}
