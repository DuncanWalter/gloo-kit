package GlooKit.Utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Represents a {@code JSONObject} as a map of keys and values. Keys will always be Strings, while values will can be
 * Strings, String arrays, {@code JSONObjects}, or {@code JSONObject} arrays.
 * <p>
 * Several convenience methods are provided to retrieve a value of a given type from the {@code JSONObject}. Methods are
 * also provided to read in key-value pairs from a {@code .json} file and write {@code JSONObjects} to file. In addition,
 * one can specify whether the {@code JSONObject} should be written to file in a way that is human-readable or compact
 * through the {@link JSONObject#inline inline} boolean.
 *
 * @see JSONable
 *
 * @author Eli Jergensen
 * @author Duncan Walter
 * @since 1.0
 * */
public class JSONObject {

    /** Regular expression that finds instances of " and \ in strings */
    private static Pattern nonWhiteSpaceRegex = Pattern.compile("([\"\\\\])");

    /** Regular expression that finds instances of the newline character in strings */
    private static Pattern newlineRegex = Pattern.compile("\n");

    /** Regular expression that finds instances of the tab character in strings */
    private static Pattern tabRegex = Pattern.compile("\t");

    /** Regular expression that finds instances of the form feed character in strings */
    private static Pattern formfeedRegex = Pattern.compile("\f");

    /** Regular expression that finds instances of the carriage return character in strings */
    private static Pattern carriagereturnRegex = Pattern.compile("\r");

    /** Map of the key-value pairs of this {@code JSONObject} */
    private Map<String, Object> values;

    /** Boolean for whether this {@code JSONObject} should be written to file with as little whitespace as possible */
    private boolean inline = false;

    /**
     * Constructs an empty {@code JSONObject}
     * */
    public JSONObject() {
        values = new HashMap<>();
    }

    /**
     * Constructs a {@code JSONObject} and loads in the key-value pairs from the file specified.
     *
     * @param filepath String for the location of the file to be loaded in
     * */
    public JSONObject(String filepath) {
        this(); // instantiate the object
        readFromFile(filepath); // read in key-value pairs from the filepath given
    }

    /**
     * Adds a key-value pair to this {@code JSONObject}. Values that are not Strings or {@code JSONObjects} (or arrays)
     * are interpreted as Strings.
     *
     * @param key String for the name of the field to be added to this JSONObject
     * @param value Object for the value of the field to be added to this JSONObject.
     *              Must be a String, String[], JSONObject, JSONObject[], or something with a toString() method.
     * @return this JSONObject itself. This allows for multiple .add calls to be strung together
     * */
    public JSONObject add(String key, Object value) {
        return add(key, value, false);
    }

    /**
     * Actually adds a key-value pair to this {@code JSONObject}, given the key-value pair and a boolean for whether we
     * have already parsed the strings. alreadyParsed should be set to true if this key-value pair has already been read
     * from file and set to false if a user is adding a new key-value pair from inside the program. Parsing, in this
     * case, refers to the process of re-escaping characters that need to remain escaped within the string.
     *
     * @param key String for the name of the field to be added to this JSONObject
     * @param value Object for the value of the field to be added to this JSONObject.
     *              Must be a String, String[], JSONObject, JSONObject[], or something with a toString() method.
     * @param alreadyParsed Boolean for whether this key-value pair has already been read from file
     * @return this JSONObject itself. This allows for multiple .add calls to be strung together
     * */
    private JSONObject add(String key, Object value, boolean alreadyParsed) {
        
        // the key is a String, so we need to go through and reescape every escaped character if not alreadyParsed
        if (!alreadyParsed) {
            key = reescapeString(key);
        }
        
        if(value instanceof JSONObject || value instanceof JSONObject[]) {
            // if the value is a JSONObject or JSONObject[], simply add it
            values.put(key, value);
        }else if (value instanceof String) {

            // cast the value to a string so we can handle it
            String valueAsString = (String) value;

            // the value is a String, so we need to go through and reescape every escaped character if not alreadyParsed
            if (!alreadyParsed) {
                valueAsString = reescapeString(valueAsString);
            }

            values.put(key, valueAsString); // add the key-value pair

        } else if (value instanceof String[]) {

            // cast the value to a string[] so we can handle each of the elements
            String[] valueAsStrings = (String[]) value;

            // since each element of value is a String, we need to go through each one and reescape every escaped character if not alreadyParsed
            if (!alreadyParsed) {
                for (int i = 0; i < valueAsStrings.length; i++) {
                    valueAsStrings[i] = reescapeString(valueAsStrings[i]);
                }
            }

            values.put(key, valueAsStrings); // add the key-value pair

        } else if (value == null) {
            values.put(key, null); // nulls get passed along
        } else {
            // if the value is not a String, String[], JSONObject, or JSONObject[], attempt to parse it as a string
            // It's probably a number, which we want to be a string
            values.put(key, value.toString());
        }
        return this; // return this so that multiple .add calls can be strung together
    }

    /**
     * Retrieves a value (or multiple values) of this {@code JSONObject} or its child objects given an array of strings
     * (a var arg of strings). If one of the values along the path of keys is actually a {@code JSONObject[]}, data
     * lensing is performed. That is, all of the corresponding values of all of the child objects are returned in an
     * array.
     *
     * @param keys A variable number of strings of the keys for the value to be retrieved
     * @return the value (or values) corresponding to the key list given
     * */
    private Object fetch(String... keys) {

        Object value = values.get(keys[0]); // grab the value of this JSONObject corresponding to the first key

        if (keys.length == 1) {
            return value; // if there is only one key remaining in the list, return the value we have
        }

        if (value == null) { // if the value doesn't exist, return null (instead of continuing to parse keys recursively)
            return null;
        } else if (value instanceof String){ // if the value is a string, but there are still keys remaining, return null (instead of continuing to parse keys recursively)
            return null;
        } else if (value instanceof String[]){ // if the value is a string[], but there are still keys remaining, return null (instead of continuing to parse keys recursively)
            return null;
        } else if (value instanceof JSONObject) { // if the value is a JSONObject...

            JSONObject childObject = (JSONObject) value; // ... cast to a JSONObject
            return childObject.fetch( (String[] ) Arrays.copyOfRange(keys, 1, keys.length) ); // call this method recursively on the JSONObject

        } else if (value instanceof JSONObject[]) { // if the value is a JSONObject[], do data lensing!

            JSONObject[] childObjects = (JSONObject[]) value; // cast to a JSONOjbect[]

            String[] strings = new String[childObjects.length]; // result of the data lensing as a string[]
            int numStrings = 0; // start counting the number of strings

            JSONObject[] objects = new JSONObject[childObjects.length]; // result of the data lensing as a JSONObject[]
            int numObjects = 0; // start counting the number of objects

            Object fetchResult;

            for(int i = 0; i < childObjects.length; i++) { // cycle through all the objects in the JSONObject[]
                if (childObjects[i] == null) {

                    strings[i] = null; // if the child object is null, add null to both the string and object arrays
                    objects[i] = null;

                } else {

                    // call this method recursively on the childobject using the remaining keys
                    fetchResult = childObjects[i].fetch( (String[] ) Arrays.copyOfRange(keys, 1, keys.length) );

                    if (fetchResult instanceof String) { // if the result is a string

                        strings[i] = (String) fetchResult; // add it to the results array
                        objects[i] = null; // add null to the objects array
                        numStrings++; // increment the number of strings

                    } else if (fetchResult instanceof JSONObject) { // if the result is an object

                        strings[i] = null; // add null to the strings array
                        objects[i] = (JSONObject) fetchResult; // add it to the objects array
                        numObjects++; // increment the number of objects

                    }

                }
            }

            if (numStrings > numObjects) { // return the result array as a string array only if there were more strings than objects
                return strings;
            } else {
                return objects; // (default to returning the result array as an object array if there were the same number of strings and objects parsed)
            }

        } else { // if the value is not of a known type, return null
            return null;
        }
    }

    /**
     * Overloads the {@link JSONObject#fetchBoolean(boolean, String...) fetchBoolean(boolean, String...)} method to
     * assume a default value of false.
     *
     * @param keys A variable number of strings of the keys for the value to be retrieved
     * @return the boolean value corresponding to the key list given, defaulting to false
     * */
    public boolean fetchBoolean(String... keys) {
        return fetchBoolean(false, keys); // default to returning false
    }

    /**
     * Retrieves a boolean value of this {@code JSONObject}. Defaults to {@code defaultValue} given if no value can be
     * parsed.
     *
     * @param defaultValue a boolean for the value to be returned if no value can be parsed from the JSONObject
     * @param keys A variable number of strings of the keys for the value to be retrieved
     * @return the boolean value corresponding to the key list given, defaulting to defaultValue
     * */
    public boolean fetchBoolean(boolean defaultValue, String... keys) {
        Object value = this.fetch(keys); // grab the value as an object

        if (value instanceof String) { // check to make sure the value is of type String
            return Boolean.parseBoolean((String) value); // this returns a boolean, not a Boolean
        } else {
            return defaultValue; // default to whatever was given
        }
    }

    /**
     * Retrieves a boolean array of values of this {@code JSONObject}. If the list of values cannot be parsed, it
     * returns a new boolean[].
     *
     * @param keys A variable number of strings of the keys for the values to be retrieved
     * @return a boolean array of values corresponding to the key list given, defaulting to a new boolean[] if the
     * entire list could not be parsed
     * */
    public boolean[] fetchBooleans(String... keys) {
        Object value = this.fetch(keys); // grab the value as an object

        if (value == null) {
            return new boolean[] {}; // return a new boolean[] if we cannot parse the list
        } else if (value instanceof String) {
            return new boolean[] {}; // return a new boolean[] if we cannot parse the list
        } else if (value instanceof String[]) {

            String[] valueAsStringArray = (String[]) value;
            boolean[] result = new boolean[ valueAsStringArray.length ];

            for(int i = 0; i < result.length; i++) {
                result[i] = Boolean.parseBoolean(valueAsStringArray[i]); // returns a boolean, not a Boolean
            }

            return result; // return the array of booleans

        } else {
            return new boolean[] {}; // return a new boolean[] if we cannot parse the list
        }
    }

    /**
     * Retrieves a Double value of this {@code JSONObject}. Any value not parsed defaults to null.
     *
     * @param keys A variable number of strings of the keys for the value to be retrieved
     * @return a Double of the value corresponding to the key list given, defaulting to null
     * */
    public Double fetchDouble(String... keys) {
        Object value = this.fetch(keys); // grab the value as an object

        if (value instanceof String) { // check to make sure the value is a string
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return null; // return null if a value could not be parsed
            }
        } else {
            return null; // return null if the value itself could not be parsed
        }
    }

    /**
     * Retrieves a primitive-safe double value of this {@code JSONObject}. Any value not parsed defaults to
     * {@code defaultValue}
     *
     * @param defaultValue a double for the value to be returned if no value can be parsed from the JSONObject
     * @param keys A variable number of strings of the keys for the value to be retrieved
     * @return a primitive-safe double of the value corresponding to the key list given, defaulting to defaultValue
     * */
    public double fetchDouble(double defaultValue, String... keys) {
        Object value = this.fetch(keys); // grab the value as an object

        if (value instanceof String) { // check to make sure the value is of type string
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return defaultValue; // return the defaultValue if a double could not be parsed
            }
        } else {
            return defaultValue; // return the defaultValue if the value itself could not be parsed
        }
    }

    /**
     * Retrieves a Double array of values of this {@code JSONObject}. If the list of values cannot be parsed, it returns
     * a new Double[]. Individual elements that cannot be parsed default to null.
     *
     * @param keys A variable number of strings of the keys for the value to be retrieved
     * @return a Double array of values corresponding to the key list given, defaulting to a new Double[] if the entire
     * list could not be parsed
     * */
    public Double[] fetchDoubles(String... keys) {
        Object value = this.fetch(keys); // grab the value as an object

        if (value == null) {
            return new Double[] {}; // default to a new Double[] if we cannot parse the list
        } else if (value instanceof String) {
            return new Double[] {}; // default to a new Double[] if we cannot parse the list
        } else if (value instanceof String[]) {

            String[] valueAsStringArray = (String[]) value;
            Double[] result = new Double[ valueAsStringArray.length ];

            for(int i = 0; i < result.length; i++) {
                try {
                    result[i] = Double.parseDouble(valueAsStringArray[i]);
                } catch (NumberFormatException e) {
                    result[i] = null; // return null if we cannot parse this element
                }
            }

            return result; // return the array of Doubles

        } else {
            return new Double[] {}; // default to a new Double[] if we cannot parse the list
        }
    }

    /**
     * Retrieves an Integer value of this {@code JSONObject}. Any value not parsed defaults to null.
     *
     * @param keys A variable number of strings of the keys for the value to be retrieved
     * @return an Integer of the value corresponding to the key list given, defaulting to null
     * */
    public Integer fetchInteger(String... keys) {
        Object value = this.fetch(keys); // grab the value as an object

        if (value instanceof String) { // check to make sure the value is of type string
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return null; // return null if an Integer could not be parsed
            }
        } else {
            return null; // return null if the value itself could not be parsed
        }
    }

    /**
     * Retrieves a primitive-safe int of this {@code JSONObject}. Any value not parsed defaults to {@code defaultValue}.
     *
     * @param defaultValue an int for the value to be returned if no value can be parsed from the JSONObject
     * @param keys A variable number of strings of the keys for the value to be retrieved
     * @return a primitive-safe int of the value corresponding to the key list given, defaulting to defaultValue
     * */
    public Integer fetchInt(int defaultValue, String... keys) {
        Object value = this.fetch(keys); // grab the value as an object

        if (value instanceof String) { // check to make sure the value is of type string
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return defaultValue; // return defaultValue if an int could not be parsed
            }
        } else {
            return defaultValue; // return defaultValue if the value itself could not be parsed
        }
    }

    /**
     * Retrieves an Integer array of values of this {@code JSONObject}. If the list of values cannot be parsed, it
     * returns a new Integer[]. Individual elements that cannot be parsed default to null.
     *
     * @param keys A variable number of strings of the keys for the value to be retrieved
     * @return an Integer array of values corresponding to the key list given, defaulting to a new Integer[] if the
     * entire list could not be parsed
     * */
    public Integer[] fetchIntegers(String... keys) {
        Object value = this.fetch(keys); // grab the value as an object

        if (value == null) {
            return new Integer[] {}; // default to a new Integer[] if we cannot parse the list
        } else if (value instanceof String) {
            return new Integer[] {}; // default to a new Integer[] if we cannot parse the list
        } else if (value instanceof String[]) {

            String[] valueAsStringArray = (String[]) value;
            Integer[] result = new Integer[ valueAsStringArray.length ];

            for(int i = 0; i < result.length; i++) {
                try {
                    result[i] = Integer.parseInt(valueAsStringArray[i]);
                } catch (NumberFormatException e) {
                    result[i] = null; // return null if we cannot parse this element
                }
            }

            return result; // return the array of Integers

        } else {
            return new Integer[] {}; // default to a new Integer[] if we cannot parse the list
        }
    }

    /**
     * Retrieves a JSONObject value of this {@code JSONObject}. If the value could not be parsed, it defaults to null.
     *
     * @param keys A variable number of strings of the keys for the value to be retrieved
     * @return a JSONObject corresponding to the key list given, defaulting to null
     * */
    public JSONObject fetchJSONObject(String... keys) {
        Object value = this.fetch(keys); // grab the value as an object

        if (value instanceof JSONObject) { // check to make sure it is of type JSONObject
            return (JSONObject) value;
        } else {
            return null; // return null if not a JSONObject
        }
    }

    /**
     * Retrieves an array of JSONObject values of this {@code JSONObject}. If the list of values cannot be parsed, it
     * returns a new JSONObject[]. Individual elements that cannot be parsed default to null.
     *
     * @param keys A variable number of strings of the keys for the value to be retrieved
     * @return a JSONObject array of values corresponding to the key list given, defaulting to a new JSONObject[] if the
     * entire list could not be parsed
     * */
    public JSONObject[] fetchJSONObjects(String... keys) {
        Object value = this.fetch(keys); // grab the value as an object

        if (value instanceof JSONObject[]) { // check to make sure it is of type JSONObject[]
            return (JSONObject[]) value;
        } else {
            return new JSONObject[] {}; // return a new JSONObject[] if not
        }
    }

    /**
     * Retrieves a String value of this {@code JSONObject}. If the value could not be parsed, it defaults to null.
     *
     * @param keys A variable number of strings of the keys for the value to be retrieved
     * @return a String corresponding to the key list given, defaulting to null
     * */
    public String fetchString(String... keys) {
        Object value = this.fetch(keys); // grab the value as an object

        if (value instanceof String) { // check to make sure it is of type String
            return (String) value;
        } else {
            return null; // return null if not a String
        }
    }

    /**
     * Retrieves an array of String values of this {@code JSONObject}. If the list of values cannot be parsed, it
     * returns a new String[]. Individual elements that cannot be parsed default to null.
     *
     * @param keys A variable number of strings of the keys for the value to be retrieved
     * @return a String array of values corresponding to the key list given, defaulting to a new String[] if the entire
     * list could not be parsed
     * */
    public String[] fetchStrings(String... keys) {
        Object value = this.fetch(keys); // grab the value as an object

        if (value instanceof String[]) { // check to make sure it is of type String[]
            return (String[]) value;
        } else {
            return new String[] {}; // return a new String[] if not
        }
    }

    /**
     * Parses key-value pairs to add to this {@code JSONObject} based on a char array.
     *
     * @param chars a char array whose key-value pairs are to be loaded in
     * */
    public void parseFromCharacters(char[] chars) {

        int depth = 0; // number of tabs "deep" we are in the object (this ensures that values of child objects aren't parsed yet)
        int lastDivider = 0; // index of the last divider in the char[]
        boolean inString = false; // boolean for whether we are currently in a string

        for(int i = 0; i < chars.length; i++) { // cycle over all of the characters

            if(inString) { // check to see if we are in a String
                if(chars[i] == '"' && chars[i-1] != '\\') { // if we find the end " not preceded by a \, we are no longer in the String
                    inString = false;
                }
                continue; // skip over this character
            }

            if (chars[i] == '"' && chars[i-1] != '\\') { // we are not in a String, but we have an open " not preceded by a \
                inString = true; // we are now in a string
                continue; // skip over this character
            }

            if (chars[i] == '{' || chars[i] == '[') { // we have an open bracket
                if (depth == 0) {
                    lastDivider = i; // only if the depth is currently 0 is this a divider
                }
                depth++; // increment the depth
            }

            if (chars[i] == ']' || chars[i] == '}') { // we have a close bracket
                depth--; // decrement the depth
            }

            if (chars[i] == ',' && depth == 1) { // if our depth is 1 and we have a comma...
                // ... the chars between the lastDivider and this character contain a key-value pair
                parseKeyValuePair(Arrays.copyOfRange(chars, lastDivider+1, i)); // call on the parseKeyValuePair method
                lastDivider = i; // set the index of this comma as the last divider
            }

        }

        // we have to add the last value manually, since it will not end in a comma
        parseKeyValuePair(Arrays.copyOfRange(chars, lastDivider+1, (new String(chars)).lastIndexOf('}'))); // call on the parseKeyValuePair method

    }

    /**
     * Parses a string for the key of a value to add to this {@code JSONObject} based on a char array.
     *
     * @param chars a char array containing a key
     * @return a String for the key of the value to be added
     * */
    private String parseKey(char[] chars) {
        String string = new String(chars);

        return string.substring(string.indexOf("\"") + 1, string.lastIndexOf("\"")); // grab only the portion of the string in the first and last quotes

    }

    /**
     * Parses a single key-value pair to add to this {@code JSONObject} based on a char array.
     *
     * @param chars a char array containing a single key-value pair to be loaded in
     * */
    private void parseKeyValuePair(char[] chars) {

        String key;
        Object value;

        for (int i = 0; i < chars.length; i++) {

            if (chars[i] == ':') { // the : denotes the end of the key and the beginning of the value
                key = parseKey(Arrays.copyOfRange(chars, 0, i)); // parse the key from all characters before the :
                value = parseValue(Arrays.copyOfRange(chars, i+1, chars.length)); // parse the value from all characters after the :
                add(key, value, true); // add the key value pair, noting that it is already parsed
                return;
            }
        }
        // We should never get here, unless something went wrong
        System.out.println("WARNING: Unable to parse key-value pair! No colon detected.");
    }

    /**
     * Parses a single value to add to this {@code JSONObject} based on a char array.
     *
     * @param chars a char array containing the value
     * @return Either a String, String[], JSONObject, or JSONObject[]
     * */
    private Object parseValue(char[] chars) {

        String string = new String(chars);
        string = string.trim(); // trim off the whitespace on the ends of the string

        if (string.charAt(0) == '"') { // this value is a string
            return string.substring(1, string.lastIndexOf('"')); // return the substring inside the first and last quotes
        } else if (string.charAt(0) == '[') { // this value is an array

            if(string.indexOf('{') > -1 && (!string.contains("\"") || string.indexOf('{') < string.indexOf("\"")) ) {
                // this is a JSONObject array
                List<JSONObject> resultList = new ArrayList<>(); // make a new arraylist

                // We need to parse the entire list in much the same way that we parsed key-value pairs
                int depth = 0; // how "deep" we are in child objects
                int lastDivider = 0; // index of the last divider in the char array
                boolean inString = false; // boolean for whether we are currently in a string

                for(int i = 0; i < chars.length; i++) { // cycle over all the chars (not the string)

                    if(inString) { // check to see if we are in a string
                        if(chars[i] == '\"') {
                            inString = false; // if this character is the end quote we are no longer in the string
                        }
                        continue; // skip this character
                    }

                    if (chars[i] == '\"') { // if we are not in a string and we have a quote...
                        inString = true; // we are now in a string
                        continue; // skip this character
                    }

                    if (chars[i] == '{' || chars[i] == '[') { // we have an open bracket
                        if (depth == 0) {
                            lastDivider = i; // only if our depth is currently 0 is this char a divider
                        }
                        depth++; // increment the depth
                    }

                    if (chars[i] == ']' || chars[i] == '}') { // we have a close bracket
                        depth--; // decrement the depth
                    }

                    if ((chars[i] == ',' && depth == 1)) { // if the depth is exactly one and we have a comma...
                        resultList.add( (JSONObject) parseValue(Arrays.copyOfRange(chars, lastDivider+1, i))); // ... (recursively) parse and add the JSONObject value
                        lastDivider = i; // this char is now the new last divider
                    }

                }

                // we have to add the last value manually, since it will not end in a comma
                resultList.add( (JSONObject) parseValue(Arrays.copyOfRange(chars, lastDivider+1, string.lastIndexOf(']')))); // (recursively) parse and add the JSONObject value

                JSONObject[] result = new JSONObject[resultList.size()]; // convert the result arraylist to an array
                for(int i = 0; i < resultList.size(); i++) {
                    result[i] = resultList.get(i);
                }

                return result; // return the JSONObject[]


            } else {
                // this is a String array
                List<String> resultList = new ArrayList<>(); // make a new arraylist

                // We need to parse the entire list in much the same way that we parsed key-value pairs
                int depth = 0; // how "deep" we are in child objects
                int lastDivider = 0; // index of the last divider in the char array
                boolean inString = false; // boolean for whether we are currently in a string

                for(int i = 0; i < chars.length; i++) { // cycle over all the chars (not the string)

                    if(inString) { // check to see if we are in a string
                        if(chars[i] == '\"') {
                            inString = false; // if this character is the end quote we are no longer in the string
                        }
                        continue; // skip this character
                    }

                    if (chars[i] == '\"') { // if we are not in a string and we have a quote...
                        inString = true; // we are now in a string
                        continue; // skip this character
                    }

                    // we have an open bracket
                    if (chars[i] == '{' || chars[i] == '[') { // the curly brackets should never trigger. If they do, it should cause a parsing error
                        if (depth == 0) {
                            lastDivider = i; // this char is only a divider if the depth is currently 0
                        }
                        depth++; // increment the depth
                    }

                    // we have a close bracket
                    if (chars[i] == ']' || chars[i] == '}') { // the curly brackets should never trigger. If they do, it should cause a parsing error
                        depth--; // decrement the depth
                    }

                    if (chars[i] == ',' && depth == 1) { // if the depth is 1 and this is a comma...
                        resultList.add( (String) parseValue(Arrays.copyOfRange(chars, lastDivider+1, i))); // ... (recursively) parse the string and add it to the list
                        lastDivider = i; // this char is now the last divider
                    }

                }

                // we have to add the last value manually, since it will not end in a comma
                resultList.add( (String) parseValue(Arrays.copyOfRange(chars, lastDivider+1, string.lastIndexOf(']')))); // (recursively) parse the string and add it to the list

                String[] result = new String[resultList.size()]; // convert the arraylist to an array
                for(int i = 0; i < resultList.size(); i++) {
                    result[i] = resultList.get(i);
                }

                return result; // return the string[]
            }

        } else if (string.charAt(0) == '{') { // this is a single JSONObject

            JSONObject childObject = new JSONObject();
            childObject.parseFromCharacters(chars); // recursively tell the child to parse itself

            return childObject; // return the JSONObject
        } else { // this was probably a number...
            return string; // return it as a string and hope for the best
        }
    }

    /**
     * Adds key-value pairs to this {@code JSONObject} based on a {@code .json} file.
     *
     * @param filepath a String of the filepath of the .json file whose key-value pairs are to be loaded in
     * */
    public void readFromFile(String filepath) {

        // warn if the filepath does not end in .json
        if (!filepath.endsWith(".json")) {
            System.out.println("WARNING: File is not a .json file. Parsing may not work!");
        }

        try {

            FileInputStream inputStream = new FileInputStream(filepath);

            char[] chars = new char[inputStream.available()];

            int index = 0;
            while(inputStream.available() > 0) { // read the inputStream in as an array of chars
                chars[index] = ( (char) inputStream.read() );
                index++;
            }

            // call on the parseFromCharacters method to add the key-value pairs
            parseFromCharacters(chars);


        } catch (IOException e) {
            e.printStackTrace();
            System.exit(500); // something happened that wasn't our fault
        }
    }

    /**
     * Takes a String and preemptively escapes all special characters that will get interpreted when the String is
     * constructed. This preserves them in their raw form.
     *
     * @param string String whose special characters will be escaped
     * @return Same String with all special characters escaped
     * */
    private String reescapeString(String string) {
        string = nonWhiteSpaceRegex.matcher(string).replaceAll("\\\\$1"); // Escape all quotes and slashes
        string = newlineRegex.matcher(string).replaceAll("\\\\n"); // Escape all newline characters
        string = tabRegex.matcher(string).replaceAll("\\\\t"); // Escape all tabs
        string = formfeedRegex.matcher(string).replaceAll("\\\\f"); // Escape all formfeeds (who uses a formfeed?)
        return carriagereturnRegex.matcher(string).replaceAll("\\\\r"); // Escape all carriage returns (yay, windows...)
    }

    /**
     * Sets whether this {@code JSONObject} should print itself out on one line.
     *
     * @param inline Boolean for whether this JSONObject should print out inline (true means inline)
     * */
    public void setInline(boolean inline) {
        this.inline = inline;
    }

    /**
     * Returns a String of {@code numTabs} tabs. If a non-positive number is given, returns the empty string.
     *
     * @param numTabs int number of tabs to be in String
     * @return String containing numTabs tabs
     * */
    private String tabber(int numTabs) {
        if (numTabs < 0) { // Ensure we have at least 0 tabs
            numTabs = 0;
        }

        String result = "";

        for(int i = 0; i < numTabs; i++) { // Add a tab until we have the right number of tabs
            result += "\t";
        }

        return result;
    }

    /**
     * Overloads the standard {@code toString()} method to call our special {@link JSONObject#toString(int) toString(int)}
     * method with a depth of 0.
     *
     * @return String of this JSONObject
     * */
    public String toString(){
        return toString(0);
    }

    /**
     * Overloads the {@link JSONObject#toString(int, boolean) toString(int, boolean)} method to use the inline boolean
     * of this {@code JSONObject}.
     *
     * @param depth int for the number of tabs to prefix to every line (if not inline)
     * @return String of this JSONObject
     * */
    public String toString(int depth) {
        return toString(depth, inline);
    }

    /**
     * Returns a String representation of this {@code JSONObject}. If {@code inline} is true, then the String will be as
     * compact as possible. If {@code inline} is false, then the String will be human-readable with at least
     * {@code depth} tabs prefixed to every line.
     *
     * @param depth int for the number of tabs to prefix to every line (if not inline)
     * @param inline boolean for whether the String should be as compact as possible
     * @return String of this JSONObject
     * */
    public String toString(int depth, boolean inline) {

        if (inline) { // if we are inline, then we strip out all the useless whitespace outside of strings

            String result = "{"; // the open bracket around the entire object

            int keyNum = 0; // number for the key we are currently on
            for (String key : values.keySet()) { // cycle over every key
                result += ("\"" + key + "\":"); // add the key in quotes before adding a colon

                Object value = this.fetch(key); // grab the value
                if (value == null) {
                    result += "\"null\""; // if it is null, print the string "null" (in quotes)
                } else if (value instanceof String) {
                    result += ("\"" + value + "\""); // if it is a string, simply add it in quotes
                } else if (value instanceof String[]) {
                    // if the value is a string array...
                    String[] valueAsStringArray = (String[]) value;

                    result += "["; // start the array

                    for (int i = 0; i < valueAsStringArray.length; i++) { // cycle over all the strings in the array
                        result += "\"" + valueAsStringArray[i] + "\""; // add each value in quotes

                        result += (i == valueAsStringArray.length - 1) ? "]" : ","; // add a comma, unless it's the last value, in which case add the closing bracket
                    }

                } else if (value instanceof JSONObject) { // if the value is a JSONObject
                    result += ((JSONObject) value).toString(true); // have the object convert itself to a string (also inline)
                } else if (value instanceof JSONObject[]) {
                    // if the value is a JSONObject array...
                    JSONObject[] valueAsJSONObjectArray = (JSONObject[]) value;

                    result += "["; // start the array

                    for (int i = 0; i < valueAsJSONObjectArray.length; i++) { // cycle over all the JSONObjects in the array

                        result += valueAsJSONObjectArray[i].toString(true); // add each value (also inline)

                        result += (i == valueAsJSONObjectArray.length - 1) ? "]" : ","; // add a comma, unless it's the last value, in which case add the closing bracket
                    }
                } else { // if we don't know what the value is...
                    result += "\"ERROR!\""; // print ERROR! in quotes
                    System.out.println("WARNING: Value of Key " + key + " was not a valid type!"); // warn that the value was not valid
                }

                result += (keyNum == values.keySet().size() - 1) ? "" : ","; // if this key-value pair was not the last one, add a comma
                keyNum++; // increment the key number
            }

            result += "}"; // add the closing bracket

            return result; // return the finished string

        } else { // we are not inline, so we make it look all pretty and human-readable

            String result = "{\n" + tabber(depth + 1); // add a newline and an extra tab after the first line

            int keyNum = 0; // number for the key we are currently on
            for (String key : values.keySet()) { // cycle over all the keys
                result += ("\"" + key + "\":"); // add the key in quotes before adding the colon

                Object value = this.fetch(key); // grab the value
                if (value == null) {
                    result += "\"null\""; // if it is null, print the string "null" (in quotes)
                } else if (value instanceof String) {
                    result += ("\"" + value + "\""); // if it is a string, simply add it in quotes
                } else if (value instanceof String[]) {
                    // if value is a String array...
                    String[] valueAsStringArray = (String[]) value;

                    result += "\n" + tabber(depth + 1) + "[\n" + tabber(depth + 2); // add a newline and one extra tab before the open bracket, another newline, and two extra tabs

                    for (int i = 0; i < valueAsStringArray.length; i++) { // cycle over all the strings in the array
                        result += "\"" + valueAsStringArray[i] + "\""; // add each value in quotes

                        result += (i == valueAsStringArray.length - 1) ? "\n" + tabber(depth + 1) + "]" : ",\n" + tabber(depth + 2);
                        // if this is not the last value, add a comma, drop a line, and add two extra tabs
                        // if this is the last value, drop the line, add one extra tab, and then the closing bracket
                    }

                } else if (value instanceof JSONObject) {
                    result += ((JSONObject) value).toString(depth + 1); // if value is a JSONObject, let it choose whether to be inline, but give it one depth more than this object's depth
                } else if (value instanceof JSONObject[]) {
                    // if value is a JSONObject array...
                    JSONObject[] valueAsJSONObjectArray = (JSONObject[]) value;

                    result += "\n" + tabber(depth + 1) + "[\n" + tabber(depth + 2); // add a newline and one extra tab before the open bracket, another newline, and two extra tabs

                    for (int i = 0; i < valueAsJSONObjectArray.length; i++) { // cycle over all the JSONObjects in the array

                        result += valueAsJSONObjectArray[i].toString(depth + 2); // let the JSONObject choose whether to be inline, but give it two depths more than this object's depth

                        result += (i == valueAsJSONObjectArray.length - 1) ? "\n" + tabber(depth + 1) + "]" : ",\n" + tabber(depth + 2);
                        // if this is not the last value, add a comma, drop a line, and add two extra tabs
                        // if this is the last value, drop the line, add one extra tab, and then the closing bracket
                    }
                } else { // if we don't know what the value is...
                    result += "\"ERROR!\""; // print out ERROR! in quotes
                    System.out.println("WARNING: Value of Key " + key + " was not a valid type!"); // warn that the value was not valid
                }

                result += (keyNum == values.keySet().size() - 1) ? "\n" : ",\n" + tabber(depth + 1);
                // if this key-value pair was not the last one, add a comma, a newline, and one extra tab
                // if this is the last pair, just drop the line
                keyNum++; // increment the key number
            }

            result += tabber(depth) + "}"; // add no extra tabs before the final closing bracket

            return result; // return the finished string
        }
    }

    /**
     * Overloads the {@link JSONObject#toString(int, boolean) toString(int, boolean)} method with a depth of 0.
     *
     * @param inline boolean for whether the String should be as compact as possible
     * @return String of this JSONObject
     * */
    public String toString(boolean inline) {
        return toString(0, inline);
    }

    /**
     * Writes a copy of this {@code JSONObject} to the {@code .json} filepath given.
     *
     * @param filepath String for the filepath of the .json file to be saved to
     * @return boolean for whether this {@code JSONObject} was successfully written to file
     * */
    public boolean writeToFile(String filepath) {

        // warn if the filepath does not end in .json
        if (!filepath.endsWith(".json")) {
            System.out.println("WARNING: File " + filepath + " is not a .json file. Writing may not work!");
        }

        try {

            FileOutputStream outputStream = new FileOutputStream(filepath); // establish the output stream

            outputStream.write(this.toString().getBytes()); // convert this JSONObject to a string before converting it to bytes

            outputStream.close(); // close once we are done

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
//            System.exit(500); // Something went wrong that wasn't our fault
        }

    }

}
