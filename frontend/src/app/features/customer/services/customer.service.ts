import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
    providedIn: 'root'
})
export class CustomerService {

    API = "http://localhost:8080/api/v1";

    constructor(private http: HttpClient) { }

    /* RISK */

    createRisk(data: any) {
        return this.http.post(`${this.API}/risk`, data);
    }

    getRisk() {
        return this.http.get(`${this.API}/risk`);
    }

    /* POLICIES */

    getPolicies() {
        return this.http.get<any[]>(`${this.API}/policies`);
    }

    /* SUBSCRIPTIONS */

    subscribePolicy(data: any) {
        return this.http.post(`${this.API}/subscriptions`, data);
    }

    getSubscriptions() {
        return this.http.get<any[]>(`${this.API}/subscriptions`);
    }

    /* INCIDENTS */

    reportIncident(data: any) {
        return this.http.post(`${this.API}/incidents`, data);
    }

    /* CLAIMS */

    getClaims() {
        return this.http.get<any[]>(`${this.API}/claims`);
    }

    fileClaim(data: any) {
        return this.http.post(`${this.API}/claims`, data);
    }

}