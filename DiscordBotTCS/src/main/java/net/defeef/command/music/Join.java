package net.defeef.command.music;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.managers.AudioManager;
import net.defeef.Main;
import net.defeef.command.ICommand;
import net.defeef.util.Response;
import net.defeef.music.GuildMusicManager;
import net.defeef.music.MusicPermissions;
import net.defeef.music.PlayerManager;

public class Join implements ICommand {

    public Response execute(MessageChannelUnion channel, Member sender, Object[] args) {

        Guild guild = sender.getGuild();
        AudioManager audioManager = guild.getAudioManager();
        boolean hasDJ = MusicPermissions.hasDJ(sender);

        if (audioManager.isConnected() && !hasDJ) {
            return Response.ERROR("I'm already connected to a channel");
        }

        GuildVoiceState memberVoiceState = sender.getVoiceState();

        assert memberVoiceState != null;
        if (!memberVoiceState.inAudioChannel()) {
            return Response.ERROR("Please join a voice channel first");
        }

        AudioChannel audioChannel = memberVoiceState.getChannel();
        Member selfMember = guild.getSelfMember();

        assert audioChannel != null;
        if (!selfMember.hasPermission(audioChannel, Permission.VOICE_CONNECT)) {
            return Response.ERROR("I am missing permission to join " +audioChannel);
        }

        audioManager.openAudioConnection(audioChannel);
        PlayerManager playerManager = Main.getInstance().getPlayerManager();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(guild);
        musicManager.autoLeaveManager.startTimeout(guild);

        return Response.OK("Joining your voice channel");
    }

    @Override
    public String getInvoke() {
        return "join";
    }

    @Override
    public Object[] getArgs() {
        // TODO Auto-generated method stub
        return null;
    }
}

