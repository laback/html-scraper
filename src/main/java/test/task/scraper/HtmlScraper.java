package test.task.scraper;

import org.jsoup.nodes.Document;
import test.task.model.SportEvent;

import java.util.List;

public interface HtmlScraper {

    List<SportEvent> parse(Document htmlPage);
}
