package net.defeef.command.music;


import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import net.defeef.util.Response;
import net.defeef.music.MusicPermissions;

public class Leave {

    public Response execute(Member sender, Guild guild) {

        AudioManager audioManager = guild.getAudioManager();

        if (!audioManager.isConnected()) {
            return Response.error("I'm not connected to a voice channel");
        }

        AudioChannel audioChannel = audioManager.getConnectedChannel();

        assert audioChannel != null;
        if (!audioChannel.getMembers().contains(sender)) {
            return Response.error("You have to be in the same voice channel as me to use this command");
        }

        if(MusicPermissions.hasPermission(sender, audioChannel)) {
            audioManager.closeAudioConnection();
            return Response.success("Disconnected from your channel");
        } else {
            return Response.error("You must be the only person in the VC or have the `DJ` role to do this");
        }

    }
}