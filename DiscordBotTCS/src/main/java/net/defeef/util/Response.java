package net.defeef.util;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class Response {

    private final Status status;
    private final String message;
    private final MessageEmbed[] embeds;
    private List<Button> buttons;

    private Response(Status status, String message, MessageEmbed... embeds) {
        this.status = status;
        this.message = message;
        this.embeds = embeds;
        this.buttons = new ArrayList<>();
    }

    public static Response OK(String message) {
        return new Response(Status.OK, "");
    }

    public static Response OK(MessageEmbed... embeds) {
        return new Response(Status.OK, "", embeds);
    }

    public static Response ERROR(String message) {
        return new Response(Status.ERROR, message);
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public MessageEmbed[] getEmbeds() {
        return embeds;
    }

    public List<Button> getButtons() {
        return buttons;
    }

    public boolean hasEmbeds() {
        return embeds.length != 0;
    }

    public boolean hasButtons() {
        return !buttons.isEmpty();
    }

    public enum Status {
        OK,
        ERROR
    }

    public Response addPrimaryButton(String callback, String id, String label){
        if(buttons == null) buttons = new ArrayList<>();
        buttons.add(Button.primary(callback + "_" + id, label));
        return this;
    }

    public Response addSecondaryButton(String callback, String id, String label){
        if(buttons == null) buttons = new ArrayList<>();
        buttons.add(Button.secondary(callback + "_" + id, label));
        return this;
    }

    public Response addSuccessButton(String callback, String id, String label){
        if(buttons == null) buttons = new ArrayList<>();
        buttons.add(Button.success(callback + "_" + id, label));
        return this;
    }

    public Response addDangerButton(String callback, String id, String label){
        if(buttons == null) buttons = new ArrayList<>();
        buttons.add(Button.danger(callback + "_" + id, label));
        return this;
    }

}