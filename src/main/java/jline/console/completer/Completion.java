/*
 * Copyright (c) 2014, the original author or authors.
 *
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package jline.console.completer;

import static jline.internal.Preconditions.checkNotNull;
/**
 * A completion proposal which, in addition to its actual value, can have:<ul>
 * <li>a heading under which it may be categorized</li>
 * <li>a display label, to be used as an alternative to the value when outputting to 
 * the screen (<i>e.g.</i> to save screen space or apply ANSI effects)</li>
 * </ul>
 * 
 * @author Eric Bottard
 */
public class Completion {
	
	private final CharSequence value;
	
	private final CharSequence heading;
	
	private final CharSequence label;

	public Completion(CharSequence value, CharSequence heading,
			CharSequence label) {
		checkNotNull(value);
		checkNotNull(label);
		this.value = value;
		this.heading = heading;
		this.label = label;
	}

	public Completion(CharSequence value) {
		this(value, null, value);
	}

	public CharSequence getValue() {
		return value;
	}

	public CharSequence getHeading() {
		return heading;
	}

	public CharSequence getLabel() {
		return label;
	}
	
	

}
