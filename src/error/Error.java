package error;

public class Error {
    private final ErrorType errorType;
    private final int lineNumber;

    public Error(ErrorType errorType, int lineNumber) {
        this.errorType = errorType;
        this.lineNumber = lineNumber;
    }

    public ErrorType GetErrorType() {
        return this.errorType;
    }

    public int GetLineNumber() {
        return this.lineNumber;
    }

    @Override
    public String toString() {
        return this.lineNumber + " " + this.errorType;
    }
}
