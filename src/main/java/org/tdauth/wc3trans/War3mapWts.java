package org.tdauth.wc3trans;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * STRING 0
 * // Items: I0D0 (Inferno), Ubertip (Tooltip - Extended)
 * {
 * Calls an Infernal down from the sky, dealing damage and stunning enemy land units for <AUin,Dur1> seconds in an area. The Infernal does not last permanently. |n|n|cffffcc00Increases the damage, duration and stats of the Infernal with every level.|r
 * }
 */
public class War3mapWts {
    private static final String UTF8_BOM = "\uFEFF";
    private static final String EOL = "\r\n";
    private static final Pattern ID_PATTERN = Pattern.compile("\\s(\\d+)");

    public final Map<Long, StringEntry> entries = new HashMap<>();

    public War3mapWts(String filePath) throws IOException {
        System.out.println("Reading " + filePath + ".");
        long lines = 0L;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            StringEntry current = null;
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(UTF8_BOM)) {
                    line = line.substring(1);
                }

                if (line.startsWith("STRING")) {
                    String subString = line.substring("STRING".length());
                    Matcher m = ID_PATTERN.matcher(subString);
                    if (m.find()) {
                        current = new StringEntry(Long.parseLong(m.group(1)));
                    }
                } else if (line.startsWith("//")) {
                    if (current != null) {
                        current.comment = line.substring("//".length());
                    }
                } else if (line.startsWith("{")) {
                    // do nothing
                } else if (line.startsWith("}")) {
                    if (current != null) {
                        entries.put(current.id, current);
                        current = null;
                    }
                } else if (current != null) {
                    if (current.text.length() > 0) {
                        current.text += EOL;
                    }

                    current.text += line;
                }

                lines++;
            }
        }

        System.out.println("Read " + filePath + " with " + entries.size() + " entries and " + lines + " lines.");
    }

    public void updateTarget(War3mapWts target) {
        long updated = 0L;
        long added = 0L;
        long removed = 0L;
        long hotkeys = 0L;
        long translated = 0L;
        long translatedNoHotkeys = 0L;

        for (var e : entries.entrySet()) {
            if (target.entries.containsKey(e.getKey())) {
                target.entries.get(e.getKey()).comment = e.getValue().comment;
                updated++;

                if (e.getValue().comment.contains("Hotkey ")) {
                    hotkeys++;
                }

                if (!target.entries.get(e.getKey()).text.equals(e.getValue().text)) {
                    translated++;

                    if (!e.getValue().comment.contains("Hotkey ")) {
                        translatedNoHotkeys++;
                    }
                }
            } else {
                target.entries.put(e.getKey(), e.getValue());
                added++;
            }
        }

        var it = target.entries.entrySet().iterator();

        while (it.hasNext()) {
            var e = it.next();

            if (!entries.containsKey(e.getKey())) {
                it.remove();
                removed++;
            }
        }

        System.out.println("Added " + added);
        System.out.println("Updated " + updated);
        System.out.println("Removed " + removed);
        System.out.println("Hotkeys " + hotkeys);
        System.out.println("Translated " + translated);
        long translatedPercentage = (long)((double)(translated) / (double)(entries.size()) * 100.0);
        System.out.println("Translated Percentage: " + translatedPercentage + " %");
        System.out.println("Translated (not hotkeys) " + translatedNoHotkeys);
        long translatedNoHotkeysPercentage = (long)((double)(translatedNoHotkeys) / (double)(entries.size()) * 100.0);
        System.out.println("Translated Percentage (no hotkeys): " + translatedNoHotkeysPercentage + " %");
    }

    public void writeIntoFile(String filePath) throws IOException {
        System.out.println("Writing file " + filePath);
        try (OutputStream os = new FileOutputStream(filePath)) {
            try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))) {
                boolean writtenLine = false;

                for (var e : entries.entrySet()) {
                    if (writtenLine) {
                        writer.print(EOL);
                    } else {
                        writer.print(UTF8_BOM);
                    }

                    writer.print("STRING " + e.getKey());
                    writer.print(EOL);
                    if (e.getValue().comment.length() > 0) {
                        writer.print("//" + e.getValue().comment);
                        writer.print(EOL);
                    }
                    writer.print("{");
                    writer.print(EOL);
                    writer.print(e.getValue().text);
                    writer.print(EOL);
                    writer.print("}");
                    writer.print(EOL);
                    writtenLine = true;
                }

                writer.print(EOL);

                System.out.println("Written file " + filePath);
            }
        }
    }

    public static void main(String[] args) {
        final String mapDir = "C:\\Users\\Tamino\\Documents\\Projekte\\wowr\\wowr.w3x";
        final String sourceFilePath = mapDir + "\\war3map.wts";
        final String targetFilePath = mapDir +  "\\_Locales\\deDE.w3mod\\war3map.wts";

        Main.main(new String[] { sourceFilePath, targetFilePath });
    }
}
