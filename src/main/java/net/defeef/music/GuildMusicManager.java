package net.defeef.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;

public class GuildMusicManager {

    public final AudioPlayer player;
    public final TrackScheduler scheduler;
    public final AutoLeaveManager autoLeaveManager;
    public final Guild guild;

    public GuildMessageChannel boundChannel;

    public GuildMusicManager(Guild guild, AudioPlayerManager manager) {
        player = manager.createPlayer();
        scheduler = new TrackScheduler(player, guild);
        player.addListener(scheduler);
        this.guild = guild;
        autoLeaveManager = new AutoLeaveManager();
    }

    public AudioPlayerSendHandler getSendHandler() {
        return new AudioPlayerSendHandler(player);
    }
}