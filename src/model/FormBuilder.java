package model;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class FormBuilder {
    public static User fromParameters(Map<String, String[]> parameters) {
        var userClass = parameters.get("className")[0];

        if (!(userClass.equals("model.Doctor")) && (!userClass.equals("model.Patient"))) {
            throw new UnsupportedOperationException("Error: saving type " + userClass + " is not yet implemented");
        }

        if (userClass.equals("model.Doctor")) {
            return buildDoctor(parameters);
        }

        return buildPatient(parameters);
    }

    private static Doctor buildDoctor(Map<String, String[]> parameters) {
        UUID id = UUID.fromString(parameters.get("id")[0]);
        String name = parameters.get("name")[0];
        LocalDate birthday = LocalDate.parse(parameters.get("birthday")[0]);
        String mail = parameters.get("mail")[0];
        User.Gender gender = User.Gender.valueOf(parameters.get("gender")[0]);

        List<Doctor.Speciality> specialities = Arrays.stream(parameters.get("specialities"))
                .map(speciality -> Doctor.Speciality.valueOf(speciality))
                .toList();

        int yearsOfExperience = Integer.parseInt(parameters.get("yearsOfExperience")[0]);

        return new Doctor(
                id,
                name,
                birthday,
                mail,
                gender,
                specialities,
                yearsOfExperience);
    }

    public static Patient buildPatient(Map<String, String[]> parameters) {
        UUID id = UUID.fromString(parameters.get("id")[0]);
        String name = parameters.get("name")[0];
        LocalDate birthday = LocalDate.parse(parameters.get("birthday")[0]);
        String mail = parameters.get("mail")[0];
        User.Gender gender = User.Gender.valueOf(parameters.get("gender")[0]);

        String disease = parameters.get("disease")[0];
        int severity = Integer.parseInt(parameters.get("severity")[0]);

        return new Patient(
                id,
                name,
                birthday,
                mail,
                gender,
                disease,
                severity);
    }

    public static String toHtml(Class<?> clazz, String action) {
        return new StringBuilder()
                .append("<form method=\"POST\" action=\"").append(action).append("\">\n")
                .append("<input type=\"hidden\" name=\"className\" value=\"")
                .append(clazz.getName()).append("\">\n")
                .append(fieldsToHtml(clazz))
                .append("<button type=\"submit\">Submit</button>\n")
                .append("</form>")
                .toString();
    }

    private static String fieldsToHtml(Class<?> clazz) {
        var html = new StringBuilder();
        var fields = getAllFields(clazz);

        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            Class<?> fieldType = field.getType();

            if (fieldType == String.class) {
                html.append(stringTypeToHtml(field));
            } else if (fieldType.isPrimitive() || isWrapperType(fieldType)) {
                html.append(primitiveTypeToHtml(field));
            } else if (fieldType.isEnum()) {
                html.append(enumTypeToHtml(field));
            } else if (fieldType == LocalDate.class
                    || fieldType == LocalDateTime.class
                    || fieldType == Instant.class
                    || fieldType == Date.class) {
                html.append(dateTypeToHtml(field));
            } else if (fieldType == UUID.class) {
                html.append(uuidTypeToHtml(field));
            } else if (fieldType.isArray()) {
                html.append(arrayTypeToHtml(field));
            } else if (field.getGenericType() instanceof ParameterizedType) {
                html.append(parametrizedTypeToHtml(field));
            } else {
                throw new UnsupportedOperationException(
                        "Cannot convert type " + fieldType.getSimpleName() + " to HTML form: not yet implemented");
            }
        }

        return html.toString();
    }

    private static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            fields.addAll(0, List.of(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    private static String stringTypeToHtml(Field field) {
        String fieldName = field.getName();
        return htmlLabel(fieldName, fieldName) +
                "<input type=\"text\" id=\"" + fieldName + "\" name=\"" + fieldName + "\">\n";
    }

    private static String primitiveTypeToHtml(Field field) {
        Class<?> type = field.getType();

        if (type == boolean.class || type == Boolean.class) {
            return booleanToHtmlCheckbox(field);
        }

        String fieldName = field.getName();
        String inputType = (type == char.class || type == Character.class) ? "text" : "number";

        return htmlLabel(fieldName, fieldName) +
                "<input type=\"" + inputType + "\" id=\"" + fieldName + "\" name=\"" + fieldName + "\">\n";
    }

    private static String booleanToHtmlCheckbox(Field field) {
        String fieldName = field.getName();
        return htmlLabel(fieldName, fieldName) +
                "<input type=\"checkbox\" id=\"" + fieldName + "\" name=\"" + fieldName + "\">\n";
    }

    private static String enumTypeToHtml(Field field) {
        Class<?> type = field.getType();
        String fieldName = field.getName();
        var html = new StringBuilder();

        html.append(htmlLabel(fieldName, fieldName));
        html.append("<select id=\"").append(fieldName).append("\" name=\"").append(fieldName).append("\">\n");
        for (Object constant : type.getEnumConstants()) {
            html.append("<option value=\"").append(constant).append("\">")
                    .append(constant).append("</option>\n");
        }
        html.append("</select>\n");

        return html.toString();
    }

    private static String dateTypeToHtml(Field field) {
        Class<?> type = field.getType();
        String fieldName = field.getName();
        String inputType;

        if (type == LocalDate.class) {
            inputType = "date";
        } else if (type == LocalDateTime.class || type == Instant.class) {
            inputType = "datetime-local";
        } else if (type == Date.class) {
            inputType = "datetime-local";
        } else {
            throw new UnsupportedOperationException(
                    "Unsupported date type: " + type.getSimpleName());
        }

        return htmlLabel(fieldName, fieldName) +
                "<input type=\"" + inputType + "\" id=\"" + fieldName + "\" name=\"" + fieldName + "\">\n";
    }

    private static String uuidTypeToHtml(Field field) {
        String fieldName = field.getName();
        return htmlLabel(fieldName, fieldName) +
                "<input type=\"text\" id=\"" + fieldName + "\" name=\"" + fieldName +
                "\" pattern=\"[0-9a-fA-F-]{36}\">\n";
    }

    private static String arrayTypeToHtml(Field field) {
        Class<?> componentType = field.getType().getComponentType();

        if (!isSimpleType(componentType)) {
            throw new UnsupportedOperationException(
                    "Cannot convert array of type " + componentType.getSimpleName()
                            + " to HTML form: not yet implemented");
        }

        if (componentType.isEnum()) {
            return enumCollectionTypeToHtml(field, componentType);
        }

        return collectionOfSimpleTypeToHtml(field, componentType);
    }

    private static String parametrizedTypeToHtml(Field field) {
        Class<?> fieldType = field.getType();

        if (Map.class.isAssignableFrom(fieldType)) {
            throw new UnsupportedOperationException("Maps cannot be converted to HTML yet");
        }

        if (!List.class.isAssignableFrom(fieldType) && !Set.class.isAssignableFrom(fieldType)) {
            throw new UnsupportedOperationException(
                    "Cannot convert type " + fieldType.getSimpleName() + " to HTML form: not yet implemented");
        }

        ParameterizedType genericType = (ParameterizedType) field.getGenericType();
        Type[] typeArguments = genericType.getActualTypeArguments();
        Class<?> elementType = (Class<?>) typeArguments[0];

        if (!isSimpleType(elementType)) {
            throw new UnsupportedOperationException(
                    "Cannot convert " + fieldType.getSimpleName() + "<" + elementType.getSimpleName() +
                            "> to HTML form: only simple element types are supported");
        }

        if (elementType.isEnum()) {
            return enumCollectionTypeToHtml(field, elementType);
        }

        return collectionOfSimpleTypeToHtml(field, elementType);
    }

    private static String collectionOfSimpleTypeToHtml(Field field, Class<?> elementType) {
        String fieldName = field.getName();

        return htmlLabel(fieldName, fieldName) +
                "<input type=\"text\" id=\"" + fieldName +
                "\" name=\"" + fieldName +
                "\" placeholder=\"Comma-separated " +
                elementType.getSimpleName() + " values\">\n";
    }

    private static String enumCollectionTypeToHtml(Field field, Class<?> enumType) {
        String fieldName = field.getName();
        var html = new StringBuilder();

        html.append(htmlLabel(fieldName, fieldName));
        html.append("<select id=\"").append(fieldName)
                .append("\" name=\"").append(fieldName)
                .append("\" multiple>\n");

        for (Object constant : enumType.getEnumConstants()) {
            html.append("<option value=\"").append(constant).append("\">")
                    .append(constant).append("</option>\n");
        }

        html.append("</select>\n");

        return html.toString();
    }

    private static boolean isWrapperType(Class<?> type) {
        return type == Integer.class || type == Long.class || type == Short.class
                || type == Byte.class || type == Double.class || type == Float.class
                || type == Boolean.class || type == Character.class;
    }

    private static boolean isSimpleType(Class<?> type) {
        return type.isPrimitive() || isWrapperType(type) || type == String.class || type.isEnum();
    }

    private static String htmlLabel(String name, String targetId) {
        return "<label for=\"" + targetId + "\">" + toNormalCase(name) + "</label>\n";
    }

    private static String toNormalCase(String camelCase) {
        if (camelCase == null || camelCase.isEmpty()) {
            return camelCase;
        }

        var result = new StringBuilder();
        var isFirst = true;
        for (char c : camelCase.toCharArray()) {
            if (isFirst) {
                result.append(Character.toUpperCase(c));
                isFirst = false;
            } else if (Character.isUpperCase(c) && result.charAt(result.length() - 1) != ' ') {
                result.append(' ').append(c);
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }
}