package com.destiny.irc.bot;

import org.pircbotx.exception.IrcException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.inject.Inject;
import java.io.IOException;

/**
 * Created by Maman et Papa on 26/04/2016.
 */
@SpringBootApplication
public class SearchBotApplication {
    @Inject
    protected SearchBot searchBot;

    public SearchBotApplication() {
    }

    public static void main(String[] args) {

        ConfigurableApplicationContext ctx = SpringApplication.run(SearchBotApplication.class, args);

        SearchBotApplication searchBotApplication = ctx.getBean(SearchBotApplication.class);
        SearchBot bot = searchBotApplication.getSearchBot();
        try {
            // Start the bot up.
            bot.startBot();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IrcException e) {
            e.printStackTrace();
        }
    }

    public SearchBot getSearchBot() {
        return searchBot;
    }
}
