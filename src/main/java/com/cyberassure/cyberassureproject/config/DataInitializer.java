package com.cyberassure.cyberassureproject.config;

import com.cyberassure.cyberassureproject.entity.*;
import com.cyberassure.cyberassureproject.repository.*;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

        private final RoleRepository roleRepository;
        private final UserRepository userRepository;
        private final CyberPolicyRepository policyRepository;
        private final PolicySubscriptionRepository subscriptionRepository;
        private final IncidentReportRepository incidentRepository;
        private final RiskAssessmentRepository riskAssessmentRepository;
        private final PasswordEncoder passwordEncoder;

        @Override
        public void run(String... args) {
                System.out.println("Starting Database Seeding...");
                seedRoles();
                seedAdmin();
                seedUsers();
                seedPolicies();
                seedCustomerData();
                System.out.println("Database Seeding Completed Successfully.");
        }

        private void seedRoles() {

                createRoleIfNotExists("ROLE_ADMIN", "System Administrator");
                createRoleIfNotExists("ROLE_CUSTOMER", "Customer User");
                createRoleIfNotExists("ROLE_UNDERWRITER", "Policy Approval Officer");
                createRoleIfNotExists("ROLE_CLAIMS_OFFICER", "Claims Processing Officer");
        }

        private void createRoleIfNotExists(String name, String desc) {
                if (!roleRepository.existsByRoleName(name)) {
                        roleRepository.save(
                                        Role.builder()
                                                        .roleName(name)
                                                        .roleDescription(desc)
                                                        .isActive(true)
                                                        .build());
                }
        }

        private void seedAdmin() {

                if (!userRepository.existsByEmail("admin@cyberassure.com")) {

                        Role adminRole = roleRepository
                                        .findByRoleName("ROLE_ADMIN") // IMPORTANT FIX
                                        .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found"));

                        User admin = User.builder()
                                        .fullName("System Administrator")
                                        .email("admin@cyberassure.com")
                                        .passwordHash(passwordEncoder.encode("Admin@123"))
                                        .companyName("CyberAssure Platform")
                                        .phoneNumber("9999999999")
                                        .role(adminRole)
                                        .accountStatus("ACTIVE")
                                        .createdAt(LocalDateTime.now())
                                        .build();

                        userRepository.save(admin);

                        System.out.println("Default ADMIN created.");
                }
        }

        private void seedUsers() {
                Role customerRole = roleRepository.findByRoleName("ROLE_CUSTOMER").orElseThrow();
                Role underwriterRole = roleRepository.findByRoleName("ROLE_UNDERWRITER").orElseThrow();
                Role claimsRole = roleRepository.findByRoleName("ROLE_CLAIMS_OFFICER").orElseThrow();

                if (userRepository.count() <= 1) { // Only admin exists
                        // Customers
                        userRepository.saveAll(List.of(
                                        createUser("Rajesh Kumar", "rajesh@techcorp.in", "9876543210", "TechCorp India",
                                                        "IT / SaaS", "Medium (50-250)", "123 Tech Park, Bangalore",
                                                        "https://techcorp.in", "CIN-U72900KA2020PTC123456", "10000000",
                                                        customerRole),
                                        createUser("Priya Sharma", "priya@globalhealth.in", "9123456780",
                                                        "Global Health Clinics", "Healthcare", "Small (1-50)", null,
                                                        null, null, null,
                                                        customerRole),
                                        createUser("Amit Patel", "amit@finsecure.in", "9988776655", "FinSecure Bank",
                                                        "Banking", "Large (250+)", null, null, null, null,
                                                        customerRole),
                                        createUser("Sneha Gupta", "sneha@edutech.in", "9876501234", "EduTech Solutions",
                                                        "Education", "Medium (50-250)", null, null, null, null,
                                                        customerRole)));

                        // Underwriters
                        userRepository.saveAll(List.of(
                                        createUser("Vikram Singh", "vikram@cyberassure.in", "9112223333",
                                                        "CyberAssure Internal", null, null, null, null, null, null,
                                                        underwriterRole),
                                        createUser("Neha Verma", "neha@cyberassure.in", "9223334444",
                                                        "CyberAssure Internal", null, null, null, null, null, null,
                                                        underwriterRole)));

                        // Claims Officers
                        userRepository.saveAll(List.of(
                                        createUser("Arjun Desai", "arjun@cyberassure.in", "9334445555",
                                                        "CyberAssure Internal", null, null, null, null, null, null,
                                                        claimsRole),
                                        createUser("Meera Reddy", "meera@cyberassure.in", "9445556666",
                                                        "CyberAssure Internal", null, null, null, null, null, null,
                                                        claimsRole)));

                        System.out.println("Default staff and customers seeded.");
                }
        }

        private User createUser(String name, String email, String phone, String company, String industry,
                        String companySize, String companyAddress, String companyWebsite, String registrationNumber,
                        String annualRevenue, Role role) {
                return User.builder()
                                .fullName(name)
                                .email(email)
                                .passwordHash(passwordEncoder.encode("Password@123"))
                                .companyName(company)
                                .industry(industry)
                                .companySize(companySize)
                                .companyAddress(companyAddress)
                                .companyWebsite(companyWebsite)
                                .registrationNumber(registrationNumber)
                                .annualRevenue(annualRevenue)
                                .phoneNumber(phone)
                                .role(role)
                                .accountStatus("ACTIVE")
                                .createdAt(LocalDateTime.now())
                                .build();
        }

        // HELPERS FOR DATA SEEDING
        private CyberPolicy createPolicy(String name, String sector, int premium, long coverage, String desc,
                        String... benefits) {
                return CyberPolicy.builder()
                                .policyName(name)
                                .sector(sector)
                                .durationMonths(12)
                                .basePremium(BigDecimal.valueOf(premium))
                                .coverageLimit(BigDecimal.valueOf(coverage))
                                .isActive(true)
                                .createdAt(LocalDateTime.now())
                                .description(desc)
                                .benefits(List.of(benefits))
                                .build();
        }

        private void seedPolicies() {
                if (policyRepository.count() == 0) {
                        policyRepository.saveAll(List.of(
                                        // Banking
                                        createPolicy("Financial Data Protection Plan", "Banking", 250000, 500000000,
                                                        "Comprehensive cyber insurance for the banking sector protecting financial data.",
                                                        "Data breach response", "Customer notification costs",
                                                        "Legal defense coverage", "Credit monitoring for customers"),
                                        createPolicy("ATM Network Security Plan", "Banking", 180000, 300000000,
                                                        "Protection for ATM networks against malware and fraud.",
                                                        "ATM malware protection", "ATM fraud monitoring",
                                                        "Transaction data recovery", "Incident investigation"),
                                        createPolicy("Digital Payment Security Plan", "Banking", 300000, 750000000,
                                                        "Protection for digital payment gateways and platforms.",
                                                        "Payment gateway protection", "Fraud chargeback coverage",
                                                        "Digital wallet security"),
                                        createPolicy("Core Banking System Shield", "Banking", 500000, 1000000000,
                                                        "Total coverage for core banking infrastructure.",
                                                        "Ransomware protection", "System downtime compensation",
                                                        "IT forensics"),
                                        createPolicy("Investment Data Privacy", "Banking", 200000, 400000000,
                                                        "Protects high-net-worth client investment data.",
                                                        "Insider threat coverage", "Phishing protection",
                                                        "Regulatory fine coverage"),

                                        // Healthcare
                                        createPolicy("Patient Data Protection Policy", "Healthcare", 120000, 200000000,
                                                        "Ensures patient records are protected from medical data breaches.",
                                                        "Patient record breach coverage", "Medical data recovery",
                                                        "Legal liability coverage", "Compliance support"),
                                        createPolicy("Telemedicine Security Plan", "Healthcare", 90000, 150000000,
                                                        "Protects telehealth sessions and remote patient monitoring.",
                                                        "Video feed encryption failure", "Remote device hacking",
                                                        "Patient privacy defense"),
                                        createPolicy("Medical IoT Shield", "Healthcare", 150000, 300000000,
                                                        "Protection for connected medical devices.",
                                                        "Pacemaker/Monitor hacking", "Device network isolation failure",
                                                        "Ransomware on medical devices"),
                                        createPolicy("Hospital Ransomware Defense", "Healthcare", 350000, 800000000,
                                                        "Prevents hospital shutdown due to ransomware attacks.",
                                                        "Ransom payment coverage", "Emergency patient transfer costs",
                                                        "System rebuild expenses"),
                                        createPolicy("Pharmacy Data Security", "Healthcare", 80000, 100000000,
                                                        "Protects prescription records and pharmacy databases.",
                                                        "Prescription fraud", "Customer data theft",
                                                        "Compliance fines"),

                                        // E-commerce
                                        createPolicy("Online Retail Security Plan", "E-commerce", 80000, 100000000,
                                                        "Protects online stores from payment fraud and customer data theft.",
                                                        "Customer data protection", "Payment fraud coverage",
                                                        "Website attack protection"),
                                        createPolicy("E-commerce DDoS Protection", "E-commerce", 110000, 200000000,
                                                        "Covers losses due to website downtime from DDoS attacks.",
                                                        "Downtime revenue loss", "Server overload recovery",
                                                        "Traffic filtering costs"),
                                        createPolicy("Supply Chain Cyber Security", "E-commerce", 150000, 300000000,
                                                        "Protects against breaches originating from third-party vendors.",
                                                        "Vendor breach liability", "Supply chain disruption",
                                                        "Inventory system hacking"),
                                        createPolicy("Customer Account Shield", "E-commerce", 60000, 80000000,
                                                        "Protects against credential stuffing and account takeovers.",
                                                        "Account takeover remediation", "Loyalty point fraud",
                                                        "Password reset handling"),
                                        createPolicy("Mobile Commerce Protection", "E-commerce", 95000, 150000000,
                                                        "Specific coverage for mobile app shopping platforms.",
                                                        "App vulnerabilities", "Mobile payment fraud",
                                                        "Fake app takedown"),

                                        // IT / SaaS
                                        createPolicy("Software Liability Protection", "IT / SaaS", 200000, 400000000,
                                                        "Coverage for IT and SaaS companies against software failure liability.",
                                                        "Software failure liability", "Client data breach protection",
                                                        "Legal claims coverage"),
                                        createPolicy("Cloud Service Interruption Plan", "IT / SaaS", 250000, 500000000,
                                                        "Covers the cost of cloud outages affecting clients.",
                                                        "SLA breach compensation", "Cloud provider dispute costs",
                                                        "Data loss recovery"),
                                        createPolicy("Code Vulnerability Defense", "IT / SaaS", 180000, 350000000,
                                                        "Protects against zero-day exploits and poor code security.",
                                                        "Zero-day exploit recovery", "Patch deployment costs",
                                                        "Client notification"),
                                        createPolicy("Managed Service Provider Shield", "IT / SaaS", 300000, 600000000,
                                                        "Comprehensive coverage for MSPs handling multiple clients.",
                                                        "Multi-tenant breach", "Remote access hacking",
                                                        "Client liability"),
                                        createPolicy("AI / Machine Learning Security", "IT / SaaS", 400000, 800000000,
                                                        "Protection for AI models against data poisoning and theft.",
                                                        "Model theft", "Data poisoning recovery",
                                                        "Algorithmic bias defense"),

                                        // Manufacturing
                                        createPolicy("Industrial Control System Protection", "Manufacturing", 120000,
                                                        200000000, "Protects industrial systems from production risks.",
                                                        "Protection against industrial cyber attacks",
                                                        "Production system recovery", "ICS malware protection"),
                                        createPolicy("Supply Chain Disruption Plan", "Manufacturing", 180000, 350000000,
                                                        "Coverage for halt in manufacturing due to cyber attacks on suppliers.",
                                                        "Supplier breach", "Production halt loss",
                                                        "Alternative sourcing costs"),
                                        createPolicy("Intellectual Property Theft", "Manufacturing", 250000, 500000000,
                                                        "Protects proprietary designs and manufacturing formulas.",
                                                        "Design theft", "Patent infringement defense",
                                                        "Espionage tracking"),
                                        createPolicy("Automated Assembly Line Security", "Manufacturing", 150000,
                                                        300000000,
                                                        "Secures robotic arms and automated factory equipment.",
                                                        "Robotics hacking", "Equipment damage repair",
                                                        "Safety hazard liability"),
                                        createPolicy("Smart Factory IoT Shield", "Manufacturing", 130000, 250000000,
                                                        "Protects IIoT sensors and smart factory networks.",
                                                        "Sensor spoofing", "Network isolation", "Data corruption"),

                                        // Education
                                        createPolicy("Student Data Privacy Plan", "Education", 70000, 100000000,
                                                        "Protects student records and administrative databases.",
                                                        "FERPA compliance", "Student data breach",
                                                        "Identity theft protection"),
                                        createPolicy("Campus Network Security", "Education", 100000, 200000000,
                                                        "Secures open campus networks against widespread threats.",
                                                        "Malware containment", "Student device isolation",
                                                        "Network rebuild"),
                                        createPolicy("Online Learning Platform Shield", "Education", 85000, 150000000,
                                                        "Protects virtual classrooms and online testing systems.",
                                                        "Exam cheating rings", "Platform downtime",
                                                        "Video bombing prevention"),
                                        createPolicy("Research Data Protection", "Education", 150000, 300000000,
                                                        "Secures valuable university research and intellectual property.",
                                                        "Research theft", "Grant compliance",
                                                        "Data destruction recovery"),
                                        createPolicy("University Financial Cyber Protection", "Education", 120000,
                                                        250000000,
                                                        "Protects tuition payment portals and university funds.",
                                                        "Tuition fraud", "Payroll diversion",
                                                        "Payment gateway hacking"),

                                        // Government
                                        createPolicy("Citizen Data Protection", "Government", 300000, 1000000000,
                                                        "Secures sensitive citizen databases and public records.",
                                                        "Public record breach", "Citizen notification",
                                                        "Identity protection services"),
                                        createPolicy("Critical Infrastructure Shield", "Government", 500000, 2000000000,
                                                        "Protects power grids, water supplies, and public transit.",
                                                        "Utility hacking", "Terrorist cyber attack",
                                                        "Emergency response coordination"),
                                        createPolicy("E-Governance Security Plan", "Government", 200000, 500000000,
                                                        "Secures online government portals and tax systems.",
                                                        "Tax fraud", "Portal defacement", "Service denial recovery"),
                                        createPolicy("Defense & Intelligence Data Security", "Government", 800000,
                                                        3000000000L,
                                                        "Top-tier protection for classified government data.",
                                                        "Espionage", "Insider threat", "Classified leak containment"),
                                        createPolicy("Local Municipality Ransomware Defense", "Government", 150000,
                                                        300000000,
                                                        "Protects towns and cities from being held hostage by ransomware.",
                                                        "Ransom negotiation", "City service restoration",
                                                        "Public communication"),

                                        // Media
                                        createPolicy("Broadcasting Disruption Protection", "Media", 180000, 400000000,
                                                        "Covers losses from broadcast signal hacking or downtime.",
                                                        "Signal hijacking", "Advertising revenue loss",
                                                        "Equipment repair"),
                                        createPolicy("Media Intellectual Property Shield", "Media", 200000, 500000000,
                                                        "Protects unreleased films, music, and manuscripts from leaks.",
                                                        "Pre-release leak", "Copyright enforcement", "Piracy tracing"),
                                        createPolicy("Journalist Source Protection", "Media", 90000, 150000000,
                                                        "Secures communication channels for investigative journalism.",
                                                        "Source unmasking", "Secure drop hacking", "Legal defense"),
                                        createPolicy("Social Media Defacement Plan", "Media", 70000, 100000000,
                                                        "Protects official brand accounts from being hacked or defaced.",
                                                        "Account recovery", "PR crisis management",
                                                        "Follower loss compensation"),
                                        createPolicy("Digital Content Platform Security", "Media", 150000, 350000000,
                                                        "Secures streaming services and digital content delivery.",
                                                        "Streaming piracy", "Subscriber data breach",
                                                        "CDN hijacking")));
                        System.out.println("Default Cyber Policies seeded.");
                }
        }

        private void seedCustomerData() {
                if (subscriptionRepository.count() == 0) {
                        User rajesh = userRepository.findByEmail("rajesh@techcorp.in").orElseThrow();
                        CyberPolicy policy = policyRepository.findAll().stream()
                                        .filter(p -> p.getSector().equals("IT / SaaS")).findFirst().orElseThrow();

                        // 1. Create Risk Assessment for Rajesh
                        RiskAssessment risk = RiskAssessment.builder()
                                        .customer(rajesh)
                                        .riskScore(35)
                                        .riskLevel("LOW")
                                        .firewallEnabled(true)
                                        .encryptionEnabled(true)
                                        .backupAvailable(true)
                                        .mfaEnabled(true)
                                        .employeeCount(150)
                                        .annualRevenue(10000000.0)
                                        .createdAt(LocalDateTime.now().minusDays(35))
                                        .build();
                        riskAssessmentRepository.save(risk);

                        // 2. Create Subscription
                        PolicySubscription sub = PolicySubscription.builder()
                                        .customer(rajesh)
                                        .policy(policy)
                                        .riskAssessment(risk)
                                        .status(SubscriptionStatus.APPROVED)
                                        .calculatedPremium(policy.getBasePremium())
                                        .startDate(java.time.LocalDate.now().minusDays(30))
                                        .endDate(java.time.LocalDate.now().plusMonths(11))
                                        .build();
                        subscriptionRepository.save(sub);

                        if (incidentRepository.count() == 0) {
                                IncidentReport incident = IncidentReport.builder()
                                                .customer(rajesh)
                                                .subscription(sub)
                                                .incidentType("Ransomware")
                                                .description("Servers encrypted by unknown payload.")
                                                .estimatedLossAmount(new java.math.BigDecimal("250000"))
                                                .status(IncidentStatus.REPORTED)
                                                .reportedAt(LocalDateTime.now().minusDays(5))
                                                .build();
                                incidentRepository.save(incident);
                        }

                        System.out.println("Golden Testing Customer Data Seeded.");
                }
        }
}