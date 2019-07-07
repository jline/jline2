package jline.console.completer;

import jline.console.ConsoleReaderTestSupport;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.fusesource.jansi.Ansi.Attribute.INTENSITY_BOLD;
import static org.fusesource.jansi.Ansi.ansi;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CandidateListCompletionHandlerTest extends ConsoleReaderTestSupport {

    @Test
    public void testGreatestCommonPrefixLength() {
        assertEquals(0, CandidateListCompletionHandler.greatestCommonPrefixLength("foo", "bar"));
        assertEquals(0, CandidateListCompletionHandler.greatestCommonPrefixLength("foo", ""));
        assertEquals(3, CandidateListCompletionHandler.greatestCommonPrefixLength("foo", "foobar"));
        assertEquals(3, CandidateListCompletionHandler.greatestCommonPrefixLength("foobar", "foogle"));
    }

    @Test
    public void testCompleteNoCandidates() throws Exception {
        CandidateListCompletionHandler handler = new CandidateListCompletionHandler();
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        assertTrue(handler.complete(console, candidates, 0));

        assertEquals("", console.getCursorBuffer().toString());
    }

    @Test
    public void testCompleteOneCandidate() throws Exception {
        CandidateListCompletionHandler handler = new CandidateListCompletionHandler();
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        candidates.add("foo");
        assertTrue(handler.complete(console, candidates, 0));
        assertEquals("foo ", console.getCursorBuffer().toString());
    }

    @Test
    public void testCompleteOneCandidateInsert() throws Exception {
        CandidateListCompletionHandler handler = new CandidateListCompletionHandler();
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        candidates.add("foobar");
        console.putString("foo");
        console.setCursorPosition(0);
        assertTrue(handler.complete(console, candidates, 0));
        assertEquals("foobar foo", console.getCursorBuffer().toString());
    }
    
    @Test
    public void testCompleteOneCandidateInsertMiddle() throws Exception {
        CandidateListCompletionHandler handler = new CandidateListCompletionHandler();
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        candidates.add("foo");
        console.putString("the fis");
        console.setCursorPosition(5);
        assertTrue(handler.complete(console, candidates, 4));
        assertEquals("the foo is", console.getCursorBuffer().toString());
        assertEquals(8, console.getCursorBuffer().cursor);
    }

    @Test
    public void testCompleteOneCandidateInsertMiddleWhitespace() throws Exception {
        CandidateListCompletionHandler handler = new CandidateListCompletionHandler();
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        candidates.add("foo");
        console.putString("the f is");
        console.setCursorPosition(5);
        assertTrue(handler.complete(console, candidates, 4));
        assertEquals("the foo is", console.getCursorBuffer().toString());
    }

    @Test
    public void testCompleteOneCandidateOverwritePartial() throws Exception {
        CandidateListCompletionHandler handler = new CandidateListCompletionHandler();
        handler.setConsumeMatchingSuffix(true);
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        candidates.add("foobar");
        console.putString("foo");
        console.setCursorPosition(0);
        assertTrue(handler.complete(console, candidates, 0));
        assertEquals("foobar ", console.getCursorBuffer().toString());
    }

    @Test
    public void testCompleteOneCandidateOverwriteNonMatchingPrefix() throws Exception {
        CandidateListCompletionHandler handler = new CandidateListCompletionHandler();
        handler.setConsumeMatchingSuffix(true);
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        candidates.add("foobar");
        console.putString("bum");
        console.setCursorPosition(0);
        assertTrue(handler.complete(console, candidates, 0));
        assertEquals("foobar bum", console.getCursorBuffer().toString());
    }

    @Test
    public void testCompleteOneCandidateOverwritePartialWithin() throws Exception {
        CandidateListCompletionHandler handler = new CandidateListCompletionHandler();
        handler.setConsumeMatchingSuffix(true);
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        candidates.add("foobar");
        console.putString("foo");
        console.setCursorPosition(2);
        assertTrue(handler.complete(console, candidates, 0));
        assertEquals("foobar ", console.getCursorBuffer().toString());
    }

    @Test
    public void testCompleteOneCandidateOverwritePartialEnd() throws Exception {
        CandidateListCompletionHandler handler = new CandidateListCompletionHandler();
        handler.setConsumeMatchingSuffix(true);
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        candidates.add("foobar");
        console.putString("foo");
        console.setCursorPosition(3);
        assertTrue(handler.complete(console, candidates, 0));
        assertEquals("foobar ", console.getCursorBuffer().toString());
    }

    @Test
    public void testCompleteOneCandidateOverwriteFull() throws Exception {
        CandidateListCompletionHandler handler = new CandidateListCompletionHandler();
        handler.setConsumeMatchingSuffix(true);
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        candidates.add("foobar");
        console.putString("the foobar");
        console.setCursorPosition(4);
        assertTrue(handler.complete(console, candidates, 4));
        assertEquals("the foobar ", console.getCursorBuffer().toString());
    }

    @Test
    public void testCompleteOneCandidateOverwriteFullMiddle() throws Exception {
        CandidateListCompletionHandler handler = new CandidateListCompletionHandler();
        handler.setConsumeMatchingSuffix(true);
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        candidates.add("foobar");
        console.putString("the foobar");
        console.setCursorPosition(7);
        assertTrue(handler.complete(console, candidates, 4));
        assertEquals("the foobar ", console.getCursorBuffer().toString());
    }

    @Test
    public void testCompleteOneCandidateOverwriteFullEnd() throws Exception {
        CandidateListCompletionHandler handler = new CandidateListCompletionHandler();
        handler.setConsumeMatchingSuffix(true);
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        candidates.add("foobar");
        console.putString("the foobar");
        console.setCursorPosition(10);
        assertTrue(handler.complete(console, candidates, 4));
        assertEquals("the foobar ", console.getCursorBuffer().toString());
    }

    @Test
    public void testCompleteOneCandidateOverwriteNonMatchingSuffix() throws Exception {
        CandidateListCompletionHandler handler = new CandidateListCompletionHandler();
        handler.setConsumeMatchingSuffix(true);
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        candidates.add("foobar");
        console.putString("foobum");
        console.setCursorPosition(0);
        assertTrue(handler.complete(console, candidates, 0));
        assertEquals("foobar um", console.getCursorBuffer().toString());
    }

    @Test
    public void testCompleteOneCandidatePrefix() throws Exception {
        CandidateListCompletionHandler handler = new CandidateListCompletionHandler();
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        candidates.add("foo");
        String buffer = "the ";
        console.putString(buffer);
        console.moveCursor(buffer.length());
        assertTrue(handler.complete(console, candidates, buffer.length()));
        assertEquals("the foo ", console.getCursorBuffer().toString());
    }

    @Test
    public void testCompleteOneCandidateNoWhitespace() throws Exception {
        CandidateListCompletionHandler handler = new CandidateListCompletionHandler();
        handler.setPrintSpaceAfterFullCompletion(false);
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        candidates.add("foo");
        assertTrue(handler.complete(console, candidates, 0));
        assertEquals("foo", console.getCursorBuffer().toString());
    }

    @Test
    public void testCompleteOneCandidateANSI() throws Exception {
        CandidateListCompletionHandler handler = new CandidateListCompletionHandler();
        //handler.setStripAnsi(true);
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        candidates.add(ansi().a(INTENSITY_BOLD).a("foo").reset().toString());
        assertTrue(handler.complete(console, candidates, 0));
        assertEquals("foo ", console.getCursorBuffer().toString());
    }

    @Test
    public void testCompleteMultiCandidate() throws Exception {
        CandidateListCompletionHandler handler = new CandidateListCompletionHandler();
        //handler.setStripAnsi(true);
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        candidates.add("foobar");
        candidates.add("foobuz");
        assertTrue(handler.complete(console, candidates, 0));
        assertEquals("foob", console.getCursorBuffer().toString());
    }

    @Test
    public void testCompleteMultiCandidateANSI() throws Exception {
        CandidateListCompletionHandler handler = new CandidateListCompletionHandler();
        handler.setStripAnsi(true);
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        candidates.add(ansi().a(INTENSITY_BOLD).a("foobar").reset().toString());
        candidates.add(ansi().a(INTENSITY_BOLD).a("foobuz").reset().toString());
        assertTrue(handler.complete(console, candidates, 0));
        assertEquals("foob", console.getCursorBuffer().toString());
    }

    @Test
    public void testCompleteMultiCandidateANSIDisabled() throws Exception {
        CandidateListCompletionHandler handler = new CandidateListCompletionHandler();
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        candidates.add(ansi().a(INTENSITY_BOLD).a("foobar").reset().toString());
        candidates.add(ansi().a(INTENSITY_BOLD).a("foobuz").reset().toString());
        assertTrue(handler.complete(console, candidates, 0));
        assertTrue(console.getCursorBuffer().toString().endsWith("foob"));
        assertFalse(console.getCursorBuffer().toString().startsWith("foob"));
    }
}
