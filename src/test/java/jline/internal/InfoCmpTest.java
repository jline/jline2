/*
 * Copyright (c) 2002-2016, the original author or authors.
 *
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package jline.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:gnodet@gmail.com">Guillaume Nodet</a>
 */
public class InfoCmpTest {

    @Test
    public void testInfoCmp() {
        Set<String> bools = new HashSet<String>();
        Map<String, Integer> ints = new HashMap<String, Integer>();
        Map<String, String> strings = new HashMap<String, String>();

        String infocmp = InfoCmp.getAnsiCaps();
        InfoCmp.parseInfoCmp(infocmp, bools, ints, strings);
        assertEquals(12, bools.size());
        assertTrue(strings.containsKey("acsc"));
    }

    @Test
    public void testInfoCmpWithHexa() {
        Set<String> bools = new HashSet<String>();
        Map<String, Integer> ints = new HashMap<String, Integer>();
        Map<String, String> strings = new HashMap<String, String>();
        String infocmp = "xterm-256color|xterm with 256 colors,\n" +
                "\tam, bce, ccc, km, mc5i, mir, msgr, npc, xenl,\n" +
                "\tcolors#0x100, cols#80, it#8, lines#24, pairs#0x7fff,\n" +
                "\tacsc=``aaffggiijjkkllmmnnooppqqrrssttuuvvwwxxyyzz{{||}}~~,\n" +
                "\tbel=^G, blink=\\E[5m, bold=\\E[1m, cbt=\\E[Z, civis=\\E[?25l\n";
        InfoCmp.parseInfoCmp(infocmp, bools, ints, strings);
        assertEquals(0x100, (int) ints.get("max_colors"));
        assertEquals(0x7fff, (int) ints.get("max_pairs"));
    }
}
