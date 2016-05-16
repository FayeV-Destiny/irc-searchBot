package com.destiny.irc.bot.response;

import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;

/**
 * Created by eric.tournier on 28/04/2016.
 */
public class IrcResponseLineComparator implements Comparator<IrcResponseLine> {
    @Override
    public int compare(IrcResponseLine line1, IrcResponseLine line2) {
        // Compare par rapport au titre
        if (StringUtils.equalsIgnoreCase(line1.getTitle(), line2.getTitle())) {
            // Compare par rapport aux dates de diffusion
            return line1.getDateTime().compareTo(line2.getDateTime());
        } else {
            return line1.getTitle().compareToIgnoreCase(line2.getTitle());
        }
    }
}
