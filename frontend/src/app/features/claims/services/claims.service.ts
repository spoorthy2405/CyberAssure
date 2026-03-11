import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ClaimDto } from '../../../shared/models/claim.dto';

@Injectable({
    providedIn: 'root'
})
export class ClaimsService {

    private API = "http://localhost:8080/api/v1/claims";
    private http = inject(HttpClient);

    // All claims (for decisions history)
    getAllClaims(): Observable<ClaimDto[]> {
        return this.http.get<ClaimDto[]>(`${this.API}`);
    }

    // Active claims (PENDING + UNDER_INVESTIGATION)
    getActiveClaims(): Observable<ClaimDto[]> {
        return this.http.get<ClaimDto[]>(`${this.API}/active`);
    }

    // Pending only
    getPendingClaims(): Observable<ClaimDto[]> {
        return this.http.get<ClaimDto[]>(`${this.API}/pending`);
    }

    // Assigned to me
    getAssignedClaims(): Observable<ClaimDto[]> {
        return this.http.get<ClaimDto[]>(`${this.API}/assigned-to-me`);
    }

    // Review claim (APPROVED / REJECTED / UNDER_INVESTIGATION)
    reviewClaim(id: number, body: any): Observable<ClaimDto> {
        return this.http.put<ClaimDto>(`${this.API}/${id}/review`, body);
    }

    // Settle claim
    settleClaim(id: number, body: any): Observable<ClaimDto> {
        return this.http.put<ClaimDto>(`${this.API}/${id}/settle`, body);
    }

    // Get claim assessment
    getClaimAssessment(id: number): Observable<any> {
        return this.http.get(`${this.API}/${id}/assessment`);
    }
}