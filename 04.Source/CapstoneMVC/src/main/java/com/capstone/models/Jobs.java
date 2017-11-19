package com.capstone.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Jobs {
    private static Map<String, String> closeSemesterJobs;

    public static void addJob(String key, String value) {
        if (closeSemesterJobs == null) closeSemesterJobs = new HashMap<>();
        closeSemesterJobs.put(key, value);
    }

    public static void removeJob(String key) {
        if (closeSemesterJobs == null) closeSemesterJobs = new HashMap<>();
        closeSemesterJobs.remove(key);
    }

    public static String getJob(String key) {
        if (closeSemesterJobs == null) closeSemesterJobs = new HashMap<>();
        return closeSemesterJobs.get(key);
    }
}
