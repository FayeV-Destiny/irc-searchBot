package com.destiny.irc.bot.listener;

import com.destiny.irc.bot.dao.ProgramDAO;
import com.destiny.irc.bot.response.IrcResponseLine;
import com.destiny.irc.bot.response.IrcResponseLineComparator;
import com.destiny.irc.bot.response.IrcResponses;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

/**
 * Created by eric.tournier on 19/04/2016.
 */
@Component
public class SearchListener extends ListenerAdapter {
    public static final String HELLO = "?hello";
    public static final String TV = "?tv";
    public static final String NEXT_SCHEDULDED = "next";
    public static final String ALL_SCHEDULED = "all";

    private DateTimeFormatter ircDateTimeFormatter;

    @Inject
    protected ProgramDAO dao;

    public SearchListener() {
        this.ircDateTimeFormatter = DateTimeFormat.forPattern("dd/MM/YYYY 'a' HH:mm");
    }

    @Override
    public void onMessage(MessageEvent event) throws Exception {
        //This way to handle commands is useful for listeners that listen for multiple commands
        String message = event.getMessage();
        if (!( StringUtils.startsWithIgnoreCase(message, "?")))
            return;

        String[] messageParts = StringUtils.splitByWholeSeparator(message, " ", 3);

        switch (messageParts[0]) {
            case HELLO: {
                event.respond("Hello member of the DESTiNY team !");
            }
            break;

            case TV: {
                switch (messageParts[1]) {
                    case NEXT_SCHEDULDED: {
                        Element foundProgram = this.dao.findNextScheduldedProgramsByName(messageParts[2]);
                        IrcResponseLine ircResponse = new IrcResponseLine(foundProgram);
                        event.respondChannel(ircResponse.getFormattedLine());
                        event.respondChannel("Done !");
                    }
                    break;

                    case ALL_SCHEDULED: {
                        List<Node> allPrograms = this.dao.findAllProgramsByName(messageParts[2]);
                        IrcResponses responses = new IrcResponses(allPrograms);
                        Collections.sort(responses, new IrcResponseLineComparator());
                        for (IrcResponseLine response : responses) {
                            event.respondChannel(response.getFormattedLine());
                        }
                        event.respondChannel("Done !");
                    }
                    break;

                    default: {
                        event.respondChannel("Unrecognized option in " + TV + " command : " + messageParts[1]);
                    }
                }
            }
            break;

            default: {
                event.respondChannel("Unrecognized command : " + messageParts[0]);
            }
        }
    }
}
