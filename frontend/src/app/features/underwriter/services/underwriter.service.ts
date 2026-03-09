import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class UnderwriterService {

    private API = "http://localhost:8080/api/v1/subscriptions";

    constructor(private http: HttpClient) { }

    // GET only subscriptions assigned to this underwriter
    getMyAssignedSubscriptions(): Observable<any[]> {
        return this.http.get<any[]>(`${this.API}/assigned-to-me`);
    }

    // REVIEW decision — full plan data
    reviewSubscription(id: number, data: any): Observable<any> {
        return this.http.put(`${this.API}/${id}/review`, data);
    }
}