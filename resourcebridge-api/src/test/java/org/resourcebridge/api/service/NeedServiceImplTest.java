package org.resourcebridge.api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.resourcebridge.api.entity.Need;
import org.resourcebridge.api.enums.Urgency;
import org.resourcebridge.api.exception.ResourceNotFoundException;
import org.resourcebridge.api.repository.NeedRepository;
import org.resourcebridge.api.service.impl.NeedServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NeedServiceImplTest {

    @Mock NeedRepository needRepository;
    @InjectMocks NeedServiceImpl needService;

    @Test
    void getById_returnsNeedWhenFound() {
        Need need = new Need();
        need.setId(1L);
        when(needRepository.findById(1L)).thenReturn(Optional.of(need));

        assertThat(needService.getById(1L).getId()).isEqualTo(1L);
    }

    @Test
    void getById_throwsResourceNotFoundExceptionWhenMissing() {
        when(needRepository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> needService.getById(42L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void markFulfilled_setsFulfilledTrueAndSaves() {
        Need need = new Need();
        need.setId(1L);
        need.setFulfilled(false);

        when(needRepository.findById(1L)).thenReturn(Optional.of(need));
        when(needRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Need result = needService.markFulfilled(1L);

        assertThat(result.isFulfilled()).isTrue();
        verify(needRepository).save(need);
    }

    @Test
    void markFulfilled_throwsWhenNeedNotFound() {
        when(needRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> needService.markFulfilled(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findUnfulfilledNeeds_returnsOnlyUnfulfilled() {
        List<Need> unfulfilled = List.of(new Need(), new Need());
        when(needRepository.findByFulfilled(false)).thenReturn(unfulfilled);

        assertThat(needService.findUnfulfilledNeeds()).hasSize(2);
        verify(needRepository).findByFulfilled(false);
    }

    @Test
    void findByUrgency_delegatesToRepository() {
        List<Need> critical = List.of(new Need());
        when(needRepository.findByUrgencyAndFulfilled(Urgency.CRITICAL, false)).thenReturn(critical);

        assertThat(needService.findByUrgency(Urgency.CRITICAL)).hasSize(1);
    }

    @Test
    void findUnfulfilledByOrganization_filtersCorrectly() {
        List<Need> needs = List.of(new Need());
        when(needRepository.findByOrganizationIdAndFulfilled(7L, false)).thenReturn(needs);

        assertThat(needService.findUnfulfilledByOrganization(7L)).hasSize(1);
    }
}
