package com.example.backend.utils;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HashtagUtils {
    private static final Pattern HASHTAG_PATTERN = Pattern.compile("#([\\p{L}0-9_]+)");

    public static Set<String> extractHashtags(String caption) {
        Set<String> hashtags = new LinkedHashSet<>();
        if (caption == null || caption.isBlank()) return hashtags;

        Matcher matcher = HASHTAG_PATTERN.matcher(caption);
        while (matcher.find()) {
            hashtags.add(matcher.group(1).toLowerCase().trim());
        }
        return hashtags;
    }
}