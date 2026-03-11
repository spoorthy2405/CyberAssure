import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DashboardStatsDto } from '../../../shared/models/dashboard.dto';
import { CyberPolicyDto } from '../../../shared/models/policy.dto';
import { PolicySubscriptionDto } from '../../../shared/models/subscription.dto';
import { IncidentReportDto } from '../../../shared/models/incident.dto';
import { ClaimDto } from '../../../shared/models/claim.dto';
import { RiskAssessmentDto } from '../../../shared/models/assessment.dto';

@Injectable({ providedIn: 'root' })
export class CustomerService {

    private base = 'http://localhost:8080/api/v1';
    private http = inject(HttpClient);

    // Dashboard
    getDashboardStats(): Observable<DashboardStatsDto> {
        return this.http.get<DashboardStatsDto>(`${this.base}/customer/dashboard`);
    }

    // Policies
    getRecommendedPolicies(): Observable<CyberPolicyDto[]> {
        return this.http.get<CyberPolicyDto[]>(`${this.base}/customer/recommended-policies`);
    }

    applyForPolicy(data: FormData): Observable<any> {
        return this.http.post(`${this.base}/customer/apply`, data, { responseType: 'text' });
    }

    // Alias used by risk-assessment component
    createRisk(data: any): Observable<RiskAssessmentDto> {
        return this.http.post<RiskAssessmentDto>(`${this.base}/customer/apply`, data);
    }

    // Subscriptions
    getSubscriptions(): Observable<PolicySubscriptionDto[]> {
        return this.http.get<PolicySubscriptionDto[]>(`${this.base}/subscriptions/my`);
    }

    paySubscription(id: number): Observable<any> {
        return this.http.post(`${this.base}/subscriptions/${id}/pay`, {});
    }

    // Incidents
    getMyIncidents(): Observable<IncidentReportDto[]> {
        return this.http.get<IncidentReportDto[]>(`${this.base}/incidents/my`);
    }

    reportIncident(formData: FormData): Observable<IncidentReportDto> {
        return this.http.post<IncidentReportDto>(`${this.base}/incidents`, formData);
    }

    // Claims
    getClaims(): Observable<ClaimDto[]> {
        return this.http.get<ClaimDto[]>(`${this.base}/claims/my`);
    }

    fileClaim(data: any): Observable<ClaimDto> {
        return this.http.post<ClaimDto>(`${this.base}/claims`, data);
    }
}