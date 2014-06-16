package de.steinpfeffer.nubilo.users;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.steinpfeffer.nubilo.users.beans.GroupBean;

/**
 * Unit test for {@link JsonGroupManagementService}.
 * 
 * @author Juergen Fickel
 * @since 1.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public final class JsonGroupManagementServiceTest {

    private final EntityConverter entityConverter = new EntityConverter();

    @Mock
    private FileAccessor<GroupBean> fileAccessorMock;

    @Mock
    private EntityCache<Group> groupsCacheMock;

    private JsonGroupManagementService serviceUnderTest;

    @Before
    public void setUp() throws URISyntaxException, IOException {
        final List<Group> dummyGroups = new ArrayList<>(2);
        dummyGroups.add(DefaultGroup.getInstance("users", "Benutzer"));
        dummyGroups.add(DefaultGroup.getInstance("admins", "Administratoren"));
        when(groupsCacheMock.getAll()).thenReturn(dummyGroups);

        final GroupBean usersGroupBean = createGroupBean("user", "Benutzer");
        final GroupBean adminsGroupBean = createGroupBean("admins", "Administratoren");

        final GroupBean[] groupBeans = new GroupBean[] { usersGroupBean, adminsGroupBean };
        when(fileAccessorMock.readBeansFromFile(GroupBean[].class)).thenReturn(groupBeans);

        serviceUnderTest = new JsonGroupManagementService(2, fileAccessorMock, groupsCacheMock, entityConverter);
    }

    private static GroupBean createGroupBean(final String name, final String displayName) {
        final GroupBean result = new GroupBean();
        result.setName(name);
        result.setDisplayName(displayName);
        return result;
    }

    @Test
    public void managerHasTwoGroupsFromGroupsFile() {
        final List<Group> allGroups = serviceUnderTest.getAllEntities();
        assertThat(allGroups).hasSize(2);
    }

    @Test
    public void managingNewGroupWithoutReachingDirtyCountLimit() throws IOException {
        final Group fooGroup = DefaultGroup.getInstance("foo", "Foo");
        serviceUnderTest.manageEntity(fooGroup);
        verify(groupsCacheMock).add(fooGroup);
        verify(fileAccessorMock, times(0)).writeBeansToFile(anyListOf(GroupBean.class));
    }

    @Test
    public void reachingDirtyCountLimitAndTryToWriteToDataFile() throws IOException {
        final Group fooGroup = DefaultGroup.getInstance("foo", "Foo");
        final Group barGroup = DefaultGroup.getInstance("bar", "Bar");
        serviceUnderTest.manageEntity(fooGroup);
        serviceUnderTest.manageEntity(barGroup);
        verify(fileAccessorMock).writeBeansToFile(anyListOf(GroupBean.class));
    }
}
