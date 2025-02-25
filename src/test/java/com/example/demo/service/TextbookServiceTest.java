package com.example.demo.service;

import com.example.demo.dto.TextbookDTO;
import com.example.demo.model.Textbook;
import com.example.demo.repository.TextbookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.exception.BadRequestException;

@ExtendWith(MockitoExtension.class)
public class TextbookServiceTest {

    @Mock
    private TextbookRepository textbookRepository;

    @InjectMocks
    private TextbookService textbookService;

    @Test
    void createTextbook_Success() {
        // Arrange
        TextbookDTO dto = new TextbookDTO();
        dto.setName("数学");
        dto.setGrade("初一");
        dto.setSubject("数学");
        dto.setPrice(new BigDecimal("29.9"));
        dto.setStock(100);

        Textbook textbook = new Textbook();
        textbook.setId(1L);
        textbook.setName(dto.getName());
        textbook.setGrade(dto.getGrade());
        textbook.setSubject(dto.getSubject());
        textbook.setPrice(dto.getPrice());
        textbook.setStock(dto.getStock());

        when(textbookRepository.save(any(Textbook.class))).thenReturn(textbook);

        // Act
        Textbook result = textbookService.create(dto);

        // Assert
        assertNotNull(result);
        assertEquals(dto.getName(), result.getName());
        verify(textbookRepository, times(1)).save(any(Textbook.class));
    }

    @Test
    void findByGrade_InvalidGrade() {
        assertThrows(BadRequestException.class, () -> 
            textbookService.findByGrade("invalid_grade")
        );
    }

    @Test
    void findBySubject_InvalidSubject() {
        assertThrows(BadRequestException.class, () -> 
            textbookService.findBySubject("invalid_subject")
        );
    }
} 