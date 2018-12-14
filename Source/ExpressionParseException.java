class ExpressionParseException extends Exception {

    /**
     * Exception to be thrown if the current
     * Expression cannot be parsed
     *
     * @param message the message given when the Expression cannot
     *                be parsed
     */
    ExpressionParseException(String message) {
        super(message);
    }
}
