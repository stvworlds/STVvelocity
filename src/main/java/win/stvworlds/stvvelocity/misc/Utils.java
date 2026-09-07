package win.stvworlds.stvvelocity.misc;

import java.util.List;

public class Utils {

    public static boolean containsIgnoreCase(List<String> names, String value) {
        for (String name : names) {
            if (name != null && name.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

}
