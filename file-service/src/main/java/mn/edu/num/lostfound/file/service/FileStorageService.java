package mn.edu.num.lostfound.file.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadPath;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) throws IOException {
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(this.uploadPath);
    }

    public String storeFile(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("Uploaded file is empty.");
            }

            String contentType = file.getContentType();

            if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
                throw new IllegalArgumentException("Only JPG, PNG, and WEBP images are allowed.");
            }

            String originalName = file.getOriginalFilename();
            String extension = getExtension(originalName);

            String safeFileName = UUID.randomUUID() + extension;
            Path targetLocation = uploadPath.resolve(safeFileName);

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return "/api/files/" + safeFileName;

        } catch (IOException e) {
            throw new RuntimeException("Could not store uploaded file.", e);
        }
    }

    public Path loadFile(String fileName) {
        return uploadPath.resolve(fileName).normalize();
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return ".jpg";
        }

        String extension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();

        if (!extension.equals(".jpg")
                && !extension.equals(".jpeg")
                && !extension.equals(".png")
                && !extension.equals(".webp")) {
            return ".jpg";
        }

        return extension;
    }
}
