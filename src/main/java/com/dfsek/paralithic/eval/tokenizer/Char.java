/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package com.dfsek.paralithic.eval.tokenizer;

/**
 * Represents a single character read from a {@link LookaheadReader}.
 * <p>
 * Provides the value as well as an exact position of the character in the stream. Also some test methods are
 * provided to determine the character class of the internal value.
 *
 * @see LookaheadReader
 */
public class Char implements Position {
    private final char value;
    private final int line;
    private final int pos;

    public Char(char value, int line, int pos) {
        this.value = value;
        this.line = line;
        this.pos = pos;
    }

    /**
     * Returns the value of this char.
     *
     * @return the internal value read from the stream
     */
    public char getValue() {
        return value;
    }

    @Override
    public int getLine() {
        return line;
    }

    @Override
    public int getPos() {
        return pos;
    }

    /**
     * Determines if the value is a digit (0..9)
     *
     * @return {@code true} if the internal value is a digit, {@code false} otherwise
     */
    public boolean isDigit() {
        return Character.isDigit(value);
    }

    /**
     * Determines if the value is a letter (a..z, A..Z)
     *
     * @return {@code true} if the internal value is a letter, {@code false} otherwise
     */
    public boolean isLetter() {
        return Character.isLetter(value);
    }

    /**
     * Determines if the value is a whitespace character like a blank, tab or line break
     *
     * @return {@code true} if the internal value is a whitespace character, {@code false} otherwise
     */
    public boolean isWhitespace() {
        return Character.isWhitespace(value) && !isEndOfInput();
    }

    /**
     * Determines if this instance represents the end of input indicator
     *
     * @return {@code true} if this instance represents the end of the underlying input,
     * {@code false} otherwise
     */
    public boolean isEndOfInput() {
        return value == '\0';
    }

    /**
     * Determines if the value is a line break
     *
     * @return {@code true} if the internal value is a line break, {@code false} otherwise
     */
    public boolean isNewLine() {
        return value == '\n';
    }

    @Override
    public String toString() {
        if(isEndOfInput()) {
            return "<End Of Input>";
        } else {
            return String.valueOf(value);
        }
    }

    /**
     * Checks if the internal value is one of the given characters
     *
     * @param tests the characters to check against
     * @return {@code true} if the value equals to one of the give characters, {@code false} otherwise
     */
    public boolean is(char... tests) {
        for(char test : tests) {
            if(test == value && test != '\0') {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the internal value as string.
     *
     * @return the internal character as string or "" if this is the end of input indicator
     */
    public String getStringValue() {
        if(isEndOfInput()) {
            return "";
        }
        return String.valueOf(value);
    }
}
