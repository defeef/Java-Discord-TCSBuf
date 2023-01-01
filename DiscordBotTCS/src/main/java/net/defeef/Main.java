package net.defeef;

import net.defeef.command.Bee;
import net.defeef.command.ICommand;
import net.defeef.command.Ping;
import net.defeef.music.PlayerManager;
import net.defeef.util.Response;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {

    private static Main instance;

    private final PlayerManager playerManager;
    private final JDA jda;

    public Main(String[] args) throws InterruptedException {
        Main.instance = this;
        this.playerManager = new PlayerManager();
        this.jda = JDABuilder.createDefault(args[0])
                .setActivity(Activity.playing("vim"))
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new Listener("$"))
                .build()
                .awaitReady();
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

}

class Listener extends ListenerAdapter {
    private final String prefix;
    private final Map<String, ICommand> commands;

    public Listener(String prefix) {
        this.prefix = prefix;
        commands = new HashMap<>();
        registerCommand(new Bee());
        registerCommand(new Ping());
    }

    public void registerCommand(ICommand command) {
        if (!commands.containsKey(command.getInvoke())) {
            commands.put(command.getInvoke(), command);
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.isFromGuild()) return;
        var message = event.getMessage().getContentRaw();
        if(!message.startsWith(this.prefix)) return;
        String[] args = message.split(" ");
        var invoke = args[0].substring(1).toLowerCase();
        ICommand command = commands.get(invoke);
        Response response = command.execute(
                event.getChannel(),
                event.getMember(),
                Arrays.copyOfRange(args, 1, args.length)
        );
        if (response.getStatus() == Response.Status.ERROR) {
            event.getChannel().sendMessageEmbeds(
                    new EmbedBuilder()
                        .setTitle(":x: Error")
                        .setDescription(response.getMessage())
                        .setColor(Color.RED)
                        .build()
            ).queue();
        } else {
            event.getChannel().sendMessage(response.getMessage()).queue();
        }
    }
}