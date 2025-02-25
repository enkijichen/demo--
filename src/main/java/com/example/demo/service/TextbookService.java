package com.example.demo.service;

import com.example.demo.dto.TextbookDTO;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Textbook;
import com.example.demo.repository.TextbookRepository;
import com.example.demo.util.ValidationUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TextbookService {
    @Autowired
    private TextbookRepository textbookRepository;

    public List<Textbook> findAll() {
        return textbookRepository.findAll();
    }

    @Cacheable(value = "textbooks", key = "#grade")
    public List<Textbook> findByGrade(String grade) {
        ValidationUtils.validateGrade(grade);
        return textbookRepository.findByGrade(grade);
    }

    public List<Textbook> findBySubject(String subject) {
        ValidationUtils.validateSubject(subject);
        return textbookRepository.findBySubject(subject);
    }

    @CacheEvict(value = "textbooks", allEntries = true)
    @Transactional
    public Textbook create(TextbookDTO dto) {
        ValidationUtils.validateGrade(dto.getGrade());
        ValidationUtils.validateSubject(dto.getSubject());

        Textbook textbook = new Textbook();
        textbook.setName(dto.getName());
        textbook.setGrade(dto.getGrade());
        textbook.setSubject(dto.getSubject());
        textbook.setPrice(dto.getPrice());
        textbook.setStock(dto.getStock());
        return textbookRepository.save(textbook);
    }

    @Transactional
    public void delete(Long id) {
        if (!textbookRepository.existsById(id)) {
            throw new ResourceNotFoundException("教材不存在");
        }
        textbookRepository.deleteById(id);
    }

    @Transactional
    public Textbook update(Long id, TextbookDTO dto) {
        Textbook textbook = textbookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("教材不存在"));
        
        ValidationUtils.validateGrade(dto.getGrade());
        ValidationUtils.validateSubject(dto.getSubject());
        
        textbook.setName(dto.getName());
        textbook.setGrade(dto.getGrade());
        textbook.setSubject(dto.getSubject());
        textbook.setPrice(dto.getPrice());
        textbook.setStock(dto.getStock());
        
        return textbookRepository.save(textbook);
    }

    public List<Textbook> findByGradeAndSubject(String grade, String subject) {
        if (grade != null && subject != null) {
            return textbookRepository.findByGradeAndSubject(grade, subject);
        } else if (grade != null) {
            return textbookRepository.findByGrade(grade);
        } else if (subject != null) {
            return textbookRepository.findBySubject(subject);
        } else {
            return textbookRepository.findAll();
        }
    }

    public Textbook save(Textbook textbook) {
        return textbookRepository.save(textbook);
    }

    public Optional<Textbook> findById(Long id) {
        return textbookRepository.findById(id);
    }

    public List<Textbook> getTextbooks(String name, String grade, String subject) {
        if (name != null && !name.isEmpty()) {
            return textbookRepository.findByNameContaining(name);
        }
        if (grade != null && !grade.isEmpty() && subject != null && !subject.isEmpty()) {
            return textbookRepository.findByGradeAndSubject(grade, subject);
        }
        if (grade != null && !grade.isEmpty()) {
            return textbookRepository.findByGrade(grade);
        }
        if (subject != null && !subject.isEmpty()) {
            return textbookRepository.findBySubject(subject);
        }
        return textbookRepository.findAll();
    }
} 