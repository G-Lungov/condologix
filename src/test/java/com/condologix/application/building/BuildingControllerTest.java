package com.condologix.application.building;

import java.util.List;

import com.condologix.application.exception.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BuildingController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
public class BuildingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BuildingService buildingService;

    @Test
    void createBuildingShouldReturnCreated() throws Exception {
        BuildingDTO response = new BuildingDTO(
            1L,
            "12345678000199",
            "Condo One LTDA",
            "Condo One",
            "11999999999",
            "condo@example.com",
            "Main Street",
            123,
            "12345678",
            "Downtown",
            "Sao Paulo",
            "SP"
        );

        when(buildingService.createBuilding(any(BuildingCreateDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/buildings")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {

                    }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cnpj").value("12345678000199"))
                .andExpect(jsonPath("$.legalName").value("Condo One LTDA"))
                .andExpect(jsonPath("$.name").value("Condo One"));
    }
        @Test
    void createBuildingShouldReturnBadRequestWhenBodyIsInvalid() throws Exception {
        mockMvc.perform(post("/api/buildings")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "cnpj": "123",
                        "legalName": "",
                        "name": "",
                        "phone": "999",
                        "email": "invalid-email",
                        "address": "",
                        "addressNumber": 0,
                        "postalCode": "123",
                        "neighborhood": "",
                        "city": "",
                        "state": "Sao Paulo"
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Validation failed"))
            .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void createBuildingShouldReturnConflictWhenCnpjAlreadyExists() throws Exception {
        when(buildingService.createBuilding(any(BuildingCreateDTO.class)))
            .thenThrow(new IllegalStateException("Building with the same CNPJ already exists"));

        mockMvc.perform(post("/api/buildings")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "cnpj": "12345678000199",
                        "legalName": "Condo One LTDA",
                        "name": "Condo One",
                        "phone": "11999999999",
                        "email": "condo@example.com",
                        "address": "Main Street",
                        "addressNumber": 123,
                        "postalCode": "12345678",
                        "neighborhood": "Downtown",
                        "city": "Sao Paulo",
                        "state": "SP"
                    }
                    """))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value("Building with the same CNPJ already exists"));
    }

    @Test
    void updateBuildingShouldReturnOk() throws Exception {
        BuildingDTO response = new BuildingDTO(
            1L,
            "12345678000199",
            "Condo One LTDA",
            "Condo One",
            "11888887777",
            "updated@example.com",
            "Main Street",
            123,
            "12345678",
            "Downtown",
            "Sao Paulo",
            "SP"
        );

        when(buildingService.updateBuilding(anyLong(), any(BuildingUpdateDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/buildings/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "phone": "11888887777",
                        "email": "updated@example.com"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.phone").value("11888887777"))
            .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    void updateBuildingShouldReturnNotFoundWhenBuildingDoesNotExist() throws Exception {
        when(buildingService.updateBuilding(anyLong(), any(BuildingUpdateDTO.class)))
            .thenThrow(new ResourceNotFoundException("Building not found: 1"));

        mockMvc.perform(put("/api/buildings/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "phone": "11888887777",
                        "email": "updated@example.com"
                    }
                    """))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Building not found: 1"));
    }

    @Test
    void deleteBuildingShouldReturnNoContent() throws Exception {
        doNothing().when(buildingService).deleteBuilding(1L);

        mockMvc.perform(delete("/api/buildings/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteBuildingShouldReturnNotFoundWhenBuildingDoesNotExist() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException("Building not found: 1"))
            .when(buildingService)
            .deleteBuilding(1L);

        mockMvc.perform(delete("/api/buildings/1"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Building not found: 1"));
    }

    @Test
    void getBuildingByIdShouldReturnOk() throws Exception {
        BuildingDTO response = new BuildingDTO(
            1L,
            "12345678000199",
            "Condo One LTDA",
            "Condo One",
            "11999999999",
            "condo@example.com",
            "Main Street",
            123,
            "12345678",
            "Downtown",
            "Sao Paulo",
            "SP"
        );

        when(buildingService.getBuildingById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/buildings/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.cnpj").value("12345678000199"));
    }

    @Test
    void getAllBuildingsShouldReturnOk() throws Exception {
        List<BuildingDTO> buildings = List.of(
            new BuildingDTO(1L, "12345678000199", "Condo One LTDA", "Condo One", "11999999999", "condo1@example.com", "Main Street", 123, "12345678", "Downtown", "Sao Paulo", "SP"),
            new BuildingDTO(2L, "99887766000155", "Condo Two LTDA", "Condo Two", "11888887777", "condo2@example.com", "Second Street", 456, "87654321", "Center", "Rio de Janeiro", "RJ")
        );

        when(buildingService.getAllBuildings()).thenReturn(buildings);

        mockMvc.perform(get("/api/buildings"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[1].id").value(2));
    }
}
