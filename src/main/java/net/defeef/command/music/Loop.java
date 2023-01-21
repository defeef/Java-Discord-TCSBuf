package net.defeef.command.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.managers.AudioManager;
import net.defeef.Main;
import net.defeef.command.ICommand;
import net.defeef.util.Response;
import net.defeef.music.GuildMusicManager;
import net.defeef.music.MusicPermissions;
import net.defeef.music.PlayerManager;

public class Loop implements ICommand {

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
            boolean loop = !musicManager.scheduler.isLooped();
            musicManager.scheduler.setLooped(loop);
            return Response.OK(String.format(
                    "%s Looping %s.",
                    loop ? ":arrows_counterclockwise:" : ":arrow_forward:",
                    loop ? "enabled" : "disabled"
            ));
        } else {
            return Response.ERROR("You must be the only person in the VC or have the `DJ` role to do this");
        }

    }

    @Override
    public String getInvoke() {
        // TODO Auto-generated method stub
        return "loop";
    }

    @Override
    public Object[] getArgs() {
        // TODO Auto-generated method stub
        return null;
    }
}