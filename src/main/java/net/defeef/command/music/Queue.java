package net.defeef.command.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.defeef.Main;
import net.defeef.command.IButtonCallback;
import net.defeef.command.ICommand;
import net.defeef.util.Response;
import net.defeef.music.GuildMusicManager;
import net.defeef.music.PlayerManager;
import net.defeef.util.Checks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class Queue implements ICommand, IButtonCallback {

    public Response execute(MessageChannelUnion channel, Member sender, Object[] args) {

        Guild guild = sender.getGuild();
        PlayerManager playerManager = Main.getInstance().getPlayerManager();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(guild);
        MessageEmbed queue = getQueuePage(musicManager, 1);
        if(queue == null){
            return Response.ERROR("Queue is empty");
        }
        return Response.OK(queue).addSecondaryButton("queue", "previous", "Previous").addSecondaryButton("queue", "next", "Next");
    }

    public Response onButton(MessageChannelUnion channel, Member sender, String id, Message message) {
        Guild guild = sender.getGuild();
        PlayerManager playerManager = Main.getInstance().getPlayerManager();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(guild);
        BlockingQueue<AudioTrack> queue = musicManager.scheduler.getQueue();

        try { Checks.hasPagedEmbed(message); }
        catch (RuntimeException e) { return Response.ERROR(e.getMessage()); }

        int page = Integer.parseInt(message.getEmbeds().get(0).getFooter().getText().split("/")[0].substring(5));
        int pages = (queue.size()-1)/10+1;
        if(id.equals("previous"))
            page = page - 1 < 1 ? pages : page - 1;
        else
            page = page + 1 > pages ? 1 : page + 1;
        MessageEmbed embed = getQueuePage(musicManager, page);
        if(embed == null){
            return Response.OK("Queue is empty");
        }
        return Response.OK(embed);
    }

    private MessageEmbed getQueuePage(GuildMusicManager musicManager, int page){
        BlockingQueue<AudioTrack> queue = musicManager.scheduler.getQueue();
        if(queue.isEmpty()) return null;
        int startingIndex = (page-1)*10;
        int size = queue.size();
        int pages = (size-1)/10+1;
        if(startingIndex > size){
            startingIndex = 0;
            page = 1;
        }
        EmbedBuilder builder = Main.getInstance().getDefaultEmbed()
                .setTitle("Current Queue (Total: "+size+")")
                .setFooter("Page "+page+"/"+pages);

        List<AudioTrack> tracks = new ArrayList<>(queue);
        for(int i = startingIndex; i < Math.min(startingIndex+10,size); i++) {
            AudioTrack track = tracks.get(i);
            AudioTrackInfo info = track.getInfo();
            builder.appendDescription(String.format(
                    "**%s.** %s - %s\n",
                    i+1,
                    info.title,
                    info.author
            ));
        }
        return builder.build();
    }

    @Override
    public String getInvoke() {
        // TODO Auto-generated method stub
        return "queue";
    }

    @Override
    public Object[] getArgs() {
        // TODO Auto-generated method stub
        return null;
    }
}
