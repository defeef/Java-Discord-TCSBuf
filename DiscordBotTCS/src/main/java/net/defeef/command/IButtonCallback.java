package net.defeef.command;

import net.defeef.util.Response;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

public interface IButtonCallback {
    
    public Response onButton(MessageChannelUnion channel, Member sender, String id, Message message);

}
