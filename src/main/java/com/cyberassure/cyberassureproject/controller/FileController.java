package com.cyberassure.cyberassureproject.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    /**
     * Serve uploaded proof documents for underwriter review.
     * GET /api/v1/files/proof/{userId}/{filename}
     */
    @PreAuthorize("hasRole('UNDERWRITER') or hasRole('ADMIN')")
    @GetMapping("/proof/{userId}/{filename:.+}")
    public ResponseEntity<Resource> serveProof(
            @PathVariable String userId,
            @PathVariable String filename) {

        try {
            Path filePath = Paths.get("uploads/proofs/" + userId + "/" + filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = "application/octet-stream";
            String lowerName = filename.toLowerCase();
            if (lowerName.endsWith(".pdf"))
                contentType = "application/pdf";
            else if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg"))
                contentType = "image/jpeg";
            else if (lowerName.endsWith(".png"))
                contentType = "image/png";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
