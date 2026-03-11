import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserDto } from '../../shared/models/user.dto';
import { CyberPolicyDto } from '../../shared/models/policy.dto';
import { RiskAssessmentDto } from '../../shared/models/assessment.dto';
import { ClaimDto } from '../../shared/models/claim.dto';

@Injectable({
    providedIn: 'root'
})
export class AdminApiService {

    private API = 'http://localhost:8080/api/v1';
    private http = inject(HttpClient);

    getPolicies(): Observable<CyberPolicyDto[]> {
        return this.http.get<CyberPolicyDto[]>(`${this.API}/policies`);
    }

    getRiskAssessments(): Observable<RiskAssessmentDto[]> {
        return this.http.get<RiskAssessmentDto[]>(`${this.API}/risk`);
    }

    getPendingClaims(): Observable<ClaimDto[]> {
        return this.http.get<ClaimDto[]>(`${this.API}/claims/pending`);
    }

    getCustomers(): Observable<UserDto[]> {
        return this.http.get<UserDto[]>(`${this.API}/admin/customers`);
    }
    
    getRisks(): Observable<RiskAssessmentDto[]> {
        return this.http.get<RiskAssessmentDto[]>(`${this.API}/risk`);
    }
    
    getClaims(): Observable<ClaimDto[]> {
        return this.http.get<ClaimDto[]>(`${this.API}/claims`);
    }

}