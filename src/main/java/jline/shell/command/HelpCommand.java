/*
 * Copyright (c) 2002-2012, the original author or authors.
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 * http://www.opensource.org/licenses/bsd-license.php
 */
package jline.shell.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;
import jline.shell.ShellContext;

/**
 * Default implementation of the help command
 * 
 * @author Baptiste Mesta
 */
public class HelpCommand<T extends ShellContext> extends ShellCommand<T> {

    private final HashMap<String, ShellCommand<T>> commands;

    /**
     * @param commands
     */
    public HelpCommand(final HashMap<String, ShellCommand<T>> commands) {
        this.commands = commands;
    }

    @Override
    public boolean execute(final List<String> args, final T context) throws Exception {
        commands.get(args.get(0)).printHelp();
        return true;
    }

    @Override
    public void printHelp() {
        printUsage();
    }

    @Override
    public boolean validate(final List<String> args) {
        return args.size() == 1 && commands.containsKey(args.get(0));
    }

    @Override
    public List<Completer> getCompleters() {
        return Arrays.asList((Completer) new StringsCompleter(commands.keySet()));
    }

    private void printUsage() {
        System.out.println("-----------------------------------------------");
        System.out.println("Usage: <Command> <arguments>");
        System.out.println("Command can be:");
        final Set<String> keySet = commands.keySet();
        final ArrayList<String> list = new ArrayList<String>(keySet);
        Collections.sort(list);
        for (final String entry : list) {
            System.out.println(entry);
        }
        System.out.println("");
        System.out.println("Use 'PlatformAdmin help <Command>' for help about a command");
        System.out.println("-----------------------------------------------");
    }

    @Override
    public String getName() {
        return "help";
    }
}
