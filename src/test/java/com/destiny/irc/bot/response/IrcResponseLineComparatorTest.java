package com.destiny.irc.bot.response;

import org.junit.Test;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * Created by eric.tournier on 28/04/2016.
 */
@SpringApplicationConfiguration(classes = {
        IrcResponseLineTestConfiguration.class
})
public class IrcResponseLineComparatorTest  extends AbstractJUnit4SpringContextTests {
    @Test
    public void ircResponseLinesCanBeSortedByName() throws Exception {


    }
}
