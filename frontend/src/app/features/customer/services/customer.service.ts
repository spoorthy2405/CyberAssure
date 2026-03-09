import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class CustomerService {

    private base = 'http://localhost:8080/api/v1';

    constructor(private http: HttpClient) { }

    // Dashboard
    getDashboardStats(): Observable<any> {
        return this.http.get(`${this.base}/customer/dashboard`);
    }

    // Policies
    getRecommendedPolicies(): Observable<any[]> {
        return this.http.get<any[]>(`${this.base}/customer/recommended-policies`);
    }

    applyForPolicy(data: FormData): Observable<any> {
        return this.http.post(`${this.base}/customer/apply`, data, { responseType: 'text' });
    }

    // Alias used by risk-assessment component
    createRisk(data: any): Observable<any> {
        return this.http.post(`${this.base}/customer/apply`, data);
    }

    // Subscriptions
    getSubscriptions(): Observable<any[]> {
        return this.http.get<any[]>(`${this.base}/subscriptions/my`);
    }

    // Incidents
    getMyIncidents(): Observable<any[]> {
        return this.http.get<any[]>(`${this.base}/incidents/my`);
    }

    reportIncident(formData: FormData): Observable<any> {
        return this.http.post(`${this.base}/incidents`, formData);
    }

    // Claims
    getClaims(): Observable<any[]> {
        return this.http.get<any[]>(`${this.base}/claims/my`);
    }

    fileClaim(data: any): Observable<any> {
        return this.http.post(`${this.base}/claims`, data);
    }
}