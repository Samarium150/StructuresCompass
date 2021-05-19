package com.github.samarium150.structurescompass.util;

import java.io.*;
import java.util.Base64;

/**
 * Interface for serialize and deserialize objects
 * @param <T> class of the object
 */
public interface Serializer<T extends Serializable> {
    
    /**
     * Serialize the object
     * @param obj object to serialize
     * @return serialized object as a string
     * @throws IOException any exception thrown by the underlying OutputStream
     */
    default String serialize(T obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(obj);
        oos.close();
        return Base64.getEncoder().encodeToString(out.toByteArray());
    }
    
    /**
     * Deserialize the object T
     * @param s serialized object
     * @return deserialized object
     * @throws IOException if something is wrong with a class used by serialization
     * @throws ClassNotFoundException if the class of a serialized object cannot be found
     */
    @SuppressWarnings("unchecked")
    default T deserialize(String s) throws IOException, ClassNotFoundException {
        byte[] data = Base64.getDecoder().decode(s);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        T obj = (T) ois.readObject();
        ois.close();
        return obj;
    }
}
