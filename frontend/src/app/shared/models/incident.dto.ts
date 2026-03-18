import { PolicySubscriptionDto } from './subscription.dto';
import { UserDto } from './user.dto';

export interface IncidentReportDto {
    id?: number;
    subscription?: PolicySubscriptionDto;
    customer?: UserDto;
    incidentType?: string;
    description?: string;
    incidentDate?: string;
    reportedAt?: string;
    impactLevel?: string;
    status?: string;
    rejectionReason?: string;
    reviewedBy?: UserDto;
}
