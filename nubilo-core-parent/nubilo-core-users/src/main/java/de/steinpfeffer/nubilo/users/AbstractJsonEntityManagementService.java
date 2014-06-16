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
import static de.steinpfeffer.utilities.validation.Validator.validateState;
import static java.lang.String.format;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of the {@link UserManagementService} which uses
 * JSON files for data management. Because of the fact that this
 * implementation is backed by a JSON file it offers the additional
 * method {@link #tryToWwriteUsersCacheToFile()} to trigger
 * persisting. Also the users data is written to the file when a
 * certain amount of changes occurred.
 * 
 * @author Juergen Fickel
 * @since 1.0.0
 */
@ThreadSafe
abstract class AbstractJsonEntityManagementService<B, E extends Entity> implements EntityManagementService<E> {

    private static final int DEFAULT_DIRTY_LIMIT = 10;

    private final int dirtyLimit;
    private final FileAccessor<B> fileAccessor;
    private final EntityConverter entityConverter;
    private final Logger logger;
    private final EntityCache<E> entitiesCache;
    private final AtomicInteger dirtyCounter;

    public AbstractJsonEntityManagementService(final FileAccessor<B> fileAccessor,
            final EntityCache<E> entitiesCache,
            final EntityConverter entityConverter) {
        this(DEFAULT_DIRTY_LIMIT, fileAccessor, entitiesCache, entityConverter);
    }

    /**
     * Constructs a new {@link AbstractJsonEntityManagementService}
     * object.
     * 
     * @throws IOException
     */
    public AbstractJsonEntityManagementService(final int theDirtyLimit,
            final FileAccessor<B> theFileAccessor,
            final EntityCache<E> theEntitiesCache,
            final EntityConverter theEntityConverter) {
        validateState(1 <= theDirtyLimit);
        argumentNotNull(theFileAccessor);
        argumentNotNull(theEntityConverter);
        argumentNotNull(theEntityConverter);
        dirtyLimit = theDirtyLimit;
        fileAccessor = theFileAccessor;
        entityConverter = theEntityConverter;
        logger = LoggerFactory.getLogger(getClass());
        dirtyCounter = new AtomicInteger();
        entitiesCache = initialiseCache(tryToReadEntityBeansFromDataFile(), entityConverter, theEntitiesCache);
    }

    protected abstract EntityCache<E> initialiseCache(B[] entityBeans,
            EntityConverter entityConverter,
            EntityCache<E> entityCache);

    private B[] tryToReadEntityBeansFromDataFile() {
        try {
            return readEntityBeansFromFile();
        } catch (final IOException e) {
            final String msgTemplate = "Unable to read JSON file with entity beans data: %s";
            throw new UserManagementException(format(msgTemplate, fileAccessor.getDataSourceIdentification()), e);
        }
    }

    private B[] readEntityBeansFromFile() throws IOException {
        final B[] result = readEntityBeansFromFile(fileAccessor);
        logger.debug("Read {} entity bean(s).", result.length);
        return result;
    }

    protected abstract B[] readEntityBeansFromFile(FileAccessor<B> fileAccessor) throws IOException;

    @Override
    public List<E> getAllEntities() {
        return new ArrayList<E>(entitiesCache.getAll());
    }

    @Override
    public E getEntity(final String name) {
        return entitiesCache.get(name);
    }

    @Override
    public void manageEntity(final E entity) throws UserManagementException {
        entitiesCache.add(entity);
        dirtyCounter.incrementAndGet();
        writeEntitiesCacheToFileIfDirtyEnough();
    }

    private void writeEntitiesCacheToFileIfDirtyEnough() {
        if (isDirtyEnough()) {
            tryToWriteEntitiesCacheToFile();
            dirtyCounter.set(0);
        }
    }

    private boolean isDirtyEnough() {
        return dirtyLimit <= dirtyCounter.intValue();
    }

    private void tryToWriteEntitiesCacheToFile() {
        try {
            writeEntitiesCacheToFile();
        } catch (final IOException e) {
            final String msgTemplate = "Unable to write entities data to JSON file: %s";
            throw new UserManagementException(format(msgTemplate, fileAccessor.getDataSourceIdentification()), e);
        }
    }

    private void writeEntitiesCacheToFile() throws IOException {
        final List<B> beans = convertEntitiesToBeans(entityConverter, entitiesCache.getAll());
        fileAccessor.writeBeansToFile(beans);
        logger.debug("Wrote {} entity bean(s) to {}.", beans.size(), fileAccessor.getDataSourceIdentification());
    }

    protected abstract List<B> convertEntitiesToBeans(EntityConverter entityConverter, Collection<E> all);

    @Override
    public void updateEntity(final E entity) {
        if (null == entity) {
            return;
        }
        entitiesCache.add(entity);
        dirtyCounter.incrementAndGet();
        writeEntitiesCacheToFileIfDirtyEnough();
    }

    @Override
    public void deleteEntity(final E entity) throws UserManagementException {
        entitiesCache.remove(entity);
        dirtyCounter.incrementAndGet();
        writeEntitiesCacheToFileIfDirtyEnough();
    }

    public void writeToFileIfDirty() {
        if (0 < dirtyCounter.intValue()) {
            tryToWriteEntitiesCacheToFile();
        }
    }

}
