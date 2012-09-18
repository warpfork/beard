package us.exultant.beard;

public class BeardException extends RuntimeException {
	public BeardException() {
		super();
	}
	
	public BeardException(String $message, Throwable $cause) {
		super($message, $cause);
	}
	
	public BeardException(String $message) {
		super($message);
	}
	
	public BeardException(Throwable $cause) {
		super($cause);
	}
}
