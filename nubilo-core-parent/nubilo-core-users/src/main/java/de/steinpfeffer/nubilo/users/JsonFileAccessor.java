/**
 * Copyright 2013-2014 Juergen Fickel <steinpfeffer@gmx.de>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.steinpfeffer.nubilo.users;

import static de.steinpfeffer.utilities.validation.Validator.argumentNotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.steinpfeffer.utilities.api.string.ToStringBuilder;
import de.steinpfeffer.utilities.string.DefaultToStringBuilder;

/**
 * This class allows to read and write entity beans from and to a JSON
 * file.
 * 
 * @param <T>
 *            The type of the entity beans the JSON file provides.
 * 
 * @author Juergen Fickel
 * @since 1.0.0
 */
@Immutable
final class JsonFileAccessor<T> implements FileAccessor<T> {

    private final File jsonFile;
    private final ObjectMapper objectMapper;

    public JsonFileAccessor(final File theJsonFile) {
        argumentNotNull(theJsonFile, "The JSON file containing the data file must not be null!");
        jsonFile = theJsonFile;
        objectMapper = new ObjectMapper();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T[] readBeansFromFile(final Class<T[]> beanType) throws IOException {
        final T[] result;
        if (jsonFile.exists()) {
            result = objectMapper.readValue(jsonFile, beanType);
        } else {
            result = (T[]) new Object[0];
        }
        return result;
    }

    @Override
    public void writeBeansToFile(final List<T> beans) throws IOException {
        createJsonFileIfNecessary();
        objectMapper.writeValue(jsonFile, beans);
    }

    private void createJsonFileIfNecessary() throws IOException {
        if (!jsonFile.exists()) {
            jsonFile.createNewFile();
        }
    }

    @Override
    public String getDataSourceIdentification() {
        return jsonFile.getAbsolutePath();
    }

    @Override
    public String toString() {
        final ToStringBuilder builder = DefaultToStringBuilder.newInstance(getClass());
        builder.add("jsonFile", jsonFile).add("objectMapper", objectMapper).toString();
        return builder.toString();
    }

}
