package com.thgplugins.guild.core.sign;

@FunctionalInterface
public interface SignCompleteHandler {
    void onSignClose(SignCompletedEvent event);
}