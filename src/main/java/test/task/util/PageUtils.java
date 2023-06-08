package test.task.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import test.task.exception.ConnectionException;

import java.io.IOException;

public class PageUtils {

    public static Document getHtmlPage(String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new ConnectionException(String.format("Can't get data from url %s. Reason: %s", url, e.getMessage()));
        }
    }
}
