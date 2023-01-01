package net.defeef.command.music;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import net.defeef.Main;
import net.defeef.util.Response;
import net.defeef.music.GuildMusicManager;
import net.defeef.music.MusicPermissions;
import net.defeef.music.PlayerManager;

public class Join {

    public Response execute(Member sender, Guild guild) {

        AudioManager audioManager = guild.getAudioManager();
        boolean hasDJ = MusicPermissions.hasDJ(sender);

        if (audioManager.isConnected() && !hasDJ) {
            return Response.error("I'm already connected to a channel");
        }

        GuildVoiceState memberVoiceState = sender.getVoiceState();

        assert memberVoiceState != null;
        if (!memberVoiceState.inAudioChannel()) {
            return Response.error("Please join a voice channel first");
        }

        AudioChannel audioChannel = memberVoiceState.getChannel();
        Member selfMember = guild.getSelfMember();

        assert audioChannel != null;
        if (!selfMember.hasPermission(audioChannel, Permission.VOICE_CONNECT)) {
            return Response.error("I am missing permission to join " +audioChannel);
        }

        audioManager.openAudioConnection(audioChannel);
        PlayerManager playerManager = Main.getInstance().getPlayerManager();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(guild);
        musicManager.autoLeaveManager.startTimeout(guild);

        return Response.success("Joining your voice channel");
    }

}

