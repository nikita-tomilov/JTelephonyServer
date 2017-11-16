package com.programmer74.jtdb;

import java.util.Date;

public class Token implements java.io.Serializable{
    private String TokenString;
    private Date ExpiresAt;

    public Token() {

    }

    public String getTokenString() {
        return TokenString;
    }

    public void setTokenString(String tokenString) {
        TokenString = tokenString;
    }

    public Date getExpiresAt() {
        return ExpiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        ExpiresAt = expiresAt;
    }

    @Override
    public String toString() {
        return "Token{" +
                "TokenString='" + TokenString + '\'' +
                ", ExpiresAt=" + ExpiresAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Token token = (Token) o;

        if (TokenString != null ? !TokenString.equals(token.TokenString) : token.TokenString != null) return false;
        return ExpiresAt != null ? ExpiresAt.equals(token.ExpiresAt) : token.ExpiresAt == null;
    }

    @Override
    public int hashCode() {
        int result = TokenString != null ? TokenString.hashCode() : 0;
        result = 31 * result + (ExpiresAt != null ? ExpiresAt.hashCode() : 0);
        return result;
    }
}