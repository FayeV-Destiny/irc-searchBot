package com.destiny.irc.bot.response;

import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by eric.tournier on 28/04/2016.
 */
@Configuration
public class IrcResponseLineTestConfiguration {
    @Bean
    public Element elementWithEpisodeNb() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        InputStream resourceAsStream = this.getClass().getResourceAsStream("/programWithEpisodeNb.xml");
        Document document = builder.parse(resourceAsStream);

        return (Element) document.getElementsByTagName("programme").item(0);
    }

    @Bean
    public Element elementWithoutEpisodeNb() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        InputStream resourceAsStream = this.getClass().getResourceAsStream("/programWithoutEpisodeNb.xml");
        Document document = builder.parse(resourceAsStream);

        return (Element) document.getElementsByTagName("programme").item(0);
    }

    @Bean
    public List<IrcResponseLine> ircResponseLinesWithSameTitles() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        InputStream resourceAsStream = this.getClass().getResourceAsStream("/multipleProgramsWithSameTitle.xml");
        Document document = builder.parse(resourceAsStream);

        return new IrcResponses(document.getElementsByTagName("programme"));
    }

    @Bean
    public List<IrcResponseLine> ircResponseLinesWithDifferentTitles() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        InputStream resourceAsStream = this.getClass().getResourceAsStream("/multipleProgramsWithDifferentTitles.xml");
        Document document = builder.parse(resourceAsStream);

        return new IrcResponses(document.getElementsByTagName("programme"));
    }
}
