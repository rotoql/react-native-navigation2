package com.reactnativenavigation.parse.params;

import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.view.MenuItem;

import com.reactnativenavigation.parse.Component;
import com.reactnativenavigation.parse.parsers.BoolParser;
import com.reactnativenavigation.parse.parsers.ColorParser;
import com.reactnativenavigation.parse.parsers.NumberParser;
import com.reactnativenavigation.parse.parsers.TextParser;
import com.reactnativenavigation.utils.TypefaceLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Button {
    public String id;
    public Text text = new NullText();
    public Bool enabled = new NullBool();
    public Bool disableIconTint = new NullBool();
    public Number showAsAction = new NullNumber();
    public Color color = new NullColor();
    public Color disabledColor = new NullColor();
    public Number fontSize = new NullNumber();
    private Text fontWeight = new NullText();
    @Nullable public Typeface fontFamily;
    public Text icon = new NullText();
    public Text testId = new NullText();
    public Component component = new Component();

    protected static Button parseJson(JSONObject json, TypefaceLoader typefaceManager) {
        Button button = new Button();
        button.id = json.optString("id");
        button.text = TextParser.parse(json, "text");
        button.enabled = BoolParser.parse(json, "enabled");
        button.disableIconTint = BoolParser.parse(json, "disableIconTint");
        button.showAsAction = parseShowAsAction(json);
        button.color = ColorParser.parse(json, "color");
        button.disabledColor = ColorParser.parse(json, "disabledColor");
        button.fontSize = NumberParser.parse(json, "fontSize");
        button.fontFamily = typefaceManager.getTypeFace(json.optString("fontFamily", ""));
        button.fontWeight = TextParser.parse(json, "fontWeight");
        button.testId = TextParser.parse(json, "testID");
        button.component = Component.parse(json.optJSONObject("component"));

        if (json.has("icon")) {
            button.icon = TextParser.parse(json.optJSONObject("icon"), "uri");
        }

        return button;
    }

    public static ArrayList<Button> parse(JSONObject json, String buttonsType, TypefaceLoader typefaceLoader) {
        ArrayList<Button> buttons = new ArrayList<>();
        if (!json.has(buttonsType)) {
            return null;
        }

        JSONArray jsonArray = json.optJSONArray(buttonsType);
        if (jsonArray != null) {
            buttons.addAll(parseJsonArray(jsonArray, typefaceLoader));
        } else {
            buttons.add(parseJson(json.optJSONObject(buttonsType), typefaceLoader));
        }
        return buttons;
    }

    private static ArrayList<Button> parseJsonArray(JSONArray jsonArray, TypefaceLoader typefaceLoader) {
        ArrayList<Button> buttons = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject json = jsonArray.optJSONObject(i);
            Button button = Button.parseJson(json, typefaceLoader);
            buttons.add(button);
        }
        return buttons;
    }

    public boolean hasComponent() {
        return component.hasValue();
    }

    public boolean hasIcon() {
        return icon.hasValue();
    }

    private static Number parseShowAsAction(JSONObject json) {
        final Text showAsAction = TextParser.parse(json, "showAsAction");
        if (!showAsAction.hasValue()) {
            return new Number(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }

        switch (showAsAction.get()) {
            case "always":
                return new Number(MenuItem.SHOW_AS_ACTION_ALWAYS);
            case "never":
                return new Number(MenuItem.SHOW_AS_ACTION_NEVER);
            case "withText":
                return new Number(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
            case "ifRoom":
            default:
                return new Number(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
    }

    public void mergeWithDefault(Button defaultOptions) {
        if (!text.hasValue()) text = defaultOptions.text;
        if (!enabled.hasValue()) enabled = defaultOptions.enabled;
        if (!disableIconTint.hasValue()) disableIconTint = defaultOptions.disableIconTint;
        if (!color.hasValue()) color = defaultOptions.color;
        if (!disabledColor.hasValue()) disabledColor = defaultOptions.disabledColor;
        if (!fontSize.hasValue()) fontSize = defaultOptions.fontSize;
        if (fontFamily == null) fontFamily = defaultOptions.fontFamily;
        if (!fontWeight.hasValue()) fontWeight = defaultOptions.fontWeight;
        if (!testId.hasValue()) testId = defaultOptions.testId;
        if (!component.hasValue()) component = defaultOptions.component;
        if (!showAsAction.hasValue()) showAsAction = defaultOptions.showAsAction;
        if (!icon.hasValue()) icon = defaultOptions.icon;
    }
}
