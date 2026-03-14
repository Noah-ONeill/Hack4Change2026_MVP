package org.resourcebridge.api.config;

import lombok.RequiredArgsConstructor;
import org.resourcebridge.api.entity.*;
import org.resourcebridge.api.enums.*;
import org.resourcebridge.api.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final OrganizationRepository organizationRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository;
    private final NeedRepository needRepository;
    private final DonationRepository donationRepository;
    private final AnnouncementRepository announcementRepository;
    private final TransferRepository transferRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Only seed if DB is empty
        if (organizationRepository.count() > 0) return;

        // ── ORGANIZATIONS ──────────────────────────────────────────
        Organization nazareth    = org("House of Nazareth",            "Emergency Shelter",             "75 Albert St, Moncton, NB",          "Men, women, families",                "info@maisonnazareth.ca",       "506-858-5702");
        Organization harvest     = org("Harvest House Atlantic",        "Emergency Shelter",             "182 High St, Moncton, NB",           "Men and women",                       "info@harvesthouseatlantic.org","506-388-4357");
        Organization crossroads  = org("Crossroads for Women",          "Transition Shelter",            "Moncton, NB (confidential)",         "Women and children fleeing violence",  "adminfo@crossroadsforwomen.ca","506-853-0811");
        Organization ywca        = org("YWCA Moncton",                  "Supportive Housing",            "135 Kendra St, Moncton, NB",         "Women and children, ages 16+",        "info@ywcamoncton.com",         "506-855-4349");
        Organization johnHoward  = org("John Howard Society of SE NB",  "Transitional Housing",          "15 Flanders Court, Moncton, NB",     "Men (justice-involved)",              "info@johnhowardsenb.com",      "506-854-3499");
        Organization peterMckee  = org("Peter McKee Community Food Centre","Food Bank + Community Kitchen","66 Capitol St, Moncton, NB",        "General public, low-income",          "info@petermckeecfc.ca",        "506-383-4281");
        Organization secondMile  = org("Second Mile Food Bank",         "Food Bank",                     "243 Lewisville Rd, Moncton, NB",     "Dieppe area and part of Moncton",     "info@secondmilefoodbank.ca",   "506-857-9121");
        Organization salvation   = org("Salvation Army Community Services","Community Services",         "32 King St, Moncton, NB",            "Families and individuals in crisis",  "moncton@salvationarmy.ca",     "506-389-9901");
        Organization svdp        = org("St. Vincent de Paul Society",   "Clothing + Household Depot",    "113 Norwood Ave, Moncton, NB",       "General public in need",              "svdp.moncton@gmail.com",       "506-857-2088");
        Organization humanity    = org("The Humanity Project",          "Meals + Outreach",              "449 St George St, Moncton, NB",      "Anyone in need",                      "TheHumanityProjectNB@gmail.com","506-382-6840");

        // ── ITEMS ──────────────────────────────────────────────────
        Item soup      = item("Canned Soup",        "Chicken noodle, tomato varieties",  "cans",    true);
        Item beans     = item("Canned Beans",        "Black, kidney, chickpea",           "cans",    true);
        Item milk      = item("Shelf-Stable Milk",   "1L cartons, UHT",                   "cartons", true);
        Item granola   = item("Granola Bars",        "Individually wrapped variety pack", "boxes",   true);
        Item formula   = item("Baby Formula",        "Powdered, 0–12 months",             "cans",    true);
        Item toothpaste= item("Toothpaste",          "Standard tube",                     "tubes",   false);
        Item tshirt    = item("T-Shirts",            "Mixed sizes, men and women",        "pieces",  false);
        Item blanket   = item("Blankets",            "Fleece or wool",                    "pieces",  false);
        Item shampoo   = item("Shampoo",             "250ml bottle",                      "bottles", false);
        Item jeans     = item("Jeans",               "Mixed sizes",                       "pieces",  false);

        // ── DEMO USERS ─────────────────────────────────────────────
        user("Sarah Coordinator", "coordinator@resourcebridge.ca", "demo1234", Role.COORDINATOR, nazareth);
        user("James Staff",       "staff@resourcebridge.ca",       "demo1234", Role.STAFF,       nazareth);
        user("Marie Lefebvre",    "marie@harvesthouse.ca",         "demo1234", Role.STAFF,       harvest);
        user("Tom Walsh",         "tom@crossroads.ca",             "demo1234", Role.STAFF,       crossroads);

        // ── INVENTORY ──────────────────────────────────────────────
        // House of Nazareth — some items expiring soon
        inventory(nazareth, soup,      24, LocalDate.now().plusDays(5));   // expiring in 5 days!
        inventory(nazareth, blanket,   12, null);
        inventory(nazareth, toothpaste,30, null);
        inventory(nazareth, milk,       8, LocalDate.now().plusDays(3));   // expiring in 3 days!

        // Harvest House
        inventory(harvest, beans,     18, LocalDate.now().plusDays(60));
        inventory(harvest, tshirt,    20, null);
        inventory(harvest, shampoo,   15, null);

        // Crossroads for Women
        inventory(crossroads, formula,  6, LocalDate.now().plusDays(4));   // expiring in 4 days!
        inventory(crossroads, granola, 10, LocalDate.now().plusDays(20));

        // Peter McKee Food Centre
        inventory(peterMckee, soup,   50, LocalDate.now().plusDays(90));
        inventory(peterMckee, beans,  40, LocalDate.now().plusDays(120));

        // ── NEEDS ──────────────────────────────────────────────────
        need(crossroads,  formula,    12, Urgency.CRITICAL);   // women's shelter needs baby formula urgently
        need(nazareth,    blanket,    20, Urgency.CRITICAL);   // emergency shelter — winter
        need(harvest,     soup,       30, Urgency.HIGH);
        need(ywca,        formula,     8, Urgency.HIGH);
        need(johnHoward,  tshirt,     15, Urgency.HIGH);
        need(humanity,    granola,    24, Urgency.MEDIUM);
        need(salvation,   toothpaste, 50, Urgency.MEDIUM);
        need(secondMile,  milk,       20, Urgency.MEDIUM);
        need(svdp,        jeans,      10, Urgency.LOW);
        need(nazareth,    shampoo,    20, Urgency.LOW);

        // ── DONATIONS ──────────────────────────────────────────────
        Donation d1 = donation("Marie Tremblay",  "marie.t@gmail.com",     blanket,    15, null,                              DonationStatus.OFFERED);
        Donation d2 = donation("John Smith",       "john.s@outlook.com",    soup,       24, LocalDate.now().plusDays(60),      DonationStatus.OFFERED);
        Donation d3 = donation("Isabelle Roy",     "isabelle@hotmail.com",  formula,     6, LocalDate.now().plusDays(30),      DonationStatus.OFFERED);
        Donation d4 = donation("Robert Chen",      "rchen@gmail.com",       tshirt,     20, null,                              DonationStatus.ASSIGNED);
        Donation d5 = donation("Nadia Williams",   "nadia.w@gmail.com",     granola,    16, LocalDate.now().plusDays(45),      DonationStatus.DELIVERED);
        Donation d6 = donation("François Leblanc", "fleblanc@gmail.com",    toothpaste, 35, null,                              DonationStatus.RECEIVED);

        // ── ANNOUNCEMENTS ──────────────────────────────────────────
        announcement(nazareth,   milk,    8,  AnnouncementType.EXPIRY,  "Shelf-stable milk expiring in 3 days — can any org use these?");
        announcement(crossroads, formula, 6,  AnnouncementType.URGENT,  "Critically low on baby formula — urgent need for 12+ cans");
        announcement(peterMckee, soup,   20,  AnnouncementType.SURPLUS, "Surplus canned soup available — first come first served");
        announcement(harvest,    tshirt, 15,  AnnouncementType.SURPLUS, "Clothing surplus: T-shirts in all sizes available for pickup");
        announcement(nazareth,   soup,    8,  AnnouncementType.EXPIRY,  "Canned soup expiring soon — redistributing to shelters in need");

        // ── TRANSFERS ──────────────────────────────────────────────
        User coordinator = userRepository.findByEmail("coordinator@resourcebridge.ca").orElseThrow();
        transfer(d4, johnHoward, 15, coordinator, TransferStatus.IN_TRANSIT);
        transfer(d5, humanity,   16, coordinator, TransferStatus.COMPLETED);
        transfer(d6, salvation,  35, coordinator, TransferStatus.COMPLETED);

        System.out.println("✅ ResourceBridge seed data loaded successfully!");
    }

    // ── HELPERS ────────────────────────────────────────────────────

    private Organization org(String name, String type, String address, String pop, String email, String phone) {
        Organization o = new Organization();
        o.setName(name); o.setType(type); o.setAddress(address);
        o.setPopulationServed(pop); o.setContactEmail(email); o.setContactPhone(phone);
        return organizationRepository.save(o);
    }

    private Item item(String name, String desc, String unit, boolean expiryRelevant) {
        Item i = new Item();
        i.setName(name); i.setDescription(desc); i.setUnit(unit); i.setExpiryRelevant(expiryRelevant);
        return itemRepository.save(i);
    }

    private void user(String name, String email, String password, Role role, Organization org) {
        User u = new User();
        u.setName(name); u.setEmail(email);
        u.setPassword(passwordEncoder.encode(password));
        u.setRole(role); u.setOrganization(org);
        userRepository.save(u);
    }

    private void inventory(Organization org, Item item, int qty, LocalDate expiry) {
        Inventory inv = new Inventory();
        inv.setOrganization(org); inv.setItem(item);
        inv.setQuantity(qty); inv.setExpiryDate(expiry);
        inventoryRepository.save(inv);
    }

    private void need(Organization org, Item item, int qty, Urgency urgency) {
        Need n = new Need();
        n.setOrganization(org); n.setItem(item);
        n.setQuantityNeeded(qty); n.setUrgency(urgency);
        n.setFulfilled(false); n.setCreatedAt(LocalDateTime.now());
        needRepository.save(n);
    }

    private Donation donation(String donorName, String donorEmail, Item item, int qty, LocalDate expiry, DonationStatus status) {
        Donation d = new Donation();
        d.setDonorName(donorName); d.setDonorEmail(donorEmail);
        d.setItem(item); d.setQuantity(qty);
        d.setExpiryDate(expiry); d.setStatus(status);
        d.setCreatedAt(LocalDateTime.now());
        return donationRepository.save(d);
    }

    private void announcement(Organization org, Item item, int qty, AnnouncementType type, String message) {
        Announcement a = new Announcement();
        a.setOrganization(org); a.setItem(item);
        a.setQuantity(qty); a.setType(type);
        a.setMessage(message); a.setCreatedAt(LocalDateTime.now());
        announcementRepository.save(a);
    }

    private void transfer(Donation donation, Organization toOrg, int qty, User coordinator, TransferStatus status) {
        Transfer t = new Transfer();
        t.setDonation(donation); t.setToOrganization(toOrg);
        t.setQuantityAssigned(qty); t.setCoordinator(coordinator);
        t.setStatus(status); t.setCreatedAt(LocalDateTime.now());
        transferRepository.save(t);
    }
}
