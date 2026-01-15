package org.revizit.service;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
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
import jakarta.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
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

  @MockitoBean
  private Clock clock;

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
    Mockito.when(userService.isUserAdmin(adminUser)).thenReturn(true);

    Mockito.when(clock.getZone()).thenReturn(ZoneId.of("UTC"));
    Mockito.when(clock.instant()).thenAnswer(_ -> Instant.now());
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
    assertThat(updatedReport.getApprovedBy().getId()).isEqualTo(adminUser.getId());
  }

  @Test
  void acceptReports_multipleReportsAreAccepted_Success() {
    WaterFlavour flavour = waterService.createFlavour("Apple");
    WaterFlavour flavour2 = waterService.createFlavour("Orange");
    waterService.defineState(new WaterStateDto()
        .flavour(new WaterFlavourDto().id(flavour.getId().longValue()).name("Apple"))
        .waterLevel(100)
        .emptyGallons(0)
        .fullGallons(10));

    final List<WaterReport> reports = new ArrayList<>();
    final List<WaterReportDto> reportDtos = List.of(
        new WaterReportDto().kind(WaterReportKind.PERCENTAGE).value(70),
        new WaterReportDto().kind(WaterReportKind.PERCENTAGE).value(50),
        new WaterReportDto().kind(WaterReportKind.PERCENTAGE).value(20),
        new WaterReportDto().kind(WaterReportKind.PERCENTAGE).value(10),
        new WaterReportDto().kind(WaterReportKind.SWAP).flavourId(flavour2.getId().longValue()),
        new WaterReportDto().kind(WaterReportKind.PERCENTAGE).value(80));
    for (WaterReportDto dto : reportDtos) {
      reports.add(waterService.registerReport(dto));
    }

    final List<Long> reportIds = reports.stream()
        .map(it -> it.getId().longValue())
        .toList();
    WaterState waterState = waterService.acceptReports(reportIds);
    assertThat(waterState)
        .returns(80, WaterState::getCurrPct)
        .returns(1, WaterState::getEmptyCnt)
        .returns(9, WaterState::getFullCnt)
        .returns("Orange", it -> it.getReport().getFlavour().getName());
    // SIZE+1 including the defineState
    assertThat(waterReportRepository.findAll()).hasSize(reportDtos.size() + 1);
    assertThat(waterReportRepository.findAll()).allSatisfy(
        it -> assertThat(it.getApprovedAt()).isNotNull());
  }

  @Test
  void acceptReports_constraintViolatingReport_throwsException_andPreservesState() {
    WaterFlavour flavour = waterService.createFlavour("Apple");
    WaterFlavour flavour2 = waterService.createFlavour("Orange");

    // there is only one full gallon...
    waterService.defineState(new WaterStateDto()
        .flavour(new WaterFlavourDto().id(flavour.getId().longValue()).name("Apple"))
        .waterLevel(100)
        .emptyGallons(0)
        .fullGallons(1));

    final List<WaterReport> reports = new ArrayList<>();
    final List<WaterReportDto> reportDtos = List.of(
        new WaterReportDto().kind(WaterReportKind.PERCENTAGE).value(70),
        new WaterReportDto().kind(WaterReportKind.PERCENTAGE).value(50),
        new WaterReportDto().kind(WaterReportKind.PERCENTAGE).value(20),
        new WaterReportDto().kind(WaterReportKind.PERCENTAGE).value(10),
        new WaterReportDto().kind(WaterReportKind.SWAP).flavourId(flavour2.getId().longValue()),
        new WaterReportDto().kind(WaterReportKind.PERCENTAGE).value(80),
        new WaterReportDto().kind(WaterReportKind.PERCENTAGE).value(60),
        new WaterReportDto().kind(WaterReportKind.PERCENTAGE).value(40),
        new WaterReportDto().kind(WaterReportKind.PERCENTAGE).value(20),
        new WaterReportDto().kind(WaterReportKind.PERCENTAGE).value(0),
        // ...but we are trying to submit a second swap, which is impossible:
        new WaterReportDto().kind(WaterReportKind.SWAP).flavourId(flavour2.getId().longValue()),
        new WaterReportDto().kind(WaterReportKind.PERCENTAGE).value(90));
    for (WaterReportDto dto : reportDtos) {
      reports.add(waterService.registerReport(dto));
    }

    final List<Long> reportIds = reports.stream()
        .map(it -> it.getId().longValue())
        .toList();
    assertThatThrownBy(() -> waterService.acceptReports(reportIds))
        .isInstanceOf(ConstraintViolationException.class)
        .hasMessageContaining("The number of full gallons cannot be lower than 0");

    assertThat(waterService.currentState())
        .returns(100, WaterState::getCurrPct)
        .returns(1, WaterState::getFullCnt)
        .returns(0, WaterState::getEmptyCnt)
        .returns("Apple", it -> it.getReport().getFlavour().getName());
    assertThat(waterStateRepository.findAll()).hasSize(1);
  }

  @Test
  void acceptReports_ReportingRefillWhenNoGallonsAreEmpty_isRejected_andStateIsPreserved() {
    WaterFlavour flavour = waterService.createFlavour("Apple");
    waterService.defineState(new WaterStateDto()
        .flavour(new WaterFlavourDto().id(flavour.getId().longValue()).name("Apple"))
        .waterLevel(100)
        .emptyGallons(0)
        .fullGallons(10));
    WaterReport report =
        waterService.registerReport(new WaterReportDto().kind(WaterReportKind.REFILL));
    final var waterState = waterService.acceptReports(List.of(report.getId().longValue()));
    assertThat(waterState)
        .returns(waterService.currentState().getId(), WaterState::getId)
        .returns(100, WaterState::getCurrPct)
        .returns(10, WaterState::getFullCnt)
        .returns(0, WaterState::getEmptyCnt);
    // no extraneous state was persisted:
    assertThat(waterStateRepository.findAll()).hasSize(1);

    report = waterReportRepository.findById(report.getId()).orElseThrow();
    assertThat(report)
        .satisfies(it -> assertThat(it.getApprovedAt()).isNull())
        .satisfies(it -> assertThat(it.getApprovedBy()).isNull())
        .satisfies(it -> assertThat(it.getRejectedAt()).isNotNull())
        .satisfies(it -> assertThat(it.getRejectedBy().getId()).isEqualTo(adminUser.getId()));
  }

  @Test
  void registerReport_attemptingToRegisterWhenStateIsYetUndefined_ThrowsException() {
    final var dto = new WaterReportDto()
        .kind(WaterReportKind.PERCENTAGE)
        .value(50);
    assertThatThrownBy(() -> waterService.registerReport(dto))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Initial water state is not yet defined");
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
    assertThat(updatedReport.getRejectedBy().getId()).isEqualTo(adminUser.getId());
    assertThat(waterService.currentState().getCurrPct()).isEqualTo(100);
    assertThat(waterService.getPendingReports()).isEmpty();
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
    WaterReport report = waterService.registerReport(new WaterReportDto()
        .kind(WaterReportKind.PERCENTAGE)
        .value(90));
    waterService.acceptReports(List.of(report.getId().longValue()));

    WaterState state2 = waterService.currentState();
    assertThat(state2.getCurrPct()).isEqualTo(90);
    assertThat(state2.getCreatedAt()).isAfterOrEqualTo(state1.getCreatedAt());
  }

  @Test
  void stateHistory_ReturnsCorrectRange() {
    WaterFlavour flavour = waterService.createFlavour("Apple");

    // T0: Initial state at 10:00
    Instant t0 = Instant.parse("2025-01-01T10:00:00Z");
    Mockito.when(clock.instant()).thenReturn(t0);
    waterService.defineState(new WaterStateDto()
        .flavour(new WaterFlavourDto().id(flavour.getId().longValue()).name("Apple"))
        .waterLevel(100)
        .emptyGallons(0)
        .fullGallons(10));

    // T1: Report at 11:00, accepted at 11:30
    Instant t1Report = Instant.parse("2025-01-01T11:00:00Z");
    Mockito.when(clock.instant()).thenReturn(t1Report);
    WaterReport report1 = waterService.registerReport(new WaterReportDto()
        .kind(WaterReportKind.PERCENTAGE)
        .value(80));

    Instant t1Accept = Instant.parse("2025-01-01T11:30:00Z");
    Mockito.when(clock.instant()).thenReturn(t1Accept);
    waterService.acceptReports(List.of(report1.getId().longValue()));

    // T2: Report at 12:00, accepted at 12:30
    Instant t2Report = Instant.parse("2025-01-01T12:00:00Z");
    Mockito.when(clock.instant()).thenReturn(t2Report);
    WaterReport report2 = waterService.registerReport(new WaterReportDto()
        .kind(WaterReportKind.PERCENTAGE)
        .value(60));

    Instant t2Accept = Instant.parse("2025-01-01T12:30:00Z");
    Mockito.when(clock.instant()).thenReturn(t2Accept);
    waterService.acceptReports(List.of(report2.getId().longValue()));

    // Check history ranges
    LocalDateTime t1Dt = LocalDateTime.ofInstant(t1Report, ZoneId.of("UTC"));
    LocalDateTime t2Dt = LocalDateTime.ofInstant(t2Report, ZoneId.of("UTC"));

    List<WaterState> history = waterService.stateHistory(t1Dt.minusSeconds(1), t2Dt.plusSeconds(1));
    // Should include states from t1Accept and t2Accept
    assertThat(history).hasSize(2);
    assertThat(history.get(0).getCurrPct()).isEqualTo(80);
    assertThat(history.get(1).getCurrPct()).isEqualTo(60);

    // Check range excluding the first one
    List<WaterState> history2 = waterService.stateHistory(t1Dt.plusMinutes(1), t2Dt.plusSeconds(1));
    assertThat(history2).hasSize(1);
    assertThat(history2.getFirst().getCurrPct()).isEqualTo(60);
  }

  @Test
  void defineState_NotAdmin_ThrowsException() {
    Mockito.when(userService.isCurrentUserAdmin()).thenReturn(false);
    final var dto = new WaterStateDto();
    assertThatThrownBy(() -> waterService.defineState(dto))
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
    final var ids = List.of(999L);
    assertThatThrownBy(() -> waterService.acceptReports(ids))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void acceptReports_attemptingToAcceptAlreadyAcceptedReport_ThrowsException() {
    final var flavour = waterService.createFlavour("Apple");
    WaterStateDto dto = new WaterStateDto()
        .flavour(new WaterFlavourDto().id(flavour.getId().longValue()).name("Apple"))
        .waterLevel(80)
        .emptyGallons(1)
        .fullGallons(2);
    waterService.defineState(dto);
    WaterReport report = waterService.registerReport(new WaterReportDto()
        .kind(WaterReportKind.PERCENTAGE)
        .value(50));
    final var reportId = List.of(report.getId().longValue());
    waterService.acceptReports(reportId);
    assertThat(waterStateRepository.findAll()).hasSize(2);
    assertThat(waterReportRepository.findAll()).hasSize(2);
    assertThat(waterService.currentState())
        .returns(50, WaterState::getCurrPct)
        .returns(2, WaterState::getFullCnt)
        .returns(1, WaterState::getEmptyCnt);

    assertThatThrownBy(() -> waterService.acceptReports(reportId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("One or more reports are not pending");

    // check state and report count is unchanged:
    assertThat(waterStateRepository.findAll()).hasSize(2);
    assertThat(waterReportRepository.findAll()).hasSize(2);
    assertThat(waterService.currentState())
        .returns(50, WaterState::getCurrPct)
        .returns(2, WaterState::getFullCnt)
        .returns(1, WaterState::getEmptyCnt);
  }

  @Test
  void acceptReports_NotLoggedIn_ThrowsException() {
    Mockito.when(userService.currentUser()).thenReturn(null);
    final var ids = List.of(1L);
    assertThatThrownBy(() -> waterService.acceptReports(ids))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Log-in is required");
  }

  @Test
  void rollbackToPreviousState_whenHeadStateIdIsSubmitted_succeedsWithSingleStateRevert() {
    final var stateAfterAccepts = initAndAcceptReportsForRollbackTest();

    final var headStateId = waterService.currentState().getId().longValue();
    assertThat(headStateId).isEqualTo(stateAfterAccepts.getId().longValue());

    final var newState = waterService.rollbackToPreviousState(headStateId);
    assertThat(newState)
        .returns(100, WaterState::getCurrPct)
        .returns(2, WaterState::getEmptyCnt)
        .returns(1, WaterState::getFullCnt);
    final var newStateId = newState.getId().longValue();
    assertThat(waterService.stateHistory(null, null).getLast().getId().longValue()).isEqualTo(
        newStateId);
    assertPendingReportList(List.of(ReportAssert.percentage(80)));
  }

  @Test
  void rollbackToPreviousState_whenNonHeadStateIdIsSubmitted_succeedsWithMultipleStateRevert() {
    final var stateAfterAccepts = initAndAcceptReportsForRollbackTest();
    final var headStateId = waterService.currentState().getId().longValue();
    assertThat(headStateId).isEqualTo(stateAfterAccepts.getId().longValue());

    final var targetStateId = headStateId - 1;
    final var newState = waterService.rollbackToPreviousState(targetStateId);
    assertThat(newState)
        .returns(20, WaterState::getCurrPct)
        .returns(1, WaterState::getEmptyCnt)
        .returns(2, WaterState::getFullCnt);
    final var newStateId = newState.getId().longValue();
    assertThat(waterService.stateHistory(null, null).getLast().getId().longValue()).isEqualTo(
        newStateId);
    assertPendingReportList(List.of(ReportAssert.swap(), ReportAssert.percentage(80)));
  }

  @Test
  void rrollbackToPreviousState_whenInvalidStateId_failsWithNotFoundException() {
    final var stateAfterAccepts = initAndAcceptReportsForRollbackTest();
    final var headStateId = waterService.currentState().getId().longValue();
    assertThat(headStateId).isEqualTo(stateAfterAccepts.getId().longValue());

    assertThatThrownBy(() -> waterService.rollbackToPreviousState(1_337L))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void rollbackToPreviousState_rollbackToInitialState_succeeds() {
    final var stateAfterAccepts = initAndAcceptReportsForRollbackTest();
    final var headStateId = waterService.currentState().getId().longValue();
    assertThat(headStateId).isEqualTo(stateAfterAccepts.getId().longValue());

    final var initialStateId = waterService.stateHistory(null, null).getFirst().getId().longValue();
    final var newState = waterService.rollbackToPreviousState(initialStateId);
    assertThat(newState).isNull();
    assertThat(waterService.currentState()).isNull();
    assertPendingReportList(List.of(
        ReportAssert.percentage(80), // <-- initial state
        ReportAssert.percentage(70),
        ReportAssert.percentage(50),
        ReportAssert.percentage(20),
        ReportAssert.swap(),
        ReportAssert.percentage(80)));
  }

  @Test
  void rollbackToPreviousState_whenUserIsNotAdmin_fails() {
    final var stateAfterAccepts = initAndAcceptReportsForRollbackTest();
    final var headStateId = waterService.currentState().getId().longValue();
    assertThat(headStateId).isEqualTo(stateAfterAccepts.getId().longValue());

    Mockito.when(userService.isCurrentUserAdmin()).thenReturn(false);
    Mockito.when(userService.currentUser()).thenReturn(regularUser);
    Mockito.when(userService.isUserAdmin(regularUser)).thenReturn(false);

    assertThatThrownBy(() -> waterService.rollbackToPreviousState(headStateId))
        .isInstanceOf(NotAuthorisedException.class);
  }

  private WaterState initAndAcceptReportsForRollbackTest() {
    final var flavour = waterService.createFlavour("Apple");
    WaterStateDto dto = new WaterStateDto()
        .flavour(new WaterFlavourDto().id(flavour.getId().longValue()).name("Apple"))
        .waterLevel(80)
        .emptyGallons(1)
        .fullGallons(2);
    waterService.defineState(dto);
    final List<WaterReportDto> reportDtos = List.of(
        new WaterReportDto().kind(WaterReportKind.PERCENTAGE).value(70),
        new WaterReportDto().kind(WaterReportKind.PERCENTAGE).value(50),
        new WaterReportDto().kind(WaterReportKind.PERCENTAGE).value(20),
        new WaterReportDto().kind(WaterReportKind.SWAP).flavourId(flavour.getId().longValue()),
        new WaterReportDto().kind(WaterReportKind.PERCENTAGE).value(80));
    final var ids = new ArrayList<Long>(reportDtos.size());
    reportDtos.forEach(
        reportDto -> ids.add(waterService.registerReport(reportDto).getId().longValue()));
    return waterService.acceptReports(ids);
  }

  sealed interface ReportAssert {

    static ReportAssert percentage(int value) {
      return new Percentage(value);
    }

    static ReportAssert refill() {
      return new Refill();
    }

    static ReportAssert swap() {
      return new Swap();
    }

    void check(WaterReport report);

    record Percentage(int value) implements ReportAssert {
      @Override
      public void check(WaterReport report) {
        assertThat(report)
            .returns(value, WaterReport::getVal)
            .returns(ReportType.SET_PERCENTAGE, WaterReport::getKind);
      }
    }


    record Refill() implements ReportAssert {

      @Override
      public void check(WaterReport report) {
        assertThat(report).returns(ReportType.BALLOON_REFILL, WaterReport::getKind);
      }
    }


    record Swap() implements ReportAssert {
      @Override
      public void check(WaterReport report) {
        assertThat(report).returns(ReportType.BALLOON_CHANGE, WaterReport::getKind);
      }
    }

  }

  private void assertPendingReportList(List<ReportAssert> reportAsserts) {
    final var pendingReports = waterService.getPendingReports();
    assertThat(pendingReports).hasSize(reportAsserts.size());
    for (int i = 0; i < reportAsserts.size(); i++) {
      final var report = pendingReports.get(i);
      final var reportAssert = reportAsserts.get(i);
      reportAssert.check(report);
    }
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
    final var flavourId = orange.getId().longValue();
    assertThatThrownBy(() -> waterService.renameFlavour(flavourId, "Apple"))
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
