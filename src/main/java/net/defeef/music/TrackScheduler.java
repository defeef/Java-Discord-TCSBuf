package net.defeef.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.defeef.Main;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private final Guild guild;
    private final BlockingQueue<AudioTrack> queue;
    private boolean looping,queueLooped;
    private AudioTrack trackToBeLooped;
    public GuildMusicManager musicManager;

    public TrackScheduler(AudioPlayer player, Guild guild) {
        this.player = player;
        this.guild = guild;
        this.queue = new LinkedBlockingQueue<>();
        looping = false;
        queueLooped = false;
    }

    private boolean play(AudioTrack track, boolean dontForce) {
        boolean success = player.startTrack(track, dontForce);
        trackToBeLooped = track;
        if(queueLooped)  queue.offer(track.makeClone());
        if(success) {
            AudioTrackInfo info = player.getPlayingTrack().getInfo();

            String videoURL = track.getInfo().uri;
            String videoID = videoURL.substring(videoURL.indexOf("=")+1);
            String imageURL = String.format("https://img.youtube.com/vi/%s/default.jpg",videoID);

            musicManager.autoLeaveManager.stopTimeout();

            EmbedBuilder builder = Main.getInstance().getDefaultEmbed()
                    .setTitle("**Now Playing**")
                    .setDescription(String.format(
                            "[%s](%s)\n`[%s/%s]`\n\nRequested by %s",
                            info.title,
                            info.uri,
                            formatTime(player.getPlayingTrack().getPosition()),
                            formatTime(player.getPlayingTrack().getDuration()),
                            (User)player.getPlayingTrack().getUserData()
                    )).setThumbnail(imageURL);
            musicManager.boundChannel.sendMessageEmbeds(builder.build()).queue();

        }
        return success;
    }

    public boolean queue(AudioTrack track) {
        if (!play(track, true)) {
            queue.offer(track);
            return false;
        }
        return true;
    }

    public BlockingQueue<AudioTrack> getQueue() {
        return queue;
    }

    public void loopQueue() {
        queueLooped = true;
    }

    public void unLoopQueue() {
        queueLooped = false;
    }

    public void nextTrack() {

        if(looping) {
            repeatTrack(); return;
        }

        AudioTrack nextTrack = queue.poll();

        player.stopTrack();
        if(nextTrack == null) {
            looping = false;
            queueLooped = false;

            PlayerManager manager = Main.getInstance().getPlayerManager();
            GuildMusicManager musicManager = manager.getGuildMusicManager(guild);
            musicManager.autoLeaveManager.startTimeout(guild);

            return;
        }

        play(nextTrack.makeClone(), false);
    }

    public void repeatTrack() {
        play(trackToBeLooped.makeClone(), false);
    }

    public boolean removeFromQueue(int num) {
        int i = 0;
        Iterator<AudioTrack> itr = queue.iterator();
        while(itr.hasNext()) {
            AudioTrack t = itr.next();
            if(num == i) {
                return queue.remove(t);
            }
            i++;
        }
        return false;
    }

    public void setLooped(boolean value) {
        looping = value;
    }

    public boolean isLooped() {
        return looping;
    }

    public boolean isQueueLooped() {
        return queueLooped;
    }

    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            if(looping) {
                repeatTrack();
            } else {
                nextTrack();
            }
        }
    }

    public int getLength() {
        return queue.size();
    }

    private String formatTime(long timeInMillis) {
        final long hours = timeInMillis / TimeUnit.HOURS.toMillis(1);
        final long minutes = timeInMillis / TimeUnit.MINUTES.toMillis(1);
        final long seconeds = timeInMillis % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);

        if(hours == 0) {
            return String.format("%02d:%02d", minutes, seconeds);
        } else {
            return String.format("%02d:%02d:%02d", hours, minutes, seconeds);
        }
    }
}