package be.tgermain.m3uepgmatcher.m3u;

/**
 * Created by tgermain on 30/12/2017.
 */
public class M3uParsingException extends RuntimeException {

    private int line;

    public M3uParsingException(int line, String message) {
        super(message + " at line " + line);
        this.line = line;
    }

    public M3uParsingException(int line, String message, Exception cause) {
        super(message + " at line " + line, cause);
        this.line = line;
    }

    public int getLine() {
        return line;
    }
}
