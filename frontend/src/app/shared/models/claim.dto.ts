import { UserDto } from './user.dto';
import { IncidentReportDto } from './incident.dto';

export interface ClaimDto {
    id?: number;
    claimAmount?: number;
    settlementAmount?: number;
    status?: string;
    rejectionReason?: string;
    investigationNotes?: string;
    bankAccountNumber?: string;
    bankIfscCode?: string;
    policeReportFiled?: boolean;
    policeReportNumber?: string;
    claimDescription?: string;
    filedAt?: string;
    reviewedAt?: string;
    customer?: UserDto;
    reviewedBy?: UserDto;
    assignedOfficer?: UserDto;
    incident?: IncidentReportDto;
}

export interface ClaimResponseDto extends ClaimDto {}
