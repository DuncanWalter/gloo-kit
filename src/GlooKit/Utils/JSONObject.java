package GlooKit.Utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class JSONObject {

    private static Pattern nonWhiteSpaceRegex = Pattern.compile("([\"\\\\])");
    private static Pattern newlineRegex = Pattern.compile("\n");
    private static Pattern tabRegex = Pattern.compile("\t");
    private static Pattern formfeedRegex = Pattern.compile("\f");
    private static Pattern carriagereturnRegex = Pattern.compile("\r");

    private Map<String, Object> values;
    private boolean inline = false;

    public JSONObject() {
        values = new HashMap<>();
    }

    public JSONObject add(String key, Object value) {
        return add(key, value, false);
    }

    private JSONObject add(String key, Object value, boolean alreadyParsed) {
        
        // the key is a String, so we need to go through and add an extra \\ to every escaped character if not alreadyParsed
        if (!alreadyParsed) {
            key = reescapeString(key);
        }
        
        if(value instanceof JSONObject || value instanceof JSONObject[]) {
            values.put(key, value);
        }else if (value instanceof String) {

            String valueAsString = (String) value;

            // if value is a String, we need to go through and add an extra \\ to every escaped character if not alreadyParsed
            if (!alreadyParsed) {
                valueAsString = reescapeString(valueAsString);
            }

            values.put(key, valueAsString);

        } else if (value instanceof String[]) {
            // if its a String[], we need to go through each one and add an extra \\ to every escaped character
            String[] valueAsStrings = (String[]) value;

            for (int i = 0; i < valueAsStrings.length; i++) {
                valueAsStrings[i] = nonWhiteSpaceRegex.matcher(valueAsStrings[i]).replaceAll("\\\\$1");
                valueAsStrings[i] = newlineRegex.matcher(valueAsStrings[i]).replaceAll("\\\\n");
                valueAsStrings[i] = tabRegex.matcher(valueAsStrings[i]).replaceAll("\\\\t");
                valueAsStrings[i] = formfeedRegex.matcher(valueAsStrings[i]).replaceAll("\\\\f");
                valueAsStrings[i] = carriagereturnRegex.matcher(valueAsStrings[i]).replaceAll("\\\\r");
            }

            values.put(key, valueAsStrings);

        } else {
            System.out.println("Warning on this key: " + key + "; and value: " + value);
            System.out.println("Value interpreted as a String, but this might not have been intended.");
            values.put(key, value.toString());
        }
        return this;
    }

    private Object fetch(String... keys) {

        Object value = values.get(keys[0]);

        if (keys.length == 1) {
            return value;
        }

        if (value == null) {
            return null;
        } else if (value instanceof String){
            return null;
        } else if (value instanceof String[]){
            return null;
        } else if (value instanceof JSONObject) {

            JSONObject childObject = (JSONObject) value;
            return childObject.fetch( (String[] ) Arrays.copyOfRange(keys, 1, keys.length) );


        } else if (value instanceof JSONObject[]) {

            JSONObject[] childObjects = (JSONObject[]) value;

            String[] strings = new String[childObjects.length];
            int numStrings = 0;

            JSONObject[] objects = new JSONObject[childObjects.length];
            int numObjects = 0;

            Object fetchResult;

            for(int i = 0; i < childObjects.length; i++) {
                if (childObjects[i] == null) {

                    strings[i] = null;
                    objects[i] = null;

                } else {

                    fetchResult = childObjects[i].fetch( (String[] ) Arrays.copyOfRange(keys, 1, keys.length) );

                    if (fetchResult instanceof String) {
                        strings[i] = (String) fetchResult;
                        numStrings++;
                    } else if (fetchResult instanceof JSONObject) {
                        objects[i] = (JSONObject) fetchResult;
                        numObjects++;
                    }

                }
            }

            if (numStrings > numObjects) {
                return strings;
            } else {
                return objects;
            }

        } else {
            return null;
        }
    }

    public Boolean fetchBoolean(String... keys) {
        Object value = this.fetch(keys);

        if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        } else {
            return false;
        }
    }

    public Boolean[] fetchBooleans(String... keys) {
        Object value = this.fetch(keys);

        if (value == null) {
            return new Boolean[] {};
        } else if (value instanceof String) {
            return new Boolean[] {};
        } else if (value instanceof String[]) {

            String[] valueAsStringArray = (String[]) value;
            Boolean[] result = new Boolean[ valueAsStringArray.length ];

            for(int i = 0; i < result.length; i++) {
                result[i] = Boolean.parseBoolean(valueAsStringArray[i]);
            }

            return result;

        } else {
            return new Boolean[] {};
        }
    }

    public Double fetchDouble(String... keys) {
        return fetchDouble(null, keys);
    }

    public Double fetchDouble(Double defaultValue, String... keys) {
        Object value = this.fetch(keys);

        if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    public Double[] fetchDoubles(String... keys) {
        return fetchDoubles(null, keys);
    }

    public Double[] fetchDoubles(Double defaultValue, String... keys) {
        Object value = this.fetch(keys);

        if (value == null) {
            return new Double[] {};
        } else if (value instanceof String) {
            return new Double[] {};
        } else if (value instanceof String[]) {

            String[] valueAsStringArray = (String[]) value;
            Double[] result = new Double[ valueAsStringArray.length ];

            for(int i = 0; i < result.length; i++) {
                try {
                    result[i] = Double.parseDouble(valueAsStringArray[i]);
                } catch (NumberFormatException e) {
                    result[i] = defaultValue;
                }
            }

            return result;

        } else {
            return new Double[] {};
        }
    }

    public Integer fetchInteger(String... keys) {
        return fetchInteger(null, keys);
    }

    public Integer fetchInteger(Integer defaultValue, String... keys) {
        Object value = this.fetch(keys);

        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    public Integer[] fetchIntegers(String... keys) {
        return fetchIntegers(null, keys);
    }

    public Integer[] fetchIntegers(Integer defaultValue, String... keys) {
        Object value = this.fetch(keys);

        if (value == null) {
            return new Integer[] {};
        } else if (value instanceof String) {
            return new Integer[] {};
        } else if (value instanceof String[]) {

            String[] valueAsStringArray = (String[]) value;
            Integer[] result = new Integer[ valueAsStringArray.length ];

            for(int i = 0; i < result.length; i++) {
                try {
                    result[i] = Integer.parseInt(valueAsStringArray[i]);
                } catch (NumberFormatException e) {
                    result[i] = defaultValue;
                }
            }

            return result;

        } else {
            return new Integer[] {};
        }
    }

    public JSONObject fetchJSONObject(String... keys) {
        Object value = this.fetch(keys);

        if (value instanceof JSONObject) {
            return (JSONObject) value;
        } else {
            return null;
        }
    }

    public JSONObject[] fetchJSONObjects(String... keys) {
        Object value = this.fetch(keys);

        if (value instanceof JSONObject[]) {
            return (JSONObject[]) value;
        } else {
            return null;
        }
    }

    public String fetchString(String... keys) {
        return fetchString(null, keys);
    }

    public String fetchString(String defaultValue, String... keys) {
        Object value = this.fetch(keys);


        if (value instanceof String) {
            return (String) value;
        } else {
            return defaultValue;
        }
    }

    public String[] fetchStrings(String... keys) {
        return fetchStrings(null, keys);
    }

    public String[] fetchStrings(String defaultValue, String... keys) {
        Object value = this.fetch(keys);

        if (value == null){
            return new String[] {};
        } else if (value instanceof String[]) {
            String[] valueAsStringArray = (String[]) value;

            for(int i = 0; i < valueAsStringArray.length; i++) {

                if (valueAsStringArray[i] == null) {
                    valueAsStringArray[i] = defaultValue;
                }

            }

            return valueAsStringArray;

        } else {
            return new String[] {};
        }

    }

    public void parseFromCharacters(char[] chars) {

        int depth = 0;
        int lastDivider = 0;
        boolean inString = false;

        for(int i = 0; i < chars.length; i++) {

            if(inString) {
                if(chars[i] == '"') {
                    inString = false;
                }
                continue;
            }

            if (chars[i] == '"') {
                inString = true;
                continue;
            }

            if (chars[i] == '{' || chars[i] == '[') {
                if (depth == 0) {
                    lastDivider = i;
                }
                depth++;
            }

            if (chars[i] == ']' || chars[i] == '}') {
                depth--;
            }

            if (chars[i] == ',' && depth == 1) {
                System.out.println("Parsing Key Value Pair...");
                parseKeyValuePair(Arrays.copyOfRange(chars, lastDivider, i));
                lastDivider = i;
            }

        }

        // we have to add the last value manually, since it will not end in a comma
        System.out.println("Parsing last Key Value Pair...");
        parseKeyValuePair(Arrays.copyOfRange(chars, lastDivider, (new String(chars)).lastIndexOf('}')));

    }

    private String parseKey(char[] chars) {
        String string = new String(chars);

        System.out.println(string.substring(string.indexOf("\"")+1, string.lastIndexOf("\"")));
        return string.substring(string.indexOf("\"") + 1, string.lastIndexOf("\"")); // grab only the portion of the string in the quotes

    }

    private void parseKeyValuePair(char[] chars) {

        String key;
        Object value;

        for (int i = 0; i < chars.length; i++) {

            if (chars[i] == ':') {
                System.out.println("Parsing key...");
                key = parseKey(Arrays.copyOfRange(chars, 0, i));
                System.out.println("Key: " + key);
                System.out.println("Parsing value...");
                value = parseValue(Arrays.copyOfRange(chars, i+1, chars.length));
                System.out.println("Value: " + value);
                add(key, value, true);
                return;
            }
        }
    }

    private Object parseValue(char[] chars) {
        String string = new String(chars);
        string = string.trim();

        if (string.charAt(0) == '"') { // this is a string
            return string.substring(1, string.lastIndexOf('"'));
        } else if (string.charAt(0) == '[') {

            if(string.indexOf('{') > string.indexOf("\"")) {
                // this is a JSONObject array
                List<JSONObject> resultList = new ArrayList<>();

                int depth = 0;
                int lastDivider = 0;
                boolean inString = false;

                for(int i = 0; i < chars.length; i++) {

                    if(inString) {
                        if(chars[i] == '\"') {
                            inString = false;
                        }
                        continue;
                    }

                    if (chars[i] == '\"') {
                        inString = true;
                        continue;
                    }

                    if (chars[i] == '{' || chars[i] == '[') { // the curly brackets should never trigger. If they do, it should cause a parsing error
                        if (depth == 0) {
                            lastDivider = i;
                        }
                        depth++;
                    }

                    if (chars[i] == ']' || chars[i] == '}') { // the curly brackets should never trigger. If they do, it should cause a parsing error
                        depth--;
                    }

                    if ((chars[i] == ',' && depth == 1)) {
                        resultList.add( (JSONObject) parseValue(Arrays.copyOfRange(chars, lastDivider, i)));
                        lastDivider = i;
                    }

                }

                // we have to add the last value manually, since it will not end in a comma
                resultList.add( (JSONObject) parseValue(Arrays.copyOfRange(chars, lastDivider, string.lastIndexOf(']'))));

                JSONObject[] result = new JSONObject[resultList.size()];
                for(int i = 0; i < resultList.size(); i++) {
                    result[i] = resultList.get(i);
                }

                return result;


            } else {
                // this is a String array

                List<String> resultList = new ArrayList<>();

                int depth = 0;
                int lastDivider = 0;
                boolean inString = false;

                for(int i = 0; i < chars.length; i++) {

                    if(inString) {
                        if(chars[i] == '"') {
                            inString = false;
                        }
                        continue;
                    }

                    if (chars[i] == '"') {
                        inString = true;
                        continue;
                    }

                    if (chars[i] == '{' || chars[i] == '[') { // the curly brackets should never trigger. If they do, it should cause a parsing error
                        if (depth == 0) {
                            lastDivider = i;
                        }
                        depth++;
                    }

                    if (chars[i] == ']' || chars[i] == '}') { // the curly brackets should never trigger. If they do, it should cause a parsing error
                        depth--;
                    }

                    if (chars[i] == ',' && depth == 1) {
                        resultList.add( (String) parseValue(Arrays.copyOfRange(chars, lastDivider, i)));
                        lastDivider = i;
                    }

                }

                // we have to add the last value manually, since it will not end in a comma
                resultList.add( (String) parseValue(Arrays.copyOfRange(chars, lastDivider, string.lastIndexOf(']'))));

                String[] result = new String[resultList.size()];
                for(int i = 0; i < resultList.size(); i++) {
                    result[i] = resultList.get(i);
                }

                return result;
            }

        } else if (string.charAt(0) == '{') {

            JSONObject childObject = new JSONObject();
            childObject.parseFromCharacters(chars); // recursively tell the child to parse itself

            return childObject;
        } else {
            return string;
        }
    }

    public void readFromFile(String filepath) {

        try {

            FileInputStream inputStream = new FileInputStream(filepath);

            char[] chars = new char[inputStream.available()];

            int index = 0;
            while(inputStream.available() > 0) {
                chars[index] = ( (char) inputStream.read() );
                index++;
            }

            System.out.println("Attempting to parse characters...");
            parseFromCharacters(chars);


        } catch (IOException e) {
            e.printStackTrace();
            System.exit(500);
        }
    }
    
    private String reescapeString(String string) {
        string = nonWhiteSpaceRegex.matcher(string).replaceAll("\\\\$1");
        string = newlineRegex.matcher(string).replaceAll("\\\\n");
        string = tabRegex.matcher(string).replaceAll("\\\\t");
        string = formfeedRegex.matcher(string).replaceAll("\\\\f");
        return carriagereturnRegex.matcher(string).replaceAll("\\\\r");
    }

    public void setInline(boolean inline) {
        this.inline = inline;
    }

    private String tabber(int numTabs) {
        if (numTabs < 0) {
            numTabs = 0;
        }

        String result = "";

        for(int i = 0; i < numTabs; i++) {
            result += "\t";
        }

        return result;
    }

    public String toString(){
        return toString(0);
    }

    public String toString(int depth) {
        String result = "{\n" + tabber(depth + 1);

        int keyNum = 0;
        for(String key : values.keySet()) {
            result += ("\"" + key + "\":");

            Object value = this.fetch(key);
            if (value == null) {
                result += "\"null\"";
            } else if (value instanceof String) {
                result += ("\"" + value + "\"");
            } else if (value instanceof String[]) {

                String[] valueAsStringArray = (String[]) value;

                result += "\n"  + tabber(depth + 1) + "[\n"  + tabber(depth + 2);

                for (int i = 0; i < valueAsStringArray.length; i++) {
                    result += "\"" + valueAsStringArray[i] + "\"";

                    result += (i == valueAsStringArray.length-1) ? "\n" + tabber(depth + 1) + "]" : ",\n"  + tabber(depth + 2);
                }

            } else if (value instanceof JSONObject) {
                result += ((JSONObject)value).toString(depth + 1);
            } else if (value instanceof JSONObject[]) {

                JSONObject[] valueAsJSONObjectArray = (JSONObject[]) value;

                result += "\n" + tabber(depth + 1) + "[\n" + tabber(depth + 2);

                for (int i = 0; i < valueAsJSONObjectArray.length; i++) {

                    result += valueAsJSONObjectArray[i].toString(depth + 2);

                    result += (i == valueAsJSONObjectArray.length-1) ? "\n" + tabber(depth + 1) + "]" : ",\n" + tabber(depth + 2);
                }
            } else {
                result += "\"ERROR!\"";
                System.out.println("WARNING: Value of Key " + key + " was not a valid type!");
            }

            result += (keyNum == values.keySet().size() - 1) ? "\n" : ",\n" + tabber(depth + 1);
            keyNum++;
        }

        result += tabber(depth) + "}";

        return result;
    }

    public void writeToFile(String filepath) {

        try {

            FileOutputStream outputStream = new FileOutputStream(filepath);

            outputStream.write(this.toString().getBytes());

            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(500);
        }

    }

}
