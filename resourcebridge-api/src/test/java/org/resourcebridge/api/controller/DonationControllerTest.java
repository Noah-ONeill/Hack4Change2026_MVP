package org.resourcebridge.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.resourcebridge.api.entity.Donation;
import org.resourcebridge.api.entity.Item;
import org.resourcebridge.api.enums.DonationStatus;
import org.resourcebridge.api.enums.DonationType;
import org.resourcebridge.api.exception.ResourceNotFoundException;
import org.resourcebridge.api.service.DonationService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DonationControllerTest {

    @Mock DonationService donationService;
    @InjectMocks DonationController donationController;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(donationController).build();
    }

    private Donation makeDonation(long id, DonationStatus status) {
        Item item = new Item();
        item.setId(1L);
        item.setName("Canned Food");
        item.setUnit("cans");

        Donation d = new Donation();
        d.setId(id);
        d.setDonorName("Jane Smith");
        d.setDonorEmail("jane@email.com");
        d.setItem(item);
        d.setQuantity(10);
        d.setStatus(status);
        d.setDonationType(DonationType.DROP_OFF);
        return d;
    }

    @Test
    void createDonation_returns200() throws Exception {
        Donation donation = makeDonation(1L, DonationStatus.OFFERED);
        when(donationService.save(any())).thenReturn(donation);

        mockMvc.perform(post("/api/donations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(donation)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.donorEmail").value("jane@email.com"))
                .andExpect(jsonPath("$.status").value("OFFERED"));
    }

    @Test
    void createDonation_returnsMatchedDonation() throws Exception {
        Donation offered = makeDonation(2L, DonationStatus.OFFERED);
        Donation assigned = makeDonation(2L, DonationStatus.ASSIGNED);
        when(donationService.save(any())).thenReturn(assigned);

        mockMvc.perform(post("/api/donations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(offered)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ASSIGNED"));
    }

    @Test
    void getOffered_returns200() throws Exception {
        List<Donation> donations = List.of(makeDonation(1L, DonationStatus.OFFERED));
        when(donationService.findByStatus(DonationStatus.OFFERED)).thenReturn(donations);

        mockMvc.perform(get("/api/donations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getById_returns200() throws Exception {
        Donation donation = makeDonation(5L, DonationStatus.OFFERED);
        when(donationService.getById(5L)).thenReturn(donation);

        mockMvc.perform(get("/api/donations/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        when(donationService.getById(99L)).thenThrow(new ResourceNotFoundException("Donation", 99L));

        mockMvc.perform(get("/api/donations/99"))
                .andExpect(status().isNotFound());
    }
}
