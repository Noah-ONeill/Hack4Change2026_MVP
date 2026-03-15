package org.resourcebridge.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.resourcebridge.api.entity.Item;
import org.resourcebridge.api.entity.Need;
import org.resourcebridge.api.entity.Organization;
import org.resourcebridge.api.enums.Urgency;
import org.resourcebridge.api.exception.ResourceNotFoundException;
import org.resourcebridge.api.security.JwtAuthFilter;
import org.resourcebridge.api.security.JwtUtil;
import org.resourcebridge.api.service.NeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NeedController.class)
class NeedControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockitoBean NeedService needService;
    @MockitoBean JwtAuthFilter jwtAuthFilter;
    @MockitoBean JwtUtil jwtUtil;

    private Need makeNeed(long id, Urgency urgency) {
        Item item = new Item();
        item.setId(1L);
        item.setName("Blankets");
        item.setUnit("units");

        Organization org = new Organization();
        org.setId(1L);
        org.setName("House of Nazareth");

        Need need = new Need();
        need.setId(id);
        need.setItem(item);
        need.setOrganization(org);
        need.setQuantityNeeded(20);
        need.setUrgency(urgency);
        need.setFulfilled(false);
        return need;
    }

    // GET /api/needs is public
    @Test
    void getUnfulfilled_publicEndpoint_returns200() throws Exception {
        List<Need> needs = List.of(
                makeNeed(1L, Urgency.CRITICAL),
                makeNeed(2L, Urgency.HIGH)
        );
        when(needService.findUnfulfilledNeeds()).thenReturn(needs);

        mockMvc.perform(get("/api/needs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].urgency").value("CRITICAL"));
    }

    @Test
    void getUnfulfilled_returnsEmptyListWhenNoNeeds() throws Exception {
        when(needService.findUnfulfilledNeeds()).thenReturn(List.of());

        mockMvc.perform(get("/api/needs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getById_publicEndpoint_returns200() throws Exception {
        Need need = makeNeed(3L, Urgency.HIGH);
        when(needService.getById(3L)).thenReturn(need);

        mockMvc.perform(get("/api/needs/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        when(needService.getById(99L)).thenThrow(new ResourceNotFoundException("Need", 99L));

        mockMvc.perform(get("/api/needs/99"))
                .andExpect(status().isNotFound());
    }

    // POST /api/needs requires STAFF role
    @Test
    @WithMockUser(roles = "STAFF")
    void createNeed_withStaffRole_returns200() throws Exception {
        Need need = makeNeed(1L, Urgency.CRITICAL);
        when(needService.save(any())).thenReturn(need);

        mockMvc.perform(post("/api/needs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(need)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.urgency").value("CRITICAL"));
    }

    @Test
    void createNeed_withoutAuth_returns401or403() throws Exception {
        Need need = makeNeed(1L, Urgency.HIGH);

        mockMvc.perform(post("/api/needs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(need)))
                .andExpect(status().is(org.hamcrest.Matchers.anyOf(
                        org.hamcrest.Matchers.is(401),
                        org.hamcrest.Matchers.is(403))));
    }

    // PATCH /api/needs/{id}/fulfill requires STAFF role
    @Test
    @WithMockUser(roles = "STAFF")
    void markFulfilled_withStaffRole_returns200() throws Exception {
        Need fulfilled = makeNeed(1L, Urgency.HIGH);
        fulfilled.setFulfilled(true);
        when(needService.markFulfilled(1L)).thenReturn(fulfilled);

        mockMvc.perform(patch("/api/needs/1/fulfill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fulfilled").value(true));
    }

    @Test
    void markFulfilled_withoutAuth_returns401or403() throws Exception {
        mockMvc.perform(patch("/api/needs/1/fulfill"))
                .andExpect(status().is(org.hamcrest.Matchers.anyOf(
                        org.hamcrest.Matchers.is(401),
                        org.hamcrest.Matchers.is(403))));
    }

    @Test
    void getByOrganization_publicEndpoint_returnsNeeds() throws Exception {
        List<Need> needs = List.of(makeNeed(1L, Urgency.MEDIUM));
        when(needService.findUnfulfilledByOrganization(1L)).thenReturn(needs);

        mockMvc.perform(get("/api/needs/organization/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
