package test.task;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.jsoup.nodes.Document;
import test.task.callable.CollectorCallable;
import test.task.model.SportEvent;
import test.task.scraper.BetbonanzaHtmlScraper;
import test.task.scraper.HtmlScraper;
import test.task.util.Constants;
import test.task.util.PageUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Main {
    private static final String PATH_TO_TEMPLATE = "src/main/resources/template/pageTemplage.vm";
    private static final String BONANZA_URL = "https://lite.betbonanza.com/sports";
    private static final String PATH_TO_PAGES = "src/main/resources/pages";
    private static final String HTML_EXTENSION = ".html";

    public static void main(String[] args) throws IOException {
        var htmlPage = PageUtils.getHtmlPage(BONANZA_URL);
        var scraper = new BetbonanzaHtmlScraper();
        var events = collectEvents(htmlPage, scraper);
        var renderedPage = getRenderedPage(events, PATH_TO_TEMPLATE);
        saveHtml(renderedPage);
    }

    private static Set<SportEvent> collectEvents(Document sportsPage, HtmlScraper scraper){
        var executorService = Executors.newFixedThreadPool(10);
        var futures = startCollectors(sportsPage, scraper, executorService);

        Set<SportEvent> sportEvents = collectEventsFromFutures(futures);

        executorService.shutdown();

        return sportEvents;
    }

    private static List<Future<Set<SportEvent>>> startCollectors(Document sportsPage, HtmlScraper scraper, ExecutorService executorService){
        var futures = new ArrayList<Future<Set<SportEvent>>>();
        for(var sport : sportsPage.select(Constants.NEXT_LINK_QUERY)){
            if(sport.childNodes().size() > 1){
                var sportUrl = Constants.GET_URL.apply(sport);

                //Runnable also can be used, but we need to use synchonazed collection in all threads.
                var eventsCollector = new CollectorCallable(sportUrl, scraper);
                var futureSportEvent = executorService.submit(eventsCollector);
                futures.add(futureSportEvent);
            }
        }
        return futures;
    }

    private static Set<SportEvent> collectEventsFromFutures(List<Future<Set<SportEvent>>> futures){
        Set<SportEvent> sportEvents = ConcurrentHashMap.newKeySet();
        futures.parallelStream().forEach(setFuture -> {
            try {
                sportEvents.addAll(setFuture.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
        return sportEvents;
    }

    private static String getRenderedPage(Set<SportEvent> events, String pathToTemplate) {
        var velocityEngine = new VelocityEngine();
        velocityEngine.init();

        var context = new VelocityContext();
        context.put("events", events.stream().sorted().collect(Collectors.toList()));

        var stringWriter = new StringWriter();
        var pageTemplate = velocityEngine.getTemplate(pathToTemplate);
        pageTemplate.merge(context, stringWriter);

        return stringWriter.toString();
    }

    private static void saveHtml(String renderedPage) throws IOException {
        var pathToDir = Paths.get(PATH_TO_PAGES, LocalDate.now().toString());
        pathToDir.toFile().mkdirs();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH-mm-ss");
        var file = Files.createFile(
                        Paths.get(
                                pathToDir.toAbsolutePath().toString()
                                , LocalTime.now().format(dateTimeFormatter) + HTML_EXTENSION))
                .toFile();

        try (var fileWriter = new FileWriter(file);
             var bufferedWriter = new BufferedWriter(fileWriter)) {
             bufferedWriter.write(renderedPage);
             bufferedWriter.flush();
        }
    }
}