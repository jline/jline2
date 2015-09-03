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
