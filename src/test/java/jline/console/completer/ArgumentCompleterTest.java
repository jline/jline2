/*
 * Copyright (c) 2002-2016, the original author or authors.
 *
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package jline.console.completer;

import jline.console.ConsoleReaderTestSupport;
import jline.console.completer.ArgumentCompleter;
import jline.console.completer.StringsCompleter;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;


/**
 * Tests for {@link jline.console.completer.ArgumentCompleter}.
 *
 * @author <a href="mailto:mwp1@cornell.edu">Marc Prud'hommeaux</a>
 */
public class ArgumentCompleterTest
    extends ConsoleReaderTestSupport
{
    @Test
    public void test1() throws Exception {
        console.addCompleter(new ArgumentCompleter(new StringsCompleter("foo", "bar", "baz")));

        assertBuffer("foo foo ", new Buffer("foo f").tab());
        assertBuffer("foo ba", new Buffer("foo b").tab());
        assertBuffer("foo ba", new Buffer("foo ba").tab());
        assertBuffer("foo baz ", new Buffer("foo baz").tab());

        // test completion in the mid range
        assertBuffer("foo baz", new Buffer("f baz").left().left().left().left().tab());
        assertBuffer("ba foo", new Buffer("b foo").left().left().left().left().tab());
        assertBuffer("foo ba baz", new Buffer("foo b baz").left().left().left().left().tab());
        assertBuffer("foo foo baz", new Buffer("foo f baz").left().left().left().left().tab());
    }

    @Test
    public void testMultiple() throws Exception {
        ArgumentCompleter argCompleter = new ArgumentCompleter(
                new StringsCompleter("bar", "baz"),
                new StringsCompleter("foo"),
                new StringsCompleter("ree"));
        console.addCompleter(argCompleter);

        assertBuffer("bar foo ", new Buffer("bar f").tab());
        assertBuffer("baz foo ", new Buffer("baz f").tab());
        // co completion of 2nd arg in strict mode when 1st argument is not matched exactly
        assertBuffer("ba f", new Buffer("ba f").tab());
        assertBuffer("bar fo r", new Buffer("bar fo r").tab());

        argCompleter.setStrict(false);
        assertBuffer("ba foo ", new Buffer("ba f").tab());
        assertBuffer("ba fo ree ", new Buffer("ba fo r").tab());
    }

    @Test
    public void test2() throws Exception {
        console.addCompleter(
                new ArgumentCompleter(
                        new StringsCompleter("some", "any"),
                        new StringsCompleter("foo", "bar", "baz")));

        assertBuffer("some foo ", new Buffer("some fo").tab());
    }

    @Test
    public void testQuoted() throws Exception {
        ArgumentCompleter argCompleter = new ArgumentCompleter(
                new StringsCompleter("bar"),
                new StringsCompleter("foo "));
        console.addCompleter(argCompleter);

        assertBuffer("'bar' 'foo' ", new Buffer("'bar' 'f").tab());
    }

    @Test
    public void testArgumentDelimiter() throws Exception {
        ArgumentCompleter.WhitespaceArgumentDelimiter wsDelimiter = new ArgumentCompleter.WhitespaceArgumentDelimiter();
        String buffer = "\"a\\\"a2\"'b\\'b2'c\\ c2 d\\\\d2\\e";
        List<String> expected = Arrays.asList("a\"a2", "b'b2", "c c2", "d\\d2e");
        assertEquals(expected, Arrays.asList(wsDelimiter.delimit(buffer, buffer.length()).getArguments()));
        assertEquals("a\\ b\\\"c\\\'d\\\\e", wsDelimiter.escapeArgument("a b\"c'd\\e"));
        assertEquals("a\\ b\\\"c\\\'d\\\\e ", wsDelimiter.escapeArgument("a b\"c'd\\e "));
    }

    @Test
    public void testEscaping() throws Exception {
        ArgumentCompleter argCompleter = new ArgumentCompleter(
                new StringsCompleter("bar"),
                new StringsCompleter("foo foo2"));
        console.addCompleter(argCompleter);

        assertBuffer("bar foo\\ foo2 ", new Buffer("bar f").tab());
    }

    @Test
    public void testEscapingQuoted() throws Exception {
        ArgumentCompleter argCompleter = new ArgumentCompleter(
                new StringsCompleter("bar"),
                new StringsCompleter("foo foo2"));
        console.addCompleter(argCompleter);
        boolean backup = ((CandidateListCompletionHandler) console.getCompletionHandler()).getPrintSpaceAfterFullCompletion();
        try {
            ((CandidateListCompletionHandler) console.getCompletionHandler()).setPrintSpaceAfterFullCompletion(false);
            assertBuffer("bar 'foo foo2", new Buffer("bar 'f").tab());
        } finally {
            ((CandidateListCompletionHandler) console.getCompletionHandler()).setPrintSpaceAfterFullCompletion(backup);
        }
    }

    @Test
    public void testEscapingQuotedBlank() throws Exception {
        ArgumentCompleter argCompleter = new ArgumentCompleter(
                new StringsCompleter("bar"),
                new StringsCompleter("foo foo2 "));
        console.addCompleter(argCompleter);
        boolean backup = ((CandidateListCompletionHandler) console.getCompletionHandler()).getPrintSpaceAfterFullCompletion();
        try {
            assertBuffer("bar 'foo foo2' ", new Buffer("bar 'f").tab());
        } finally {
            ((CandidateListCompletionHandler) console.getCompletionHandler()).setPrintSpaceAfterFullCompletion(backup);
        }
    }
}
