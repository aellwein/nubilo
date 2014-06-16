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

import java.io.File;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.annotation.concurrent.NotThreadSafe;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

import de.steinpfeffer.nubilo.users.beans.GroupBean;
import de.steinpfeffer.nubilo.users.beans.UserBean;

/**
 * {@link BundleActivator} for the Nubilo core user management.
 * 
 * @author Juergen Fickel
 * @since 1.0.0
 */
@NotThreadSafe
public final class Activator implements BundleActivator {

    /**
     * This {@link ManagedService} is responsible for updating the
     * Nubilo user management service. The user management service is
     * only then registered if all necessary configuration properties
     * are given. If configuration properties are updated an already
     * registered user management service will be unregistered and
     * replaced by a new one.
     * 
     * @see UserManagementService
     */
    private final class UserManagementServiceUpdater implements ManagedService {

        private final BundleContext bundleContext;

        public UserManagementServiceUpdater(final BundleContext theBundleContext) {
            bundleContext = theBundleContext;
        }

        @Override
        public void updated(final Dictionary<String, ?> properties) throws ConfigurationException {
            if (isUserManagementServiceAlreadyRegistered()) {
                unregisterUserManagementService();
            }
            if (null != properties) {
                registerUserManagementService(properties);
            }
        }

        private boolean isUserManagementServiceAlreadyRegistered() {
            return null != userManagementServiceRegistration;
        }

        private void registerUserManagementService(final Dictionary<String, ?> properties)
                throws ConfigurationException {
            final EntityConverter entityConverter = new EntityConverter();

            final File groupsDataFile = getGroupDataFile(properties);
            final JsonFileAccessor<GroupBean> jsonGroupFileAccessor = new JsonFileAccessor<>(groupsDataFile);
            final EntityCache<Group> groupsCache = DefaultEntityCache.getInstance();
            groupManagementService = new JsonGroupManagementService(jsonGroupFileAccessor, groupsCache, entityConverter);
            final Dictionary<String, Object> groupManagementServiceProperties = new Hashtable<>();
            groupManagementServiceProperties.put("entityType", Group.class);
            groupManagementServiceRegistration = bundleContext.registerService(EntityManagementService.class,
                    groupManagementService, groupManagementServiceProperties);

            final File usersDataFile = getUsersDataFile(properties);
            final JsonFileAccessor<UserBean> jsonUserFileAccessor = new JsonFileAccessor<>(usersDataFile);
            final EntityCache<User> usersCache = DefaultEntityCache.getInstance();
            userManagementService = new JsonUserManagementService(jsonUserFileAccessor, usersCache,
                    groupManagementService, entityConverter);
            final Dictionary<String, Object> userManagementServiceProperties = new Hashtable<>();
            userManagementServiceProperties.put("entityType", User.class);
            userManagementServiceRegistration = bundleContext.registerService(EntityManagementService.class,
                    userManagementService, userManagementServiceProperties);
        }

        private File getGroupDataFile(final Dictionary<String, ?> properties) throws ConfigurationException {
            final String dataDir = safelyGetProperty(properties, "data.dir");
            final String groupsDataResource = safelyGetProperty(properties, "groups.data.resource");
            return new File(dataDir, groupsDataResource);
        }

        private File getUsersDataFile(final Dictionary<String, ?> properties) throws ConfigurationException {
            final String dataDir = safelyGetProperty(properties, "data.dir");
            final String usersDataResource = safelyGetProperty(properties, "users.data.resource");
            return new File(dataDir, usersDataResource);
        }

        private String safelyGetProperty(final Dictionary<String, ?> properties, final String propertyKey)
                throws ConfigurationException {
            final String result = String.valueOf(properties.get(propertyKey));
            if (null == result) {
                throw new ConfigurationException(propertyKey, "is not defined");
            }
            return result;
        }

    }

    private static final String CONFIG_PID = "nubilo.core";

    private volatile JsonGroupManagementService groupManagementService;
    private volatile JsonUserManagementService userManagementService;
    private volatile ServiceRegistration<?> groupManagementServiceRegistration;
    private volatile ServiceRegistration<?> userManagementServiceUpdaterRegistration;
    private volatile ServiceRegistration<?> userManagementServiceRegistration;

    /**
     * Constructs a new {@link Activator} object.
     */
    public Activator() {
        groupManagementService = null;
        userManagementService = null;
        userManagementServiceUpdaterRegistration = null;
        groupManagementServiceRegistration = null;
        userManagementServiceRegistration = null;
    }

    @Override
    public void start(final BundleContext bundleContext) throws Exception {
        registerUserManagementServiceUpdater(bundleContext);
    }

    private void registerUserManagementServiceUpdater(final BundleContext context) {
        final ManagedService updaterService = new UserManagementServiceUpdater(context);
        final Dictionary<String, Object> properties = new Hashtable<>();
        properties.put(Constants.SERVICE_PID, CONFIG_PID);
        userManagementServiceUpdaterRegistration = context.registerService(ManagedService.class, updaterService,
                properties);
    }

    @Override
    public void stop(final BundleContext bundleContext) throws Exception {
        unregisterUserManagementService();
        unregisterGroupManagementService();
        unregisterUserManagementServiceUpdater();
    }

    private void unregisterUserManagementService() {
        if (null != userManagementServiceRegistration) {
            userManagementServiceRegistration.unregister();
            userManagementServiceRegistration = null;
        }
        if (null != userManagementService) {
            userManagementService.writeToFileIfDirty();
            userManagementService = null;
        }
    }

    private void unregisterGroupManagementService() {
        if (null != groupManagementServiceRegistration) {
            groupManagementServiceRegistration.unregister();
            groupManagementServiceRegistration = null;
        }
        if (null != groupManagementService) {
            groupManagementService.writeToFileIfDirty();
            groupManagementService = null;
        }
    }

    private void unregisterUserManagementServiceUpdater() {
        userManagementServiceUpdaterRegistration.unregister();
        userManagementServiceUpdaterRegistration = null;
    }

}
