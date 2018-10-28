package youmeet.wpam.DTO;


import org.hibernate.annotations.Type;

import javax.persistence.Column;
import java.util.HashMap;

public class Params {

    @Column(name = "params", columnDefinition = "jsonb")
    @Type(type = "JsonbType")
    private HashMap<String, Object> params;

    public void addParam (String name, Object obj) {
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
        return this.params.get(name) != null;
    }

    public HashMap<String, Object> getParams() {
        return params;
    }

    public void setParams(HashMap<String, Object> params) {
        this.params = params;
    }

}
