package net.defeef.command.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.defeef.Main;
import net.defeef.music.GuildMusicManager;
import net.defeef.music.MusicPermissions;
import net.defeef.music.PlayerManager;
import net.defeef.music.TrackScheduler;
import net.defeef.util.Response;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class ForceSkip {

    public Response execute(Member sender, Guild guild) {
        PlayerManager playerManager = Main.getInstance().getPlayerManager();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(guild);
        TrackScheduler scheduler = musicManager.scheduler;
        AudioPlayer player = musicManager.player;
        AudioManager audioManager = guild.getAudioManager();
        AudioChannel audioChannel = audioManager.getConnectedChannel();
        AudioTrack track = player.getPlayingTrack();

        if (track == null) {
            return Response.error("Nothing is currently playing.");
        }

        if(audioChannel == null) {
            return Response.error("Im not connected to a voice channel.");
        }

        if (!audioChannel.getMembers().contains(sender)) {
            return Response.error("You have to be in the same voice channel as me to use this command");
        }

        if(!MusicPermissions.hasDJ(sender)) {
            return Response.error("You must have the `DJ` role to do this.");
        }

        scheduler.nextTrack();
        return Response.success(":arrow_right: Skipping the current track");
    }

}
