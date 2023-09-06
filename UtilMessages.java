package com.thgplugins.domination.core;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static com.thgplugins.domination.util.DominationConstants.PREFIX;

public final class UtilMessages {

    private UtilMessages() { }


    @NotNull
    public static String noPermission() {
        return sendMessageError("You do not have sufficient permissions to execute this command.");
    }

    public static void sendNoPermission(@NotNull CommandSender sender) {
        sender.sendMessage(sendMessageError("You do not have sufficient permissions to execute this command."));
    }
    @NotNull
    public static String sendMessageError(@NotNull String message) {
        return "§e§l" + PREFIX + " §c§l✘ §f" + message;
    }

    @NotNull
    public static String sendMessageOk(@NotNull String message) {
        return "§e§l" + PREFIX + " §a§l✔ §f" + message;
    }

    @NotNull
    public static String sendMessageInfo(@NotNull String message) {
        return "§e§l" + PREFIX + " §6§l➥ §f" + message;
    }

    public static void sendMessage(@NotNull CommandSender sender, boolean ok, @NotNull String message) {
        sender.sendMessage(ok ? sendMessageOk(message) : sendMessageError(message));
    }

    public static void sendConsoleMessage(@NotNull String message) {
        Bukkit.getConsoleSender().sendMessage(message);
    }

    public static void sendConsoleSQLMessage(@NotNull String message) {
        sendConsoleMessage("§7[SQL] -> " + message);
    }

}
