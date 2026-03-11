import { CyberPolicyDto } from './policy.dto';
import { UserDto } from './user.dto';
import { RiskAssessmentDto } from './assessment.dto';

export interface PolicySubscriptionDto {
    id?: number;
    policy?: CyberPolicyDto;
    customer?: UserDto;
    riskAssessment?: RiskAssessmentDto;
    calculatedPremium?: number;
    status?: string;
    startDate?: string;
    endDate?: string;
    createdAt?: string;
    approvedBy?: UserDto;
    assignedUnderwriter?: UserDto;
    approvedAt?: string;
    rejectionReason?: string;
    riskScore?: number;
    coverageAmount?: number;
    tenureMonths?: number;
    underwriterNotes?: string;
    policyLimit?: number;
    deductible?: number;
    exclusions?: string;
}
