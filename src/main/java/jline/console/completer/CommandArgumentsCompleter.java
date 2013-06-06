/*
 * Copyright (c) 2002-2012, the original author or authors.
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 * http://www.opensource.org/licenses/bsd-license.php
 */
package jline.console.completer;

import static jline.internal.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.List;

import jline.shell.ShellContext;
import jline.shell.command.ShellCommand;

/**
 * Allow to complete a set of given commands
 * Each command can have different completer
 * At the start of the line the completer complete with command name
 * Then it completes the line using command's completers
 * 
 * @author Baptiste Mesta
 */
public class CommandArgumentsCompleter<T extends ShellContext> implements Completer {

    private final HashMap<String, ShellCommand<T>> commands;

    private final StringsCompleter commandCompleter;

    /**
     * @param commands
     */
    public CommandArgumentsCompleter(final HashMap<String, ShellCommand<T>> commands) {
        this.commands = commands;
        commandCompleter = new StringsCompleter(commands.keySet());
    }

    public int complete(final String buffer, final int cursor, final List<CharSequence> candidates) {
        checkNotNull(candidates);
        final int pos = commandCompleter.complete(buffer, cursor, candidates);
        if (pos != -1) {
            return pos;
        }
        if (buffer != null) {
            final ArgumentParser argumentParser = new ArgumentParser(buffer);
            final String command = argumentParser.getCommand();
            if (command != null) {
                final int lastArgumentIndex = Math.max(argumentParser.getLastArgumentIndex(), 0);
                // complete with element from completer of the command
                final ShellCommand<T> clientCommand = commands.get(command);
                if (clientCommand != null) {
                    final List<Completer> completers = clientCommand.getCompleters();
                    if (completers.size() > lastArgumentIndex) {
                        final Completer completer = completers.get(lastArgumentIndex);
                        final String lastArgument = argumentParser.getLastArgument();
                        final int complete = completer.complete(lastArgument, lastArgument != null ? lastArgument.length() : 0, candidates);
                        return complete + argumentParser.getOffset();
                    }
                }
            }
        }

        if (candidates.size() == 1) {
            candidates.set(0, candidates.get(0) + " ");
        }
        return candidates.isEmpty() ? -1 : cursor;
    }

}
