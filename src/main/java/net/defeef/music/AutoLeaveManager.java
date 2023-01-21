package net.defeef.music;

import java.util.Timer;
import java.util.TimerTask;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import net.defeef.Main;

public class AutoLeaveManager extends ListenerAdapter {

    Timer timer;
    TimeoutTask task;

    public AutoLeaveManager() {
        Main.getInstance().getJDA().addEventListener(this);
    }

    public void startTimeout(Guild guild) {
        task = new TimeoutTask(guild);
        timer = new Timer();
        timer.schedule(task, 0, 5000);
    }

    public void stopTimeout() {
        if(task == null) return;
        timer.cancel();
        timer = null;
        task = null;
    }

}

class TimeoutTask extends TimerTask {

    public TimeoutTask(Guild guild) {

        PlayerManager manager = Main.getInstance().getPlayerManager();
        GuildMusicManager musicManager = manager.getGuildMusicManager(guild);

        this.channel = musicManager.boundChannel;
        this.manager = guild.getAudioManager();
    }

    private final AudioManager manager;
    private final MessageChannel channel;

    private long lastTime = 0;

    public void run() {
        if(lastTime == 0) {
            lastTime = System.nanoTime();
        }
        if( (System.nanoTime() - lastTime) / 1_000_000_000L > 5 * 60) {
            EmbedBuilder builder = Main.getInstance().getDefaultEmbed()
                    .setDescription("Left voice channel due to bot inactivity.");
            channel.sendMessageEmbeds(builder.build()).queue();
            manager.closeAudioConnection();
            this.cancel();
        }
    }


}