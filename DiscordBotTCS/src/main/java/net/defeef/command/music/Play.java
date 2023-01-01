package net.defeef.command.music;

import com.google.api.services.youtube.model.SearchResult;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import net.defeef.Main;
import net.defeef.util.Response;
import net.defeef.music.PlayerManager;
import java.net.URL;
import java.util.Collections;
import java.util.List;

public class Play {

    public Response execute(Member sender, GuildMessageChannel channel, Guild guild, List<Object> args) {
        AudioManager audioManager = guild.getAudioManager();
        String input = (String) args.get(0);
        if(!isUrl(input)){
            String ytSearched = searchYoutube(input);
            if(ytSearched == null) {
                return Response.error("Unable to find youtube video with a similar name");
            }
            input = ytSearched;
        }

        PlayerManager manager = Main.getInstance().getPlayerManager();

        if(!audioManager.isConnected()) {
            GuildVoiceState memberVoiceState = sender.getVoiceState();
            if (memberVoiceState == null || !memberVoiceState.inAudioChannel()) {
                return Response.error("Please join a voice channel first");
            }
            AudioChannel audioChannel = memberVoiceState.getChannel();
            Member selfMember = guild.getSelfMember();
            assert audioChannel != null;
            if (!selfMember.hasPermission(audioChannel, Permission.VOICE_CONNECT)) {
                return Response.error("I am missing permission to join "+audioChannel);
            }
            audioManager.openAudioConnection(audioChannel);
            manager.loadAndPlay(channel, input, sender.getUser());
        } else if(manager.getGuildMusicManager(guild).scheduler.isQueueLooped()) {
            return Response.error("Queue is currently looped");
        } else {
            GuildVoiceState memberVoiceState = sender.getVoiceState();
            assert memberVoiceState != null;
            AudioChannel voiceChannel = memberVoiceState.getChannel();
            assert voiceChannel != null;
            AudioChannel selfVoiceChannel = audioManager.getConnectedChannel();
            assert selfVoiceChannel != null;
            if(voiceChannel.getIdLong() == selfVoiceChannel.getIdLong()) {
                manager.loadAndPlay(channel, input, sender.getUser());
            } else {
                return Response.error("You have to be in the same voice channel as me to use this command");
            }
        }
        return Response.success(":arrow_forward: Queueing Track");
    }

    private boolean isUrl(String input) {
        try {
            new URL(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String searchYoutube(String input) {
        try {
            List<SearchResult> results = Main.getInstance().getYouTube().search()
                    .list(Collections.singletonList("id,snippet"))
                    .setQ(input)
                    .setMaxResults(1L)
                    .setType(Collections.singletonList("video"))
                    .setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)")
                    .setKey(Main.getInstance().getConfig().getString("youtubeAPIKey"))
                    .execute()
                    .getItems();
            if (!results.isEmpty()) {
                String videoId = results.get(0).getId().getVideoId();
                return "https://www.youtube.com/watch?v=" + videoId;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
