package net.defeef.command.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.managers.AudioManager;
import net.defeef.Main;
import net.defeef.util.Response;
import net.defeef.music.GuildMusicManager;
import net.defeef.music.MusicPermissions;
import net.defeef.music.PlayerManager;

import java.util.List;

public class Remove {

    public Response execute(Member sender, Guild guild, List<Object> args) {

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

        if(MusicPermissions.hasPermission(sender, audioChannel)) {
            int num = (int) args.get(0);
            boolean OK = musicManager.scheduler.removeFromQueue(num-1);
            if(OK) {
                return Response.OK(":white_check_mark: Removed track OKfully");
            } else {
                return Response.ERROR("Track not found");
            }
        } else {
            return Response.ERROR("You must be the only person in the VC or have the `DJ` role to do this");
        }

    }
}
