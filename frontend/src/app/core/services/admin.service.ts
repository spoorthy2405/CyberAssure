import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class AdminService {

    private API = "http://localhost:8080/api/v1";

    constructor(private http: HttpClient) { }

    getDashboardStats(): Observable<any> {
        return this.http.get(`${this.API}/admin/dashboard`);
    }

    getUsers(): Observable<any> {
        return this.http.get(`${this.API}/admin/users`);
    }

    getPolicies(): Observable<any> {
        return this.http.get(`${this.API}/policies`);
    }

    getAssessments(): Observable<any> {
        return this.http.get(`${this.API}/risk-assessments`);
    }

    getClaims(): Observable<any> {
        return this.http.get(`${this.API}/claims`);
    }

}