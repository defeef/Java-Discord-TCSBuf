package net.defeef;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {

    private final File file;
    private final Map<String, Object> config = new HashMap<>();
    private final Map<String, Object> defaultConfig = new HashMap<>();
    private final String defaultFilename;

    public static Config create(String filename) {
        return new Config(filename, filename);
    }

    public static Config create(String filename, String defaultFilename) {
        return new Config(filename, defaultFilename);
    }

    private Config(String filename, String defaultFilename) {

        File dataFolder = Main.getInstance().getConfigFolder();

        this.defaultFilename = defaultFilename;
        this.file = new File(dataFolder, filename);

        if (!dataFolder.exists()) {
            if (!dataFolder.mkdirs()) {
                throw new RuntimeException("Failed to make directory: " + file.getPath());
            }
        }

        if (!file.exists()) {
            try{
                InputStream input = Main.getInstance().getResource(defaultFilename);
                if (input == null) {
                    throw new RuntimeException("Could not create input stream for "+defaultFilename);
                }
                Files.copy(input, file.toPath());
                input.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Could not create inputstream for "+file.getPath());
        }
        InputStreamReader reader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
        Yaml yaml = new Yaml();
        Map<String, Object> temp = null;
        try {
            temp = yaml.load(reader);
        } catch(YAMLException e) {
            throw new RuntimeException("Invalid configuration in config file: "+file.getPath());
        } catch(Exception e) {
            throw new RuntimeException("Could not access file: "+file.getPath());
        }
        parseYAML(temp, this.config, "");

        InputStream input = Main.getInstance().getResource(defaultFilename);
        if (input == null) {
            throw new RuntimeException("Could not create input stream for "+defaultFilename);
        }
        InputStreamReader default_reader = new InputStreamReader(input, StandardCharsets.UTF_8);
        try {
            temp = yaml.load(default_reader);
        } catch(YAMLException e) {
            throw new RuntimeException("Invalid configuration in config file: "+file.getPath());
        } catch(Exception e) {
            throw new RuntimeException("Could not access file: "+file.getPath());
        }
        parseYAML(temp, this.defaultConfig, "");

        try{
            fileInputStream.close();
            reader.close();
            default_reader.close();
            input.close();
        } catch (IOException e) {
            throw new RuntimeException("Unable to finalize loading of config files.");
        }

        this.saveConfig();

    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void parseYAML(Map<String, Object> temp, Map<String, Object> target, String prefix){
        for(Map.Entry<String, Object> entry : temp.entrySet()) {
            String path = entry.getKey();
            Object data = entry.getValue();
            if (data instanceof Map<?,?>) {
                Map map = (Map) data;
                parseYAML(map, target, path+".");
            } else {
                target.put(prefix+path, data);
            }
        }
    }

    public boolean contains(String path) {
        return config.containsKey(path);
    }

    private Object getValue(String path){
        if (!contains(path)) {
            return defaultConfig.get(path);
        } else {
            return config.get(path);
        }
    }

    public Integer getInt(String path) {
        Object v = getValue(path);
        if(v instanceof Integer)
            return (Integer)v;
        else
            return 0;
    }

    public Double getDouble(String path) {
        Object v = getValue(path);
        if(v instanceof Double)
            return (Double) v;
        else
            return 0.0;
    }

    public Float getFloat(String path) {
        Object v = getValue(path);
        if(v instanceof Float)
            return (Float)v;
        else
            return 0.0f;
    }

    public Long getLong(String path) {
        Object v = getValue(path);
        if(v instanceof Long)
            return (Long)v;
        else
            return 0L;
    }

    public String getString(String path) {
        Object v = getValue(path);
        if(v instanceof String)
            return (String)v;
        else
            return null;
    }

    public Boolean getBoolean(String path) {
        Object v = getValue(path);
        if(v instanceof Boolean)
            return (Boolean) v;
        else
            return false;
    }

    public void reset(String path) {
        config.put(path, defaultConfig.get(path));
    }

    public void set(String path, Object value) {
        config.put(path, value);
    }

    public void saveConfig() {
        try {
            InputStream is = Main.getInstance().getResource(defaultFilename);
            if (is == null) {
                throw new RuntimeException("Could not create input stream for "+defaultFilename);
            }
            StringBuilder textBuilder = new StringBuilder(new String("".getBytes(), StandardCharsets.UTF_8));
            Reader reader = new BufferedReader(new InputStreamReader(is, Charset.forName(StandardCharsets.UTF_8.name())));
            int c;
            while((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
            String yamlString = new String(textBuilder.toString().getBytes(), StandardCharsets.UTF_8);
            Map<String, Object> temp = new HashMap<>(this.config);
            for(Map.Entry<String, Object> entry: temp.entrySet()) {
                if (entry.getValue() instanceof Integer || entry.getValue() instanceof Double || entry.getValue() instanceof String || entry.getValue() instanceof Boolean || entry.getValue() instanceof List) {
                    String[] parts = entry.getKey().split("\\.");
                    int index = 0;
                    int i = 0;
                    for(String part : parts) {
                        if (i == 0) {
                            index = yamlString.indexOf(part+":", index);
                        } else {
                            index = yamlString.indexOf(" " + part+":", index);
                            index++;
                        }
                        i++;
                        if (index == -1) break;
                    }
                    if (index < 10)  continue;
                    int start = yamlString.indexOf(' ', index);
                    int end = yamlString.indexOf('\n', index);
                    if (end == -1) end = yamlString.length();
                    StringBuilder replace = new StringBuilder(new String("".getBytes(), StandardCharsets.UTF_8));
                    if (entry.getValue() instanceof List) {
                        if (((List<?>) entry.getValue()).isEmpty()) {
                            replace.append("[]");
                        } else {
                            replace.append("[");
                            for (Object o : (List<?>) entry.getValue()) {
                                replace.append(o.toString()).append(", ");
                            }
                            replace = new StringBuilder(replace.substring(0, replace.length() - 2));
                            replace.append("]");
                        }
                    } else {
                        replace.append(entry.getValue());
                    }
                    if (entry.getValue() instanceof String) {
                        replace.append("\"");
                        replace.reverse();
                        replace.append("\"");
                        replace.reverse();
                    }
                    StringBuilder builder = new StringBuilder(yamlString);
                    builder.replace(start+1, end, replace.toString());
                    yamlString = builder.toString();
                }
            }
            Writer fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
            fileWriter.write(yamlString);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}