
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface User {
    userId: number;
    fullName: string;
    email: string;
    phoneNumber: string;
    companyName?: string;
    industry?: string;
    companySize?: string;
    role: {
        id: number;
        roleName: string;
    };
    accountStatus: string;
}

@Injectable({
    providedIn: 'root'
})
export class AdminApiService {

    private apiUrl = 'http://localhost:8080/api/v1/admin';

    constructor(private http: HttpClient) { }

    getDashboardStats(): Observable<any> {
        return this.http.get<any>(`${this.apiUrl}/dashboard`);
    }

    getCustomers(): Observable<User[]> {
        return this.http.get<User[]>(`${this.apiUrl}/customers`);
    }

    getStaff(): Observable<User[]> {
        return this.http.get<User[]>(`${this.apiUrl}/staff`);
    }

    getUnderwriters(): Observable<User[]> {
        return this.http.get<User[]>(`${this.apiUrl}/underwriters`);
    }

    getClaimsOfficers(): Observable<User[]> {
        return this.http.get<User[]>(`${this.apiUrl}/claims-officers`);
    }

    getPolicies(): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}/policies`);
    }

    getClaims(): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}/claims`);
    }

    getAnalytics(): Observable<any> {
        return this.http.get<any>(`${this.apiUrl}/analytics`);
    }

    getRiskMonitoring(): Observable<any> {
        return this.http.get<any>(`${this.apiUrl}/risk-monitoring`);
    }

    createStaff(staffData: any): Observable<any> {
        return this.http.post<any>(`${this.apiUrl}/create-staff`, staffData);
    }

    // CYBER POLICY CATALOG
    getCyberPolicies(): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}/cyber-policies`);
    }

    createCyberPolicy(policyData: any): Observable<any> {
        return this.http.post<any>(`${this.apiUrl}/cyber-policies`, policyData);
    }

    updateCyberPolicy(id: number, policyData: any): Observable<any> {
        return this.http.put<any>(`${this.apiUrl}/cyber-policies/${id}`, policyData);
    }

    deleteCyberPolicy(id: number): Observable<any> {
        return this.http.delete<any>(`${this.apiUrl}/cyber-policies/${id}`);
    }

    // SUBSCRIPTIONS / PENDING APPLICATIONS
    getSubscriptions(): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}/policies`);
    }

    assignUnderwriter(subscriptionId: number, underwriterId: number): Observable<any> {
        return this.http.put<any>(`${this.apiUrl}/subscriptions/${subscriptionId}/assign`, { underwriterId });
    }

}
