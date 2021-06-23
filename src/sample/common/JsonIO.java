package sample.common;

import com.google.gson.Gson;
import org.hildan.fxgson.FxGson;

import java.io.*;

public class JsonIO {

    public static <T> T loadFromJsonFile(final String importJsonFilePath, Class<T> classOfT) throws Exception {
        //jsonをチェックしてロード
        final StringBuilder jsonObj = new StringBuilder();
        String line;
        try (final BufferedReader br = new BufferedReader(new FileReader(importJsonFilePath))) {
            while ((line = br.readLine()) != null) {
                jsonObj.append(line);
            }
        }
        final Gson gson = FxGson.create();
        return gson.fromJson(jsonObj.toString(), classOfT);
    }

    public static void saveToJsonFile(final String exportJsonFilePath, Object convertType) throws Exception {
        final Gson gson = FxGson.coreBuilder().setPrettyPrinting().create();
        final String jsonObj = gson.toJson(convertType);
        try (final Writer writer = new FileWriter(exportJsonFilePath)) {
            writer.write(jsonObj);
        }
        System.out.println("SAVE JSON:" + jsonObj);
    }
}
