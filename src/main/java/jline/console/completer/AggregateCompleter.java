/*
 * Copyright (c) 2002-2012, the original author or authors.
 *
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package jline.console.completer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static jline.internal.Preconditions.checkNotNull;

/**
 * Completer which contains multiple completers and aggregates them together.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.3
 */
public class AggregateCompleter
    implements Completer
{
    private final List<Completer> completers = new ArrayList<Completer>();

    public AggregateCompleter() {
        // empty
    }

    /**
     * Construct an AggregateCompleter with the given collection of completers.
     * The completers will be used in the iteration order of the collection.
     *
     * @param completers the collection of completers
     */
    public AggregateCompleter(final Collection<Completer> completers) {
        checkNotNull(completers);
        this.completers.addAll(completers);
    }

    /**
     * Construct an AggregateCompleter with the given completers.
     * The completers will be used in the order given.
     *
     * @param completers the completers
     */
    public AggregateCompleter(final Completer... completers) {
        this(Arrays.asList(completers));
    }

    /**
     * Retrieve the collection of completers currently being aggregated.
     *
     * @return the aggregated completers
     */
    public Collection<Completer> getCompleters() {
        return completers;
    }

    /**
     * Perform a completion operation across all aggregated completers.
     *
     * @see Completer#complete(String, int, List)
     * @return the highest completion return value from all completers
     */
    public int complete(final String buffer, final int cursor, final List<Completion> candidates) {
        // buffer could be null
        checkNotNull(candidates);

        List<CompletionResult> completions = new ArrayList<CompletionResult>(completers.size());

        // Run each completer, saving its completion results
        int max = -1;
        for (Completer completer : completers) {
            CompletionResult completion = new CompletionResult(candidates);
            completion.complete(completer, buffer, cursor);

            // Compute the max cursor position
            max = Math.max(max, completion.cursor);

            completions.add(completion);
        }

        // Append candidates from completions which have the same cursor position as max
        for (CompletionResult completion : completions) {
            if (completion.cursor == max) {
                candidates.addAll(completion.candidates);
            }
        }

        return max;
    }

    /**
     * @return a string representing the aggregated completers
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
            "completers=" + completers +
            '}';
    }

    private class CompletionResult
    {
        public final List<Completion> candidates;

        public int cursor;

        public CompletionResult(final List<Completion> candidates) {
            checkNotNull(candidates);
            this.candidates = new LinkedList<Completion>(candidates);
        }

        public void complete(final Completer completer, final String buffer, final int cursor) {
            checkNotNull(completer);
            this.cursor = completer.complete(buffer, cursor, candidates);
        }
    }
}
