package fis.front;

public class FISException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FISException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public FISException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public FISException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public FISException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
}
