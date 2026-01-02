package org.revizit.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageStorageService {

  private static final String PREFIX_IMAGE_STORAGE = "/img";

  private final Path rootLocation;

  @Autowired
  public ImageStorageService(@Value("${revizit.media-storage-root:./media}") String rootDir) {
    this.rootLocation = Paths.get(rootDir + PREFIX_IMAGE_STORAGE);
    try {
      Files.createDirectories(rootLocation);
    } catch (IOException e) {
      throw new ImageStorageException("Could not initialize storage", e);
    }
  }

  public String storeImage(MultipartFile file) throws ImageStorageException {
    try {
      if (file.isEmpty()) {
        throw new ImageStorageException("Failed to store empty file.");
      }

      // Generate hash filename
      String hash = generateFileHash(file.getBytes());
      String extension = FilenameUtils.getExtension(file.getOriginalFilename());
      if (extension == null || extension.isEmpty()) {
        extension = getExtensionFromContentType(file.getContentType());
      }

      // Create nested directory structure (abcd/efgh/)
      String relativePath = createNestedPath(hash, extension);
      Path destinationFile = this.rootLocation.resolve(relativePath)
          .normalize().toAbsolutePath();

      // Make sure directory exists
      Files.createDirectories(destinationFile.getParent());

      // Save file
      Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

      return PREFIX_IMAGE_STORAGE + "/" + relativePath;
    } catch (IOException e) {
      throw new ImageStorageException("Failed to store file.", e);
    }
  }

  public Resource loadImage(String relativePath) throws ImageStorageException {
    try {
      Path file = rootLocation.resolve(relativePath);
      Resource resource = new UrlResource(file.toUri());
      if (resource.exists() || resource.isReadable()) {
        return resource;
      } else {
        throw new ImageStorageException("Could not read file: " + relativePath);
      }
    } catch (MalformedURLException e) {
      throw new ImageStorageException("Could not read file: " + relativePath, e);
    }
  }

  public void deleteImage(String relativePath) throws ImageStorageException {
    try {
      Path file = rootLocation.resolve(relativePath);
      Files.deleteIfExists(file);
    } catch (IOException e) {
      throw new ImageStorageException("Could not delete file: " + relativePath, e);
    }
  }

  private String generateFileHash(final byte[] fileBytes) throws ImageStorageException {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hashBytes = digest.digest(fileBytes);

      // Convert to hex and take first 12 characters for our 12-char hash
      StringBuilder hexString = new StringBuilder();
      for (byte b : hashBytes) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length() == 1) {
          hexString.append('0');
        }
        hexString.append(hex);
      }

      return hexString.substring(0, 20);
    } catch (NoSuchAlgorithmException e) {
      throw new ImageStorageException("Failed to generate file hash", e);
    }
  }

  private String createNestedPath(String hash, String extension) {
    return hash.substring(0, 4) + "/" +
        hash.substring(4, 8) + "/" +
        hash.substring(8, 12) + "/" +
        hash.substring(12) + "." + extension.toLowerCase();
  }

  private String getExtensionFromContentType(String contentType) {
    if (contentType == null) {
      return "dat";
    }
    return switch (contentType) {
      case "image/jpeg" -> "jpg";
      case "image/png" -> "png";
      case "image/gif" -> "gif";
      case "image/webp" -> "webp";
      default -> contentType.split("/")[1];
    };
  }

}
