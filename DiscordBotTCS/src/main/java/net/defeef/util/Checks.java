package net.defeef.util;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class Checks {
    
    public static void hasPagedEmbed(Message message) {
        if(message == null || message.getEmbeds().size() < 1)
            throw new RuntimeException("Message is missing");
        MessageEmbed temp = message.getEmbeds().get(0);
        if(temp == null || temp.getDescription() == null || temp.getFooter() == null)
            throw new RuntimeException("Embed is missing");
    }

}
