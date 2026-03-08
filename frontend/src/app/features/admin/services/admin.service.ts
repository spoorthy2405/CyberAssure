
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface User {
    id: number;
    fullName: string;
    email: string;
    phoneNumber: string;
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

}

