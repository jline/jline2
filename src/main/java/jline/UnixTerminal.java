/*
 * Copyright (c) 2002-2012, the original author or authors.
 *
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package jline;

import jline.internal.Log;
import jline.internal.TerminalLineSettings;

/**
 * Terminal that is used for unix platforms. Terminal initialization
 * is handled by issuing the <em>stty</em> command against the
 * <em>/dev/tty</em> file to disable character echoing and enable
 * character input. All known unix systems (including
 * Linux and Macintosh OS X) support the <em>stty</em>), so this
 * implementation should work for an reasonable POSIX system.
 *
 * @author <a href="mailto:mwp1@cornell.edu">Marc Prud'hommeaux</a>
 * @author <a href="mailto:dwkemp@gmail.com">Dale Kemp</a>
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @author <a href="mailto:jbonofre@apache.org">Jean-Baptiste Onofré</a>
 * @since 2.0
 */
public class UnixTerminal
    extends TerminalSupport
{
    public UnixTerminal() throws Exception {
        super(true);
        TerminalLineSettings.initialize();
    }

    /**
     * Remove line-buffered input by invoking "stty -icanon min 1"
     * against the current terminal.
     */
    @Override
    public void init() throws Exception {
        super.init();

        setAnsiSupported(true);

        // Set the console to be character-buffered instead of line-buffered.
        // Make sure we're distinguishing carriage return from newline.
        // Allow ctrl-s keypress to be used (as forward search)
        TerminalLineSettings.set("-icanon min 1 -icrnl -inlcr -ixon");
        TerminalLineSettings.set("dsusp undef");

        setEchoEnabled(false);
    }

    /**
     * Restore the original terminal configuration, which can be used when
     * shutting down the console reader. The ConsoleReader cannot be
     * used after calling this method.
     */
    @Override
    public void restore() throws Exception {
        TerminalLineSettings.restore();
        super.restore();
    }

    /**
     * Returns the value of <tt>stty columns</tt> param.
     */
    @Override
    public int getWidth() {
        int w = TerminalLineSettings.getProperty("columns");
        return w < 1 ? DEFAULT_WIDTH : w;
    }

    /**
     * Returns the value of <tt>stty rows>/tt> param.
     */
    @Override
    public int getHeight() {
        int h = TerminalLineSettings.getProperty("rows");
        return h < 1 ? DEFAULT_HEIGHT : h;
    }

    @Override
    public synchronized void setEchoEnabled(final boolean enabled) {
        try {
            if (enabled) {
                TerminalLineSettings.set("echo");
            }
            else {
                TerminalLineSettings.set("-echo");
            }
            super.setEchoEnabled(enabled);
        }
        catch (Exception e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            Log.error("Failed to ", (enabled ? "enable" : "disable"), " echo", e);
        }
    }

    public void disableInterruptCharacter()
    {
        try {
            TerminalLineSettings.set("intr undef");
        }
        catch (Exception e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            Log.error("Failed to disable interrupt character", e);
        }
    }

    public void enableInterruptCharacter()
    {
        try {
            TerminalLineSettings.set("intr ^C");
        }
        catch (Exception e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            Log.error("Failed to enable interrupt character", e);
        }
    }
}
