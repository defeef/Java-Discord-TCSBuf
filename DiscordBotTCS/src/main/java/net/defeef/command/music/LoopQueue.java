package net.defeef.command.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import net.defeef.Main;
import net.defeef.util.Response;
import net.defeef.music.GuildMusicManager;
import net.defeef.music.MusicPermissions;
import net.defeef.music.PlayerManager;

public class LoopQueue {

    public Response execute(Member sender, Guild guild) {

        AudioManager audioManager = guild.getAudioManager();
        PlayerManager playerManager = Main.getInstance().getPlayerManager();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(guild);
        AudioPlayer player = musicManager.player;

        if (player.getPlayingTrack() == null) {
            return Response.error("Nothing is currently playing");
        }

        AudioChannel audioChannel = audioManager.getConnectedChannel();

        assert audioChannel != null;
        if (!audioChannel.getMembers().contains(sender)) {
            return Response.error("You have to be in the same voice channel as me to use this command");
        }

        if(MusicPermissions.hasPermission(sender, audioChannel)) {
            boolean loop = !musicManager.scheduler.isQueueLooped();
            if(loop) {
                musicManager.scheduler.loopQueue();
            } else {
                musicManager.scheduler.unLoopQueue();
            }
            return Response.success(String.format(
                    "%s Looping %s.",
                    loop ? ":arrows_counterclockwise:" : ":arrow_forward:",
                    loop ? "enabled" : "disabled"
            ));
        } else {
            return Response.error("You must be the only person in the VC or have the `DJ` role to do this");
        }

    }
}