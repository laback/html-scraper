package test.task.scraper;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import test.task.model.SportEvent;

import java.time.LocalTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BetbonanzaHtmlScraper implements HtmlScraper {

    Function<Elements, Integer> buttonElementPosition = events -> events.size() - 1;

    @Override
    public List<SportEvent> parse(Document htmlPage) {
        var events = htmlPage.select("td.std-pd.pt-10");
        events.remove((int) buttonElementPosition.apply(events));
        return events
                .stream()
                .map(this::parseHtmlEventToSportEvent)
                .collect(Collectors.toList());
    }

    private SportEvent parseHtmlEventToSportEvent(Element element) {
        var startTime = LocalTime.parse(
                StringUtils.substringAfterLast(
                        element.select("td.time")
                                .first()
                                .childNode(2)
                                .toString()
                                .trim(), " "));

        var teams = element.select("td.clubs").first().getElementsByTag("span");
        var firstTeam = teams.first().text().trim();
        var secondTeam = teams.last().text().trim();

        var metadata = element.select("td.meta").text();
        var tournament = StringUtils.substringAfterLast(metadata, "/").trim();
        var sportType = StringUtils.substringBefore(metadata, "/").trim();

        var link = element.getElementsByTag("a").first().attr("href");

        return new SportEvent(startTime, firstTeam, secondTeam, tournament, sportType, link);
    }
}
