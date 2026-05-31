package com.condologix.application.concierge;

import com.condologix.application.exception.GlobalExceptionHandler;
import com.condologix.application.exception.ResourceNotFoundException;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConciergeController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class ConciergeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConciergeService conciergeService;

    @Test
    void createConciergeShouldReturnCreated() throws Exception {
        ConciergeDTO response = new ConciergeDTO(7L, 1L, "John Doe", "11123456789");

        when(conciergeService.createConcierge(any(ConciergeCreateDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/concierges")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "buildingId": 1,
                        "name": "John Doe",
                        "phone": "11123456789"
                    }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.buildingId").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.phone").value("11123456789"));
    }

    @Test
    void createConciergeShouldReturnBadRequestWhenBodyIsInvalid() throws Exception {
        mockMvc.perform(post("/api/concierges")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "buildingId": -1,
                        "name": "",
                        "phone": "123"
                    }
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").exists());

        verify(conciergeService, never()).createConcierge(any(ConciergeCreateDTO.class));
    }

    @Test
    void createConciergeShouldReturnNotFoundWhenBuildingDoesNotExist() throws Exception {
        when(conciergeService.createConcierge(any(ConciergeCreateDTO.class)))
            .thenThrow(new ResourceNotFoundException("Building not found with id: 684"));

        mockMvc.perform(post("/api/concierges")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "buildingId": 684,
                        "name": "John Doe",
                        "phone": "11123456789"
                    }
                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Building not found with id: 684"));
    }

    @Test
    void createConciergeShouldReturnConflictWhenServiceThrowsIllegalState() throws Exception {
        when(conciergeService.createConcierge(any(ConciergeCreateDTO.class)))
            .thenThrow(new IllegalStateException("Failed to create concierge due to data integrity violation"));

        mockMvc.perform(post("/api/concierges")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "buildingId": 1,
                        "name": "John Doe",
                        "phone": "11123456789"
                    }
                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Failed to create concierge due to data integrity violation"));
    }

    @Test
    void updateContactInfoShouldReturnOk() throws Exception {
        ConciergeDTO response = new ConciergeDTO(7L, 1L, "John Doe", "11999999999");

        when(conciergeService.updateContactInfo(anyLong(), any(ConciergeUpdateDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/concierges/7/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "phone": "11999999999"
                    }
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.buildingId").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.phone").value("11999999999"));
    }

    @Test
    void updateContactInfoShouldReturnBadRequestWhenPhoneIsInvalid() throws Exception {
        mockMvc.perform(put("/api/concierges/7/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "phone": "123"
                    }
                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").exists());

        verify(conciergeService, never()).updateContactInfo(anyLong(), any(ConciergeUpdateDTO.class));
    }

    @Test
    void updateContactInfoShouldReturnBadRequestWhenIdIsNotPositive() throws Exception {
        mockMvc.perform(put("/api/concierges/0/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "phone": "11999999999"
                    }
                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").exists());

        verify(conciergeService, never()).updateContactInfo(anyLong(), any(ConciergeUpdateDTO.class));
    }

    @Test
    void updateContactInfoShouldReturnNotFoundWhenConciergeDoesNotExist() throws Exception {
        when(conciergeService.updateContactInfo(anyLong(), any(ConciergeUpdateDTO.class)))
            .thenThrow(new ResourceNotFoundException("Concierge not found with id: 7"));

        mockMvc.perform(put("/api/concierges/7/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "phone": "11999999999"
                    }
                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Concierge not found with id: 7"));
    }

    @Test
    void deleteConciergeShouldReturnNoContent() throws Exception {
        doNothing().when(conciergeService).deleteConcierge(7L);

        mockMvc.perform(delete("/api/concierges/7"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteConciergeShouldReturnBadRequestWhenIdIsNotPositive() throws Exception {
        mockMvc.perform(delete("/api/concierges/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").exists());

        verify(conciergeService, never()).deleteConcierge(anyLong());
    }

    @Test
    void deleteConciergeShouldReturnNotFoundWhenConciergeDoesNotExist() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException("Concierge not found with id: 7"))
            .when(conciergeService)
            .deleteConcierge(7L);

        mockMvc.perform(delete("/api/concierges/7"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Concierge not found with id: 7"));
    }

    @Test
    void getConciergesByBuildingIdShouldReturnOk() throws Exception {
        List<ConciergeDTO> concierges = List.of(
            new ConciergeDTO(1L, 5L, "John Doe", "11123456789"),
            new ConciergeDTO(2L, 5L, "Jane Doe", "11999999999")
        );

        when(conciergeService.getConciergesByBuildingId(5L)).thenReturn(concierges);

        mockMvc.perform(get("/api/concierges/building/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].buildingId").value(5))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[0].phone").value("11123456789"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].buildingId").value(5))
                .andExpect(jsonPath("$[1].name").value("Jane Doe"))
                .andExpect(jsonPath("$[1].phone").value("11999999999"));
    }

    @Test
    void getConciergesByBuildingIdShouldReturnBadRequestWhenBuildingIdIsNotPositive() throws Exception {
        mockMvc.perform(get("/api/concierges/building/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").exists());

        verify(conciergeService, never()).getConciergesByBuildingId(anyLong());
    }

    @Test
    void getConciergesByBuildingIdShouldReturnNotFoundWhenBuildingDoesNotExist() throws Exception {
        when(conciergeService.getConciergesByBuildingId(684L))
            .thenThrow(new ResourceNotFoundException("Building not found with id: 684"));

        mockMvc.perform(get("/api/concierges/building/684"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Building not found with id: 684"));
    }
}
