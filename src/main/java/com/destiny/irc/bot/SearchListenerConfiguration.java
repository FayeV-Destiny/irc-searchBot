package com.destiny.irc.bot;

import com.destiny.irc.bot.listener.SearchListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by Maman et Papa on 01/05/2016.
 */
@Configuration
@Import({
        DaoConfiguration.class
})
public class SearchListenerConfiguration {
    @Bean
    public SearchListener listener() {
        return new SearchListener();
    }
}
