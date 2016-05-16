package com.destiny.irc.bot.response;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import static com.destiny.irc.bot.dao.ProgramDAO.tvGuideDateTimeFmt;

/**
 * Created by Maman et Papa on 26/04/2016.
 */
public class IrcResponseLine {
    private String fullResponse = null;
    private Element xmlElement;
    public static final DateTimeFormatter ircDateTimeFormatter = DateTimeFormat.forPattern("dd/MM/YYYY 'a' HH:mm");
    private String title;
    private DateTime dateTime;

    public IrcResponseLine(Element xmlElement) {
        this.fullResponse = "There was no such element.";
        if (xmlElement != null) {
            this.xmlElement = xmlElement;
            this.title = this.xmlElement.getElementsByTagName("title").item(0).getTextContent();
            String date = this.xmlElement.getAttribute("start");
            this.dateTime = tvGuideDateTimeFmt.parseDateTime(date);
        }
    }

    public String getFormattedLine() {
        if (this.xmlElement != null) {
            NodeList episodeNumElement = this.xmlElement.getElementsByTagName("episode-num");
            String channel = this.xmlElement.getAttribute("channel");
            if (episodeNumElement.item(0) != null) {
                String episodeNum = episodeNumElement.item(0).getTextContent();
                String formattedEpisodeNum = this.formatEpisodeNumber(episodeNum);
                fullResponse = title + " " + formattedEpisodeNum + " : "
                        + ircDateTimeFormatter.print(dateTime) + " sur " + channel;
            } else {
                fullResponse = title + " : "
                        + ircDateTimeFormatter.print(dateTime) + " sur " + channel;
            }
        }
        return fullResponse;
    }

    public String formatEpisodeNumber(String episodeNumber) {
        String formattedEpisodeNumber = episodeNumber;
        String[] split = StringUtils.split(episodeNumber, '.');
        if (split.length > 1) {
            if (split[1].length() == 1) {
                split[1] = "0" + split[1];
            }
            formattedEpisodeNumber = "S" + split[0] + "E" + split[1];
        }

        return formattedEpisodeNumber;
    }

    public String getTitle() {
        return title;
    }

    public DateTime getDateTime() {
        return dateTime;
    }
}
