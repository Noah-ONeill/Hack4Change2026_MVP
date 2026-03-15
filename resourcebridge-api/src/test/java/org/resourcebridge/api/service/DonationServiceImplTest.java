package org.resourcebridge.api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.resourcebridge.api.entity.Donation;
import org.resourcebridge.api.enums.DonationStatus;
import org.resourcebridge.api.exception.ResourceNotFoundException;
import org.resourcebridge.api.repository.DonationRepository;
import org.resourcebridge.api.service.impl.DonationServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DonationServiceImplTest {

    @Mock DonationRepository donationRepository;
    @Mock MatchingService matchingService;
    @InjectMocks DonationServiceImpl donationService;

    @Test
    void save_setsStatusToOfferedWhenNull() {
        Donation donation = new Donation();
        // status intentionally left null

        Donation saved = new Donation();
        saved.setId(1L);
        saved.setStatus(DonationStatus.OFFERED);

        when(donationRepository.save(any())).thenReturn(saved);
        when(donationRepository.findById(1L)).thenReturn(Optional.of(saved));

        donationService.save(donation);

        assertThat(donation.getStatus()).isEqualTo(DonationStatus.OFFERED);
        verify(matchingService).autoMatch(saved);
    }

    @Test
    void save_triggersAutoMatchForOfferedDonation() {
        Donation donation = new Donation();
        donation.setStatus(DonationStatus.OFFERED);

        Donation saved = new Donation();
        saved.setId(2L);
        saved.setStatus(DonationStatus.OFFERED);

        when(donationRepository.save(any())).thenReturn(saved);
        when(donationRepository.findById(2L)).thenReturn(Optional.of(saved));

        donationService.save(donation);

        verify(matchingService).autoMatch(saved);
    }

    @Test
    void save_skipsAutoMatchWhenNotOffered() {
        Donation donation = new Donation();
        donation.setStatus(DonationStatus.DELIVERED);

        Donation saved = new Donation();
        saved.setId(3L);
        saved.setStatus(DonationStatus.DELIVERED);

        when(donationRepository.save(any())).thenReturn(saved);

        donationService.save(donation);

        verifyNoInteractions(matchingService);
    }

    @Test
    void getById_throwsResourceNotFoundExceptionWhenMissing() {
        when(donationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> donationService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getById_returnsDonationWhenFound() {
        Donation donation = new Donation();
        donation.setId(1L);
        when(donationRepository.findById(1L)).thenReturn(Optional.of(donation));

        assertThat(donationService.getById(1L).getId()).isEqualTo(1L);
    }

    @Test
    void updateStatus_changesStatusAndPersists() {
        Donation existing = new Donation();
        existing.setId(1L);
        existing.setStatus(DonationStatus.OFFERED);

        when(donationRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(donationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Donation result = donationService.updateStatus(1L, DonationStatus.ASSIGNED);

        assertThat(result.getStatus()).isEqualTo(DonationStatus.ASSIGNED);
        verify(donationRepository).save(existing);
    }

    @Test
    void findByStatus_delegatesToRepository() {
        List<Donation> donations = List.of(new Donation(), new Donation());
        when(donationRepository.findByStatus(DonationStatus.OFFERED)).thenReturn(donations);

        assertThat(donationService.findByStatus(DonationStatus.OFFERED)).hasSize(2);
    }

    @Test
    void findAvailableByItem_returnsOnlyOfferedForItem() {
        List<Donation> offered = List.of(new Donation());
        when(donationRepository.findByItemIdAndStatus(5L, DonationStatus.OFFERED)).thenReturn(offered);

        assertThat(donationService.findAvailableByItem(5L)).hasSize(1);
    }
}
