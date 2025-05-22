package com.netwrkly.profile.service;

import com.netwrkly.profile.model.BrandProfile;
import com.netwrkly.profile.repository.BrandProfileRepository;
import com.netwrkly.auth.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BrandProfileService {
    private static final Logger logger = LoggerFactory.getLogger(BrandProfileService.class);
    
    private final BrandProfileRepository brandProfileRepository;
    
    @Value("${app.upload.dir:uploads}")
    private String uploadBaseDir;
    
    private final String LOGO_DIR = "logos";
    private Path fileStorageLocation;

    @Autowired
    public BrandProfileService(BrandProfileRepository brandProfileRepository) {
        this.brandProfileRepository = brandProfileRepository;
    }

    @PostConstruct
    public void init() {
        this.fileStorageLocation = Paths.get(uploadBaseDir).resolve(LOGO_DIR).normalize();
        createUploadDirectory();
    }

    private void createUploadDirectory() {
        try {
            Files.createDirectories(fileStorageLocation);
            logger.info("Created directory for file uploads: {}", fileStorageLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }

    public BrandProfile getProfile(User user) {
        return brandProfileRepository.findByUser(user)
                .orElse(new BrandProfile());
    }

    public BrandProfile createOrUpdateProfile(BrandProfile profile, User user) {
        profile.setUser(user);
        return brandProfileRepository.save(profile);
    }

    public String uploadLogo(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            logger.error("Attempted to upload empty file");
            throw new IllegalArgumentException("Failed to store empty file");
        }

        String filename = UUID.randomUUID().toString() + "_" + StringUtils.cleanPath(file.getOriginalFilename());
        logger.info("Processing file upload - Original name: {}, Size: {}, Content type: {}", 
            file.getOriginalFilename(), file.getSize(), file.getContentType());
        
        try {
            // Check if the filename contains invalid characters
            if (filename.contains("..")) {
                logger.error("Invalid filename detected: {}", filename);
                throw new IllegalArgumentException("Filename contains invalid path sequence " + filename);
            }

            // Copy file to the target location
            Path targetLocation = fileStorageLocation.resolve(filename);
            logger.info("Attempting to save file to: {}", targetLocation);
            
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            String fileUrl = "/api/uploads/" + LOGO_DIR + "/" + filename;
            logger.info("File uploaded successfully. URL: {}", fileUrl);
            
            return fileUrl;
        } catch (IOException ex) {
            logger.error("Failed to store file {}: {}", filename, ex.getMessage(), ex);
            throw new IOException("Failed to store file " + filename, ex);
        }
    }

    public void deleteLogo(String filename) {
        try {
            Path file = fileStorageLocation.resolve(filename);
            Files.deleteIfExists(file);
            logger.info("Successfully deleted logo: {}", filename);
        } catch (IOException e) {
            logger.error("Error deleting logo: {}", filename, e);
            throw new RuntimeException("Error deleting file", e);
        }
    }
} 