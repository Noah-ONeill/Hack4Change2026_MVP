package org.resourcebridge.api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.resourcebridge.api.entity.*;
import org.resourcebridge.api.enums.DonationStatus;
import org.resourcebridge.api.enums.TransferStatus;
import org.resourcebridge.api.repository.DonationRepository;
import org.resourcebridge.api.repository.NeedRepository;
import org.resourcebridge.api.repository.TransferRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchingServiceTest {

    @Mock NeedRepository needRepository;
    @Mock TransferRepository transferRepository;
    @Mock DonationRepository donationRepository;
    @InjectMocks MatchingService matchingService;

    private Item makeItem(long id) {
        Item item = new Item();
        item.setId(id);
        return item;
    }

    private Organization makeOrg(long id, String name) {
        Organization org = new Organization();
        org.setId(id);
        org.setName(name);
        return org;
    }

    private Need makeNeed(Organization org, Item item, int qty) {
        Need need = new Need();
        need.setOrganization(org);
        need.setItem(item);
        need.setQuantityNeeded(qty);
        need.setFulfilled(false);
        return need;
    }

    @Test
    void autoMatch_createsTransferToHighestUrgencyNeed() {
        Item item = makeItem(1L);
        Organization org = makeOrg(10L, "House of Nazareth");
        Need need = makeNeed(org, item, 50);

        Donation donation = new Donation();
        donation.setId(1L);
        donation.setItem(item);
        donation.setQuantity(30);
        donation.setDonorName("Jane Smith");

        when(needRepository.findUnfulfilledByItemOrderByUrgency(1L)).thenReturn(List.of(need));
        when(transferRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Transfer result = matchingService.autoMatch(donation);

        assertThat(result).isNotNull();
        assertThat(result.getQuantityAssigned()).isEqualTo(30);
        assertThat(result.getToOrganization()).isEqualTo(org);
        assertThat(result.getStatus()).isEqualTo(TransferStatus.PENDING);
        verify(donationRepository).save(donation);
        assertThat(donation.getStatus()).isEqualTo(DonationStatus.ASSIGNED);
    }

    @Test
    void autoMatch_fulfilsNeedWhenDonationFullyCoverIt() {
        Item item = makeItem(1L);
        Organization org = makeOrg(10L, "Salvation Army");
        Need need = makeNeed(org, item, 20);

        Donation donation = new Donation();
        donation.setItem(item);
        donation.setQuantity(25); // more than needed
        donation.setDonorName("Bob");

        when(needRepository.findUnfulfilledByItemOrderByUrgency(1L)).thenReturn(List.of(need));
        when(transferRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        matchingService.autoMatch(donation);

        assertThat(need.isFulfilled()).isTrue();
        verify(needRepository).save(need);
    }

    @Test
    void autoMatch_doesNotFulfilNeedWhenDonationIsPartial() {
        Item item = makeItem(1L);
        Organization org = makeOrg(10L, "Salvation Army");
        Need need = makeNeed(org, item, 50);

        Donation donation = new Donation();
        donation.setItem(item);
        donation.setQuantity(10); // less than needed
        donation.setDonorName("Bob");

        when(needRepository.findUnfulfilledByItemOrderByUrgency(1L)).thenReturn(List.of(need));
        when(transferRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        matchingService.autoMatch(donation);

        assertThat(need.isFulfilled()).isFalse();
        verify(needRepository, never()).save(need);
    }

    @Test
    void autoMatch_prefersPreferredOrgWhenItHasNeed() {
        Item item = makeItem(1L);
        Organization preferred = makeOrg(5L, "Preferred Shelter");
        Organization other = makeOrg(6L, "Other Shelter");
        Need preferredNeed = makeNeed(preferred, item, 10);
        Need otherNeed = makeNeed(other, item, 10);

        Donation donation = new Donation();
        donation.setItem(item);
        donation.setQuantity(10);
        donation.setPreferredOrganization(preferred);
        donation.setDonorName("Jane");

        when(needRepository.findUnfulfilledByItemAndOrgOrderByUrgency(1L, 5L))
                .thenReturn(List.of(preferredNeed));
        when(transferRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Transfer result = matchingService.autoMatch(donation);

        assertThat(result.getToOrganization()).isEqualTo(preferred);
        verify(needRepository, never()).findUnfulfilledByItemOrderByUrgency(any());
    }

    @Test
    void autoMatch_fallsBackToGlobalWhenPreferredOrgHasNoNeed() {
        Item item = makeItem(1L);
        Organization preferred = makeOrg(5L, "Preferred Shelter");
        Organization fallback = makeOrg(6L, "Fallback Shelter");
        Need fallbackNeed = makeNeed(fallback, item, 10);

        Donation donation = new Donation();
        donation.setItem(item);
        donation.setQuantity(10);
        donation.setPreferredOrganization(preferred);
        donation.setDonorName("Jane");

        when(needRepository.findUnfulfilledByItemAndOrgOrderByUrgency(1L, 5L))
                .thenReturn(List.of()); // preferred has no need
        when(needRepository.findUnfulfilledByItemOrderByUrgency(1L))
                .thenReturn(List.of(fallbackNeed));
        when(transferRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Transfer result = matchingService.autoMatch(donation);

        assertThat(result.getToOrganization()).isEqualTo(fallback);
    }

    @Test
    void autoMatch_returnsNullWhenNoNeedsExist() {
        Item item = makeItem(1L);
        Donation donation = new Donation();
        donation.setItem(item);
        donation.setQuantity(10);

        when(needRepository.findUnfulfilledByItemOrderByUrgency(1L)).thenReturn(List.of());

        Transfer result = matchingService.autoMatch(donation);

        assertThat(result).isNull();
        verifyNoInteractions(transferRepository);
        verify(donationRepository, never()).save(any());
    }

    @Test
    void autoMatch_assignsMinOfDonationAndNeedQuantity() {
        Item item = makeItem(1L);
        Organization org = makeOrg(10L, "Shelter");
        Need need = makeNeed(org, item, 5);

        Donation donation = new Donation();
        donation.setItem(item);
        donation.setQuantity(100);
        donation.setDonorName("Donor");

        when(needRepository.findUnfulfilledByItemOrderByUrgency(1L)).thenReturn(List.of(need));

        ArgumentCaptor<Transfer> captor = ArgumentCaptor.forClass(Transfer.class);
        when(transferRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        matchingService.autoMatch(donation);

        assertThat(captor.getValue().getQuantityAssigned()).isEqualTo(5);
    }
}
