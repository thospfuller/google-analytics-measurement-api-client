package com.coherentlogic.gama.client.core.exceptions;

import org.springframework.core.NestedRuntimeException;

/**
 * An exception that is thrown when the number of bytes exceeds the maximum.
 *
 * @author <a href="https://www.linkedin.com/in/thomasfuller">Thomas P. Fuller</a>
 * @author <a href="mailto:support@coherentlogic.com">Support</a>
 */
public class MaxLengthInBytesExceededException extends NestedRuntimeException {

    private static final long serialVersionUID = -6096713115303110441L;

    public MaxLengthInBytesExceededException (String name, String value, int maxLengthInBytes) {
        super ("The variable/parameter with name " + name + " has a value " + value + " where the "
            + "number of bytes (" + value.getBytes().length + ") exceeds the maximum number of bytes "
            + "allowed (" + maxLengthInBytes + ").");
    }
}
