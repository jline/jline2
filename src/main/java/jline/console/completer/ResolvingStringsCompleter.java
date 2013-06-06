/*
 * Copyright (c) 2002-2012, the original author or authors.
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 * http://www.opensource.org/licenses/bsd-license.php
 */
package jline.console.completer;

import java.util.List;

/**
 * @author Baptiste Mesta
 */
public abstract class ResolvingStringsCompleter extends StringsCompleter {

    @Override
    public int complete(final String buffer, final int cursor, final List<CharSequence> candidates) {
        getStrings().clear();
        final List<String> resolveStrings = resolveStrings();
        if (resolveStrings != null) {
            getStrings().addAll(resolveStrings);
        }
        return super.complete(buffer, cursor, candidates);
    }

    /**
     * @return
     */
    public abstract List<String> resolveStrings();
}
