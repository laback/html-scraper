package test.task.scraper;

import org.jsoup.nodes.Document;
import test.task.model.SportEvent;

import java.util.List;
import java.util.Set;

public interface HtmlScraper {

    Set<SportEvent> parse(Document htmlPage);
}
