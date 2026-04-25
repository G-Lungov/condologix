package com.condologix.application.unit;

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

@WebMvcTest(UnitController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class UnitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UnitService unitService;

    @Test
    void createUnitShouldReturnCreated() throws Exception {
        UnitDTO response = new UnitDTO(7L, 1L, (short) 101, "A", UnitType.RESIDENTIAL);

        when(unitService.createUnit(any(UnitCreateDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/units")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "buildingId": 1,
                        "number": 101,
                        "block": "A",
                        "unitType": "RESIDENTIAL"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(7))
            .andExpect(jsonPath("$.buildingId").value(1))
            .andExpect(jsonPath("$.number").value(101))
            .andExpect(jsonPath("$.block").value("A"))
            .andExpect(jsonPath("$.unitType").value("RESIDENTIAL"));
    }

    @Test
    void createUnitShouldReturnBadRequestWhenBodyIsInvalid() throws Exception {
        mockMvc.perform(post("/api/units")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "buildingId": -1,
                        "number": 0,
                        "block": "   ",
                        "unitType": null
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Validation failed"))
            .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void createUnitShouldReturnNotFoundWhenBuildingDoesNotExist() throws Exception {
        when(unitService.createUnit(any(UnitCreateDTO.class)))
            .thenThrow(new ResourceNotFoundException("Building not found with id: 999"));

        mockMvc.perform(post("/api/units")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "buildingId": 999,
                        "number": 101,
                        "block": "A",
                        "unitType": "RESIDENTIAL"
                    }
                    """))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Building not found with id: 999"));
    }

    @Test
    void createUnitShouldReturnConflictWhenServiceThrowsIllegalState() throws Exception {
        when(unitService.createUnit(any(UnitCreateDTO.class)))
            .thenThrow(new IllegalStateException("Unit with the same block and number already exists"));

        mockMvc.perform(post("/api/units")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "buildingId": 1,
                        "number": 101,
                        "block": "A",
                        "unitType": "RESIDENTIAL"
                    }
                    """))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value("Unit with the same block and number already exists"));
    }

    @Test
    void updateUnitTypeShouldReturnOk() throws Exception {
        UnitDTO response = new UnitDTO(7L, 1L, (short) 101, "A", UnitType.COMERCIAL);

        when(unitService.updateUnitType(anyLong(), any(UnitUpdateDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/units/7/type")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "unitType": "COMERCIAL"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(7))
            .andExpect(jsonPath("$.unitType").value("COMERCIAL"));
    }

    @Test
    void updateUnitTypeShouldReturnNotFoundWhenUnitDoesNotExist() throws Exception {
        when(unitService.updateUnitType(anyLong(), any(UnitUpdateDTO.class)))
            .thenThrow(new ResourceNotFoundException("Unit not found with id: 7"));

        mockMvc.perform(put("/api/units/7/type")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "unitType": "COMERCIAL"
                    }
                    """))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Unit not found with id: 7"));
    }

    @Test
    void deleteUnitShouldReturnNoContent() throws Exception {
        doNothing().when(unitService).deleteUnit(7L);

        mockMvc.perform(delete("/api/units/7"))
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteUnitShouldReturnNotFoundWhenUnitDoesNotExist() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException("Unit not found with id: 7"))
            .when(unitService)
            .deleteUnit(7L);

        mockMvc.perform(delete("/api/units/7"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Unit not found with id: 7"));
    }

    @Test
    void getUnitsByBuildingShouldReturnOk() throws Exception {
        List<UnitDTO> units = List.of(
            new UnitDTO(1L, 5L, (short) 101, "A", UnitType.RESIDENTIAL),
            new UnitDTO(2L, 5L, (short) 102, "A", UnitType.COMERCIAL)
        );

        when(unitService.getUnitsByBuilding(5L)).thenReturn(units);

        mockMvc.perform(get("/api/units/building/5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[1].id").value(2));
    }
}
