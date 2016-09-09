package com.coherentlogic.gama.client.core.exceptions;

import org.springframework.core.NestedRuntimeException;

/**
 * An exception that is thrown when the value is less than zero.
 *
 * @author <a href="https://www.linkedin.com/in/thomasfuller">Thomas P. Fuller</a>
 * @author <a href="mailto:support@coherentlogic.com">Support</a>
 */
public class ValueCannotBeNegativeException extends NestedRuntimeException {

    private static final long serialVersionUID = -1375588831476282063L;

    public ValueCannotBeNegativeException (String name, int value) {
        super ("The variable/parameter with name " + name + " has a value " + value + " that is less than zero.");
    }
}
