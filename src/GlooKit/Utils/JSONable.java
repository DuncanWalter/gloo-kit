package GlooKit.Utils;

/**
 * The interface {@code JSONable} allows for the linking of some type of object to a {@link JSONObject JSONObject},
 * which can be read from or written to file.
 *
 * @see JSONObject
 *
 * @author Eli Jergensen
 * @author Duncan Walter
 * @since 1.0
 * */
public interface JSONable <T> {

    /**
     * A method for turning this Object into a {@code JSONObject}. This will likely consist of several
     * {@link JSONObject#add(String, Object) JSONObject#add(String, Object)} calls.
     *
     * @return JSONObject version of this Object
     * */
    JSONObject constructJSONObject();

    /**
     * Constructs an Object from a given {@code JSONObject}. This will likely consist of several
     * {@link JSONObject JSONObject#fetch...(String...)} calls.
     *
     * @param jsonObject A JSONObject whose values will be used to construct an Object
     * @return This Object constructed from the given JSONObject
     * */
    T constructFromJSON(JSONObject jsonObject);

}
