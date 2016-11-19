package GlooKit.Utils;


public interface JSONable <T> {

    JSONObject constructJSONObject();

    T constructFromJSON(JSONObject jsonObject);

}
