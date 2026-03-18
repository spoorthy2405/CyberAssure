export interface CyberPolicyDto {
    id?: number;
    policyName: string;
    coverageLimit: number;
    basePremium: number;
    durationMonths: number;
    sector: string;
    description?: string;
    benefits?: string[];
    applicableTo?: string[];
    isActive?: boolean;
    createdAt?: string;
}
