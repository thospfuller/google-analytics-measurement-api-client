package com.coherentlogic.gama.client.core.exceptions;

import org.springframework.core.NestedRuntimeException;

/**
 * An exception that is thrown when the queue time is negative.
 *
 * @author <a href="https://www.linkedin.com/in/thomasfuller">Thomas P. Fuller</a>
 * @author <a href="mailto:support@coherentlogic.com">Support</a>
 */
public class InvalidQueueTime extends NestedRuntimeException {

    private static final long serialVersionUID = -1572018217017729742L;

    public InvalidQueueTime (long queueTimeMillis) {
        super ("The queue time cannot be negative (queueTimeMillis: " + queueTimeMillis + ")");
    }
}
