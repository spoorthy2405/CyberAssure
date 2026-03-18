import { UserDto } from './user.dto';

export interface RiskAssessmentDto {
    id?: number;
    customer?: UserDto;
    firewallEnabled?: boolean;
    encryptionEnabled?: boolean;
    backupAvailable?: boolean;
    mfaEnabled?: boolean;
    iso27001Certified?: boolean;
    hasDataPrivacyOfficer?: boolean;
    previousIncidentCount?: number;
    employeeCount?: number;
    annualRevenue?: number;
    riskScore?: number;
    riskLevel?: string;
    proofDocumentPaths?: string;
    createdAt?: string;
}
