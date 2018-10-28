package youmeet.wpam.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;

public class Params {

    @JsonProperty(value = "params")
    private HashMap<String, Object> params;

    public void addParam (String name, Object obj) {
        params.put(name, obj);
    }

    public Object getParam (String name) {
        if( hasParam(name) ) {
            return params.get(name);
        } else {
            return null;
        }
    }

    public String getStringParam(String name, String defaultName) {
        if ( hasParam(name)) {
            return (String) params.get(name);
        } else {
            return defaultName;
        }
    }

    public boolean hasParam (String name) {
        return params.get(name) != null;
    }

    public HashMap<String, Object> getParams() {
        return params;
    }

    public void setParams(HashMap<String, Object> params) {
        this.params = params;
    }
}
