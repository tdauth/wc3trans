package org.tdauth.wc3trans;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class War3mapWtsTest {

    @Test
    void updateTarget() throws IOException {
        War3mapWts source = new War3mapWts("src/test/resources/source.wts");
        War3mapWts target = new War3mapWts("src/test/resources/target.wts");
        source.updateTarget(target);

        assertEquals(3, target.entries.size());
        assertTrue(target.entries.containsKey(0L));
        assertEquals(0L, target.entries.get(0L).id);
        assertEquals(" New comment", target.entries.get(0L).comment);
        assertEquals("Same text.", target.entries.get(0L).text);
        assertTrue(target.entries.containsKey(1L));
        assertEquals(1L, target.entries.get(1L).id);
        assertEquals(" New entry", target.entries.get(1L).comment);
        assertEquals("New text.", target.entries.get(1L).text);
        assertTrue(target.entries.containsKey(3L));
        assertEquals(3L, target.entries.get(3L).id);
        assertEquals(" New entry", target.entries.get(3L).comment);
        assertEquals("Old text.", target.entries.get(3L).text);

        target.writeIntoFile("target/out.wts");

        assertEquals(
                """
                ﻿STRING 0\r
                // New comment\r
                {\r
                Same text.\r
                }\r
                \r
                STRING 1\r
                // New entry\r
                {\r
                New text.\r
                }\r
                \r
                STRING 3\r
                // New entry\r
                {\r
                Old text.\r
                }\r
                \r
                """, Files.readString(Path.of("target/out.wts")));
    }
}