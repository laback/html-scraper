package test.task;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import test.task.exception.ConnectionException;
import test.task.model.SportEvent;
import test.task.scraper.BetbonanzaHtmlScraper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Main {
    private static final String PATH_TO_TEMPLATE = "src/main/resources/template/pageTemplage.vm";
    private static final String BONANZA_URL = "https://lite.betbonanza.com";
    private static final String PATH_TO_PAGES = "src/main/resources/pages";
    private static final String HTML_EXTENSION = ".html";

    public static void main(String[] args) throws IOException {
        var htmlPage = getHtmlPage(BONANZA_URL);
        var events = new BetbonanzaHtmlScraper().parse(htmlPage);
        var renderedPage = getRenderedPage(events, PATH_TO_TEMPLATE);
        saveHtml(renderedPage);
    }

    private static Document getHtmlPage(String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new ConnectionException(String.format("Can't get data from url %s. Reason: %s", url, e.getMessage()));
        }
    }

    private static String getRenderedPage(List<SportEvent> events, String pathToTemplate) {
        var velocityEngine = new VelocityEngine();
        velocityEngine.init();

        var context = new VelocityContext();
        context.put("events", events);

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