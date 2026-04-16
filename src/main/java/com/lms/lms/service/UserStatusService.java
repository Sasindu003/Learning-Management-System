package com.lms.lms.service;

import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserStatusService {

    // Store online usernames in a concurrent set for thread safety
    private final Set<String> onlineUsers = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public void addUser(String username) {
        if (username != null) {
            onlineUsers.add(username);
        }
    }

    public void removeUser(String username) {
        if (username != null) {
            onlineUsers.remove(username);
        }
    }

    public Set<String> getOnlineUsers() {
        return Collections.unmodifiableSet(onlineUsers);
    }

    public boolean isOnline(String username) {
        return onlineUsers.contains(username);
    }
}
