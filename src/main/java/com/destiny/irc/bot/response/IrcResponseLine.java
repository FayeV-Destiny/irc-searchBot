package com.destiny.irc.bot.response;

import net.sf.saxon.s9api.Axis;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmSequenceIterator;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.w3c.dom.NodeList;

import static com.destiny.irc.bot.dao.ProgramDAO.tvGuideDateTimeFmt;

/**
 * Created by Maman et Papa on 26/04/2016.
 */
public class IrcResponseLine {
    private String fullResponse = null;
    private XdmNode xmlNode;
    public static final DateTimeFormatter ircDateTimeFormatter = DateTimeFormat.forPattern("dd/MM/YYYY 'a' HH:mm");
    private String title;
    private DateTime dateTime;
    private static QName TITLE_NAME = new QName("title");
    private static QName START = new QName("start");
    private static QName EPISODE_NUM = new QName("episode-num");
    private static QName CHANNEL = new QName("channel");


    public IrcResponseLine(XdmNode xmlNode) {
        this.fullResponse = "There was no such element.";
        if (xmlNode != null) {
            this.xmlNode = xmlNode;
            XdmNode titleNode = getChild(this.xmlNode, TITLE_NAME);
            this.title = titleNode.getStringValue();
            String date = this.xmlNode.getAttributeValue(START);
            this.dateTime = tvGuideDateTimeFmt.parseDateTime(date);
        }
    }

    public String getFormattedLine() {
        if (this.xmlNode != null) {
            XdmNode episodeNumNode = getChild(this.xmlNode, EPISODE_NUM);
            String channel = this.xmlNode.getAttributeValue(CHANNEL);
            if (episodeNumNode != null) {
                String episodeNum = episodeNumNode.getStringValue();
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

    private static XdmNode getChild(XdmNode parent, QName childName) {
        XdmSequenceIterator iter = parent.axisIterator(Axis.CHILD, childName);
        if (iter.hasNext()) {
            return (XdmNode) iter.next();
        } else {
            return null;
        }
    }
}
