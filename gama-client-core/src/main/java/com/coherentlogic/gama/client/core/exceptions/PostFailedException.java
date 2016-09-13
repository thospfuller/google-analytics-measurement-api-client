package com.coherentlogic.gama.client.core.exceptions;

import org.springframework.core.NestedRuntimeException;

/**
 * An exception that is thrown when the post returns an http status code which is not OK.
 *
 * @author <a href="https://www.linkedin.com/in/thomasfuller">Thomas P. Fuller</a>
 * @author <a href="mailto:support@coherentlogic.com">Support</a>
 */
public class PostFailedException extends NestedRuntimeException {

    private static final long serialVersionUID = -5693138994585635428L;

    public PostFailedException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public PostFailedException(String msg) {
        super(msg);
    }
}
