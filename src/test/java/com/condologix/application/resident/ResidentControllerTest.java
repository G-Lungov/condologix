package com.condologix.application.resident;

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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResidentController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class ResidentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ResidentService residentService;

    @Test
    void createResidentShouldReturnCreated() throws Exception {
        ResidentDTO response = new ResidentDTO(
            10L,
            1L,
            "John Doe",
            "john@example.com",
            "11999999999"
        );

        when(residentService.createResident(any(ResidentCreateDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/residents")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "unitId": 1,
                        "name": "John Doe",
                        "email": "john@example.com",
                        "phone": "11999999999"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(10))
            .andExpect(jsonPath("$.unitId").value(1))
            .andExpect(jsonPath("$.name").value("John Doe"))
            .andExpect(jsonPath("$.email").value("john@example.com"))
            .andExpect(jsonPath("$.phone").value("11999999999"));
    }

    @Test
    void createResidentShouldReturnBadRequestWhenBodyIsInvalid() throws Exception {
        String invalidBody = """
            {
                "unitId": -1,
                "name": "",
                "email": "invalid-email",
                "phone": "123"
            }
            """;

        mockMvc.perform(post("/api/residents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Validation failed"))
            .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void createResidentShouldReturnNotFoundWhenUnitDoesNotExist() throws Exception {
        when(residentService.createResident(any(ResidentCreateDTO.class)))
            .thenThrow(new ResourceNotFoundException("Unit not found: 999"));

        mockMvc.perform(post("/api/residents")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "unitId": 999,
                        "name": "John Doe",
                        "email": "john@example.com",
                        "phone": "11999999999"
                    }
                    """))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Unit not found: 999"));
    }

    @Test
    void createResidentShouldReturnConflictWhenServiceThrowsIllegalState() throws Exception {
        when(residentService.createResident(any(ResidentCreateDTO.class)))
            .thenThrow(new IllegalStateException("Duplicate resident data"));

        mockMvc.perform(post("/api/residents")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "unitId": 1,
                        "name": "John Doe",
                        "email": "john@example.com",
                        "phone": "11999999999"
                    }
                    """))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value("Duplicate resident data"));
    }

    @Test
    void updateResidentShouldReturnOk() throws Exception {
        ResidentDTO response = new ResidentDTO(
            10L,
            1L,
            "John Doe",
            "updated@example.com",
            "11988887777"
        );

        when(residentService.updateResident(anyLong(), any(ResidentUpdateDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/residents/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "email": "updated@example.com",
                        "phone": "11988887777"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(10))
            .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    void updateResidentShouldReturnNotFoundWhenResidentDoesNotExist() throws Exception {
        when(residentService.updateResident(anyLong(), any(ResidentUpdateDTO.class)))
            .thenThrow(new ResourceNotFoundException("Resident not found: 10"));

        mockMvc.perform(put("/api/residents/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "email": "updated@example.com",
                        "phone": "11988887777"
                    }
                    """))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Resident not found: 10"));
    }

    @Test
    void deleteResidentShouldReturnNoContent() throws Exception {
        doNothing().when(residentService).deleteResident(10L);

        mockMvc.perform(delete("/api/residents/10"))
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteResidentShouldReturnNotFoundWhenResidentDoesNotExist() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException("Resident not found: 10"))
            .when(residentService)
            .deleteResident(10L);

        mockMvc.perform(delete("/api/residents/10"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Resident not found: 10"));
    }

    @Test
    void getResidentsByUnitShouldReturnOk() throws Exception {
        List<ResidentDTO> residents = List.of(
            new ResidentDTO(1L, 5L, "John Doe", "john@example.com", "11999999999"),
            new ResidentDTO(2L, 5L, "Jane Doe", "jane@example.com", "11988887777")
        );

        when(residentService.getResidentsByUnit(5L)).thenReturn(residents);

        mockMvc.perform(get("/api/residents/unit/5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[1].id").value(2));
    }
}
