package net.defeef.command.music;


import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.managers.AudioManager;
import net.defeef.util.Response;
import net.defeef.command.ICommand;
import net.defeef.music.MusicPermissions;

public class Leave implements ICommand {

    public Response execute(MessageChannelUnion channel, Member sender, Object[] args) {

        Guild guild = sender.getGuild();
        AudioManager audioManager = guild.getAudioManager();

        if (!audioManager.isConnected()) {
            return Response.ERROR("I'm not connected to a voice channel");
        }

        AudioChannel audioChannel = audioManager.getConnectedChannel();

        assert audioChannel != null;
        if (!audioChannel.getMembers().contains(sender)) {
            return Response.ERROR("You have to be in the same voice channel as me to use this command");
        }

        if(MusicPermissions.hasPermission(sender, audioChannel)) {
            audioManager.closeAudioConnection();
            return Response.OK("Disconnected from your channel");
        } else {
            return Response.ERROR("You must be the only person in the VC or have the `DJ` role to do this");
        }

    }

    @Override
    public String getInvoke() {
        return "leave";
    }

    @Override
    public Object[] getArgs() {
        // TODO Auto-generated method stub
        return null;
    }
}
