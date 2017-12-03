package com.programmer74.jtdb;

import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.*;

public class TokenUserType implements UserType, Serializable{

    private final static int SQL_TYPE = Types.STRUCT;
    private final static String DB_OBJEXT_TYPE = "login_token_t";


    @Override
    public int[] sqlTypes() {
        return new int[] { SQL_TYPE };
    }

    @Override
    public Class returnedClass(){
        return Token.class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        if (null == x || null == y) return false;
        return x.equals(y);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String[] strings, SessionImplementor sessionImplementor, Object o) throws HibernateException, SQLException {
        final Struct struct = (Struct) resultSet.getObject(strings[0]);
        if (resultSet.wasNull()) return null;

        final Token Token = new Token();

        Token.setTokenString((String)struct.getAttributes()[0]);
        Token.setExpiresAt(new Date(((Timestamp)struct.getAttributes()[1]).getTime()));
        return Token;
    }

    @Override
    public void nullSafeSet(PreparedStatement preparedStatement, Object o, int i, SessionImplementor sessionImplementor) throws HibernateException, SQLException {
        if (o == null) preparedStatement.setNull(i, SQL_TYPE, DB_OBJEXT_TYPE);
        else {
            final Token Token = (Token) o;
            final Object[] values = new Object[] {Token.getTokenString(), Token.getExpiresAt()};
            final Connection connection = preparedStatement.getConnection();
            final STRUCT struct = new STRUCT(StructDescriptor.createDescriptor(DB_OBJEXT_TYPE, connection),
                    connection, values);
            preparedStatement.setObject(i, struct, SQL_TYPE);

        }
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        /* Token Token = (Token) value;
        Token copy = new Token(Token.getAppartement(), Token.getBuilding(), Token.getStreet(),
                Token.getCity(), Token.getZipCode());
        return copy; */
        if (value == null) return null;
        final Token Token = (Token) value;
        final Token copy = new Token();
        copy.setTokenString(Token.getTokenString());
        copy.setExpiresAt(Token.getExpiresAt());
        return copy;
    }

    @Override
    public boolean isMutable(){
        return true;
    }

    @Override
    public Serializable disassemble(Object value)
            throws HibernateException {
        return (Serializable) this.deepCopy(value);
    }

    @Override
    public Object assemble(Serializable cached, Object owner)
            throws HibernateException {
        //return this.deepCopy(cached);
        return null;
    }

    @Override
    public Object replace(Object original, Object target, Object owner)
            throws HibernateException {
        return this.deepCopy(original);
    }

}