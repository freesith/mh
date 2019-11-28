package com.freesith.jsonvision.parse;

import org.json.JSONException;
import org.json.JSONTokener;

public class JsonTokener extends JSONTokener {
    /**
     * @param in JSON encoded string. Null is not permitted and will yield a
     *           tokener that throws {@code NullPointerExceptions} when methods are
     *           called.
     */
    public JsonTokener(String in) {
        super(in);
    }

}
