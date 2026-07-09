package com.yashconsulting.eams.notification;

import com.yashconsulting.eams.BaseIntegrationTest;
import com.yashconsulting.eams.asset.entity.Asset;
import com.yashconsulting.eams.asset.entity.AssetStatus;
import com.yashconsulting.eams.asset.repository.AssetRepository;
import com.yashconsulting.eams.assignment.dto.AssetAssignmentCreateRequest;
import com.yashconsulting.eams.assignment.service.AssetAssignmentService;
import com.yashconsulting.eams.inventory.dto.SparePartCreateRequest;
import com.yashconsulting.eams.inventory.dto.StockTransactionCreateRequest;
import com.yashconsulting.eams.inventory.entity.SparePart;
import com.yashconsulting.eams.inventory.entity.StockTransactionType;
import com.yashconsulting.eams.inventory.repository.SparePartRepository;
import com.yashconsulting.eams.inventory.service.SparePartService;
import com.yashconsulting.eams.inventory.service.StockTransactionService;
import com.yashconsulting.eams.maintenance.entity.MaintenancePlan;
import com.yashconsulting.eams.maintenance.entity.MaintenanceStatus;
import com.yashconsulting.eams.maintenance.repository.MaintenancePlanRepository;
import com.yashconsulting.eams.maintenance.service.MaintenancePlanService;
import com.yashconsulting.eams.notification.entity.Notification;
import com.yashconsulting.eams.notification.repository.NotificationRepository;
import com.yashconsulting.eams.purchase.dto.PurchaseOrderCreateRequest;
import com.yashconsulting.eams.purchase.dto.PurchaseOrderUpdateRequest;
import com.yashconsulting.eams.purchase.entity.PurchaseOrder;
import com.yashconsulting.eams.purchase.entity.PurchaseOrderStatus;
import com.yashconsulting.eams.purchase.repository.PurchaseOrderRepository;
import com.yashconsulting.eams.purchase.service.PurchaseOrderService;
import com.yashconsulting.eams.security.Role;
import com.yashconsulting.eams.user.entity.User;
import com.yashconsulting.eams.user.repository.UserRepository;
import com.yashconsulting.eams.vendor.entity.Vendor;
import com.yashconsulting.eams.vendor.repository.VendorRepository;
import com.yashconsulting.eams.workorder.dto.WorkOrderCreateRequest;
import com.yashconsulting.eams.workorder.dto.WorkOrderUpdateRequest;
import com.yashconsulting.eams.workorder.entity.WorkOrder;
import com.yashconsulting.eams.workorder.entity.WorkOrderStatus;
import com.yashconsulting.eams.workorder.repository.WorkOrderRepository;
import com.yashconsulting.eams.workorder.service.WorkOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class NotificationEventIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private AssetAssignmentService assetAssignmentService;

    @Autowired
    private MaintenancePlanRepository maintenancePlanRepository;

    @Autowired
    private MaintenancePlanService maintenancePlanService;

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private WorkOrderService workOrderService;

    @Autowired
    private SparePartRepository sparePartRepository;

    @Autowired
    private SparePartService sparePartService;

    @Autowired
    private StockTransactionService stockTransactionService;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    private User adminUser;
    private User managerUser;
    private User regularUser;
    private Asset seededAsset;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
        workOrderRepository.deleteAll();
        maintenancePlanRepository.deleteAll();
        purchaseOrderRepository.deleteAll();
        sparePartRepository.deleteAll();
        vendorRepository.deleteAll();
        assetRepository.deleteAll();
        userRepository.deleteAll();

        // Seed users
        adminUser = userRepository.save(User.builder()
                .firstName("Admin")
                .lastName("Eams")
                .email("admin@eams.com")
                .password("Password@123")
                .role(Role.ADMIN)
                .active(true)
                .build());

        managerUser = userRepository.save(User.builder()
                .firstName("Manager")
                .lastName("Eams")
                .email("manager@eams.com")
                .password("Password@123")
                .role(Role.MANAGER)
                .active(true)
                .build());

        regularUser = userRepository.save(User.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@eams.com")
                .password("Password@123")
                .role(Role.USER)
                .active(true)
                .build());

        // Seed asset
        seededAsset = assetRepository.save(Asset.builder()
                .assetCode("ASSET-TEST-1")
                .assetName("Test Laptop")
                .status(AssetStatus.AVAILABLE)
                .purchaseDate(LocalDate.now())
                .purchaseCost(BigDecimal.valueOf(1200))
                .active(true)
                .build());
    }

    private void waitForNotifications(int expectedCount) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < 3000) {
            if (notificationRepository.count() >= expectedCount) {
                return;
            }
            Thread.sleep(50);
        }
    }

    @Test
    void whenAssignAsset_thenNotificationGenerated() throws Exception {
        AssetAssignmentCreateRequest request = AssetAssignmentCreateRequest.builder()
                .assetId(seededAsset.getId())
                .employeeId(regularUser.getId())
                .assignedDate(LocalDate.now())
                .expectedReturnDate(LocalDate.now().plusDays(30))
                .build();

        assetAssignmentService.assignAsset(request);

        waitForNotifications(1);

        List<Notification> notifications = notificationRepository.findAll();
        assertEquals(1, notifications.size());
        Notification n = notifications.get(0);
        assertEquals("Asset Assigned", n.getTitle());
        assertEquals("Asset ASSET-TEST-1 has been assigned to you.", n.getMessage());
        assertEquals(regularUser.getId(), n.getRecipientUserId());
    }

    @Test
    void whenMaintenanceOverdueCheck_thenNotificationGeneratedForAdminsAndManagers() throws Exception {
        // Create an overdue maintenance plan
        MaintenancePlan plan = maintenancePlanRepository.save(MaintenancePlan.builder()
                .planCode("PLAN-OVERDUE")
                .planName("Test Overdue Plan")
                .assetId(seededAsset.getId())
                .nextMaintenanceDate(LocalDate.now().minusDays(5)) // overdue
                .status(MaintenanceStatus.SCHEDULED)
                .frequencyValue(1)
                .frequencyType(com.yashconsulting.eams.maintenance.entity.FrequencyType.MONTHLY)
                .active(true)
                .build());

        // Run check
        maintenancePlanService.getMaintenanceDashboard();

        // 2 notifications: 1 for adminUser, 1 for managerUser
        waitForNotifications(2);

        List<Notification> notifications = notificationRepository.findAll();
        assertEquals(2, notifications.size());
        assertTrue(notifications.stream().anyMatch(n -> n.getRecipientUserId().equals(adminUser.getId()) && n.getTitle().equals("Maintenance Overdue")));
        assertTrue(notifications.stream().anyMatch(n -> n.getRecipientUserId().equals(managerUser.getId()) && n.getTitle().equals("Maintenance Overdue")));
    }

    @Test
    void whenWorkOrderWorkflow_thenNotificationsGenerated() throws Exception {
        // 1. Work Order Created
        WorkOrderCreateRequest request = WorkOrderCreateRequest.builder()
                .workOrderNumber("WO-AUTO-001")
                .title("Fix Laptop screen")
                .description("Screen is flickering")
                .assetId(seededAsset.getId())
                .assignedTechnician("Jane Doe") // matches regularUser's name Jane Doe
                .priority(com.yashconsulting.eams.workorder.entity.WorkOrderPriority.MEDIUM)
                .status(WorkOrderStatus.OPEN)
                .build();

        var woResponse = workOrderService.createWorkOrder(request);

        waitForNotifications(1);

        List<Notification> notifications = notificationRepository.findAll();
        assertEquals(1, notifications.size());
        assertEquals("Work Order Created", notifications.get(0).getTitle());
        assertEquals(regularUser.getId(), notifications.get(0).getRecipientUserId());

        // 2. Work Order Completed
        WorkOrderUpdateRequest updateRequest = WorkOrderUpdateRequest.builder()
                .status(WorkOrderStatus.COMPLETED)
                .build();

        workOrderService.updateWorkOrder(woResponse.getId(), updateRequest);

        waitForNotifications(2);

        notifications = notificationRepository.findAll();
        assertEquals(2, notifications.size());
        assertTrue(notifications.stream().anyMatch(n -> n.getTitle().equals("Work Order Completed")));
    }

    @Test
    void whenSparePartReachesLowStock_thenNotificationGenerated() throws Exception {
        // Create spare part with currentStock = 10, minimumStock = 5
        SparePartResponseResponse(sparePartService.createSparePart(SparePartCreateRequest.builder()
                .partNumber("PART-LOW-1")
                .partName("Screws")
                .currentStock(10)
                .minimumStock(5)
                .active(true)
                .build()));

        // Reduce stock to 4 (below 5 minimum)
        stockTransactionService.createStockTransaction(StockTransactionCreateRequest.builder()
                .sparePartId(sparePartRepository.findAll().get(0).getId())
                .transactionType(StockTransactionType.OUT)
                .quantity(6)
                .unitCost(BigDecimal.valueOf(1))
                .performedBy("Admin")
                .transactionDate(LocalDate.now())
                .build());

        // 2 low stock notifications: 1 for adminUser, 1 for managerUser
        waitForNotifications(2);

        List<Notification> notifications = notificationRepository.findAll();
        assertEquals(2, notifications.size());
        assertTrue(notifications.stream().anyMatch(n -> n.getTitle().equals("Low Stock Alert") && n.getRecipientUserId().equals(adminUser.getId())));
        assertTrue(notifications.stream().anyMatch(n -> n.getTitle().equals("Low Stock Alert") && n.getRecipientUserId().equals(managerUser.getId())));
    }

    @Test
    void whenPurchaseOrderApprovedAndReceived_thenNotificationsGenerated() throws Exception {
        Vendor vendor = vendorRepository.save(Vendor.builder()
                .vendorCode("VNDR-001")
                .vendorName("Acme Corp")
                .contactPerson("Bob")
                .email("bob@acme.com")
                .phone("123")
                .active(true)
                .build());

        // Create PO in DRAFT
        PurchaseOrderCreateRequest request = PurchaseOrderCreateRequest.builder()
                .poNumber("PO-AUTO-99")
                .vendorId(vendor.getId())
                .orderDate(LocalDate.now())
                .expectedDeliveryDate(LocalDate.now().plusDays(7))
                .remarks("Test auto PO")
                .build();

        var poResponse = purchaseOrderService.createPurchaseOrder(request);

        // Update to APPROVED
        purchaseOrderService.updatePurchaseOrderStatus(poResponse.getId(), PurchaseOrderUpdateRequest.builder()
                .status(PurchaseOrderStatus.APPROVED)
                .expectedDeliveryDate(LocalDate.now().plusDays(7))
                .active(true)
                .build());

        waitForNotifications(1);

        List<Notification> notifications = notificationRepository.findAll();
        assertEquals(1, notifications.size());
        assertEquals("Purchase Order Approved", notifications.get(0).getTitle());

        // Update to RECEIVED
        purchaseOrderService.updatePurchaseOrderStatus(poResponse.getId(), PurchaseOrderUpdateRequest.builder()
                .status(PurchaseOrderStatus.RECEIVED)
                .expectedDeliveryDate(LocalDate.now().plusDays(7))
                .active(true)
                .build());

        waitForNotifications(2);

        notifications = notificationRepository.findAll();
        assertEquals(2, notifications.size());
        assertTrue(notifications.stream().anyMatch(n -> n.getTitle().equals("Purchase Order Received")));
    }

    private void SparePartResponseResponse(Object o) {}
}
