package com.toonystank.requisitertp.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SmallLetterConvertor {

    private static final Map<Character, String> characterMap = createCharacterMap();
    private static boolean isEnabled = false;

    public static void isEnabled(boolean isSmallLetterEnabled) {
        isEnabled = isSmallLetterEnabled;
    }

    public static List<String> convert(List<String> list) {
        return list.stream().map(SmallLetterConvertor::convert).collect(Collectors.toList());
    }
    public static String convert(String input) {
        if (input == null) {
            return null;
        }
        if (!isEnabled) {
            return input;
        }
        StringBuilder result = new StringBuilder();
        Pattern colorPattern = Pattern.compile("(&[a-fA-F0-9klmnor]|&#[0-9a-fA-F]{6}&)|<[^>]*>|\\{[^}]*\\}");
        Matcher matcher = colorPattern.matcher(input);
        int currentIndex = 0;
        while (matcher.find()) {
            result.append(convertText(input.substring(currentIndex, matcher.start())));
            result.append(matcher.group());
            currentIndex = matcher.end();
        }
        result.append(convertText(input.substring(currentIndex)));

        return result.toString();
    }

    private static String convertText(String text) {
        StringBuilder convertedText = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            if ((character >= 'A' && character <= 'Z') || (character >= 'a' && character <= 'z')) {
                String unicodeEquivalent = characterMap.getOrDefault(character, String.valueOf(character));
                convertedText.append(unicodeEquivalent);
            } else {
                convertedText.append(character);
            }
        }

        return convertedText.toString();
    }

    private static Map<Character, String> createCharacterMap() {
        Map<Character, String> map = new HashMap<>();
        map.put('A', "ᴀ");
        map.put('B', "ʙ");
        map.put('C', "ᴄ");
        map.put('D', "ᴅ");
        map.put('E', "ᴇ");
        map.put('F', "ꜰ");
        map.put('G', "ɢ");
        map.put('H', "ʜ");
        map.put('I', "ɪ");
        map.put('J', "ᴊ");
        map.put('K', "ᴋ");
        map.put('L', "ʟ");
        map.put('M', "ᴍ");
        map.put('N', "ɴ");
        map.put('O', "ᴏ");
        map.put('P', "ᴘ");
        map.put('Q', "ǫ");
        map.put('R', "ʀ");
        map.put('S', "ꜱ");
        map.put('T', "ᴛ");
        map.put('U', "ᴜ");
        map.put('V', "ᴠ");
        map.put('W', "ᴡ");
        map.put('X', "x");
        map.put('Y', "ʏ");
        map.put('Z', "ᴢ");
        map.put('a', "ᴀ");
        map.put('b', "ʙ");
        map.put('c', "ᴄ");
        map.put('d', "ᴅ");
        map.put('e', "ᴇ");
        map.put('f', "ꜰ");
        map.put('g', "ɢ");
        map.put('h', "ʜ");
        map.put('i', "ɪ");
        map.put('j', "ᴊ");
        map.put('k', "ᴋ");
        map.put('l', "ʟ");
        map.put('m', "ᴍ");
        map.put('n', "ɴ");
        map.put('o', "ᴏ");
        map.put('p', "ᴘ");
        map.put('q', "ǫ");
        map.put('r', "ʀ");
        map.put('s', "ꜱ");
        map.put('t', "ᴛ");
        map.put('u', "ᴜ");
        map.put('v', "ᴠ");
        map.put('w', "ᴡ");
        map.put('x', "x");
        map.put('y', "ʏ");
        map.put('z', "ᴢ");
        return map;
    }
}
