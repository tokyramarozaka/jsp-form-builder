package model;

import java.lang.reflect.Array;
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
    public static String toHtml(Class<?> clazz, String action) {
        return buildForm(clazz, null, action);
    }

    public static String toHtml(User user, String action) {
        return buildForm(user.getClass(), user, action);
    }

    public static String toCreateOrUpdateHtmlFormByUserId(Class<?> modelClass, String id, UserRepository repository) {
        String error = null;
        String className = modelClass.getName();

        try {
            if (Modifier.isAbstract(modelClass.getModifiers())) {
                error = "Cannot build a form for abstract class: '" + className + "'";
            } else {
                return (id != null)
                        ? FormBuilder.toHtml(repository.findById(UUID.fromString(id)), "saveForm.jsp")
                        : FormBuilder.toHtml(Class.forName(className), "saveForm.jsp");
            }
        } catch (ClassNotFoundException e) {
            error = "Class not found: " + className;
        } catch (RuntimeException e) {
            error = "Could not build form for '" + className + "': " + e.getMessage();
        }

        throw new RuntimeException("Could not build the form: " + error);
    }

    private static String buildForm(Class<?> clazz, Object instance, String action) {
        return new StringBuilder()
                .append("<form method=\"POST\" action=\"").append(action).append("\">\n")
                .append("<input type=\"hidden\" name=\"className\" value=\"")
                .append(clazz.getName()).append("\">\n")
                .append(fieldsToHtml(clazz, instance))
                .append("<button type=\"submit\">Submit</button>\n")
                .append("</form>")
                .toString();
    }

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

    private static String fieldsToHtml(Class<?> clazz, Object instance) {
        var html = new StringBuilder();
        var fields = getAllFields(clazz);

        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            Class<?> fieldType = field.getType();
            Object value = currentValue(field, instance);

            if (fieldType == String.class) {
                html.append(stringTypeToHtml(field, value));
            } else if (fieldType.isPrimitive() || isWrapperType(fieldType)) {
                html.append(primitiveTypeToHtml(field, value));
            } else if (fieldType.isEnum()) {
                html.append(enumTypeToHtml(field, value));
            } else if (fieldType == LocalDate.class
                    || fieldType == LocalDateTime.class
                    || fieldType == Instant.class
                    || fieldType == Date.class) {
                html.append(dateTypeToHtml(field, value));
            } else if (fieldType == UUID.class) {
                html.append(uuidTypeToHtml(field, value));
            } else if (fieldType.isArray()) {
                html.append(arrayTypeToHtml(field, value));
            } else if (field.getGenericType() instanceof ParameterizedType) {
                html.append(parametrizedTypeToHtml(field, value));
            } else {
                throw new UnsupportedOperationException(
                        "Cannot convert type " + fieldType.getSimpleName() + " to HTML form: not yet implemented");
            }
        }

        return html.toString();
    }

    private static Object currentValue(Field field, Object instance) {
        if (instance == null) {
            return null;
        }
        try {
            field.setAccessible(true);
            return field.get(instance);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    private static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            fields.addAll(0, List.of(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    private static String stringTypeToHtml(Field field, Object value) {
        String fieldName = field.getName();
        return htmlLabel(fieldName, fieldName) +
                "<input type=\"text\" id=\"" + fieldName + "\" name=\"" + fieldName +
                "\" value=\"" + escapeHtml(value) + "\">\n";
    }

    private static String primitiveTypeToHtml(Field field, Object value) {
        Class<?> type = field.getType();

        if (type == boolean.class || type == Boolean.class) {
            return booleanToHtmlCheckbox(field, value);
        }

        String fieldName = field.getName();
        String inputType = (type == char.class || type == Character.class) ? "text" : "number";

        return htmlLabel(fieldName, fieldName) +
                "<input type=\"" + inputType + "\" id=\"" + fieldName + "\" name=\"" + fieldName +
                "\" value=\"" + escapeHtml(value) + "\">\n";
    }

    private static String booleanToHtmlCheckbox(Field field, Object value) {
        String fieldName = field.getName();
        boolean checked = Boolean.TRUE.equals(value);
        return htmlLabel(fieldName, fieldName) +
                "<input type=\"checkbox\" id=\"" + fieldName + "\" name=\"" + fieldName + "\"" +
                (checked ? " checked" : "") + ">\n";
    }

    private static String enumTypeToHtml(Field field, Object value) {
        Class<?> type = field.getType();
        String fieldName = field.getName();
        var html = new StringBuilder();

        html.append(htmlLabel(fieldName, fieldName));
        html.append("<select id=\"").append(fieldName).append("\" name=\"").append(fieldName).append("\">\n");
        for (Object constant : type.getEnumConstants()) {
            boolean selected = constant.equals(value);
            html.append("<option value=\"").append(constant).append("\"")
                    .append(selected ? " selected" : "")
                    .append(">").append(constant).append("</option>\n");
        }
        html.append("</select>\n");

        return html.toString();
    }

    private static String dateTypeToHtml(Field field, Object value) {
        Class<?> type = field.getType();
        String fieldName = field.getName();
        String inputType;
        String safeValue = "";

        if (type == LocalDate.class) {
            inputType = "date";
            if (value != null) {
                safeValue = value.toString();
            }
        } else if (type == LocalDateTime.class) {
            inputType = "datetime-local";
            if (value != null) {
                safeValue = value.toString();
            }
        } else if (type == Instant.class) {
            inputType = "datetime-local";
            if (value != null) {
                safeValue = LocalDateTime.ofInstant((Instant) value, java.time.ZoneId.systemDefault()).toString();
            }
        } else if (type == Date.class) {
            inputType = "datetime-local";
            if (value != null) {
                safeValue = new java.sql.Timestamp(((Date) value).getTime()).toLocalDateTime().toString();
            }
        } else {
            throw new UnsupportedOperationException(
                    "Unsupported date type: " + type.getSimpleName());
        }

        return htmlLabel(fieldName, fieldName) +
                "<input type=\"" + inputType + "\" id=\"" + fieldName + "\" name=\"" + fieldName +
                "\" value=\"" + safeValue + "\">\n";
    }

    private static String uuidTypeToHtml(Field field, Object value) {
        String fieldName = field.getName();
        return htmlLabel(fieldName, fieldName) +
                "<input type=\"text\" id=\"" + fieldName + "\" name=\"" + fieldName +
                "\" value=\"" + escapeHtml(value) + "\" pattern=\"[0-9a-fA-F-]{36}\">\n"; // regex: regular expression
    }

    private static String arrayTypeToHtml(Field field, Object value) {
        Class<?> componentType = field.getType().getComponentType();

        if (!isSimpleType(componentType)) {
            throw new UnsupportedOperationException(
                    "Cannot convert array of type " + componentType.getSimpleName()
                            + " to HTML form: not yet implemented");
        }

        List<Object> values = toValueList(value);

        if (componentType.isEnum()) {
            return enumCollectionTypeToHtml(field, componentType, values);
        }

        return collectionOfSimpleTypeToHtml(field, componentType, values);
    }

    private static String parametrizedTypeToHtml(Field field, Object value) {
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

        List<Object> values = toValueList(value);

        if (elementType.isEnum()) {
            return enumCollectionTypeToHtml(field, elementType, values);
        }

        return collectionOfSimpleTypeToHtml(field, elementType, values);
    }

    private static List<Object> toValueList(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof java.util.Collection<?> collection) {
            return new ArrayList<>(collection);
        }
        List<Object> list = new ArrayList<>();
        int length = Array.getLength(value);
        for (int i = 0; i < length; i++) {
            list.add(Array.get(value, i));
        }
        return list;
    }

    private static String collectionOfSimpleTypeToHtml(Field field, Class<?> elementType, List<Object> values) {
        String fieldName = field.getName();
        String safeValue = "";
        if (values != null) {
            var joined = new StringBuilder();
            for (Object v : values) {
                if (joined.length() > 0) {
                    joined.append(", ");
                }
                joined.append(v);
            }
            safeValue = escapeHtml(joined.toString());
        }

        return htmlLabel(fieldName, fieldName) +
                "<input type=\"text\" id=\"" + fieldName +
                "\" name=\"" + fieldName +
                "\" value=\"" + safeValue +
                "\" placeholder=\"Comma-separated " +
                elementType.getSimpleName() + " values\">\n";
    }

    private static String enumCollectionTypeToHtml(Field field, Class<?> enumType, List<Object> values) {
        String fieldName = field.getName();
        var html = new StringBuilder();
        html.append(htmlLabel(fieldName, fieldName));

        html.append("<div class=\"checkbox-group\" id=\"").append(fieldName).append("\">\n");
        for (Object constant : enumType.getEnumConstants()) {
            String value = constant.toString();
            String checkboxId = fieldName + "-" + value;
            boolean checked = values != null && values.contains(constant);
            html.append("<div class=\"checkbox-option\">\n");
            html.append("<input type=\"checkbox\" id=\"").append(checkboxId)
                    .append("\" name=\"").append(fieldName)
                    .append("\" value=\"").append(value).append("\"")
                    .append(checked ? " checked" : "")
                    .append(">\n");
            html.append("<label for=\"").append(checkboxId).append("\">")
                    .append(value).append("</label>\n");
            html.append("</div>\n");
        }
        html.append("</div>\n");

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

    private static String escapeHtml(Object value) {
        if (value == null) {
            return "";
        }
        return value.toString()
                .replace("&", "&amp;")
                .replace("\"", "&quot;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
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