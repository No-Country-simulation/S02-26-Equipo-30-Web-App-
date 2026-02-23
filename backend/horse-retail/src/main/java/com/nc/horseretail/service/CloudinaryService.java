package com.nc.horseretail.service;

import com.nc.horseretail.model.media.CloudinaryUploadResult;
import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    CloudinaryUploadResult upload(MultipartFile file, String folder);

    void delete(String publicId);
}
