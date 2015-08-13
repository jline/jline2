/*
 * Copyright (c) 2002-2015, the original author or authors.
 *
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package jline.console.completer;

import jline.internal.Configuration;

import java.io.File;
import java.util.List;

import static jline.internal.Preconditions.checkNotNull;

/**
 * A file name completer takes the buffer and issues a list of
 * potential completions.
 * <p/>
 * This completer tries to behave as similar as possible to
 * <i>bash</i>'s file name completion (using GNU readline)
 * with the following exceptions:
 * <p/>
 * <ul>
 * <li>Candidates that are directories will end with "/"</li>
 * <li>Wildcard regular expressions are not evaluated or replaced</li>
 * <li>The "~" character can be used to represent the user's home,
 * but it cannot complete to other users' homes, since java does
 * not provide any way of determining that easily</li>
 * </ul>
 *
 * @author <a href="mailto:mwp1@cornell.edu">Marc Prud'hommeaux</a>
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.3
 */
public class FileNameCompleter
    implements Completer
{

    private static final boolean OS_IS_WINDOWS;

    /**
     * If true, will folders as final completion result
     */
    private boolean completeFolders = false;

    /**
     * If false, will not offer files
     */
    private boolean completeFiles = true;

    /**
     * whether to append a blank after full completion (depending on whether more arguments will follow this one)
     */
    private boolean printSpaceAfterFullCompletion = true;

    public boolean getCompleteFolders() {
        return completeFolders;
    }

    public void setCompleteFolders(boolean completeFolders) {
        this.completeFolders = completeFolders;
    }

    public boolean getCompleteFiles() {
        return completeFiles;
    }

    public void setCompleteFiles(boolean completeFiles) {
        this.completeFiles = completeFiles;
    }

    public boolean getPrintSpaceAfterFullCompletion() {
        return printSpaceAfterFullCompletion;
    }

    public void setPrintSpaceAfterFullCompletion(boolean printSpaceAfterFullCompletion) {
        this.printSpaceAfterFullCompletion = printSpaceAfterFullCompletion;
    }

    static {
        String os = Configuration.getOsName();
        OS_IS_WINDOWS = os.contains("windows");
    }

    public int complete(String buffer, final int cursor, final List<CharSequence> candidates) {
        // buffer can be null
        checkNotNull(candidates);

        if (buffer == null) {
            buffer = "";
        }

        if (OS_IS_WINDOWS) {
            buffer = buffer.replace('/', '\\');
        }

        String translated = buffer;

        // Special character: ~ maps to the user's home directory in most OSs
        if (!OS_IS_WINDOWS && translated.startsWith("~")) {
            File homeDir = getUserHome();
            if (translated.startsWith("~" + separator())) {
                translated = homeDir.getPath() + translated.substring(1);
            }
            else {
                translated = homeDir.getParentFile().getAbsolutePath();
            }
        }
        else if (!(new File(translated).isAbsolute())) {
            String cwd = getUserDir().getAbsolutePath();
            translated = cwd + separator() + translated;
        }

        File file = new File(translated);
        final File dir;

        if (translated.endsWith(separator())) {
            dir = file;
        }
        else {
            dir = file.getParentFile();
        }

        File[] entries = dir == null ? new File[0] : dir.listFiles();

        return matchFiles(buffer, translated, entries, candidates);
    }

    protected String separator() {
        return File.separator;
    }

    protected File getUserHome() {
        return Configuration.getUserHome();
    }

    protected File getUserDir() {
        return new File(".");
    }

    protected int matchFiles(final String buffer, final String prefix, final File[] files, final List<CharSequence> candidates) {
        if (files == null) {
            return -1;
        }


        for (File file : files) {
            if (!completeFiles && !file.isDirectory()) {
                continue;
            }
            if (file.getAbsolutePath().startsWith(prefix)) {
                String renderedName = render(file, file.getName()).toString();
                if (file.isDirectory()) {
                    // add first candidate folder with separator for file/subfolder
                    if (completeFiles || hasSubfolders(file)) {
                        // render separator only if has subfolders
                        candidates.add(renderedName + separator());
                    }
                    // add second candidate (folder itself)
                    if (completeFolders) {
                        if (printSpaceAfterFullCompletion) {
                            renderedName += ' ';
                        }
                        candidates.add(renderedName);
                    }
                } else {
                    if (printSpaceAfterFullCompletion) {
                        renderedName += ' ';
                    }
                    candidates.add(renderedName);
                }
            }
        }

        final int index = buffer.lastIndexOf(separator());

        return index + separator().length();
    }

    protected boolean hasSubfolders(File dir) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                return true;
            }
        }
        return false;
    }

    protected CharSequence render(final File file, final CharSequence name) {
        return name;
    }
}
