package org.tdauth.wc3trans;

import java.io.*;
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

        for (var e : entries.entrySet()) {
            if (target.entries.containsKey(e.getKey())) {
                target.entries.get(e.getKey()).comment = e.getValue().comment;
                updated++;
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
    }

    public void writeIntoFile(String filePath) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(filePath, "UTF-8");
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

        writer.close();
    }

    public static void main(String[] args) {
        try {
            final String sourceFilePath = "C:\\Users\\Tamino\\Documents\\Projekte\\wowr\\wowr.w3x\\war3map.wts";
            final String targetFilePath = "C:\\Users\\Tamino\\Documents\\Projekte\\wowr\\wowr.w3x\\_Locales\\deDE.w3mod\\war3map.wts";
            War3mapWts source = new War3mapWts(sourceFilePath);
            War3mapWts target = new War3mapWts(targetFilePath);
            source.updateTarget(target);
            target.writeIntoFile(targetFilePath);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
