/*
 * Copyright (c) 2002-2015, the original author or authors.
 *
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package jline.console.completer;

import jline.console.ConsoleReaderTestSupport;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link FileNameCompleter}.
 */
public class FileNameCompleterTest
    extends ConsoleReaderTestSupport
{
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void testCompletionDefaults() throws IOException {
        FileNameCompleter completor = new FileNameCompleter();
        String filename = "file.txt";
        String foldername = "folder";
        testFolder.newFile(filename);
        testFolder.newFolder(foldername);

        String buffer = testFolder.getRoot().getAbsolutePath() +  File.separator;
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        assertEquals(buffer.length(), completor.complete(buffer, 0, candidates));
        assertEqualSet(Arrays.asList(filename + " ", foldername + File.separator), candidates);
    }

    @Test
    public void testCompletionSingleFile() throws IOException {
        FileNameCompleter completor = new FileNameCompleter();
        String filename = "file.txt";
        testFolder.newFile(filename);

        String buffer = testFolder.getRoot().getAbsolutePath() +  File.separator;
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        assertEquals(buffer.length(), completor.complete(buffer, 0, candidates));
        assertEquals(Collections.singletonList(filename + " "), candidates);
    }

    @Test
    public void testCompletionSingleFileNoSuffix() throws IOException {
        FileNameCompleter completor = new FileNameCompleter();
        completor.setPrintSpaceAfterFullCompletion(false);
        String filename = "file.txt";
        testFolder.newFile(filename);

        String buffer = testFolder.getRoot().getAbsolutePath() +  File.separator;
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        assertEquals(buffer.length(), completor.complete(buffer, 0, candidates));
        assertEquals(Collections.singletonList(filename), candidates);
    }

    @Test
    public void testCompletionSingleFolder() throws IOException {
        FileNameCompleter completor = new FileNameCompleter();
        String foldername = "folder";
        testFolder.newFolder(foldername);

        String buffer = testFolder.getRoot().getAbsolutePath() +  File.separator;
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        assertEquals(buffer.length(), completor.complete(buffer, 0, candidates));
        assertEquals(Collections.singletonList(foldername + File.separator), candidates);
    }

    @Test
    public void testCompletionLeadingHyphen() throws IOException {
        FileNameCompleter completor = new FileNameCompleter();
        String filename = "file.txt";
        String foldername = "folder";
        testFolder.newFile(filename);
        testFolder.newFolder(foldername);

        String buffer = testFolder.getRoot().getAbsolutePath() +  File.separator;
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        assertEquals(buffer.length(), completor.complete(buffer, 0, candidates));
        assertEqualSet(Arrays.asList(filename + " ", foldername + File.separator), candidates);
    }

    @Test
    public void testCompletionNoBlankSuffix() throws IOException {
        FileNameCompleter completor = new FileNameCompleter();
        completor.setPrintSpaceAfterFullCompletion(false);
        String filename = "file.txt";
        String foldername = "folder";
        testFolder.newFile(filename);
        testFolder.newFolder(foldername);

        String buffer = testFolder.getRoot().getAbsolutePath() +  File.separator;
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        assertEquals(buffer.length(), completor.complete(buffer, 0, candidates));
        assertEqualSet(Arrays.asList(filename, foldername + File.separator), candidates);
    }

    @Test
    public void testCompletionFoldersOnly() throws IOException {
        FileNameCompleter completor = new FileNameCompleter();
        completor.setCompleteFolders(true);
        completor.setCompleteFiles(false);
        String filename = "file.txt";
        String foldername = "folder";
        testFolder.newFile(filename);
        testFolder.newFolder(foldername);

        String buffer = testFolder.getRoot().getAbsolutePath() +  File.separator;
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        assertEquals(buffer.length(), completor.complete(buffer, 0, candidates));
        assertEquals(Collections.singletonList(foldername + ' '), candidates);
    }

    @Test
    public void testCompletionFoldersAndFiles() throws IOException {
        FileNameCompleter completor = new FileNameCompleter();
        completor.setCompleteFolders(true);
        String filename = "file.txt";
        String foldername = "folder";
        testFolder.newFile(filename);
        testFolder.newFolder(foldername);

        String buffer = testFolder.getRoot().getAbsolutePath() +  File.separator;
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        assertEquals(buffer.length(), completor.complete(buffer, 0, candidates));

        assertEqualSet(Arrays.asList(filename + ' ', foldername + ' ', foldername + File.separator), candidates);
    }

    @Test
    public void testCompletionFolderNoBlankSuffix() throws IOException {
        FileNameCompleter completor = new FileNameCompleter();
        completor.setPrintSpaceAfterFullCompletion(false);
        completor.setCompleteFolders(true);
        completor.setCompleteFiles(false);
        String filename = "file.txt";
        String foldername = "folder";
        testFolder.newFile(filename);
        testFolder.newFolder(foldername);

        String buffer = testFolder.getRoot().getAbsolutePath() +  File.separator;
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        assertEquals(buffer.length(), completor.complete(buffer, 0, candidates));
        assertEquals(Collections.singletonList(foldername), candidates);
    }


    @Test
    public void testCompletionWithPrefix() throws IOException {
        FileNameCompleter completor = new FileNameCompleter();
        String filename = "the file.txt";
        String foldername = "the folder";
        testFolder.newFile(filename);
        testFolder.newFolder(foldername);

        String buffer = testFolder.getRoot().getAbsolutePath() +  File.separator;
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        assertEquals(buffer.length(), completor.complete(buffer + "the", 0, candidates));
        assertEqualSet(Arrays.asList(filename + " ", foldername + File.separator), candidates);
    }

    @Test
    public void testCompletionRelativePath() throws IOException {
        FileNameCompleter completor = new FileNameCompleter() {
            protected File getUserDir() {
                // simulate being in temporary folder
                return testFolder.getRoot();
            }
        };
        //completor.setHandleLeadingHyphen(true);
        String filename = "the file.txt";
        String foldername = "the folder";
        testFolder.newFile(filename);
        testFolder.newFolder(foldername);

        String buffer = "";
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        assertEquals(buffer.length(),  completor.complete(buffer + "the", 0, candidates));
        assertEqualSet(Arrays.asList(filename + " ", foldername + File.separator), candidates);
    }

    @Test
    public void testNestedSubfolders() throws IOException {
        FileNameCompleter completor = new FileNameCompleter() {
            protected File getUserDir() {
                // simulate being in temporary folder
                return testFolder.getRoot();
            }
        };
        String foldername = "the folder";
        String subfoldername = "the subfolder";
        File folder = testFolder.newFolder(foldername);
        File subfolder = new File(folder, subfoldername);
        subfolder.mkdir();

        String buffer = "";
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        // completion must start on hyphen, not after hyphen!
        assertEquals(buffer.length(), completor.complete(buffer, 0, candidates));
        assertEquals(Collections.singletonList(folder.getName() + File.separator), candidates);
    }

    @Test
    public void testNestedSubfoldersFolderOnly() throws IOException {
        FileNameCompleter completor = new FileNameCompleter() {
            protected File getUserDir() {
                // simulate being in temporary folder
                return testFolder.getRoot();
            }
        };
        completor.setCompleteFolders(true);
        completor.setCompleteFiles(false);
        String foldername = "the folder";
        String subfoldername = "the subfolder";
        File folder = testFolder.newFolder(foldername);
        File subfolder = new File(folder, subfoldername);
        subfolder.mkdir();

        String buffer = "";
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        // completion must start on hyphen, not after hyphen!
        assertEquals(buffer.length(), completor.complete(buffer, 0, candidates));
        assertEqualSet(Arrays.asList(folder.getName() + File.separator, folder.getName() + " "), candidates);
    }

    @Test
    public void testNestedSubfoldersFolderOnlyNoSpace() throws IOException {
        FileNameCompleter completor = new FileNameCompleter() {
            protected File getUserDir() {
                // simulate being in temporary folder
                return testFolder.getRoot();
            }
        };
        completor.setCompleteFolders(true);
        completor.setCompleteFiles(false);
        completor.setPrintSpaceAfterFullCompletion(false);
        String foldername = "the folder";
        String subfoldername = "the subfolder";
        File folder = testFolder.newFolder(foldername);
        File subfolder = new File(folder, subfoldername);
        subfolder.mkdir();

        String buffer = "";
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        // completion must start on hyphen, not after hyphen!
        assertEquals(buffer.length(), completor.complete(buffer, 0, candidates));
        assertEqualSet(Arrays.asList(folder.getName() + File.separator, folder.getName()), candidates);
    }

    @Test
    public void testCompletionIgnoreOne() throws IOException {
        FileNameCompleter completor = new FileNameCompleter() {
            @Override
            protected boolean ignoreFile(File file) {
                return file.getName().endsWith(".pdf");
            }
        };
        String filename = "file.txt";
        testFolder.newFile(filename);
        String filename2 = "file.pdf";
        testFolder.newFile(filename2);

        String buffer = testFolder.getRoot().getAbsolutePath() +  File.separator;
        List<CharSequence> candidates = new ArrayList<CharSequence>();
        assertEquals(buffer.length(), completor.complete(buffer, 0, candidates));
        assertEquals(Collections.singletonList(filename + " "), candidates);
    }

    @Test
    public void testMatchFiles_Unix() {
        if(! System.getProperty("os.name").startsWith("Windows")) {
            FileNameCompleter completer = new FileNameCompleter();
            List<CharSequence> candidates = new ArrayList<CharSequence>();
            int resultIndex = completer.matchFiles("foo/bar", "/foo/bar",
                    new File[]{new File("/foo/baroo"), new File("/foo/barbee")}, candidates);
            assertEquals("foo/".length(), resultIndex);
            assertEquals(Arrays.asList("baroo ", "barbee "), candidates);
        }
    }
}
