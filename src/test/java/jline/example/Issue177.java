/*
 * Copyright (c) 2002-2016, the original author or authors.
 *
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package jline.example;

import java.io.IOException;

import jline.console.ConsoleReader;

public class Issue177 {

    public static void main(String[] args) throws IOException {

        ConsoleReader reader = new ConsoleReader();

        String output = reader.readLine("What is your quest? > ", null, "To seek the Holy Grail.");

        System.out.println("The quest is '" + output + "'");
    }
}
