package com.robertlancer.util;

import com.google.api.client.util.Maps;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.appengine.api.urlfetch.*;
import com.google.appengine.api.utils.SystemProperty;

import java.io.*;
import java.net.URL;
import java.util.Map;

public class Resources {
    private static Map<String, String> CACHE = Maps.newHashMap();

    public static final boolean IS_LOCAL = SystemProperty.Environment.environment.value() == SystemProperty.Environment.Value.Development;

    public static String getAsText(String path) throws IOException {
        String value = IS_LOCAL ? null : CACHE.get(path);

        if (value == null) {
            InputStream in = new FileInputStream(new java.io.File(path));

            InputStreamReader is = new InputStreamReader(in, "UTF-8");
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(is);
            String read = br.readLine();

            while (read != null) {
                sb.append(read + "\n");
                read = br.readLine();
            }

            br.close();
            value = sb.toString();
            CACHE.put(path, value);
        }

        return value;
    }
}
