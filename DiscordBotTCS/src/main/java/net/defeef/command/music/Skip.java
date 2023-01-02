package net.defeef.command.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.defeef.Main;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import net.defeef.util.Response;
import net.defeef.music.GuildMusicManager;
import net.defeef.music.PlayerManager;
import net.defeef.music.TrackScheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Skip {

    private final Map<AudioTrack, List<String>> skips = new HashMap<>();

    public Response execute(Member sender, GuildMessageChannel channel, Guild guild, List<Object> args) {

        AudioManager audioManager = guild.getAudioManager();
        PlayerManager playerManager = Main.getInstance().getPlayerManager();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(guild);
        TrackScheduler scheduler = musicManager.scheduler;
        AudioPlayer player = musicManager.player;
        AudioTrack track = player.getPlayingTrack();

        if (player.getPlayingTrack() == null) {
            return Response.ERROR("Nothing is currently playing");
        }

        AudioChannel audioChannel = audioManager.getConnectedChannel();

        if(skips.get(track) == null) {
            List<String> users = new ArrayList<String>();
            users.add(sender.getUser().getDiscriminator());
            skips.put(track, users);
        } else {
            List<String> users = skips.get(track);
            boolean found = false;
            for(String s : users) {
                if(sender.getUser().getDiscriminator().equals(s)) {
                    found = true;
                    break;
                }
            }
            if(!found) {
                users.add(sender.getUser().getDiscriminator());
                skips.put(track, users);
            }
        }

        int people = (int) audioChannel.getMembers().stream().filter(member -> !member.getUser().isBot()).count();
        int skipsNeeded = (int) Math.ceil(people/2.0);

        if(skips.get(track).size()>=skipsNeeded) {
            skips.remove(track);
            scheduler.nextTrack();
            return Response.OK(":arrow_right: Skipping the current track");
        } else {
            return Response.OK(String.format(
                    ":arrow_right: Skips (%s/%s) for skipping current track. Use the command fskip to force skip.",
                    skips.get(track).size(),
                    skipsNeeded
            ));
        }

    }

}