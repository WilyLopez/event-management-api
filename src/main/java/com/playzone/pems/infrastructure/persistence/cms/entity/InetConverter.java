package com.playzone.pems.infrastructure.persistence.cms.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

@Converter
public class InetConverter implements AttributeConverter<String, Object> {

    private static Class<?> pgObjectClass;
    private static Constructor<?> pgObjectConstructor;
    private static Method setTypeMethod;
    private static Method setValueMethod;

    static {
        try {
            pgObjectClass = Class.forName("org.postgresql.util.PGobject");
            pgObjectConstructor = pgObjectClass.getConstructor();
            setTypeMethod = pgObjectClass.getMethod("setType", String.class);
            setValueMethod = pgObjectClass.getMethod("setValue", String.class);
        } catch (Exception e) {
            // Manejado silenciosamente para tests unitarios (H2) u otros entornos
        }
    }

    @Override
    public Object convertToDatabaseColumn(String attribute) {
        if (pgObjectClass == null) {
            // Fallback para base de datos H2 en tests
            return attribute;
        }
        try {
            Object pgObject = pgObjectConstructor.newInstance();
            setTypeMethod.invoke(pgObject, "inet");
            if (attribute != null && !attribute.isBlank()) {
                setValueMethod.invoke(pgObject, attribute.trim());
            } else {
                setValueMethod.invoke(pgObject, (String) null);
            }
            return pgObject;
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al convertir String a inet usando reflexión: " + attribute, e);
        }
    }

    @Override
    public String convertToEntityAttribute(Object dbData) {
        if (dbData == null) {
            return null;
        }
        return dbData.toString();
    }
}
