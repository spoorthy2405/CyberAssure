export interface DashboardStatsDto {
    // Admin Stats
    totalCustomers?: number;
    totalActivePolicies?: number;
    totalClaims?: number;
    totalRevenue?: number;

    // Customer Stats
    customerName?: string;
    customerCompany?: string;
    customerIndustry?: string;

    totalCoverage?: number;
    activeClaimsCount?: number;
    claimedAmountYearly?: number;
    daysToRenewal?: number;
    latestRiskScore?: number;
    latestRiskLevel?: string;

    activePolicyName?: string;
    activePolicyId?: string;
    subscriptionStatus?: string;
    assignedUnderwriterName?: string;
    policyStartDate?: string;
    policyEndDate?: string;

    riskScore?: number;
    riskLevel?: string;
    calculatedPremium?: number;
    coverageAmount?: number;
    tenureMonths?: number;
    underwriterNotes?: string;
    policyLimit?: number;
    deductible?: number;
    exclusions?: string;

    rejectionReason?: string;
}
