package com.example.rqchallenge.utils;

import com.example.rqchallenge.employees.dto.EmployeeCollectionDto;
import com.example.rqchallenge.employees.exceptions.FileNotExist;
import com.example.rqchallenge.employees.helper.EmployeeFileUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EmployeeFileUtilsTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private EmployeeFileUtils employeeFileUtils;

    @TempDir
    Path tempDir;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        System.setProperty("user.dir", tempDir.toString());
    }

    @Test
    public void testSaveEmployeeResponseToFile() throws IOException {
        EmployeeCollectionDto employeeCollectionDto = new EmployeeCollectionDto();
        String jsonString = "{\"status\":\"success\"}";

        when(objectMapper.writeValueAsString(employeeCollectionDto)).thenReturn(jsonString);

        assertDoesNotThrow(() -> {
            employeeFileUtils.saveEmployeeResponseToFile(employeeCollectionDto);
        });

        Path filePath = tempDir.resolve("files/employee.json");
        assertTrue(Files.exists(filePath));
        String fileContent = Files.readString(filePath);
        assertEquals(jsonString, fileContent);
    }

    @Test
    public void testFetchEmployeeFromFileSuccess() throws IOException {
        EmployeeCollectionDto expectedEmployeeCollectionDto = new EmployeeCollectionDto();
        String jsonString = "{\"status\":\"success\"}";

        Path filePath = tempDir.resolve("files/employee.json");
        Files.createDirectories(filePath.getParent());
        Files.writeString(filePath, jsonString);

        when(objectMapper.readValue(jsonString, EmployeeCollectionDto.class)).thenReturn(expectedEmployeeCollectionDto);

        EmployeeCollectionDto actualEmployeeCollectionDto = employeeFileUtils.fetchEmployeeFromFile();

        assertNotNull(actualEmployeeCollectionDto);
        assertEquals(expectedEmployeeCollectionDto, actualEmployeeCollectionDto);
    }

    @Test
    public void testFetchEmployeeFromFileNotExist() {
        Exception exception = assertThrows(FileNotExist.class, () -> {
            employeeFileUtils.fetchEmployeeFromFile();
        });

        assertEquals("Employee data file not found", exception.getMessage());
    }

    @Test
    public void testFetchEmployeeFromFileUnexpectedException() throws IOException {
        Path filePath = tempDir.resolve("files/employee.json");
        Files.createDirectories(filePath.getParent());
        Files.writeString(filePath, "invalid json");

        when(objectMapper.readValue(anyString(), eq(EmployeeCollectionDto.class))).thenThrow(new RuntimeException("Test exception"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            employeeFileUtils.fetchEmployeeFromFile();
        });

        assertEquals("Unexpected error fetching employee response from file", exception.getMessage());
    }
}
