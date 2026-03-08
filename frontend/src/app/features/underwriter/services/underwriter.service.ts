import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class UnderwriterService {

    private API = "http://localhost:8080/api/v1/subscriptions";

    constructor(private http: HttpClient) { }

    // GET pending subscriptions
    getSubscriptions(): Observable<any[]> {
        return this.http.get<any[]>(this.API);
    }

    // REVIEW decision
    reviewSubscription(id: number, data: any) {
        return this.http.put(`${this.API}/${id}/review`, data);
    }

}