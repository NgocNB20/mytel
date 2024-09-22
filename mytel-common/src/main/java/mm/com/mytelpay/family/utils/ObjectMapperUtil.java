/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mm.com.mytelpay.family.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class ObjectMapperUtil {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .registerModule(new JavaTimeModule());

    private ObjectMapperUtil(){};
    
    private static final Logger logger = LogManager.getLogger(ObjectMapperUtil.class);
    
    public static <O> String toJsonString(O o){
        try {
            return OBJECT_MAPPER.writeValueAsString(o);
        } catch (JsonProcessingException ex) {
            logger.info("Write Object as String Value False: " + ex.getMessage());
            return "";
        }
    }

    public static <T> T convertJsonToObject(String jsonStr, final Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(jsonStr, clazz);
        } catch (JsonProcessingException ex) {
            logger.info("Convert String to Object False: " + ex.getMessage());
            return (T) new Object();
        }
    }
    
    public static <T> T convert(Object source, Class<T> clazz) throws InstantiationException{
        try {
            String sourceString = OBJECT_MAPPER.writeValueAsString(source);
            return OBJECT_MAPPER.readValue(sourceString, clazz);
        } catch (JsonProcessingException ex) {
            logger.info("Convert Object to Object False: " + ex.getMessage());
            return (T) new Object();
        }
    }

    public static <T> List<T> convertList(List<?> source, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        for (Object item : source) {
            result.add(OBJECT_MAPPER.convertValue(item, clazz));
        }
        return result;
    }
}
