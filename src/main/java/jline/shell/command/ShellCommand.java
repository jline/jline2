/*
 * Copyright (c) 2002-2012, the original author or authors.
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 * http://www.opensource.org/licenses/bsd-license.php
 */
package jline.shell.command;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import jline.console.completer.Completer;
import jline.shell.ShellContext;

/**
 * A command that can be contributed to a shell
 * 
 * @author Baptiste Mesta
 */
public abstract class ShellCommand<T extends ShellContext> {

    public abstract String getName();

    /**
     * @param args
     *            arguments to execute the command
     * @param context
     *            a context given by the shell
     * @return
     *         true if the command was successfully executed
     * @throws Exception
     */
    public abstract boolean execute(List<String> args, T context) throws Exception;

    public List<Completer> getCompleters() {
        return Collections.emptyList();
    }

    /**
     * Implement this to show usage help on this command
     */
    public abstract void printHelp();

    /**
     * Check if given args allow the command to be executed
     * 
     * @param args
     * @return
     *         true if the command can be executed
     */
    public abstract boolean validate(List<String> args);

    /**
     * utiliy method to get argument after an other argument:
     * e.g. if '-u user' is given to the command this return the 'user'
     * 
     * @param args
     * @param key
     * @return
     */
    protected String getParam(final List<String> args, final String key) {
        for (final Iterator<String> iterator = args.iterator(); iterator.hasNext();) {
            final String param = iterator.next();
            if (key.equals(param)) {
                return iterator.next();
            }
        }
        return null;
    }
}
