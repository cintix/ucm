package dk.tv2.web.mvc.http.utils;

import java.lang.reflect.Parameter;

/**
 *
 * @author migo
 */
public class ParamUtil {

    /**
     * 
     * @param parameter
     * @return 
     */
    public static String fromParamToRegex(Parameter parameter) {
        switch (parameter.getType().getName()) {
            case "int":
            case "long":
            case "byte":
            case "double":
            case "java.lang.Integer":
            case "java.lang.Long":
            case "java.lang.Byte":
            case "java.lang.Double":
                return "(\\\\d+)";
            default:
                return "([^\\/>]+)[/]*";
        }
    }

    /**
     * 
     * @param parameter
     * @param value
     * @return 
     */
    public static Object fromStringToParam(Parameter parameter, String value) {
        switch (parameter.getType().getName()) {
            case "java.lang.Integer":
            case "int":
                return Integer.parseInt(value);
            case "java.lang.Long":
            case "long":
                return Long.parseLong(value);
            case "double":
            case "java.lang.Double":
                return Double.parseDouble(value);
            case "java.lang.Byte":
            case "byte":
                return Byte.parseByte(value);
            case "java.lang.Boolean":
            case "boolean":
                return Boolean.parseBoolean(value);
            default:
                return value;
        }
    }
    
}
