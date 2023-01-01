package net.defeef.music;

import java.util.List;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

public class MusicPermissions {

    public static boolean hasDJ(Member member){
        List<Role> roles = member.getRoles();
        for(Role role : roles) {
            if(role.getName().equalsIgnoreCase("dj")) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasPermission(Member sender, AudioChannel channel) {
        boolean hasDJRole = hasDJ(sender);
        int people = (int) channel.getMembers().stream().filter(member -> !member.getUser().isBot()).count();
        return people == 1 || hasDJRole;
    }

}