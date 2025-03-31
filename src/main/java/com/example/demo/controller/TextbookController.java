package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.TextbookDTO;
import com.example.demo.model.Textbook;
import com.example.demo.service.TextbookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.Valid;
import com.example.demo.service.FileStorageService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/textbooks")
@RequiredArgsConstructor
@Tag(name = "教材管理", description = "教材的增删改查接口")
@CrossOrigin
@Slf4j
public class TextbookController {
    @Autowired
    private TextbookService textbookService;

    @Autowired
    private FileStorageService fileStorageService;

    @Operation(summary = "获取所有教材")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Textbook>>> getTextbooks(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String subject) {
        try {
            List<Textbook> textbooks = textbookService.getTextbooks(name, grade, subject);
            return ResponseEntity.ok(ApiResponse.success(textbooks));
        } catch (Exception e) {
            log.error("Failed to get textbooks", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("获取教材列表失败"));
        }
    }

    @GetMapping("/grade/{grade}")
    public ResponseEntity<ApiResponse<List<Textbook>>> getTextbooksByGrade(@PathVariable String grade) {
        return ResponseEntity.ok(ApiResponse.success(textbookService.findByGrade(grade)));
    }

    @GetMapping("/subject/{subject}")
    public ResponseEntity<ApiResponse<List<Textbook>>> getTextbooksBySubject(@PathVariable String subject) {
        return ResponseEntity.ok(ApiResponse.success(textbookService.findBySubject(subject)));
    }

    @Operation(summary = "创建新教材")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Textbook>> createTextbook(@RequestBody @Valid TextbookDTO textbookDTO) {
        try {
            Textbook textbook = textbookService.create(textbookDTO);
            return ResponseEntity.ok(ApiResponse.success(textbook));
        } catch (Exception e) {
            log.error("Failed to create textbook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("创建教材失败"));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Textbook>> updateTextbook(
            @PathVariable Long id,
            @RequestBody @Valid TextbookDTO textbookDTO) {
        try {
            Textbook textbook = textbookService.update(id, textbookDTO);
            return ResponseEntity.ok(ApiResponse.success(textbook));
        } catch (Exception e) {
            log.error("Failed to update textbook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("更新教材失败"));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteTextbook(@PathVariable Long id) {
        try {
            textbookService.delete(id);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (Exception e) {
            log.error("Failed to delete textbook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("删除教材失败"));
        }
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<ApiResponse<Textbook>> uploadImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            return textbookService.getTextbookById(id)
                    .map(textbook -> {
                        String fileName = fileStorageService.storeFile(file);
                        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                                .path("/api/files/")
                                .path(fileName)
                                .toUriString();
                        textbook.setImageUrl(fileDownloadUri);
                        Textbook savedTextbook = textbookService.saveTextbook(textbook);
                        return ResponseEntity.ok(ApiResponse.success(savedTextbook, "图片上传成功"));
                    })
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error("教材不存在")));
        } catch (Exception e) {
            log.error("Failed to upload image for textbook: " + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("图片上传失败"));
        }
    }
} 