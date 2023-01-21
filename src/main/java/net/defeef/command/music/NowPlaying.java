package net.defeef.command.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.defeef.Main;
import net.defeef.command.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.managers.AudioManager;
import net.defeef.util.Response;
import net.defeef.music.GuildMusicManager;
import net.defeef.music.PlayerManager;

import java.util.concurrent.TimeUnit;

public class NowPlaying implements ICommand {

    public Response execute(MessageChannelUnion channel, Member sender, Object[] args) {

        Guild guild = sender.getGuild();
        AudioManager audioManager = guild.getAudioManager();
        PlayerManager playerManager = Main.getInstance().getPlayerManager();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(guild);
        AudioPlayer player = musicManager.player;

        if (player.getPlayingTrack() == null) {
            return Response.ERROR("Nothing is currently playing");
        }

        AudioChannel audioChannel = audioManager.getConnectedChannel();

        assert audioChannel != null;
        if (!audioChannel.getMembers().contains(sender)) {
            return Response.ERROR("You have to be in the same voice channel as me to use this command");
        }

        AudioTrackInfo info = player.getPlayingTrack().getInfo();
        String videoURL = info.uri;
        String videoID = videoURL.substring(videoURL.indexOf("=")+1);
        String imageURL = String.format("https://img.youtube.com/vi/%s/default.jpg",videoID);

        EmbedBuilder builder = Main.getInstance().getDefaultEmbed()
                .setTitle("**Now Playing**")
                .setDescription(String.format(
                        "[%s](%s)\n`[%s/%s]`\n\nRequested by %s",
                        info.title,
                        info.uri,
                        formatTime(player.getPlayingTrack().getPosition()),
                        formatTime(player.getPlayingTrack().getDuration()),
                        player.getPlayingTrack().getUserData()
                )).setThumbnail(imageURL);

        return Response.OK(builder.build());
    }

    private String formatTime(long timeInMillis) {
        final long hours = timeInMillis / TimeUnit.HOURS.toMillis(1);
        final long minutes = timeInMillis / TimeUnit.MINUTES.toMillis(1);
        final long seconds = timeInMillis % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);

        if(hours == 0) {
            return String.format("%02d:%02d", minutes, seconds);
        } else {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
    }

    @Override
    public String getInvoke() {
        // TODO Auto-generated method stub
        return "nowplaying";
    }

    @Override
    public Object[] getArgs() {
        // TODO Auto-generated method stub
        return null;
    }
}