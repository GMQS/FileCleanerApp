package sample.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexCheck {
    public static boolean checkExtensionsValidation(final String text){
        Pattern pattern = Pattern.compile("(([A-Za-z0-9]*)(,?))+([A-Za-z0-9]$)");
        Matcher matcher = pattern.matcher(text);
        return !matcher.matches();
    }
}
