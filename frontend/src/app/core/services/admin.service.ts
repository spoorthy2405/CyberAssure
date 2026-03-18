import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserDto } from '../../shared/models/user.dto';
import { CyberPolicyDto } from '../../shared/models/policy.dto';
import { RiskAssessmentDto } from '../../shared/models/assessment.dto';
import { ClaimDto } from '../../shared/models/claim.dto';
import { DashboardStatsDto } from '../../shared/models/dashboard.dto';

@Injectable({
    providedIn: 'root'
})
export class AdminService {

    private API = "http://localhost:8080/api/v1";
    private http = inject(HttpClient);

    getDashboardStats(): Observable<DashboardStatsDto> {
        return this.http.get<DashboardStatsDto>(`${this.API}/admin/dashboard`);
    }

    getUsers(): Observable<UserDto[]> {
        return this.http.get<UserDto[]>(`${this.API}/admin/users`);
    }

    getPolicies(): Observable<CyberPolicyDto[]> {
        return this.http.get<CyberPolicyDto[]>(`${this.API}/policies`);
    }

    getAssessments(): Observable<RiskAssessmentDto[]> {
        return this.http.get<RiskAssessmentDto[]>(`${this.API}/risk-assessments`);
    }

    getClaims(): Observable<ClaimDto[]> {
        return this.http.get<ClaimDto[]>(`${this.API}/claims`);
    }

    assignClaimsOfficer(claimId: number, officerId: number): Observable<any> {
        return this.http.put(`${this.API}/admin/claims/${claimId}/assign`, { officerId });
    }

    assignUnderwriter(subscriptionId: number, underwriterId: number): Observable<any> {
        return this.http.put(`${this.API}/admin/subscriptions/${subscriptionId}/assign`, { underwriterId });
    }

}