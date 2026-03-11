import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PolicySubscriptionDto } from '../../../shared/models/subscription.dto';

@Injectable({
    providedIn: 'root'
})
export class UnderwriterService {

    private API = "http://localhost:8080/api/v1/subscriptions";
    private http = inject(HttpClient);

    // GET only subscriptions assigned to this underwriter
    getMyAssignedSubscriptions(): Observable<PolicySubscriptionDto[]> {
        return this.http.get<PolicySubscriptionDto[]>(`${this.API}/assigned-to-me`);
    }

    // REVIEW decision — full plan data
    reviewSubscription(id: number, data: any): Observable<PolicySubscriptionDto> {
        return this.http.put<PolicySubscriptionDto>(`${this.API}/${id}/review`, data);
    }

    // Securely view/download proof documents
    downloadProof(url: string): Observable<Blob> {
        return this.http.get(url, { responseType: 'blob' });
    }
}