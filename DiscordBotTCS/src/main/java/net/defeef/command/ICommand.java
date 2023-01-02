package net.defeef.command;

import net.defeef.util.Response;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

public interface ICommand {
    Response execute(MessageChannelUnion channel, Member sender, Object[] args);
    String getInvoke();
}
