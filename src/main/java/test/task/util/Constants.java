package test.task.util;

import org.jsoup.nodes.Element;

import java.util.function.Function;

public class Constants {

    public static final Function<Element, String> GET_URL = element -> element.getElementsByTag("a").first().attr("href");

    public static final String EVENTS_QUERY = "td.std-pd.pt-10";
    public static final String NEXT_LINK_QUERY = "td.vh-40";
}
