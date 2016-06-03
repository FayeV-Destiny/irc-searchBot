package com.destiny.irc.bot.response;

import com.destiny.irc.bot.utils.SearchUtils;
import com.google.common.collect.Lists;
import net.sf.saxon.s9api.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by eric.tournier on 28/04/2016.
 */
@Configuration
public class IrcResponseLineTestConfiguration {
    @Bean
    public XdmNode elementWithEpisodeNb() throws SaxonApiException {
        Processor proc = new Processor(false);
        XPathCompiler xpath = proc.newXPathCompiler();
        xpath.declareNamespace("saxon", "http://saxon.sf.net/"); // not actually used, just for demonstration
        net.sf.saxon.s9api.DocumentBuilder builder = proc.newDocumentBuilder();
        builder.setLineNumbering(true);
        builder.setWhitespaceStrippingPolicy(WhitespaceStrippingPolicy.ALL);

        XdmNode programWithEpisodeNb = builder.build(new File("src/test/resources/programWithEpisodeNb.xml"));
        XPathSelector selector = xpath.compile("//programme").load();
        selector.setContextItem(programWithEpisodeNb);
        List<XdmNode> listXdmNodes = SearchUtils.toListOfNodes(selector);

        return listXdmNodes.get(0);

//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder builder = factory.newDocumentBuilder();
//
//        InputStream resourceAsStream = this.getClass().getResourceAsStream("/programWithEpisodeNb.xml");
//        Document document = builder.parse(resourceAsStream);
//
//        return (Element) document.getElementsByTagName("programme").item(0);
    }

    @Bean
    public XdmNode elementWithoutEpisodeNb() throws SaxonApiException {
        Processor proc = new Processor(false);
        XPathCompiler xpath = proc.newXPathCompiler();
        xpath.declareNamespace("saxon", "http://saxon.sf.net/"); // not actually used, just for demonstration
        net.sf.saxon.s9api.DocumentBuilder builder = proc.newDocumentBuilder();
        builder.setLineNumbering(true);
        builder.setWhitespaceStrippingPolicy(WhitespaceStrippingPolicy.ALL);

        XdmNode programWithoutEpisodeNb = builder.build(new File("src/test/resources/programWithoutEpisodeNb.xml"));
        XPathSelector selector = xpath.compile("//programme").load();
        selector.setContextItem(programWithoutEpisodeNb);
        List<XdmNode> listXdmNodes = SearchUtils.toListOfNodes(selector);

        return listXdmNodes.get(0);

//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder builder = factory.newDocumentBuilder();
//
//        InputStream resourceAsStream = this.getClass().getResourceAsStream("/programWithoutEpisodeNb.xml");
//        Document document = builder.parse(resourceAsStream);
//
//        return (Element) document.getElementsByTagName("programme").item(0);
    }

    @Bean
    public List<IrcResponseLine> ircResponseLinesWithSameTitles() throws SaxonApiException {
        Processor proc = new Processor(false);
        XPathCompiler xpath = proc.newXPathCompiler();
        xpath.declareNamespace("saxon", "http://saxon.sf.net/"); // not actually used, just for demonstration

        net.sf.saxon.s9api.DocumentBuilder builder = proc.newDocumentBuilder();
        builder.setLineNumbering(true);
        builder.setWhitespaceStrippingPolicy(WhitespaceStrippingPolicy.ALL);
        XdmNode multipleProgramsWithSameTitle = builder.build(new File("src/test/resources/multipleProgramsWithSameTitle.xml"));
        XPathSelector selector = xpath.compile("//programme").load();
        selector.setContextItem(multipleProgramsWithSameTitle);
        List<XdmNode> listXdmNodes = SearchUtils.toListOfNodes(selector);
//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder builder = factory.newDocumentBuilder();
//
//        InputStream resourceAsStream = this.getClass().getResourceAsStream("/multipleProgramsWithSameTitle.xml");
//        Document document = builder.parse(resourceAsStream);
//
//        NodeList elementsByTagName = document.getElementsByTagName("programme");
//        List<Node> listOfNodes = SearchUtils.toListOfNodes(elementsByTagName);

        return new IrcResponses(listXdmNodes);
    }

    @Bean
    public List<IrcResponseLine> ircResponseLinesWithDifferentTitles() throws SaxonApiException {
        Processor proc = new Processor(false);
        XPathCompiler xpath = proc.newXPathCompiler();
        xpath.declareNamespace("saxon", "http://saxon.sf.net/"); // not actually used, just for demonstration

        net.sf.saxon.s9api.DocumentBuilder builder = proc.newDocumentBuilder();
        builder.setLineNumbering(true);
        builder.setWhitespaceStrippingPolicy(WhitespaceStrippingPolicy.ALL);
        XdmNode multipleProgramsWithDifferentTitles = builder.build(new File("src/test/resources/multipleProgramsWithDifferentTitles.xml"));
        XPathSelector selector = xpath.compile("//programme").load();
        selector.setContextItem(multipleProgramsWithDifferentTitles);
        List<XdmNode> listXdmNodes = SearchUtils.toListOfNodes(selector);

//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder builder = factory.newDocumentBuilder();
//
//        InputStream resourceAsStream = this.getClass().getResourceAsStream("/multipleProgramsWithDifferentTitles.xml");
//        Document document = builder.parse(resourceAsStream);
//
//        NodeList elementsByTagName = document.getElementsByTagName("programme");
//        List<Node> listOfNodes = SearchUtils.toListOfNodes(elementsByTagName);

        return new IrcResponses( listXdmNodes );
    }

    private static XdmNode getChild(XdmNode parent, QName childName) {
        XdmSequenceIterator iter = parent.axisIterator(Axis.CHILD, childName);
        if (iter.hasNext()) {
            return (XdmNode) iter.next();
        } else {
            return null;
        }
    }
}
