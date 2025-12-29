package org.revizit.service;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.revizit.TestcontainersConfiguration;
import org.revizit.persistence.entity.ReportType;
import org.revizit.persistence.entity.UserAccount;
import org.revizit.persistence.entity.WaterFlavour;
import org.revizit.persistence.entity.WaterReport;
import org.revizit.persistence.entity.WaterState;
import org.revizit.persistence.repository.UserAccountRepository;
import org.revizit.persistence.repository.WaterFlavourRepository;
import org.revizit.persistence.repository.WaterReportRepository;
import org.revizit.persistence.repository.WaterStateRepository;
import org.revizit.rest.model.WaterReportKind;
import org.revizit.rest.model.WaterFlavourDto;
import org.revizit.rest.model.WaterReportDto;
import org.revizit.rest.model.WaterStateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
class WaterServiceTest {

  @Autowired
  private WaterService waterService;

  @Autowired
  private WaterStateRepository waterStateRepository;

  @Autowired
  private WaterReportRepository waterReportRepository;

  @Autowired
  private WaterFlavourRepository waterFlavourRepository;

  @Autowired
  private UserAccountRepository userAccountRepository;

  @MockitoBean
  private UserService userService;

  private UserAccount adminUser;
  private UserAccount regularUser;

  @BeforeEach
  void setUp() {
    waterStateRepository.deleteAll();
    waterReportRepository.deleteAll();
    waterFlavourRepository.deleteAll();
    userAccountRepository.deleteAll();

    adminUser = new UserAccount();
    adminUser.setUsername("admin");
    adminUser.setUserRole(UserService.ROLE_ADMIN);
    adminUser.setMailAddr("admin@test.com");
    adminUser.setUserPw("password");
    adminUser = userAccountRepository.save(adminUser);

    regularUser = new UserAccount();
    regularUser.setUsername("user");
    regularUser.setUserRole(UserService.ROLE_PLAIN);
    regularUser.setMailAddr("user@test.com");
    regularUser.setUserPw("password");
    regularUser = userAccountRepository.save(regularUser);

    Mockito.when(userService.currentUser()).thenReturn(adminUser);
    Mockito.when(userService.isCurrentUserAdmin()).thenReturn(true);
  }

  @Test
  void createFlavour_Success() {
    WaterFlavour flavour = waterService.createFlavour("Apple");
    assertThat(flavour.getId()).isNotNull();
    assertThat(flavour.getName()).isEqualTo("Apple");
    assertThat(waterFlavourRepository.findAll()).hasSize(1);
  }

  @Test
  void createFlavour_AlreadyExists_ThrowsException() {
    waterService.createFlavour("Apple");
    assertThatThrownBy(() -> waterService.createFlavour("Apple"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("already exists");
  }

  @Test
  void createFlavour_NotAdmin_ThrowsException() {
    Mockito.when(userService.isCurrentUserAdmin()).thenReturn(false);
    assertThatThrownBy(() -> waterService.createFlavour("Orange"))
        .isInstanceOf(NotAuthorisedException.class);
  }

  @Test
  void deleteFlavour_Success() {
    WaterFlavour flavour = waterService.createFlavour("Apple");
    waterService.deleteFlavour(flavour.getId().longValue());
    assertThat(waterFlavourRepository.findById(flavour.getId())).isEmpty();
  }

  @Test
  void deleteFlavour_InUse_MarksAsInactive() {
    WaterFlavour flavour = waterService.createFlavour("Apple");

    // Create a state using this flavour
    WaterStateDto dto = new WaterStateDto()
        .flavour(new WaterFlavourDto().id(flavour.getId().longValue()).name("Apple"))
        .waterLevel(50)
        .emptyGallons(1)
        .fullGallons(2);
    waterService.defineState(dto);

    waterService.deleteFlavour(flavour.getId().longValue());

    WaterFlavour updatedFlavour = waterFlavourRepository.findById(flavour.getId()).orElseThrow();
    assertThat(updatedFlavour.isInactive()).isTrue();
  }

  @Test
  void restoreFlavour_Success() {
    WaterFlavour flavour = waterService.createFlavour("Apple");
    flavour.setInactive(true);
    waterFlavourRepository.save(flavour);

    waterService.restoreFlavour(flavour.getId().longValue());

    WaterFlavour restored = waterFlavourRepository.findById(flavour.getId()).orElseThrow();
    assertThat(restored.isInactive()).isFalse();
  }

  @Test
  void renameFlavour_Success() {
    WaterFlavour flavour = waterService.createFlavour("Apple");
    waterService.renameFlavour(flavour.getId().longValue(), "Green Apple");

    WaterFlavour renamed = waterFlavourRepository.findById(flavour.getId()).orElseThrow();
    assertThat(renamed.getName()).isEqualTo("Green Apple");
  }

  @Test
  void defineState_Success() {
    WaterFlavour flavour = waterService.createFlavour("Apple");
    WaterStateDto dto = new WaterStateDto()
        .flavour(new WaterFlavourDto().id(flavour.getId().longValue()).name("Apple"))
        .waterLevel(80)
        .emptyGallons(5)
        .fullGallons(10);

    WaterState state = waterService.defineState(dto);

    assertThat(state.getCurrPct()).isEqualTo(80);
    assertThat(state.getEmptyCnt()).isEqualTo(5);
    assertThat(state.getFullCnt()).isEqualTo(10);
    assertThat(state.getReport()).isNotNull();
    assertThat(state.getReport().getKind()).isEqualTo(ReportType.SET_PERCENTAGE);
  }

  @Test
  void registerReport_Percentage_Success() {
    WaterFlavour flavour = waterService.createFlavour("Apple");
    waterService.defineState(new WaterStateDto()
        .flavour(new WaterFlavourDto().id(flavour.getId().longValue()).name("Apple"))
        .waterLevel(100)
        .emptyGallons(0)
        .fullGallons(10));

    WaterReportDto reportDto = new WaterReportDto()
        .kind(WaterReportKind.PERCENTAGE)
        .value(75);

    WaterReport report = waterService.registerReport(reportDto);

    assertThat(report.getKind()).isEqualTo(ReportType.SET_PERCENTAGE);
    assertThat(report.getVal()).isEqualTo(75);
    assertThat(report.getApprovedAt()).isNull();
  }

  @Test
  void acceptReports_Success() {
    WaterFlavour flavour = waterService.createFlavour("Apple");
    waterService.defineState(new WaterStateDto()
        .flavour(new WaterFlavourDto().id(flavour.getId().longValue()).name("Apple"))
        .waterLevel(100)
        .emptyGallons(0)
        .fullGallons(10));

    WaterReport report = waterService.registerReport(new WaterReportDto()
        .kind(WaterReportKind.PERCENTAGE)
        .value(50));

    WaterState newState = waterService.acceptReports(List.of(report.getId().longValue()));

    assertThat(newState.getCurrPct()).isEqualTo(50);
    WaterReport updatedReport = waterReportRepository.findById(report.getId()).orElseThrow();
    assertThat(updatedReport.getApprovedAt()).isNotNull();
    assertThat(updatedReport.getApprovedBy()).isEqualTo(adminUser);
  }

  @Test
  void rejectReports_Success() {
    WaterFlavour flavour = waterService.createFlavour("Apple");
    waterService.defineState(new WaterStateDto()
        .flavour(new WaterFlavourDto().id(flavour.getId().longValue()).name("Apple"))
        .waterLevel(100)
        .emptyGallons(0)
        .fullGallons(10));

    WaterReport report = waterService.registerReport(new WaterReportDto()
        .kind(WaterReportKind.PERCENTAGE)
        .value(50));

    waterService.rejectReports(List.of(report.getId().longValue()));

    WaterReport updatedReport = waterReportRepository.findById(report.getId()).orElseThrow();
    assertThat(updatedReport.getRejectedAt()).isNotNull();
    assertThat(updatedReport.getRejectedBy()).isEqualTo(adminUser);
    assertThat(waterService.currentState().getCurrPct()).isEqualTo(100);
  }

  @Test
  void currentState_ReturnsLatest() {
    WaterFlavour flavour = waterService.createFlavour("Apple");
    waterService.defineState(new WaterStateDto()
        .flavour(new WaterFlavourDto().id(flavour.getId().longValue()).name("Apple"))
        .waterLevel(100)
        .emptyGallons(0)
        .fullGallons(10));

    WaterState state1 = waterService.currentState();

    // Simulate some time passed and new state
    WaterReport report = waterService.registerReport(
        new WaterReportDto().kind(WaterReportKind.PERCENTAGE).value(90));
    waterService.acceptReports(List.of(report.getId().longValue()));

    WaterState state2 = waterService.currentState();
    assertThat(state2.getCurrPct()).isEqualTo(90);
    assertThat(state2.getCreatedAt()).isAfterOrEqualTo(state1.getCreatedAt());
  }

  @Test
  void stateHistory_ReturnsCorrectRange() {
    WaterFlavour flavour = waterService.createFlavour("Apple");
    LocalDateTime now = LocalDateTime.now();

    waterService.defineState(new WaterStateDto()
        .flavour(new WaterFlavourDto().id(flavour.getId().longValue()).name("Apple"))
        .waterLevel(100)
        .emptyGallons(0)
        .fullGallons(10));

    List<WaterState> history = waterService.stateHistory(now.minusHours(1), now.plusHours(1));
    assertThat(history).hasSize(1);
  }

  @Test
  void defineState_NotAdmin_ThrowsException() {
    Mockito.when(userService.isCurrentUserAdmin()).thenReturn(false);
    assertThatThrownBy(() -> waterService.defineState(new WaterStateDto()))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Only admins");
  }

  @Test
  void registerReport_Refill_Success() {
    WaterFlavour flavour = waterService.createFlavour("Apple");
    waterService.defineState(new WaterStateDto()
        .flavour(new WaterFlavourDto().id(flavour.getId().longValue()).name("Apple"))
        .waterLevel(50)
        .emptyGallons(2)
        .fullGallons(8));

    WaterReport report =
        waterService.registerReport(new WaterReportDto().kind(WaterReportKind.REFILL));
    assertThat(report.getKind()).isEqualTo(ReportType.BALLOON_REFILL);
    assertThat(report.getVal()).isEqualTo(50);
  }

  @Test
  void registerReport_Swap_Success() {
    WaterFlavour flavour1 = waterService.createFlavour("Apple");
    WaterFlavour flavour2 = waterService.createFlavour("Orange");
    waterService.defineState(new WaterStateDto()
        .flavour(new WaterFlavourDto().id(flavour1.getId().longValue()).name("Apple"))
        .waterLevel(50)
        .emptyGallons(2)
        .fullGallons(8));

    WaterReport report = waterService.registerReport(new WaterReportDto()
        .kind(WaterReportKind.SWAP)
        .flavourId(flavour2.getId().longValue()));

    assertThat(report.getKind()).isEqualTo(ReportType.BALLOON_CHANGE);
    assertThat(report.getVal()).isEqualTo(100);
    assertThat(report.getFlavour().getName()).isEqualTo("Orange");
  }

  @Test
  void acceptReports_InvalidId_ThrowsException() {
    assertThatThrownBy(() -> waterService.acceptReports(List.of(999L)))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void acceptReports_NotLoggedIn_ThrowsException() {
    Mockito.when(userService.currentUser()).thenReturn(null);
    assertThatThrownBy(() -> waterService.acceptReports(List.of(1L)))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Log-in is required");
  }

  @Test
  void restoreFlavour_NotFound_ThrowsException() {
    assertThatThrownBy(() -> waterService.restoreFlavour(999L))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void renameFlavour_AlreadyExists_ThrowsException() {
    waterService.createFlavour("Apple");
    WaterFlavour orange = waterService.createFlavour("Orange");

    assertThatThrownBy(() -> waterService.renameFlavour(orange.getId().longValue(), "Apple"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("already exists");
  }

  @Test
  void defineState_InvalidPercentage_ThrowsConstraintViolationException() {
    WaterFlavour flavour = waterService.createFlavour("Apple");
    WaterStateDto dto = new WaterStateDto()
        .flavour(new WaterFlavourDto().id(flavour.getId().longValue()).name("Apple"))
        .waterLevel(150) // Invalid: > 100
        .emptyGallons(5)
        .fullGallons(10);

    assertThatThrownBy(() -> waterService.defineState(dto))
        .isInstanceOf(ConstraintViolationException.class);
  }
}
