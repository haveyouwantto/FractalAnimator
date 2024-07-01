package hywt.fractal.animator;

import org.json.JSONObject;

public interface Exportable {
    JSONObject exportJSON();
    void importJSON(JSONObject obj);
}
