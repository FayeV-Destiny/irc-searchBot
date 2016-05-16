package com.destiny.irc.bot.dao;

import com.destiny.irc.bot.DaoConfiguration;
import com.destiny.irc.bot.response.IrcResponseLine;
import net.sf.saxon.lib.NamespaceConstant;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.internal.matchers.GreaterThan;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.text.Normalizer;

import static com.destiny.irc.bot.dao.ProgramDAO.tvGuideDateTimeFmt;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by Maman et Papa on 25/04/2016.
 */
@SpringApplicationConfiguration(classes = {
        DaoConfiguration.class
})
public class ProgramDAOTest extends AbstractJUnit4SpringContextTests {
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
        Element programsByName = this.dao.findNextScheduldedProgramsByName("Esprits Criminels");
        assertThat(programsByName, is(notNullValue()));
        programsByName = this.dao.findNextScheduldedProgramsByName("esprits criminels");
        assertThat(programsByName, is(notNullValue()));
        programsByName = this.dao.findNextScheduldedProgramsByName("esPrits crIMinels");
        assertThat(programsByName, is(notNullValue()));
    }

    @Test
    public void iCanSearchWithFrenchOrOriginalTitle() throws Exception {
        Element programsByName = this.dao.findNextScheduldedProgramsByName("Esprits Criminels");
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

        Element nextScheduldedProgramsByName = this.dao.findNextScheduldedProgramsByName("NCIS : enquêtes spéciales", document);
        assertThat(nextScheduldedProgramsByName, is(notNullValue()));
        IrcResponseLine line = new IrcResponseLine(nextScheduldedProgramsByName);
        DateTime april27thOf2028 = tvGuideDateTimeFmt.parseDateTime("20270421211500 +0200");
        assertThat(line.getDateTime(), is(equalTo( april27thOf2028 )));
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

        NodeList allProgramsByName = this.dao.findAllProgramsByName("NCIS*", document);
        assertThat(allProgramsByName, is(notNullValue()));
        assertThat(allProgramsByName.getLength(), is(equalTo(6)));
    }

    @Test
    public void name() throws Exception {
        System.setProperty("javax.xml.xpath.XPathFactory:"+ NamespaceConstant.OBJECT_MODEL_SAXON, "net.sf.saxon.xpath.XPathFactoryImpl");
        XPathFactory xpf = XPathFactory.newInstance(NamespaceConstant.OBJECT_MODEL_SAXON);

        InputStream resourceAsStream = this.getClass().getResourceAsStream("/multipleProgramsBeginningWithSameTitle.xml");
        Document document = builder.parse(resourceAsStream);
        XPath xpath = xpf.newXPath();
        XPathExpression xPathExpression = xpath.compile("/tv/programme[lower-case(title)=\"ncis\"]");

        NodeList nodeList = (NodeList) xPathExpression.evaluate(document, XPathConstants.NODESET);
        assertThat(nodeList, is(notNullValue()));
        assertThat(nodeList.getLength(), is(greaterThan(0)));
    }

    @BeforeClass
    public static void setUpClassAttributes() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();
    }
}