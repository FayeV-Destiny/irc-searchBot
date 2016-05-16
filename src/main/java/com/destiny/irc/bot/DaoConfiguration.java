package com.destiny.irc.bot;

import com.destiny.irc.bot.configuration.SearchBotConfigurationSettings;
import com.destiny.irc.bot.dao.ProgramDAO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

/**
 * Created by Maman et Papa on 01/05/2016.
 */
@Configuration
public class DaoConfiguration {
    @Inject
    private SearchBotConfigurationSettings settings;

    @Bean
    public ProgramDAO dao() {
        return new ProgramDAO( settings.getTvGuideFile() );
    }
}
