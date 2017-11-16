package com.programmer74.jtdb;

import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;
import org.hibernate.HibernateException;
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
    public Object nullSafeGet(ResultSet rs, String[] names, org.hibernate.engine.spi.SharedSessionContractImplementor session,
                              Object owner)
            throws HibernateException, SQLException {

        final Struct struct = (Struct) rs.getObject(names[0]);
        if (rs.wasNull()) return null;

        final Token Token = new Token();

        Token.setTokenString((String)struct.getAttributes()[0]);
        Token.setExpiresAt(new Date(((Timestamp)struct.getAttributes()[1]).getTime()));
        return Token;
    }

    /*public void nullSafeSet(java.sql.PreparedStatement st, java.lang.Object value, int index),
                            org.hibernate.engine.spi.SessionImplementor session)*/
    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, org.hibernate.engine.spi.SharedSessionContractImplementor session)
            throws HibernateException, SQLException {
         if (value == null) st.setNull(index, SQL_TYPE, DB_OBJEXT_TYPE);
        else {
            final Token Token = (Token) value;
            final Object[] values = new Object[] {Token.getTokenString(), Token.getExpiresAt()};
            final Connection connection = st.getConnection();
            final STRUCT struct = new STRUCT(StructDescriptor.createDescriptor(DB_OBJEXT_TYPE, connection),
                    connection, values);
            st.setObject(index, struct, SQL_TYPE);

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