package com.example.rqchallenge.employees.helper;

import com.example.rqchallenge.employees.dto.EmployeeCollectionDto;
import com.example.rqchallenge.employees.exceptions.FileNotExist;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;

@Component
@Slf4j
public class EmployeeFileUtils {

    @Autowired
    ObjectMapper objectMapper;

    @Async
    public void saveEmployeeResponseToFile(EmployeeCollectionDto employeeCollectionDto) throws IOException {

        log.info("Started saving employee response to file");
        String fileName = "employee.json";

        String projectDirectoryPath = System.getProperty("user.dir");
        String filesDirectoryPath = projectDirectoryPath + File.separator + "files";

        File filesDirectory = new File(filesDirectoryPath);
        if (!filesDirectory.exists()) {
            filesDirectory.mkdirs();
        }

        File jsonFile = new File(filesDirectory, fileName);

        String jsonString = objectMapper.writeValueAsString(employeeCollectionDto);
        Files.writeString(Path.of(jsonFile.getAbsolutePath()), jsonString);

        log.info("Finished saving employee response to file");
    }


    public EmployeeCollectionDto fetchEmployeeFromFile() {
        log.info("Started fetching employee response from file");
        String fileName = "employee.json";

        EmployeeCollectionDto employeeCollectionDto = null;
        try {
            String projectDirectoryPath = System.getProperty("user.dir");
            String filesDirectoryPath = projectDirectoryPath + File.separator + "files";
            File jsonFile = new File(filesDirectoryPath, fileName);

            if (jsonFile.exists()) {
                String jsonString = Files.readString(Path.of(jsonFile.getAbsolutePath()));
                employeeCollectionDto = objectMapper.readValue(jsonString, EmployeeCollectionDto.class);
            } else {
                log.error("Employee data file not found: {}", fileName);
                throw new FileNotExist("Employee data file not found");
            }

            log.info("Finished fetching employee response from file");
        } catch (FileNotExist e) {
            throw e;
        } catch (IOException e) {
            log.error("Error fetching employee response from file: {}", fileName, e);
            throw new RuntimeException("Error reading employee data file", e);
        } catch (Exception e) {
            log.error("Unexpected error fetching employee response from file: {}", fileName, e);
            throw new RuntimeException("Unexpected error fetching employee response from file", e);
        }

        return employeeCollectionDto;
    }


}
