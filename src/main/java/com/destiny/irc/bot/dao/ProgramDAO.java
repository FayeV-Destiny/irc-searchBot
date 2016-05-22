package com.destiny.irc.bot.dao;

import com.destiny.irc.bot.utils.SearchUtils;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Created by Maman et Papa on 25/04/2016.
 */
public class ProgramDAO {
    private static final String XPATH_EXACT_TITLE_PROGRAM_BEGIN = "/tv/programme[title=\"";
    private static final String XPATH_EXACT_TITLE_PROGRAM_END = "\"]";
    private static final String XPATH_STARTS_WITH_TITLE_PROGRAM_BEGIN = "/tv/programme[starts-with(title,\"";
    private static final String XPATH_STARTS_WITH_TITLE_PROGRAM_END = "\")]";


    public static final DateTimeFormatter tvGuideDateTimeFmt = DateTimeFormat.forPattern("YYYYMMddHHmmss Z");
    private final File xmlGuideNameFile;

    public ProgramDAO(File xmlGuideNameFile) {
        this.xmlGuideNameFile = xmlGuideNameFile;
    }

    protected Document loadXmlDocument() throws ParserConfigurationException, IOException, SAXException {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();

        InputStream fIS = new FileInputStream(xmlGuideNameFile);

        return builder.parse(fIS);
    }

    public List<Node> findAllProgramsByName(String title)
            throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
        return this.findAllProgramsByName(this.findStandardTitleFrom(title), this.loadXmlDocument());
    }

    protected List<Node> findAllProgramsByName(String title, Document document)
            throws XPathExpressionException, IOException {
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        String stdTitle;
        XPathExpression expr;
        if (title.endsWith("*")) {
            String titleWithoutWildcard = title.substring(0, title.indexOf('*'));
            expr = xpath.compile(startWith(titleWithoutWildcard));
        } else {
            stdTitle = this.findStandardTitleFrom(title);
            expr = xpath.compile(containsExactly(stdTitle));
        }

        NodeList nodeList = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
        List<Node> listNodes = SearchUtils.toListOfNodes(nodeList);

        return this.filterPastPrograms(listNodes);
    }

    private String containsExactly(String stdTitle) {
        return XPATH_EXACT_TITLE_PROGRAM_BEGIN + stdTitle + XPATH_EXACT_TITLE_PROGRAM_END;
    }

    private String startWith(String stdTitle) {
        return XPATH_STARTS_WITH_TITLE_PROGRAM_BEGIN + stdTitle + XPATH_STARTS_WITH_TITLE_PROGRAM_END;
    }

    protected List<Node> filterPastPrograms(List<Node> programs) {
        List<Node> nodes = programs.stream()
                .filter(node -> this.getStartDateFromProgram((Element) node).isAfterNow())
                .collect(Collectors.toList());

        return nodes;
    }

    public Element findNextScheduldedProgramsByName(String title)
            throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
        return this.findNextScheduldedProgramsByName(this.findStandardTitleFrom(title), this.loadXmlDocument());
    }

    protected Element findNextScheduldedProgramsByName(String title, Document document)
            throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
        List<Node> allPrograms = this.findAllProgramsByName(title, document);
        DateTime startDate = null;
        Element foundProgram = null;

        for (Node program : allPrograms) {
            if (program.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) program;
                DateTime start = this.getStartDateFromProgram(eElement);
                if (start.isAfterNow()) {
                    if ((startDate == null)
                            || (start.isBefore(startDate))) {
                        foundProgram = eElement;
                        startDate = start;
                    }
                }
            }
        }

        return foundProgram;
    }

    protected DateTime getStartDateFromProgram(Element eElement) {
        String date = eElement.getAttribute("start");
        return tvGuideDateTimeFmt.parseDateTime(date);
    }

    protected String findStandardTitleFrom(String messagePart) throws IOException {
        Properties props = new Properties();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("/titles.properties");
        props.load(resourceAsStream);
        String normalizedTitle = SearchUtils.removeAccentsAndUpperCaseChars(messagePart);
        String standardTitle = props.getProperty(normalizedTitle);
        if (standardTitle == null) {
            standardTitle = messagePart;
        }
        return standardTitle;
    }

    public String formatEpisodeNumber(String episodeNumber) {
        String formattedEpisodeNumber = episodeNumber;
        String[] split = StringUtils.split(episodeNumber, '.');
        if (split.length > 1) {
            if (split[1].length() == 1) {
                split[1] = "0" + split[1];
            }
            formattedEpisodeNumber = "S" + split[0] + "E" + split[1];
        }

        return formattedEpisodeNumber;
    }
}
