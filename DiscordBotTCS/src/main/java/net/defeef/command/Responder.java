package net.defeef.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.awt.Color;

import net.defeef.Main;
import net.defeef.command.music.ForceSkip;
import net.defeef.command.music.Join;
import net.defeef.command.music.Leave;
import net.defeef.command.music.Loop;
import net.defeef.command.music.LoopQueue;
import net.defeef.command.music.NowPlaying;
import net.defeef.command.music.Pause;
import net.defeef.command.music.Play;
import net.defeef.command.music.Queue;
import net.defeef.command.music.Remove;
import net.defeef.command.music.Resume;
import net.defeef.command.music.Skip;
import net.defeef.command.music.Stop;
import net.defeef.util.Response;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;

public class Responder extends ListenerAdapter {
    private final Map<String, ICommand> commands;
    private final Map<String, IButtonCallback> buttons;

    public Responder() {
        commands = new HashMap<>();
        buttons = new HashMap<>();
        registerCommand(new Ping());
        registerCommand(new Bee());
        registerCommand(new ForceSkip());
        registerCommand(new Join());
        registerCommand(new Leave());
        registerCommand(new Loop());
        registerCommand(new LoopQueue());
        registerCommand(new NowPlaying());
        registerCommand(new Pause());
        registerCommand(new Play());
        registerCommand(new Queue());
        registerCommand(new Remove());
        registerCommand(new Resume());
        registerCommand(new Skip());
        registerCommand(new Stop());
    }

    public void registerCommand(Object o) {
        if (!(o instanceof ICommand command)) {
            return;
        }
        if (commands.containsKey(command.getInvoke())) {
            return;
        }
        commands.put(command.getInvoke(), command);
        if (command instanceof IButtonCallback callback) {
            buttons.put(command.getInvoke(), callback);
        }
        SlashCommandData data = Commands.slash(command.getInvoke(), "Runs the " + command.getInvoke() + " command!`");
        Object[] args = command.getArgs();
        if (args != null) {
            for (int i = 0; i < args.length; i += 4) {
                OptionData option = new OptionData(
                    (OptionType) args[i + 2],
                    (String) args[i],
                    (String) args[i + 1],
                    (boolean) args[i + 3]
                );
                data.addOptions(option);
            }
        }
        Main.getInstance().getJDA().upsertCommand(data).queue();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.isFromGuild()) return;
        if (!event.getChannel().canTalk()) return;
        if (event.getMember() == null || event.getUser().isBot()) return;
        var invoke = event.getName().toLowerCase();
        if (!commands.containsKey(invoke)) { return; }
        ICommand command = commands.get(invoke);
        List<Object> args = getOptions(event.getOptions());
        net.defeef.util.Response response = command.execute(
                event.getChannel(),
                event.getMember(),
                args.toArray()
        );
        event.deferReply().queue();
        reply(response, event.getHook());
    }

    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (!event.isFromGuild()) return;
        if (!event.getChannel().canTalk()) return;
        if (event.getMember() == null || event.getUser().isBot()) return;

        final String invoke = event.getComponentId().toLowerCase(Locale.ROOT).split("_")[0];
        final String id = event.getComponentId().substring(invoke.length() + 1);

        if (!buttons.containsKey(invoke)) return;
        IButtonCallback callback = buttons.get(invoke);
        Response response = callback.onButton(
            event.getChannel(),
            event.getMember(),
            id,
            event.getMessage()
        );
        edit(response, event.getHook());
    }

    private void reply(Response response, InteractionHook hook) {
        if (response.getStatus() == Response.Status.ERROR) {
            hook.sendMessageEmbeds(
                    new EmbedBuilder()
                        .setTitle(":x: Error")
                        .setDescription(response.getMessage())
                        .setColor(Color.RED)
                        .build()
            ).queue();
        } else {
            WebhookMessageCreateAction<Message> message;
            if (response.hasEmbeds()) {
                message = hook.sendMessageEmbeds(Arrays.asList(response.getEmbeds()));
            } else {
                message = hook.sendMessage(response.getMessage());
            }
            if (response.hasButtons()) {
                message = message.addActionRow(response.getButtons());
            }
            message.queue();
        }
    }

    private void edit(Response response, InteractionHook hook) {
        if (response.getStatus() == Response.Status.ERROR) {
            hook.sendMessageEmbeds(
                    new EmbedBuilder()
                        .setTitle(":x: Error")
                        .setDescription(response.getMessage())
                        .setColor(Color.RED)
                        .build()
            ).queue();
        } else {
            if (response.hasEmbeds()) {
                hook.editOriginalEmbeds(response.getEmbeds()).queue();
            } else {
                hook.editOriginal(response.getMessage()).queue();
            }
            if (response.hasButtons()) {
                List<LayoutComponent> components = new ArrayList<>();
                components.add(ActionRow.of(response.getButtons()));
                hook.editOriginalComponents(components).queue();
            } else {
                hook.editOriginalComponents(new ArrayList<>()).queue();
            }
        }
    }


    private List<Object> getOptions(List<OptionMapping> options){
        List<Object> args = new ArrayList<>();
        options.forEach(option -> {
            switch (option.getType()) {
                case STRING -> args.add(option.getAsString());
                case INTEGER -> args.add(option.getAsInt());
                case NUMBER -> args.add(option.getAsDouble());
                case BOOLEAN -> args.add(option.getAsBoolean());
                case USER -> args.add(option.getAsMember());
                case CHANNEL -> args.add(option.getAsChannel());
                case ROLE -> args.add(option.getAsRole());
                case MENTIONABLE -> args.add(option.getAsMentionable());
                case ATTACHMENT -> args.add(option.getAsAttachment());
                default -> {
                }
            }
        });
        return args;
    }
}