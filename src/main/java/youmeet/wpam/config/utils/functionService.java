package youmeet.wpam.config.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class functionService {

    public static List<String> getStringArray(Object object) {
        if (object != null) {
            List<String> list = new ArrayList<>();
            for(Object ob: (ArrayList<Object>) object){
                list.add((String) ob);
            }
            return list;
        }
        return Collections.emptyList();
    }

    public static List<Integer> getIntegerArray(Object object) {
        if (object != null) {
            List<Integer> list = new ArrayList<>();
            for(Object o: (ArrayList<Object>) object){
                list.add((int)o);
            }
            return list;
        }
        return Collections.emptyList();
    }

}
