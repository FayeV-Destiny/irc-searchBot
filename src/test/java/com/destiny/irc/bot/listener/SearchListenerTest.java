package com.destiny.irc.bot.listener;

import com.destiny.irc.bot.SearchBotSpringBootConfiguration;
import com.destiny.irc.bot.SearchListenerConfiguration;
import org.junit.Test;
import org.mockito.Mockito;
import org.pircbotx.hooks.events.MessageEvent;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import javax.inject.Inject;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created by eric.tournier on 20/04/2016.
 */
@SpringApplicationConfiguration(classes = {
        SearchListenerConfiguration.class
})
public class SearchListenerTest extends AbstractJUnit4SpringContextTests {
    @Inject
    private SearchListener listener;

    @Test
    public void whenReceivingHelloMessageShouldRespondHelloWorld() throws Exception {
        MessageEvent eventMsg = mock(MessageEvent.class);
        when(eventMsg.getMessage()).thenReturn("?hello");

        this.listener.onMessage(eventMsg);

        Mockito.verify(eventMsg, atLeastOnce()).respond("Hello member of the DESTiNY team !");
    }

    @Test
    public void whenReceivingSearchMessageForNextScheduldedEpisodeShouldRespondWithDateHourAndChannel() throws Exception {
        MessageEvent eventMsg = mock(MessageEvent.class);
        when(eventMsg.getMessage()).thenReturn("?tv next ESPRITS Criminels");

        this.listener.onMessage(eventMsg);

        Mockito.verify(eventMsg, atLeastOnce()).respondChannel("Esprits criminels S09E10 : 17/04/2032 a 00:45 sur RTS Un");
    }
}
