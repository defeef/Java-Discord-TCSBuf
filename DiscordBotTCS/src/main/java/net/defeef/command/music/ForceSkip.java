package net.defeef.command.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.defeef.Main;
import net.defeef.command.ICommand;
import net.defeef.music.GuildMusicManager;
import net.defeef.music.MusicPermissions;
import net.defeef.music.PlayerManager;
import net.defeef.music.TrackScheduler;
import net.defeef.util.Response;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.managers.AudioManager;

public class ForceSkip implements ICommand {

    @Override
    public Response execute(MessageChannelUnion channel, Member sender, Object[] args) {
        Guild guild = sender.getGuild();
        PlayerManager playerManager = Main.getInstance().getPlayerManager();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(guild);
        TrackScheduler scheduler = musicManager.scheduler;
        AudioPlayer player = musicManager.player;
        AudioManager audioManager = guild.getAudioManager();
        AudioChannel audioChannel = audioManager.getConnectedChannel();
        AudioTrack track = player.getPlayingTrack();

        if (track == null) {
            return Response.ERROR("Nothing is currently playing.");
        }

        if(audioChannel == null) {
            return Response.ERROR("Im not connected to a voice channel.");
        }

        if (!audioChannel.getMembers().contains(sender)) {
            return Response.ERROR("You have to be in the same voice channel as me to use this command");
        }

        if(!MusicPermissions.hasDJ(sender)) {
            return Response.ERROR("You must have the `DJ` role to do this.");
        }

        scheduler.nextTrack();
        return Response.OK(":arrow_right: Skipping the current track");
    }

    @Override
    public String getInvoke() {
        return "fskip";
    }    
}
