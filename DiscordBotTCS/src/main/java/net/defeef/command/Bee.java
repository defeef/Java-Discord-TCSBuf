package net.defeef.command;

import net.defeef.util.Response;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

public class Bee implements ICommand {

    @Override
    public Response execute(MessageChannelUnion channel, Member sender, Object[] args) {
        var script = """
            According to all known laws of aviation,
            there is no way a bee should be able to fly.
            Its wings are too small to get its fat little body off the ground.
            The bee, of course, flies anyway""";
        channel.sendMessage(script).queue();
        return Response.OK(script);
    }

    @Override
    public String getInvoke() {
        return "bee";
    }
}
