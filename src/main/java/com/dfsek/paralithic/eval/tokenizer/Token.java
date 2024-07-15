/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package com.dfsek.paralithic.eval.tokenizer;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * Represents a token of text read from a {@link Tokenizer}.
 * <p>
 * A token consists of a position, a type and up to three string values. The first is the {@code trigger}. Along with
 * the type this uniquely identifies what kind of token we're looking at. For example the input {@code "Hello"}, the
 * id {@code Hello} and a special id like {@code #Hello} all have the same content (Hello). However, they will
 * have different types ({@code STRING}, {@code ID}, {@code SPECIAL_ID}). However, the two special ids
 * {@code $Hello} and {@code #Hello} will both have the same token type and content. Those will differ in their
 * {@code trigger} which will be {@code #} and {@code $} respectively.
 * <p>
 * As already shown, the {@code content} contains the effective content of the token which should be used for
 * further processing. Finally the {@code source} contains the complete text which was consumed while reading the
 * token. If we look at a string constant {@code "Hello"} the content will be {@code Hello} and the source will
 * be {@code "Hello"}.
 * <p>
 * The basic token types supported are:
 * <ul>
 * <li>ID: Represents an name or reference like a function name or variable.</li>
 * <li>SPECIAL_ID: Represents an id which starts with a special character like $ or # and continues with an id</li>
 * <li>STRING: Represents a string constant</li>
 * <li>DECIMAL: Represents a decimal constant</li>
 * <li>SCIENTIFIC_DECIMAL: Represents a decimal constant with a scientific notation</li>
 * <li>INTEGER: Represents an integer constant</li>
 * <li>SYMBOL: Represents any combination of "special" chars like + - ** etc. This will also be all bracket</li>
 * <li>KEYWORD: Represents an ID which was identified as a keyword</li>
 * <li>EOI: Signals the end of input</li>
 * </ul>
 */
public class Token implements Position {

    protected int pos;
    private TokenType type;
    private String trigger = "";
    private String internTrigger;
    private String contents = "";
    private String source = "";

    private int line;

    /*
     * Use one of the static factory methods
     */
    private Token() {
    }

    /**
     * Creates a new token with the given type, using the given position as location info.
     *
     * @param type the type if this token. Can be further specified by supplying a trigger.
     * @param pos  the location of this token
     * @return a new token which can be filled with content and trigger infos
     */
    public static Token create(TokenType type, Position pos) {
        Token result = new Token();
        result.type = type;
        result.line = pos.getLine();
        result.pos = pos.getPos();

        return result;
    }

    /**
     * Creates a new token with the given type, using the Char a initial trigger and content.
     *
     * @param type the type if this token. The supplied Char will be used as initial part of the trigger to further
     *             specify the token
     * @param ch   first character of the content and trigger of this token. Also specifies the position of the token.
     * @return a new token which is initialized with the given Char
     */
    public static Token createAndFill(TokenType type, Char ch) {
        Token result = new Token();
        result.type = type;
        result.line = ch.getLine();
        result.pos = ch.getPos();
        result.contents = ch.getStringValue();
        result.trigger = ch.getStringValue();
        result.source = ch.toString();
        return result;
    }

    /**
     * Adds the given Char to the trigger (and the source) but not to the content
     *
     * @param ch the character to add to the trigger and source
     * @return {@code this} to support fluent method calls
     */
    public Token addToTrigger(Char ch) {
        trigger += ch.getValue();
        internTrigger = null;
        source += ch.getValue();
        return this;
    }

    /**
     * Adds the given Char to the source of this token, but neither to the trigger nor to the content.
     *
     * @param ch the character to add to the source
     * @return {@code this} to support fluent method calls
     */
    public Token addToSource(Char ch) {
        source += ch.getValue();
        return this;
    }

    /**
     * Adds the given Char to the content (and the source) but not to the trigger
     *
     * @param ch the character to add to the content and source
     * @return {@code this} to support fluent method calls
     */
    public Token addToContent(Char ch) {
        return addToContent(ch.getValue());
    }

    /**
     * Adds the given character to the content (and the source) but not to the trigger
     *
     * @param ch the character to add to the content and source
     * @return {@code this} to support fluent method calls
     */
    public Token addToContent(char ch) {
        contents += ch;
        source += ch;
        return this;
    }

    /**
     * Adds a character to the content without adding it to the source.
     *
     * @param ch the character to add to the content
     * @return {@code this} to support fluent method calls
     */
    public Token silentAddToContent(char ch) {
        contents += ch;
        return this;
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
     * Externally sets the content used for this token.
     * <p>
     * This will neither change the trigger nor the source of this token.
     *
     * @param content the new content of this token
     */
    public void setContent(String content) {
        this.contents = content;
    }

    /**
     * Determines if this is an end of input token
     *
     * @return {@code true} if this is an end of input token (with EOI as type), {@code false} otherwise
     */
    public boolean isEnd() {
        return type == TokenType.EOI;
    }

    /**
     * Opposite of {@link #isEnd()}.
     *
     * @return {@code false} if this is an end of input token (with EOI as type), {@code true} otherwise
     */
    public boolean isNotEnd() {
        return type != TokenType.EOI;
    }

    /**
     * Determines if this token was triggered by one of the given triggers.
     *
     * @param triggers a list of possible triggers to compare to
     * @return {@code true} if this token was triggered by one of the given triggers, {@code false} otherwise
     */
    public boolean wasTriggeredBy(String... triggers) {
        return Stream.of(triggers).filter(Objects::nonNull).anyMatch(trigger -> Objects.equals(trigger, getTrigger()));
    }

    /**
     * Returns the string or character which further specifies this token.
     *
     * @return a first character or characters which where used to determine the token type
     */
    public String getTrigger() {
        if(internTrigger == null) {
            internTrigger = trigger.intern();
        }
        return internTrigger;
    }

    /**
     * Externally sets the trigger used for this token.
     * <p>
     * This will neither change the content nor the source of this token.
     *
     * @param trigger the new trigger of this token
     */
    public void setTrigger(String trigger) {
        this.trigger = trigger;
        this.internTrigger = null;
    }

    /**
     * Determines if the given content matches the content of this token.
     *
     * @param content the content to check for
     * @return {@code true} if the content of this token equals the given content (ignoring case),
     * {@code false} otherwise
     */
    public boolean hasContent(String content) {
        if(content == null) {
            throw new IllegalArgumentException("content must not be null");
        }
        return content.equalsIgnoreCase(contents);
    }

    /**
     * Returns the effective content of this token
     *
     * @return the content of this token
     */
    public String getContents() {
        return contents;
    }

    /**
     * Determines if this token is a symbol.
     * <p>
     * If a list of {@code symbols} is given, this method checks that the trigger matches one of them.
     *
     * @param symbols the symbols to check for. If the list es empty, only the token type is checked.
     * @return {@code true} if this token is a symbol and matches one of the given {@code symbols} if the list
     * is not empty.
     */
    public boolean isSymbol(String... symbols) {
        if(symbols.length == 0) {
            return is(TokenType.SYMBOL);
        }
        for(String symbol : symbols) {
            if(matches(TokenType.SYMBOL, symbol)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the token has the given type
     *
     * @param type the expected type
     * @return {@code true} if this token has the given type, {@code false} otherwise
     */
    public boolean is(TokenType type) {
        return this.type == type;
    }

    /**
     * Determines if this token has the given type and trigger.
     *
     * @param type    the expected type
     * @param trigger the expected trigger
     * @return {@code true} if this token matches the given type and trigger, {@code false} otherwise
     */
    public boolean matches(TokenType type, String trigger) {
        if(!is(type)) {
            return false;
        }
        if(trigger == null) {
            throw new IllegalArgumentException("trigger must not be null");
        }

        return Objects.equals(getTrigger(), trigger.intern());
    }

    /**
     * Determines if this token is a keyword.
     * <p>
     * If a list of {@code symbols} is given, this method checks that the trigger matches one of them.
     *
     * @param keywords the keywords to check for. If the list es empty, only the token type is checked.
     * @return {@code true} if this token is a keyword and matches one of the given {@code keywords} if the list
     * is not empty.
     */
    public boolean isKeyword(String... keywords) {
        if(keywords.length == 0) {
            return is(TokenType.KEYWORD);
        }
        for(String keyword : keywords) {
            if(matches(TokenType.KEYWORD, keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if this token is an identifier.
     * <p>
     * If a list of {@code values} is given, this method checks that the content matches one of them.
     *
     * @param values the values to check for. If the list es empty, only the token type is checked.
     * @return {@code true} if this token is an identifier and matches one of the given {@code values} if the list
     * is not empty.
     */
    public boolean isIdentifier(String... values) {
        if(values.length == 0) {
            return is(TokenType.ID);
        }
        for(String value : values) {
            if(matches(TokenType.ID, value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if this token is a special identifier.
     * <p>
     * If a list of {@code triggers} is given, this method checks that the trigger matches one of them.
     *
     * @param triggers the triggers to check for. If the list es empty, only the token type is checked.
     * @return {@code true} if this token is a special identifier and matches one of the given {@code triggers}
     * if the list is not empty.
     */
    public boolean isSpecialIdentifier(String... triggers) {
        if(triggers.length == 0) {
            return is(TokenType.SPECIAL_ID);
        }
        for(String possibleTrigger : triggers) {
            if(matches(TokenType.SPECIAL_ID, possibleTrigger)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if this token is a special identifier with the given trigger.
     * <p>
     * If a list of {@code contents} is given, this method checks that the content matches one of them.
     *
     * @param trigger  the trigger of the special id
     * @param contents the content to check for. If the list es empty, only the token type and the trigger is checked.
     * @return {@code true} if this token is a special identifier with the given trigger.
     * If {@code contents} is not empty, the content must also match one of the elements.
     */
    public boolean isSpecialIdentifierWithContent(String trigger, String... contents) {
        if(!matches(TokenType.SPECIAL_ID, trigger)) {
            return false;
        }
        if(contents.length == 0) {
            return true;
        }
        for(String content : contents) {
            if(content != null && content.equals(this.contents)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if this token is an integer or decimal number.
     *
     * @return {@code true} if this token is an integer or decimal number, {@code false} otherwise
     */
    public boolean isNumber() {
        return isInteger() || isDecimal() || isScientificDecimal();
    }

    /**
     * Determines if this token is an integer number.
     *
     * @return {@code true} if this token is an integer number, {@code false} otherwise
     */
    public boolean isInteger() {
        return is(TokenType.INTEGER);
    }

    /**
     * Determines if this token is a decimal number.
     *
     * @return {@code true} if this token is a decimal number, {@code false} otherwise
     */
    public boolean isDecimal() {
        return is(TokenType.DECIMAL);
    }

    /**
     * Determines if this token is a scientific decimal number (e.g. 3.2e5).
     *
     * @return {@code true} if this token is a scientific decimal number, {@code false} otherwise
     */
    public boolean isScientificDecimal() {
        return is(TokenType.SCIENTIFIC_DECIMAL);
    }

    /**
     * Determines if this token is a string constant
     *
     * @return {@code true} if this token is a string constant, {@code false} otherwise
     */
    public boolean isString() {
        return is(TokenType.STRING);
    }

    @Override
    public String toString() {
        return type.toString() + ":" + source + " (" + line + ":" + pos + ")";
    }

    /**
     * Returns the basic classification of this token
     *
     * @return the type of this toke
     */
    public TokenType getType() {
        return type;
    }

    /**
     * Returns the complete source string consumed while parsing this token
     *
     * @return all characters consumed while parsing this token
     */
    public String getSource() {
        return source;
    }

    /**
     * Externally sets the source used for this token.
     * <p>
     * This will neither change the trigger nor the content of this token.
     *
     * @param source the new source of this token
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * Contains the different token types supported by this class.
     */
    public enum TokenType {
        ID,
        SPECIAL_ID,
        STRING,
        DECIMAL,
        SCIENTIFIC_DECIMAL,
        INTEGER,
        SYMBOL,
        KEYWORD,
        EOI
    }
}
