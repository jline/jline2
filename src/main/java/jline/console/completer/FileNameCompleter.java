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
     * If true, will not offer non-folders, if false, will always suggest files
     */
    private boolean completeFolders = false;

    /**
     * when no further completion is possible, whether to append a blank
     */
    private boolean printSpaceAfterFullCompletion = true;

    /**
     * when argument to be completed starts with a hyphen, whether to take that into account
     */
    private boolean handleLeadingHyphen = false;


    public boolean getCompleteFolders() {
        return completeFolders;
    }

    public void setCompleteFoldersOnly(boolean completeFolders) {
        this.completeFolders = completeFolders;
    }

    public boolean getPrintSpaceAfterFullCompletion() {
        return printSpaceAfterFullCompletion;
    }

    public void setPrintSpaceAfterFullCompletion(boolean printSpaceAfterFullCompletion) {
        this.printSpaceAfterFullCompletion = printSpaceAfterFullCompletion;
    }

    public boolean getHandleLeadingHyphen() {
        return handleLeadingHyphen;
    }

    public void setHandleLeadingHyphen(boolean handleLeadingHyphen) {
        this.handleLeadingHyphen = handleLeadingHyphen;
    }

    static {
        String os = Configuration.getOsName();
        OS_IS_WINDOWS = os.contains("windows");
    }

    public int complete(String buffer, final int cursor, final List<CharSequence> candidates) {
        // buffer can be null
        checkNotNull(candidates);
        String hyphenChar = null;

        if (buffer == null) {
            buffer = "";
        }

        if (OS_IS_WINDOWS) {
            buffer = buffer.replace('/', '\\');
        }

        String translated = buffer;
        if (handleLeadingHyphen && (translated.startsWith("\'") || translated.startsWith("\""))) {
            hyphenChar = translated.substring(0, 1);
            translated = translated.substring(1);
        }

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

        return matchFiles(buffer, translated, entries, candidates, hyphenChar);
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

    protected int matchFiles(final String buffer, final String translated, final File[] files,
                             final List<CharSequence> candidates, final String hyphenChar) {
        if (files == null) {
            return -1;
        }


        for (File file : files) {
            if (completeFolders && !file.isDirectory()) {
                continue;
            }
            if (ignoreFile(file)) {
                continue;
            }
            if (file.getAbsolutePath().startsWith(translated)) {
                CharSequence name = file.getName();
                /*
                 * Basically we need an opening hyphen if there is none yet, or there
                 * is one that we will overwrite with completion.
                 *
                 * We need a closing hyphen when completion is complete (no further path possible)
                 */
                String renderedName = render(name, hyphenChar,
                        /*
                         * completion should possibly render a beginning hyphen if there is none in the buffer, or
                         * there is but there is no foldername involved in the path
                         */
                        hyphenChar == null || buffer.lastIndexOf(separator()) < 0,
                        /*
                         * Completion should possibly render a final hyphen if we complete a file,
                         * or we complete a folder, we only try to complete folders, and there are no subfolders
                         */
                        !file.isDirectory() || (completeFolders && !hasSubfolders(file))).toString();
                if (file.isDirectory()) {
                    // add first candidate folder with separator for file/subfolder
                    if (!completeFolders || hasSubfolders(file)) {
                        // render separator only if has subfolders
                        candidates.add(renderedName + separator());
                    }
                    // add second candidate (folder itself)
                    if (completeFolders) {
                        // add hyphen unless already added before
                        if (hasSubfolders(file)) {
                            if (hyphenChar != null) {
                                renderedName += hyphenChar;
                            } else if (renderedName.startsWith("'") || renderedName.startsWith("\"")) {
                                renderedName += renderedName.charAt(0);
                            }
                        }
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

    // hook to extend Filename COmpleter to exclude certain files / folders
    protected boolean ignoreFile(File file) {
        return false;
    }

    protected boolean hasSubfolders(File dir) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param name
     * @param hyphenChar force hyphenation with this if not null
     * @return name in hyphens if it contains a blank
     */
    protected static CharSequence render(final CharSequence name, final String hyphenChar,
                                         final boolean initialHyphen, final boolean finalHyphen) {
        if (hyphenChar != null) {
            return escapedNameInHyphens(name, hyphenChar, initialHyphen, finalHyphen);
        }
        if (name.toString().contains(" ")) {
            return escapedNameInHyphens(name, "\'", initialHyphen, finalHyphen);
        }
        return name;
    }

    /**
     *
     * @return name in hyphens Strings with hyphens and backslashes escaped
     */
    private static String escapedNameInHyphens(final CharSequence name, final CharSequence hyphen,
                                               final boolean initialHyphen, final boolean finalHyphen) {
        StringBuilder result = new StringBuilder(name.length() + 2 * hyphen.length());
        if (initialHyphen) {
            result.append(hyphen);
        }
        // need to escape every instance of chartoEscape, and every instance of the escape char backslash
        result.append(name.toString().replace("\\", "\\\\").replace(hyphen, "\\" + hyphen));
        if (finalHyphen) {
            result.append(hyphen);
        }
        return result.toString();
    }
}
