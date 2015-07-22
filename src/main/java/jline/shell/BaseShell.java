/*
 * Copyright (c) 2002-2012, the original author or authors.
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 * http://www.opensource.org/licenses/bsd-license.php
 */
package jline.shell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import jline.console.ConsoleReader;
import jline.console.completer.CommandArgumentsCompleter;
import jline.shell.command.HelpCommand;
import jline.shell.command.ShellCommand;

/**
 * A basic shell
 * Implement abstract methods
 * to run it just execute (e.g. in a main)
 * shell.run();
 * 
 * @author Baptiste Mesta
 */
public abstract class BaseShell<T extends ShellContext> {

    private HashMap<String, ShellCommand<T>> commands;

    private HelpCommand<T> helpCommand;

    protected void init() throws Exception {
        final List<ShellCommand<T>> commandList = initShellCommands();
        commands = new HashMap<String, ShellCommand<T>>();
        for (final ShellCommand<T> shellCommand : commandList) {
            commands.put(shellCommand.getName(), shellCommand);
        }
        helpCommand = getHelpCommand();
        if (helpCommand != null) {
            commands.put(helpCommand.getName(), helpCommand);
        }

    }

    /**
     * return the help command used
     * Can be overridden
     */
    protected HelpCommand<T> getHelpCommand() {
        return new HelpCommand<T>(commands);
    }

    /**
     * @return
     *         list of commands contributed to the shell
     * @throws Exception
     */
    protected abstract List<ShellCommand<T>> initShellCommands() throws Exception;

    /**
     * called by {@link BaseShell} when the shell is exited
     * 
     * @throws Exception
     */
    protected void destroy() throws Exception {
    }

    public void run() throws Exception {
        init();
        printWelcomeMessage();
        final ConsoleReader reader = new ConsoleReader();
        reader.setBellEnabled(false);
        final CommandArgumentsCompleter<T> commandArgumentsCompleter = new CommandArgumentsCompleter<T>(commands);

        reader.addCompleter(commandArgumentsCompleter);

        String line;
        while ((line = reader.readLine("\n" + getPrompt())) != null) {
            final List<String> args = parse(line);
            final String command = args.remove(0);
            if (commands.containsKey(command)) {
                final ShellCommand<T> clientCommand = commands.get(command);
                if (clientCommand.validate(args)) {
                    try {
                        clientCommand.execute(args, getContext());
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    clientCommand.printHelp();
                }
            } else if ("exit".equals(line)) {
                System.out.println("Exiting application");
                destroy();
                return;
            } else {
                System.out.println("Wrong argument");
                helpCommand.printHelp();
            }
        }
        destroy();
    }

    /**
     * @return
     */
    protected abstract T getContext();

    /**
     * Override this to print a welcom message
     */
    protected abstract void printWelcomeMessage();

    /**
     * allow to specify the prompt used
     */
    protected abstract String getPrompt();

    /**
     * used to parse arguments of the line
     * 
     * @param line
     * @return
     */
    protected List<String> parse(final String line) {
        return new ArrayList<String>(Arrays.asList(line.trim().split("(\\s)+")));
    }

}
