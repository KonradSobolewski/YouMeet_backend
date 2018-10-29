package youmeet.wpam.DTO;


import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.HashMap;

@TypeDefs({
        @TypeDef(name = "JsonMapUserType", typeClass = JsonMapUserType.class)
})
@MappedSuperclass
public class Params {

    @Column(name = "params")
    @Type(type = "JsonMapUserType")
    private HashMap<String, Object> params;

    public void addParam (String name, Object obj) {
        if(params == null) params = new HashMap<>(5);
        this.params.put(name, obj);
    }

    public Object getParam (String name) {
        if( hasParam(name) ) {
            return this.params.get(name);
        } else {
            return null;
        }
    }

    public String getStringParam(String name, String defaultName) {
        if ( hasParam(name)) {
            return (String) this.params.get(name);
        } else {
            return defaultName;
        }
    }

    public boolean hasParam (String name) {
        return this.params.get(name) != null && params.containsKey(name);
    }

    public HashMap<String, Object> getParams() {
        return params;
    }

    public void setParams(HashMap<String, Object> params) {
        this.params = params;
    }

    public Object getOrDefault(String key, Object defaultValue) {
        return params != null ? params.getOrDefault(key, defaultValue) : defaultValue;
    }

}
