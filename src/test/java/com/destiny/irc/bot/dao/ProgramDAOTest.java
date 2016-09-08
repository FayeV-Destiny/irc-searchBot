package com.destiny.irc.bot.dao;

import com.destiny.irc.bot.DaoConfiguration;
import com.destiny.irc.bot.response.IrcResponseLine;
import com.destiny.irc.bot.response.IrcResponses;
import com.destiny.irc.bot.utils.SearchUtils;
import com.omertron.thetvdbapi.TheTVDBApi;
import com.omertron.thetvdbapi.model.Series;
import net.sf.saxon.s9api.*;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.w3c.dom.Document;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.destiny.irc.bot.dao.ProgramDAO.tvGuideDateTimeFmt;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by Maman et Papa on 25/04/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {
        DaoConfiguration.class
})
public class ProgramDAOTest /*extends AbstractJUnit4SpringContextTests*/ {
    private static DocumentBuilder builder;
    @Inject
    protected ProgramDAO dao;

    @Test
    public void iCanLoadWebgrabPlusXmlFile() throws Exception {
        Document document = this.dao.loadXmlDocument();
        assertThat(document, is(notNullValue()));
    }

    @Test
    public void searchingForProgramTitlesIsCaseUnsensitive() throws Exception {
        XdmNode programsByName = this.dao.findNextScheduldedProgramsByName("Esprits Criminels");
        assertThat(programsByName, is(notNullValue()));
        programsByName = this.dao.findNextScheduldedProgramsByName("esprits criminels");
        assertThat(programsByName, is(notNullValue()));
        programsByName = this.dao.findNextScheduldedProgramsByName("esPrits crIMinels");
        assertThat(programsByName, is(notNullValue()));
    }

    @Test
    public void iCanSearchWithFrenchOrOriginalTitle() throws Exception {
        XdmNode programsByName = this.dao.findNextScheduldedProgramsByName("Esprits Criminels");
        assertThat(programsByName, is(notNullValue()));
        programsByName = this.dao.findNextScheduldedProgramsByName("CRIMINAL Minds");
        assertThat(programsByName, is(notNullValue()));
    }

    @Test
    public void iCanRetrieveStandardProgramTitle() throws Exception {
        String standardTitle = this.dao.findStandardTitleFrom("Esprits Criminels");
        assertThat(standardTitle, is(notNullValue()));
        assertThat(standardTitle, is(equalTo("Esprits criminels")));

        standardTitle = this.dao.findStandardTitleFrom("esPrits crIMinels");
        assertThat(standardTitle, is(notNullValue()));
        assertThat(standardTitle, is(equalTo("Esprits criminels")));

        standardTitle = this.dao.findStandardTitleFrom("CRIMINAL Minds");
        assertThat(standardTitle, is(notNullValue()));
        assertThat(standardTitle, is(equalTo("Esprits criminels")));

        standardTitle = this.dao.findStandardTitleFrom("ncis");
        assertThat(standardTitle, is(notNullValue()));
        assertThat(standardTitle, is(equalTo("NCIS")));

        standardTitle = this.dao.findStandardTitleFrom("ncis : enquetes spéciales");
        assertThat(standardTitle, is(notNullValue()));
        assertThat(standardTitle, is(equalTo("NCIS")));
    }

    @Test
    public void whenFindingNextScheduldedProgram_shouldBeInTheFuture() throws Exception {
        InputStream resourceAsStream = this.getClass().getResourceAsStream("/multipleProgramsWithDifferentStartDates.xml");
        Document document = builder.parse(resourceAsStream);

        XdmNode nextScheduldedProgramsByName = this.dao.findNextScheduldedProgramsByName("NCIS : enquêtes spéciales", document);
        assertThat(nextScheduldedProgramsByName, is(notNullValue()));
        IrcResponseLine line = new IrcResponseLine(nextScheduldedProgramsByName);
        DateTime april27thOf2027 = tvGuideDateTimeFmt.parseDateTime("20270421211500 +0200");
        assertThat(line.getDateTime(), is(equalTo(april27thOf2027)));
    }

    @Test
    public void allFoundSchelduldedProgramsMustBeInTheFuture() throws Exception {
        InputStream resourceAsStream = this.getClass().getResourceAsStream("/multipleProgramsWithDifferentStartDates.xml");
        Document document = builder.parse(resourceAsStream);

        List<XdmNode> allProgramsByName = this.dao.findAllProgramsByName("NCIS", document);
        assertThat(allProgramsByName, is(Matchers.notNullValue()));
        assertThat(allProgramsByName.size(), is(greaterThan(0)));

        IrcResponses responses = new IrcResponses(allProgramsByName);
        IrcResponseLine responseLine = responses.get(0);
        DateTime april27thOf2027 = tvGuideDateTimeFmt.parseDateTime("20270421211500 +0200");
        assertThat(responseLine.getDateTime(), is(equalTo(april27thOf2027)));
    }

    @Test
    public void ifICannotRetrieveStandardTitleProvidedOneIsReturned() throws Exception {
        String standardTitle = this.dao.findStandardTitleFrom("Toto");
        assertThat(standardTitle, is(notNullValue()));
        assertThat(standardTitle, is(equalTo("Toto")));
    }

    @Test
    public void episodeNumberIsAlwaysFormattedAsSxxExx() throws Exception {
        String episodeNumber = this.dao.formatEpisodeNumber("S09E11");
        assertThat(episodeNumber, is(equalTo("S09E11")));
        episodeNumber = this.dao.formatEpisodeNumber("11.5.");
        assertThat(episodeNumber, is(equalTo("S11E05")));
    }

    @Test
    public void iCanUseWilcardsToFindPrograms() throws Exception {
        InputStream resourceAsStream = this.getClass().getResourceAsStream("/multipleProgramsBeginningWithSameTitle.xml");
        Document document = builder.parse(resourceAsStream);

        List<XdmNode> allProgramsByName = this.dao.findAllProgramsByName("NCIS*", document);
        assertThat(allProgramsByName, is(notNullValue()));
        assertThat(allProgramsByName.size(), is(equalTo(6)));

        allProgramsByName = this.dao.findAllProgramsByName("ncIS*", document);
        assertThat(allProgramsByName, is(notNullValue()));
        assertThat(allProgramsByName.size(), is(equalTo(6)));

        allProgramsByName = this.dao.findAllProgramsByName("ncis*", document);
        assertThat(allProgramsByName, is(notNullValue()));
        assertThat(allProgramsByName.size(), is(equalTo(6)));
    }

    @Test
    public void iCanUseWildcardsToFindMultiplesProgramsBegiiningBySameSequence() throws Exception {
        InputStream resourceAsStream = this.getClass().getResourceAsStream("/multipleProgramsWithDifferentTitles.xml");
        Document document = builder.parse(resourceAsStream);

        List<XdmNode> allProgramsByName = this.dao.findAllProgramsByName("NC*", document);
        assertThat(allProgramsByName, is(notNullValue()));
    }

    @Test
    public void test() throws Exception {
        Processor proc = new Processor(false);
        XPathCompiler xpath = proc.newXPathCompiler();
        xpath.declareNamespace("saxon", "http://saxon.sf.net/"); // not actually used, just for demonstration

        net.sf.saxon.s9api.DocumentBuilder builder = proc.newDocumentBuilder();
        builder.setLineNumbering(true);
        builder.setWhitespaceStrippingPolicy(WhitespaceStrippingPolicy.ALL);
        XdmNode booksDoc = builder.build(new File("src/test/resources/multipleProgramsWithDifferentTitles.xml"));

        // find all the ITEM elements, and for each one display the TITLE child

        XPathSelector selector = xpath.compile("/tv/programme[starts-with(lower-case(title), lower-case(\"NCIS\"))]").load();
        selector.setContextItem(booksDoc);
        QName titleName = new QName("title");
        QName attribute = new QName("start");
        for (XdmItem xdmItem : selector) {
            if (!(xdmItem.isAtomicValue())) {
                XdmNode xdmNode = (XdmNode) xdmItem;
                String attributeValue = xdmNode.getAttributeValue(attribute);
                XdmNode title = getChild(xdmNode, titleName);
                System.out.println(title.getNodeName() +
                        "(" + title.getLineNumber() + "): " +
                        title.getStringValue() +
                        " - Start at :" + attributeValue);
            }
        }

        List<XdmNode> xdmNodes = SearchUtils.toListOfNodes(selector);
        Map<String, List<XdmNode>> nameListMap = xdmNodes.stream()
                .collect(
                        Collectors.groupingBy(node -> getChild(node, titleName).getStringValue())
                );

        System.out.println(nameListMap);
    }

    @BeforeClass
    public static void setUpClassAttributes() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();
    }

    // Helper method to get the first child of an element having a given name.
    // If there is no child with the given name it returns null

    private static XdmNode getChild(XdmNode parent, QName childName) {
        XdmSequenceIterator iter = parent.axisIterator(Axis.CHILD, childName);
        if (iter.hasNext()) {
            return (XdmNode) iter.next();
        } else {
            return null;
        }
    }

    @Test
    public void wiki() throws Exception {
//        long debut = System.currentTimeMillis();
//        String url = "https://fr.wikipedia.org/wiki/Liste_de_titres_québécois_de_séries_télévisées";
//
//        org.jsoup.nodes.Document doc = Jsoup.connect(url)
//                .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:46.0) Gecko/20100101 Firefox/46.0")
//                .get();
//        Elements table = doc.select(".wikitable");
//        Elements trs = doc.select("table.wikitable tr");
//        long fin = System.currentTimeMillis();
//        System.out.println(fin - debut);
//        //remove header row
//        trs.remove(0);
//        System.out.println(trs.size());
//
//        Element element = trs.get(0);
//        Elements tds = element.getElementsByTag("td");
//        Element td = tds.first();
//        System.out.println(td.text());

        TheTVDBApi tvDB = new TheTVDBApi("F13D64DC1C64C9CC");
        List<Series> results = tvDB.searchSeries("Le bureau legendes", "fr");
        for (Series result : results) {
            System.out.println(result.getSeriesName());
        }
    }
}
