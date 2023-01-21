package net.defeef.command.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.defeef.Main;
import net.defeef.command.ICommand;
import net.defeef.music.GuildMusicManager;
import net.defeef.music.MusicPermissions;
import net.defeef.music.PlayerManager;
import net.defeef.util.Response;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.managers.AudioManager;

public class Stop implements ICommand {

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

        if(MusicPermissions.hasPermission(sender, audioChannel)) {
            musicManager.scheduler.getQueue().clear();
            musicManager.player.stopTrack();
            musicManager.player.setPaused(false);
            musicManager.scheduler.unLoopQueue();
            musicManager.scheduler.setLooped(false);
            musicManager.autoLeaveManager.startTimeout(guild);
            return Response.OK("Stopped the music and cleared the song queue");
        } else {
            return Response.ERROR("You must be the only person in the VC or have the `DJ` role to do this");
        }

    }

    @Override
    public String getInvoke() {
        // TODO Auto-generated method stub
        return "stop";
    }

    @Override
    public Object[] getArgs() {
        // TODO Auto-generated method stub
        return null;
    }
}
