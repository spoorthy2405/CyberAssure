import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class AdminApiService {

    private API = 'http://localhost:8080/api/v1';

    constructor(private http: HttpClient) { }

    getPolicies(): Observable<any[]> {
        return this.http.get<any[]>(`${this.API}/policies`);
    }

    getRiskAssessments(): Observable<any[]> {
        return this.http.get<any[]>(`${this.API}/risk`);
    }

    getPendingClaims(): Observable<any[]> {
        return this.http.get<any[]>(`${this.API}/claims/pending`);
    }

    getCustomers() {
        return this.http.get<any[]>(`${this.API}/admin/customers`);
    }
    getRisks() {
        return this.http.get<any[]>(`${this.API}/risk`);
    }
    getClaims() {
        return this.http.get<any[]>(`${this.API}/claims`);
    }

}