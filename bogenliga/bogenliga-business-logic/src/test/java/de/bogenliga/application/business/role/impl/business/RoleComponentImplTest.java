package de.bogenliga.application.business.role.impl.business;

import java.util.Collections;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import de.bogenliga.application.business.role.api.types.RoleDO;
import de.bogenliga.application.business.role.impl.dao.RoleDAO;
import de.bogenliga.application.business.role.impl.entity.RoleBE;
import de.bogenliga.application.common.errorhandling.exception.BusinessException;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Andre Lehnert, eXXcellent solutions consulting & software gmbh
 */
@SuppressWarnings({"pmd-unit-tests:JUnitTestsShouldIncludeAssert", "squid:S2187"})
public class RoleComponentImplTest {
    private static final Long ID = 1L;
    private static final Long VERSION = 2L;
    private static final String ROLENAME = "ADMIN";


    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private RoleDAO roleDAO;

    @InjectMocks
    private RoleComponentImpl underTest;

    @Captor
    private ArgumentCaptor<RoleBE> roleBEArgumentCaptor;


    @Test
    public void findAll() {
        // prepare test data
        RoleBE expectedBE = new RoleBE();
        expectedBE.setRoleId(ID);
        expectedBE.setRoleName(ROLENAME);
        expectedBE.setVersion(VERSION);

        // configure mocks
        when(roleDAO.findAll()).thenReturn(Collections.singletonList(expectedBE));

        // call test method
        List<RoleDO> actual = underTest.findAll();

        // assert result
        assertThat(actual).isNotNull().isNotEmpty();

        assertThat(actual.get(0).getId())
                .isEqualTo(expectedBE.getRoleId());
        assertThat(actual.get(0).getRoleName())
                .isEqualTo(expectedBE.getRoleName());
        assertThat(actual.get(0).getVersion())
                .isEqualTo(expectedBE.getVersion());

        // verify invocations
        verify(roleDAO).findAll();
    }


    @Test
    public void findByName() {
        //check if error thrown when roelName is NULL
        try {
            RoleDO nullCheck = underTest.findByName(null);
        } catch (BusinessException e) {
            assertThat(e.getMessage()).isEqualTo("INVALID_ARGUMENT_ERROR: RoleDO name must not be null or empty");
        }

        // Test for non-null valid String inputs
        String invalidRoleName = "MODRATOHR";

        try {
            RoleDO nullCheck = underTest.findByName(invalidRoleName);
        } catch (BusinessException e) {
            assertThat(e.getMessage()).isEqualTo(String.format("ENTITY_NOT_FOUND_ERROR: No result found for RoleName '%s'", invalidRoleName));
        }
    }

}