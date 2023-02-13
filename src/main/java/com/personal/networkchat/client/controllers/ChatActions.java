package com.personal.networkchat.client.controllers;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

public interface ChatActions {

    void sendMessage();

    void addMessage(String message);

    void addServerMessage(String serverMessage);

    void refreshChatMembersList(String[] users);
}
