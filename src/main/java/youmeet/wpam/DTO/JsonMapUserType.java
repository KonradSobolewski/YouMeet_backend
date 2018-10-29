package youmeet.wpam.DTO;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.StringType;
import org.hibernate.usertype.UserType;

import java.io.IOException;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.postgresql.ds.common.PGObjectFactory;
import org.postgresql.util.PGobject;

public class JsonMapUserType implements UserType {
    private static ObjectWriter objectWriter;
    private static ObjectReader objectReader;
    private static ObjectMapper objectMapper;
    private static PGObjectFactory objectFactory;
    private static TypeReference<Map<String, Object>> typeRef;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        typeRef = new TypeReference<Map<String,Object>>() {};
        objectFactory = new PGObjectFactory();
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        try {
            return objectMapper.readValue((String)cached, typeRef);
        } catch (Exception e) {
            throw new HibernateException(e);
        }
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new HibernateException(e);
        }
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        try {
            String val = objectMapper.writeValueAsString(value);
            return objectMapper.readValue(val, typeRef);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == null) {
            return y == null;
        }
        return x.equals(y);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        try {
            String value = StringType.INSTANCE.nullSafeGet(rs, names[0], session);
            return ((value != null) ? objectMapper.readValue(value, typeRef) : null);
        } catch (IOException e) {
            throw new HibernateException(e);
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        try {
            if (value == null) {
                st.setNull(index, Types.OTHER);
                return;
            }
            PGobject object = getPgObject(value);
            st.setObject(index, object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    private PGobject getPgObject(Object value) throws SQLException, JsonProcessingException {
        PGobject object = new PGobject();
        object.setType("jsonb");
        object.setValue(objectMapper.writeValueAsString(value));
        return object;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

    @Override
    public Class returnedClass() {
        return HashMap.class;
    }

    @Override
    public int[] sqlTypes() {
        return new int[] {Types.JAVA_OBJECT};
    }
}
