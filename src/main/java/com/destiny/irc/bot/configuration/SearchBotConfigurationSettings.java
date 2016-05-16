package com.destiny.irc.bot.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;

/**
 * Created by Maman et Papa on 05/05/2016.
 */
@ConfigurationProperties(prefix = "irc")
public class SearchBotConfigurationSettings {
    private String botName;

    private String botLogin;

    private String autoJoinChannel;

    private String server;

    private File tvGuideFile;

    public String getBotName() {
        return botName;
    }

    public void setBotName(String botName) {
        this.botName = botName;
    }

    public String getBotLogin() {
        return botLogin;
    }

    public void setBotLogin(String botLogin) {
        this.botLogin = botLogin;
    }

    public String getAutoJoinChannel() {
        return autoJoinChannel;
    }

    public void setAutoJoinChannel(String autoJoinChannel) {
        this.autoJoinChannel = autoJoinChannel;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public File getTvGuideFile() {
        return tvGuideFile;
    }

    public void setTvGuideFile(File tvGuideFile) {
        this.tvGuideFile = tvGuideFile;
    }
}
