package ss.framework.entities.xmlentities;

import ss.common.UnexpectedRuntimeException;

public class CannotCreateEntityException extends UnexpectedRuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 3507723891859943456L;

    /**
     * @param message
     */
    public CannotCreateEntityException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public CannotCreateEntityException(Exception cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }


}

