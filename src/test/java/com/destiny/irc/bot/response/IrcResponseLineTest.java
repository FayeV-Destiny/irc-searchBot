package com.destiny.irc.bot.response;

import net.sf.saxon.s9api.XdmNode;
import org.hamcrest.core.CombinableMatcher;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.w3c.dom.Element;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.CombinableMatcher.both;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;

/**
 * Created by eric.tournier on 28/04/2016.
 */
@SpringApplicationConfiguration(classes = {
        IrcResponseLineTestConfiguration.class
})
public class IrcResponseLineTest extends AbstractJUnit4SpringContextTests {
    @Inject
    XdmNode elementWithEpisodeNb;

    @Inject
    XdmNode elementWithoutEpisodeNb;

    @Resource
    List<IrcResponseLine> ircResponseLinesWithSameTitles;

    @Resource
    List<IrcResponseLine> ircResponseLinesWithDifferentTitles;

    @Test
    public void formattedLineFromElementWithEpisodeNb() throws Exception {
        IrcResponseLine ircResponseLine = new IrcResponseLine(elementWithEpisodeNb);
        assertThat(ircResponseLine.getFormattedLine(),
                is(equalTo("Silicon Valley S02E07 : 29/04/2016 a 23:15 sur Be Séries")));
    }

    @Test
    public void formattedLineFromElementWithoutEpisodeNb() throws Exception {
        IrcResponseLine ircResponseLine = new IrcResponseLine(elementWithoutEpisodeNb);
        assertThat(ircResponseLine.getFormattedLine(),
                is(equalTo("JT 13h : 19/04/2016 a 12:58 sur La Une")));
    }

    @Test
    public void programNodesWithSameTitlesCanBeSorted() throws Exception {
        assertThat(this.ircResponseLinesWithSameTitles.get(0).getFormattedLine(),
                both(containsString("NCIS : enquêtes spéciales")).and(containsString("S13E02")));
        Collections.sort(this.ircResponseLinesWithSameTitles, new IrcResponseLineComparator());
        assertThat(this.ircResponseLinesWithSameTitles.get(0).getFormattedLine(),
                both(containsString("NCIS : enquêtes spéciales")).and(containsString("S13E01")));;
    }

    @Test
    public void programNodesWithDifferentTitlesCanBeSorted() throws Exception {
        assertThat(this.ircResponseLinesWithDifferentTitles.get(0).getFormattedLine(),
                both(containsString("NCIS : Nouvelle-Orléans")).and(containsString("S02E10")));
        Collections.sort(this.ircResponseLinesWithDifferentTitles, new IrcResponseLineComparator());
        assertThat(this.ircResponseLinesWithDifferentTitles.get(0).getFormattedLine(),
                both(containsString("Esprits criminels")).and(containsString("S04E11")));
    }
}
