import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class ClaimsService {

    private API = "http://localhost:8080/api/v1/claims";

    constructor(private http: HttpClient) { }

    getPendingClaims(): Observable<any> {
        return this.http.get(`${this.API}/pending`);
    }

    getAllClaims(): Observable<any> {
        return this.http.get(`${this.API}`);
    }

    reviewClaim(id: number, body: any) {
        return this.http.put(`${this.API}/${id}/review`, body);
    }

}