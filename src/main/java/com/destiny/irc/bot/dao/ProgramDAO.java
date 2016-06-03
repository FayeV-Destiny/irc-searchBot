package com.destiny.irc.bot.dao;

import com.destiny.irc.bot.utils.SearchUtils;
import net.sf.saxon.s9api.*;
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
import javax.xml.transform.dom.DOMSource;
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
    private static final String XPATH_STARTS_WITH_TITLE_PROGRAM_BEGIN = "/tv/programme[starts-with(lower-case(title),lower-case(\"";
    private static final String XPATH_STARTS_WITH_TITLE_PROGRAM_END = "\"))]";


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

    public List<XdmNode> findAllProgramsByName(String title)
            throws IOException, SAXException, ParserConfigurationException, XPathExpressionException, SaxonApiException {
        return this.findAllProgramsByName(this.findStandardTitleFrom(title), this.loadXmlDocument());
    }

    protected List<XdmNode> findAllProgramsByName(String title, Document document)
            throws XPathExpressionException, IOException, SaxonApiException {
//        XPathFactory xPathfactory = XPathFactory.newInstance();
//        XPath xpath = xPathfactory.newXPath();
        String stdTitle;
//        XPathExpression expr;
//        if (title.endsWith("*")) {
//            String titleWithoutWildcard = title.substring(0, title.indexOf('*'));
//            expr = xpath.compile(startWith(titleWithoutWildcard));
//        } else {
//            stdTitle = this.findStandardTitleFrom(title);
//            expr = xpath.compile(containsExactly(stdTitle));
//        }
//
//        NodeList nodeList = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
//        List<Node> listNodes = SearchUtils.toListOfNodes(nodeList);

        Processor proc = new Processor(false);
        XPathCompiler xPathCompiler = proc.newXPathCompiler();

        net.sf.saxon.s9api.DocumentBuilder builder = proc.newDocumentBuilder();
        builder.setLineNumbering(true);
        builder.setWhitespaceStrippingPolicy(WhitespaceStrippingPolicy.ALL);
        XdmNode booksDoc = builder.build( new DOMSource(document) );

        XPathSelector selector;
        if (title.endsWith("*")) {
            String titleWithoutWildcard = title.substring(0, title.indexOf('*'));
            selector = xPathCompiler.compile(startWith(titleWithoutWildcard)).load();
        } else {
            stdTitle = this.findStandardTitleFrom(title);
            selector = xPathCompiler.compile(containsExactly(stdTitle)).load();
        }
        selector.setContextItem(booksDoc);
        List<XdmNode> listXdmNodes = SearchUtils.toListOfNodes(selector);

        return this.filterPastProgramsWithSaxon(listXdmNodes);
    }

    private String containsExactly(String stdTitle) {
        return XPATH_EXACT_TITLE_PROGRAM_BEGIN + stdTitle + XPATH_EXACT_TITLE_PROGRAM_END;
    }

    private String startWith(String stdTitle) {
        return XPATH_STARTS_WITH_TITLE_PROGRAM_BEGIN + stdTitle + XPATH_STARTS_WITH_TITLE_PROGRAM_END;
    }

//    protected List<Node> filterPastPrograms(List<Node> programs) {
//        List<Node> nodes = programs.stream()
//                .filter(node -> this.getStartDateFromProgram((Element) node).isAfterNow())
//                .collect(Collectors.toList());
//        return nodes;
//    }

    protected List<XdmNode> filterPastProgramsWithSaxon(List<XdmNode> programs) {
        List<XdmNode> nodes = programs.stream()
                .filter(node -> this.getStartDateFromProgram( node ).isAfterNow())
                .collect(Collectors.toList());

        return nodes;
    }

    public XdmNode findNextScheduldedProgramsByName(String title)
            throws ParserConfigurationException, IOException, SAXException, XPathExpressionException, SaxonApiException {
        return this.findNextScheduldedProgramsByName(this.findStandardTitleFrom(title), this.loadXmlDocument());
    }

    protected XdmNode findNextScheduldedProgramsByName(String title, Document document)
            throws ParserConfigurationException, IOException, SAXException, XPathExpressionException, SaxonApiException {
        List<XdmNode> allPrograms = this.findAllProgramsByName(title, document);
        DateTime startDate = null;
        XdmNode foundProgram = null;

        for (XdmNode program : allPrograms) {
            DateTime start = this.getStartDateFromProgram(program);
                if (start.isAfterNow()) {
                    if ((startDate == null)
                            || (start.isBefore(startDate))) {
                        foundProgram = program;
                        startDate = start;
                    }
                }
        }

//        for (Node program : allPrograms) {
//            if (program.getNodeType() == Node.ELEMENT_NODE) {
//                Element eElement = (Element) program;
//                DateTime start = this.getStartDateFromProgram(eElement);
//                if (start.isAfterNow()) {
//                    if ((startDate == null)
//                            || (start.isBefore(startDate))) {
//                        foundProgram = eElement;
//                        startDate = start;
//                    }
//                }
//            }
//        }

        return foundProgram;
    }

//    protected DateTime getStartDateFromProgram(Element eElement) {
//        String date = eElement.getAttribute("start");
//        return tvGuideDateTimeFmt.parseDateTime(date);
//    }

    protected DateTime getStartDateFromProgram(XdmNode xdmNode) {
        QName attribute = new QName("start");
        String date = xdmNode.getAttributeValue(attribute);
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
