package org.resourcebridge.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.resourcebridge.api.entity.Donation;
import org.resourcebridge.api.entity.Item;
import org.resourcebridge.api.enums.DonationStatus;
import org.resourcebridge.api.enums.DonationType;
import org.resourcebridge.api.exception.ResourceNotFoundException;
import org.resourcebridge.api.security.JwtAuthFilter;
import org.resourcebridge.api.security.JwtUtil;
import org.resourcebridge.api.service.DonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DonationController.class)
class DonationControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockitoBean DonationService donationService;
    @MockitoBean JwtAuthFilter jwtAuthFilter;   // prevents SecurityConfig wiring error
    @MockitoBean JwtUtil jwtUtil;

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

    // POST /api/donations is public — no auth required
    @Test
    void createDonation_publicEndpoint_returns200() throws Exception {
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
        Donation assigned = makeDonation(2L, DonationStatus.ASSIGNED); // auto-matched
        when(donationService.save(any())).thenReturn(assigned);

        mockMvc.perform(post("/api/donations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(offered)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ASSIGNED"));
    }

    // GET /api/donations requires STAFF role
    @Test
    @WithMockUser(roles = "STAFF")
    void getOffered_withStaffRole_returns200() throws Exception {
        List<Donation> donations = List.of(makeDonation(1L, DonationStatus.OFFERED));
        when(donationService.findByStatus(DonationStatus.OFFERED)).thenReturn(donations);

        mockMvc.perform(get("/api/donations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getOffered_withoutAuth_returns401or403() throws Exception {
        mockMvc.perform(get("/api/donations"))
                .andExpect(status().is(org.hamcrest.Matchers.anyOf(
                        org.hamcrest.Matchers.is(401),
                        org.hamcrest.Matchers.is(403))));
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void getById_withStaffRole_returns200() throws Exception {
        Donation donation = makeDonation(5L, DonationStatus.OFFERED);
        when(donationService.getById(5L)).thenReturn(donation);

        mockMvc.perform(get("/api/donations/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void getById_notFound_returns404() throws Exception {
        when(donationService.getById(99L)).thenThrow(new ResourceNotFoundException("Donation", 99L));

        mockMvc.perform(get("/api/donations/99"))
                .andExpect(status().isNotFound());
    }
}
