package net.defeef.command;

import net.defeef.util.Response;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

public class Ping implements ICommand {
    @Override
    public Response execute(MessageChannelUnion channel, Member sender, Object[] args) {
        if (args.length > 0) {
            var script = """
                    The FitnessGram™ Pacer Test is a multistage aerobic capacity test that progressively gets more difficult as it continues. The 20 meter pacer test will begin in 30 seconds. Line up at the start. The running speed starts slowly, but gets faster each minute after you hear this signal. [beep] A single lap should be completed each time you hear this sound. [ding] Remember to run in a straight line, and run as long as possible. The second time you fail to complete a lap before the sound, your test is over. The test will begin on the word start. On your mark, get ready, start.""";
            return Response.ERROR(script);
        }
        channel.sendMessage("Pong!").queue();
        return Response.OK("Pong!");
    }

    @Override
    public String getInvoke() {
        return "ping";
    }

    @Override
    public Object[] getArgs() {
        // TODO Auto-generated method stub
        return null;
    }
}
