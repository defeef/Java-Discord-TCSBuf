package net.defeef;

import net.defeef.command.Responder;
import net.defeef.command.Bee;
import net.defeef.command.ICommand;
import net.defeef.command.Ping;
import net.defeef.music.PlayerManager;
import net.defeef.util.Response;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;

public class Main {

    private static Main instance;

    private final PlayerManager playerManager;
    private final Config config;
    private final YouTube youTube;
    private final JDA jda;

    public Main(String[] args) throws InterruptedException {
        Main.instance = this;
        this.playerManager = new PlayerManager();
        this.config = Config.create("config.yml");
        this.jda = JDABuilder.createDefault(config.getString("botToken"))
                .setActivity(Activity.playing("vim"))
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT)
                .build()
                .awaitReady();
        this.jda.addEventListener(new Responder());
        try {
            this.youTube = new YouTube.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            GsonFactory.getDefaultInstance(),
            null).setApplicationName("DiscordBot").build();
        } catch (Exception e) {
            throw new RuntimeException("Unable to connect with YouTube API");
        }
    }

    public static Main getInstance() {
        return instance;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public JDA getJDA() {
        return jda;
    }

    public EmbedBuilder getDefaultEmbed() {
        return new EmbedBuilder();
    }

    public static void main(String[] args) throws InterruptedException {
        new Main(args);
    }

    public File getConfigFolder() {
        return new File("config");
    }

    public Config getConfig() {
        return config;
    }

    public YouTube getYouTube() {
        return youTube;
    }

    public InputStream getResource(String filename) {
        return getClass().getClassLoader().getResourceAsStream(filename);
    }

}
