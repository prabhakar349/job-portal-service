package com.ceitechs.dproz.jobportalservice.utils;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.Getter;
import org.springframework.util.StringUtils;

public class DprozUtility {

    /**
     * DATE_FORMAT = "YYYY-MM-dd"
     */
    public static final String  DATE_FORMAT_PATTERN = "\\d{4}-\\d{2}-\\d{2}";
    public static final String DATE_FORMAT_PATTERN_SEPEATOR ="-";

    @Getter
    private String SECRET_KEY; // To be passed in as environment variable


    /**
     * @param sECRET_KEY the sECRET_KEY to set
     */
    public void setSECRET_KEY(String sECRET_KEY) {
        String[] secrets = sECRET_KEY.split("-");
        SECRET_KEY = secrets[secrets.length - 1];
    }

    /**
     * Used by JCE for D/Encryption
     *
     * @return Cipher
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    static Cipher getCipher() throws NoSuchAlgorithmException, NoSuchPaddingException {
        return Cipher.getInstance("AES");
    }

    public static String encrypt(String plainText, SecretKey secretKey) throws Exception {
        Cipher cipher = getCipher();
        byte[] plainTextByte = plainText.getBytes();
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedByte = cipher.doFinal(plainTextByte);
        Base64.Encoder encoder = Base64.getEncoder();
        String encryptedText = encoder.encodeToString(encryptedByte);
        return encryptedText;
    }

    public static String encrypt(String plainText, String encodedKey) throws Exception {
        SecretKey secretKey = secretKey(encodedKey);
        return encrypt(plainText, secretKey);
    }

    public static String decrypt(String encryptedText, String encodedKey) throws Exception {
        SecretKey secretKey = secretKey(encodedKey);
        return decrypt(encryptedText, secretKey);
    }

    public static String decrypt(String encryptedText, SecretKey secretKey) throws Exception {
        Cipher cipher = getCipher();
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] encryptedTextByte = decoder.decode(encryptedText);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedByte = cipher.doFinal(encryptedTextByte);
        String decryptedText = new String(decryptedByte);
        return decryptedText;
    }

    static SecretKey secretKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        SecretKey secretKey = keyGenerator.generateKey();
        return secretKey;
    }

    static SecretKey secretKey(String encodedKey) throws NoSuchAlgorithmException {
        // decode the base64 encoded string
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        // rebuild key using SecretKeySpec
        SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        return secretKey;
    }

    static String secretKeyText() throws NoSuchAlgorithmException {
        // create new key
        SecretKey secretKey = secretKey();
        // get base64 encoded version of the key
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    public static String generateIdAsString() {
        return replaceHyphens(UUID.randomUUID().toString());
    }

    public static String generateIdAsUUID() {
        return UUID.randomUUID().toString();
    }

    public static String replaceSpaces(String codefrom) {
        return codefrom.replaceAll("\\s+", "");
    }

    public static String replaceHyphens(String codefrom) {
        return codefrom.replaceAll("-", "");
    }

    public static long generateIdAsLong() {
        Random randomno = new Random();

        // get next long value
        long value = randomno.nextLong();
        return value < 0 ? -1 * value : value;
    }

    public static <T> ArrayList<T> toArrayList(final Iterator<T> iterator) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false)
                .collect(Collectors.toCollection(ArrayList::new));

    }

    public static <T> List<T> toList(final Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
    }

    public static Optional<LocalDate> getLocalDateDateFrom(String dateString){
        if (StringUtils.hasText(dateString) && dateString.matches(DATE_FORMAT_PATTERN)){
            String[] dateParts  = dateString.split(DATE_FORMAT_PATTERN_SEPEATOR);
            return Optional.of(LocalDate.of(Integer.valueOf(dateParts[0]),Integer.valueOf(dateParts[1]),Integer.valueOf(dateParts[2])));
        }
        return Optional.empty();
    }

    /**
     * updates the original object with new values from the updatable object
     * @param originalObject SOR object
     * @param updatedObject with new updates
     * @param updatableProperties eligible fields to update
     * @param clazz
     * @param <T>
     * @return
     * @throws IntrospectionException
     */
    public static <T> boolean updatedSomeObjectProperties(T originalObject, T updatedObject, List<String> updatableProperties, Class<T> clazz)  {
        if (originalObject == null || updatedObject == null || (updatableProperties == null || updatableProperties.isEmpty()))
            return false; // null parameters were passed.
        if (originalObject != updatedObject) { //only go through the process if objects are not same reference
            try {
                Arrays.stream(Introspector.getBeanInfo(clazz).getPropertyDescriptors())
                        .filter(propertyDescriptor -> updatableProperties.contains(propertyDescriptor.getName()))
                        .forEach(propertyDescriptor -> {
                            updateProperty(originalObject, updatedObject, propertyDescriptor);

                        });
            } catch (IntrospectionException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    /**
     * Updates an original object field value with an updated object value
     *
     * @param originalObject
     * @param updatedObject
     * @param descriptor
     */
    private static void updateProperty(Object originalObject, Object updatedObject, PropertyDescriptor descriptor) {
        try {
            Method readMethod = descriptor.getReadMethod();
            Object newValue = readMethod.invoke(updatedObject);
            if (newValue != null && !newValue.equals(readMethod.invoke(originalObject))) {
                Method writeMethod = descriptor.getWriteMethod();
                writeMethod.invoke(originalObject, newValue);
            }
        }catch (Exception ex){
            ex.printStackTrace(); //log this exception
        }
    }


    /**
     * Retrieves all annotated fields in a class by annotations
     *
     * @param clazz a class with fields of interest
     * @param annotationClass annotation class
     * @return
     */
    public static List<String> fieldNamesByAnnotation(Class clazz, Class<? extends Annotation> annotationClass){
        Field[] fieldList = clazz.getDeclaredFields();
        return Arrays.stream(fieldList).filter(field -> field.isAnnotationPresent(annotationClass)).map(Field::getName).collect(Collectors.toList());
    }
}
