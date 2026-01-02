package org.revizit.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import static java.util.stream.Collectors.groupingBy;
import org.apache.commons.csv.CSVFormat;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DataImportService {

  public record UserImport(String username, String email, boolean admin) {
  }


  private enum UserImportHeader { USERNAME, EMAIL, ADMIN }


  public List<UserImport> importUsersFromCsv(MultipartFile file) {
    try (final InputStream in = file.getInputStream();
        final var reader = new InputStreamReader(in, StandardCharsets.UTF_8);
        final var parser = CSVFormat.Builder.create(CSVFormat.EXCEL)
            .setDelimiter(';')
            .setHeader(UserImportHeader.class)
            .setSkipHeaderRecord(true)
            .get()
            .parse(reader)) {

      return parser.stream()
          .map(it -> {
            final var username = it.get(UserImportHeader.USERNAME);
            final var email = it.get(UserImportHeader.EMAIL);
            final var adminStr = it.get(UserImportHeader.ADMIN);
            final var admin = Boolean.TRUE.toString().equalsIgnoreCase(adminStr)
                || "y".equalsIgnoreCase(adminStr);
            return new UserImport(username, email, admin);
          })
          .collect(groupingBy(UserImport::username))
          .values().stream()
          .map(List::getFirst)
          .toList();

    } catch (final IOException e) {
      throw new IllegalArgumentException(e);
    }
  }

}
