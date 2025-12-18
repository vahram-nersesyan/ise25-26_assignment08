package de.seuhd.campuscoffee.domain.implementation;

import de.seuhd.campuscoffee.domain.configuration.ApprovalConfiguration;
import de.seuhd.campuscoffee.domain.exceptions.NotFoundException;
import de.seuhd.campuscoffee.domain.model.objects.Pos;
import de.seuhd.campuscoffee.domain.model.objects.Review;
import de.seuhd.campuscoffee.domain.model.objects.User;
import de.seuhd.campuscoffee.domain.ports.api.CrudService;
import de.seuhd.campuscoffee.domain.ports.data.CrudDataService;
import de.seuhd.campuscoffee.domain.ports.data.PosDataService;
import de.seuhd.campuscoffee.domain.ports.data.ReviewDataService;
import de.seuhd.campuscoffee.domain.ports.data.UserDataService;
import de.seuhd.campuscoffee.domain.tests.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Objects;

import static de.seuhd.campuscoffee.domain.tests.TestFixtures.getApprovalConfiguration;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CrudServiceTest {

    @Mock
    private CrudDataService<Pos, Long> posDataService;

    public class CrudServiceTestModel extends CrudServiceImpl<Pos, Long>{

        public CrudServiceTestModel() {
            super(Pos.class);
        }

        @Override
        protected CrudDataService<Pos, Long> dataService(){
            return posDataService;
        }
    }

    @Test
    void getAllPosRetrievesExpectedPos() {
        // given
        CrudServiceTestModel testModel = new CrudServiceTestModel();
        List<Pos> testFixtures = TestFixtures.getPosFixtures();
        when(posDataService.getAll()).thenReturn(testFixtures);

        // when
        List<Pos> retrievedPos = testModel.getAll();

        // then
        verify(posDataService).getAll();
        assertEquals(testFixtures.size(), retrievedPos.size());
        assertThat(retrievedPos)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrderElementsOf(testFixtures);
    }

    @Test
    void getPosByIdNotFound(){
        //given
        CrudServiceTestModel testModel = new CrudServiceTestModel();
        when(posDataService.getById(anyLong())).thenThrow(new NotFoundException(Pos.class, 1L));

        //when, then
        assertThrows(NotFoundException.class, () -> testModel.getById(anyLong()));
        verify(posDataService).getById(anyLong());
    }

    @Test
    void upsertPosNotFound(){
        // given
        CrudServiceTestModel testModel = new CrudServiceTestModel();
        Pos pos = TestFixtures.getPosFixtures().getFirst();
        Objects.requireNonNull(pos.id());
        when(posDataService.getById(pos.id())).thenThrow(new NotFoundException(Pos.class, pos.id()));

        // when, then
        assertThrows(NotFoundException.class, () -> testModel.upsert(pos));
        verify(posDataService).getById(pos.id());
    }

    @Test
    void upsertNewPos(){
        CrudServiceTestModel testModel = new CrudServiceTestModel();
        Pos pos = TestFixtures.getPosFixtures().getFirst();
        pos = pos.toBuilder().id(null).build();
        when(posDataService.upsert(pos)).thenReturn(pos.toBuilder().id(1L).build());

        // when, then
        testModel.upsert(pos);

        verify(posDataService).upsert(pos);
    }

    @Test
    void clearPosClearsAllPos(){
        // given
        CrudServiceTestModel testModel = new CrudServiceTestModel();

        // when
        testModel.clear();

        // then
        verify(posDataService).clear();
    }
}

