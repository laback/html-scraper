package test.task.callable;

import org.jsoup.nodes.Document;
import test.task.model.SportEvent;
import test.task.scraper.HtmlScraper;
import test.task.util.Constants;
import test.task.util.PageUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

public class CollectorCallable implements Callable<Set<SportEvent>> {
    private final String startSportUrl;
    private final List<Document> eventPages = new ArrayList<>();
    private final HtmlScraper scraper;
    public CollectorCallable(String startSportUrl, HtmlScraper scraper) {
        super();
        this.startSportUrl = startSportUrl;
        this.scraper = scraper;
    }

    @Override
    public Set<SportEvent> call() {
        collectEventPages(startSportUrl);
        var resultSet = new HashSet<SportEvent>();
        for(var eventPage : eventPages){
            resultSet.addAll(scraper.parse(eventPage));
        }
        return resultSet;
    }

    private void collectEventPages(String url){
        var page = PageUtils.getHtmlPage(url);
        var events = page.select(Constants.EVENTS_QUERY);
        if(events.size() == 0){
            for(var nextUrl : page.select(Constants.NEXT_LINK_QUERY)){
                collectEventPages(Constants.GET_URL.apply(nextUrl));
            }
        } else{
            eventPages.add(page);
        }
    }
}
