package com.example.demohttpclient.commons;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonMapperUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonMapperUtil.class);
    private ObjectMapper rawMapper;
    private ObjectWriter rawWriter;

    public JsonMapperUtil() {
        rawMapper = new ObjectMapper();
        rawWriter = rawMapper.writer();
        configureMappingProvider();
    }

    private void configureMappingProvider() {
        rawMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        rawMapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, true);
        rawMapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
    }

    public <T> String mapToJson(T entity) throws Exception {
        try {
            return rawWriter.writeValueAsString(entity);
        } catch (Exception e) {
            LOGGER.error("Error when call mapToJson method:{}", e.getMessage());
            throw e;
        }
    }

    public <T> List<String> mapToJson(List<T> entities) throws Exception {
        try {
            List<String> entitiesJson = new ArrayList<String>();
            for (T entity : entities) {
                entitiesJson.add(rawWriter.writeValueAsString(entity));
            }
            return entitiesJson;
        } catch (Exception e) {
            LOGGER.error("Error when call mapToJson method:{}", e.getMessage());
            throw e;
        }
    }

    public <T> T mapToEntity(String entityJson, Class<T> entityClass) throws Exception {
        try {
            return rawMapper.readValue(entityJson, entityClass);
        } catch (Exception e) {
            LOGGER.error("Error when call mapToEntity method:{}", e.getMessage());
            throw e;
        }
    }

    public <T> List<T> mapObjectToListEntity(Object obj, Class<T> entityClass) throws Exception {
        try {
            CollectionType listType = rawMapper.getTypeFactory().constructCollectionType(ArrayList.class, entityClass);
            return rawMapper.convertValue(obj, listType);
        } catch (Exception ex) {
            LOGGER.error("error: " + ex.getMessage());
            return null;
        }
    }

    public <T> T mapObjectToEntity(Object object, Class<T> entityClass) throws Exception {
        try {
            T entity = rawMapper.convertValue(object, entityClass);
            return entity;
        } catch (Exception ex) {
            LOGGER.error("error: " + ex.getMessage());
            return null;
        }
    }

    public <T> List<T> mapToList(String entitiesJson, Class<T> entityClass) throws Exception {
        try {
            CollectionType listType = rawMapper.getTypeFactory().constructCollectionType(ArrayList.class, entityClass);
            return rawMapper.readValue(entitiesJson, listType);
        } catch (JsonParseException e) {
            LOGGER.error("Error when call mapToList method:{}", e.getMessage());
            throw e;
        }
    }

    public <T, S> S mapToNestedLists(int depth, String entitiesJson, Class<T> entityClass) throws Exception {
        try {
            CollectionType outmostListType = rawMapper.getTypeFactory().constructCollectionType(ArrayList.class, entityClass);
            for (int i = 0; i < depth; i++) {
                outmostListType = rawMapper.getTypeFactory().constructCollectionType(ArrayList.class, outmostListType);
            }
            return rawMapper.readValue(entitiesJson, outmostListType);
        } catch (Exception e) {
            LOGGER.error("Error when call mapToNestedLists method:{} ", e.getMessage());
            throw e;
        }
    }

    public <T, S> Map<T, S> mapToMap(String entitiesJson, Class<T> keyClass, Class<S> valueClass) throws Exception {
        try {
            MapType mapType = rawMapper.getTypeFactory().constructMapType(HashMap.class, keyClass, valueClass);
            return rawMapper.readValue(entitiesJson, mapType);
        } catch (Exception e) {
            LOGGER.error("Error when call mapToMap method:{}" + e.getMessage());
            throw e;
        }
    }

    public static <T> T parseFromFile(String file, Class<T> clazz) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(new File(file), clazz);
        } catch (Exception e) {
            LOGGER.error(e.toString());
            return null;
        }
    }

    public static <T> T parseFromFile(File file, Class<T> clazz) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(file, clazz);
        } catch (Exception e) {
            LOGGER.error(e.toString());
            return null;
        }
    }

    public static <T> void writeToFile(String filePath, T obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(new File(filePath), obj);
        } catch (Exception e) {
            LOGGER.error(e.toString());
        }
    }

    public static <T> void writeToFile(File file, T obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(file, obj);
        } catch (Exception e) {
            LOGGER.error(e.toString());
        }
    }

    public static <T> T parseFromURL(URL url, Class<T> clazz) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(url, clazz);
        } catch (Exception e) {
            LOGGER.error(e.toString());
            return null;
        }
    }
}
