package cn.tianya.fw.front;

public class FisException extends Exception {

	/**
     * 
     */
    private static final long serialVersionUID = -5964463953244879874L;

    public FisException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public FisException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public FisException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public FisException(Throwable cause) {
		super(cause);
	}
}
