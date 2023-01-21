package net.defeef.command.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.managers.AudioManager;
import net.defeef.Main;
import net.defeef.command.ICommand;
import net.defeef.util.Response;
import net.defeef.music.GuildMusicManager;
import net.defeef.music.MusicPermissions;
import net.defeef.music.PlayerManager;

import java.util.List;

public class Remove implements ICommand {

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
            int num = (int) args[0];
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

    @Override
    public String getInvoke() {
        // TODO Auto-generated method stub
        return "remove";
    }

    @Override
    public Object[] getArgs() {
        // TODO Auto-generated method stub
        return new Object[]{"index", "index in queue starting from 1 you wish to remove", OptionType.INTEGER, true};
    }
}
