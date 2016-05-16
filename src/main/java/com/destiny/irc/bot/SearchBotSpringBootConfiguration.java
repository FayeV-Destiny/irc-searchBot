package com.destiny.irc.bot;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;

/**
 * Created by eric.tournier on 22/04/2016.
 */
@EnableAutoConfiguration
@Import({
        SearchBotConfiguration.class
})
public class SearchBotSpringBootConfiguration {
}
