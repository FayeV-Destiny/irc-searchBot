package com.destiny.irc.bot;

import com.destiny.irc.bot.configuration.SearchBotConfigurationSettings;
import com.destiny.irc.bot.listener.SearchListener;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.nio.charset.Charset;

/**
 * Created by eric.tournier on 22/04/2016.
 */
@Configuration
@Import({
        SearchListenerConfiguration.class
})
@EnableConfigurationProperties(SearchBotConfigurationSettings.class)
public class SearchBotConfiguration {
    @Inject
    private SearchListener listener;

    @Inject
    private SearchBotConfigurationSettings settings;

    @Bean
    public org.pircbotx.Configuration configuration() {
        org.pircbotx.Configuration.Builder builder = new org.pircbotx.Configuration.Builder()
                .setName(settings.getBotName()) //Nick of the bot.
                .setLogin(settings.getBotLogin()) //Login part of hostmask, eg name:login@host
                .setAutoNickChange(true) //Automatically change nick when the current one is in use
                .setEncoding(Charset.isSupported("UTF-8") ? Charset.forName("UTF-8") : Charset.defaultCharset())
                .addAutoJoinChannel(settings.getAutoJoinChannel())
                .addListener(this.listener);

        builder.addServer(settings.getServer());

        org.pircbotx.Configuration config = builder
                .buildConfiguration(); //Create an immutable configuration from this builder

        return config;
    }

    @Bean
    public SearchBot searchBot() {
        return new SearchBot(configuration());
    }
}

