package net.defeef.command.music;

import com.google.api.services.youtube.model.SearchResult;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.managers.AudioManager;
import net.defeef.Main;
import net.defeef.command.ICommand;
import net.defeef.util.Response;
import net.defeef.music.PlayerManager;
import java.net.URL;
import java.util.Collections;
import java.util.List;

public class Play implements ICommand {

    public Response execute(MessageChannelUnion channel, Member sender, Object[] args) {

        Guild guild = sender.getGuild();
        AudioManager audioManager = guild.getAudioManager();
        String input = (String) args[0];
        if(!isUrl(input)){
            String ytSearched = searchYoutube(input);
            if(ytSearched == null) {
                return Response.ERROR("Unable to find youtube video with a similar name");
            }
            input = ytSearched;
        }

        PlayerManager manager = Main.getInstance().getPlayerManager();

        if(!audioManager.isConnected()) {
            GuildVoiceState memberVoiceState = sender.getVoiceState();
            if (memberVoiceState == null || !memberVoiceState.inAudioChannel()) {
                return Response.ERROR("Please join a voice channel first");
            }
            AudioChannel audioChannel = memberVoiceState.getChannel();
            Member selfMember = guild.getSelfMember();
            assert audioChannel != null;
            if (!selfMember.hasPermission(audioChannel, Permission.VOICE_CONNECT)) {
                return Response.ERROR("I am missing permission to join "+audioChannel);
            }
            audioManager.openAudioConnection(audioChannel);
            manager.loadAndPlay((GuildMessageChannel) channel, input, sender.getUser());
        } else if(manager.getGuildMusicManager(guild).scheduler.isQueueLooped()) {
            return Response.ERROR("Queue is currently looped");
        } else {
            GuildVoiceState memberVoiceState = sender.getVoiceState();
            assert memberVoiceState != null;
            AudioChannel voiceChannel = memberVoiceState.getChannel();
            assert voiceChannel != null;
            AudioChannel selfVoiceChannel = audioManager.getConnectedChannel();
            assert selfVoiceChannel != null;
            if(voiceChannel.getIdLong() == selfVoiceChannel.getIdLong()) {
                manager.loadAndPlay((GuildMessageChannel) channel, input, sender.getUser());
            } else {
                return Response.ERROR("You have to be in the same voice channel as me to use this command");
            }
        }
        return Response.OK(":arrow_forward: Queueing Track");
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

    @Override
    public String getInvoke() {
        // TODO Auto-generated method stub
        return "play";
    }

    @Override
    public Object[] getArgs() {
        // TODO Auto-generated method stub
        return new Object[]{"query", "YouTube vido URL or search query", OptionType.STRING, true};
    }
}
