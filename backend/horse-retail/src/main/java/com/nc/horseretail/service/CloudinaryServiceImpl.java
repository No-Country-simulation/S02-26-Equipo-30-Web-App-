package com.nc.horseretail.service;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.nc.horseretail.exception.FileDeletionException;
import com.nc.horseretail.exception.FileUploadException;
import com.nc.horseretail.model.media.CloudinaryUploadResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024L; // 10MB

    // ==========================================================
    // UPLOAD FILE
    // ==========================================================

    @Override
    public CloudinaryUploadResult upload(MultipartFile file, String folder) {

        validateFile(file);

        try {
            Map<String, Object> options = buildUploadOptions(folder, file);

            Map<?, ?> result = cloudinary.uploader()
                    .upload(file.getBytes(), options);

            String secureUrl = (String) result.get("secure_url");
            String publicId = (String) result.get("public_id");

            if (secureUrl == null || publicId == null) {
                throw new FileUploadException("Cloudinary did not return required upload data");
            }

            log.info("Cloudinary upload success | folder={} | publicId={}", folder, publicId);

            return new CloudinaryUploadResult(secureUrl, publicId);

        } catch (IOException e) {
            log.error("Cloudinary upload failed | folder={}", folder, e);
            throw new FileUploadException("Cloudinary upload failed");
        }
    }

    // ==========================================================
    // DELETE FILE
    // ==========================================================

    @Override
    public void delete(String publicId) {

        try {
            Map<?, ?> result = cloudinary.uploader()
                    .destroy(publicId, ObjectUtils.emptyMap());

            String status = (String) result.get("result");

            if (!"ok".equalsIgnoreCase(status) &&
                    !"not found".equalsIgnoreCase(status)) {

                throw new FileDeletionException(
                        "Cloudinary deletion failed with status: " + status);
            }

            log.info("Cloudinary deletion success | publicId={}", publicId);

        } catch (IOException e) {
            log.error("Cloudinary deletion failed | publicId={}", publicId, e);
            throw new FileDeletionException("Failed to delete file from Cloudinary");
        }
    }

    // ==========================================================
    // VALIDATIONS
    // ==========================================================

    private void validateFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File exceeds maximum size of 10MB");
        }

        String contentType = file.getContentType();

        if (contentType == null) {
            throw new IllegalArgumentException("File must have a valid content type");
        }

        if (!isAllowedContentType(contentType)) {
            throw new IllegalArgumentException("Unsupported file type: " + contentType);
        }
    }

    private boolean isAllowedContentType(String contentType) {

        return contentType.startsWith("image/")
                || contentType.startsWith("video/")
                || contentType.equals("application/pdf");
    }

    // ==========================================================
    // OPTIONS
    // ==========================================================

    private Map<String, Object> buildUploadOptions(String folder, MultipartFile file) {

        String contentType = file.getContentType();

        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException("File content type is required");
        }

        String resourceType = resolveResourceType(contentType);

        Map<String, Object> options = new HashMap<>();
        options.put("folder", folder);
        options.put("resource_type", resourceType);
        options.put("use_filename", true);
        options.put("unique_filename", true);
        options.put("overwrite", false);

        return options;
    }

    private String resolveResourceType(String contentType) {

        if (contentType.startsWith("image/")) {
            return "image";
        }

        if (contentType.startsWith("video/")) {
            return "video";
        }

        if ("application/pdf".equals(contentType)) {
            return "raw";
        }

        throw new IllegalArgumentException("Unsupported content type: " + contentType);
    }
}
