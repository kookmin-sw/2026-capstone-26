package backend.capstone.domain.region.exception;

public class RegionSeedException extends RuntimeException {

    public RegionSeedException(String message) {
        super(message);
    }

    public RegionSeedException(String message, Throwable cause) {
        super(message, cause);
    }
}
