package com.songoda.ultimatetimber.locale;

import com.songoda.ultimatetimber.manager.LocaleManager;
import com.songoda.update.Module;
import com.songoda.update.Plugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.URL;

public class LocaleModule implements Module {

    @Override
    public void run(Plugin plugin) {
        JSONObject json = plugin.getJson();
        try {
            JSONArray files = (JSONArray) json.get("neededFiles");
            for (Object o : files) {
                JSONObject file = (JSONObject) o;

                if (file.get("type").equals("locale"))
                    LocaleManager.saveDefaultLocale(new URL((String) file.get("link")), (String) file.get("name"));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

