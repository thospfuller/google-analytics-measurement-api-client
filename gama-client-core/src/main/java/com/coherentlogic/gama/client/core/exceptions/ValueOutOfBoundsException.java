package com.coherentlogic.gama.client.core.exceptions;

import org.springframework.core.NestedRuntimeException;

/**
 * An exception that is thrown when the value not between the begin and end bounds.
 *
 * @author <a href="https://www.linkedin.com/in/thomasfuller">Thomas P. Fuller</a>
 * @author <a href="mailto:support@coherentlogic.com">Support</a>
 */
public class ValueOutOfBoundsException extends NestedRuntimeException {

    private static final long serialVersionUID = -7567019215464386621L;

    public ValueOutOfBoundsException (String name, int begin, int end, int actual) {
        super ("The actual value (" + actual + ") for " + name + " must be between " + begin + " and " + end + ".");
    }
}
