import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';

export interface LoginRequest {
    email: string;
    password: string;
}

export interface RegisterRequest {
    fullName: string;
    email: string;
    password: string;
    companyName: string;
    phoneNumber: string;
}

export interface AuthResponse {
    email: string;
    role: string;
    token: string;
}

@Injectable({
    providedIn: 'root'
})
export class AuthService {

    private readonly BASE_URL = 'http://localhost:8080/api/v1/auth';

    constructor(private http: HttpClient, private router: Router) { }

    // LOGIN
    login(credentials: LoginRequest): Observable<AuthResponse> {

        return this.http.post<AuthResponse>(
            `${this.BASE_URL}/login`,
            credentials
        ).pipe(

            tap(res => {

                console.log("LOGIN RESPONSE:", res);

                // store JWT
                localStorage.setItem('token', res.token);
                localStorage.setItem('role', res.role);
                localStorage.setItem('email', res.email);

                // redirect based on role
                this.redirectByRole(res.role);

            })

        );
    }

    // REGISTER
    register(data: RegisterRequest): Observable<any> {
        return this.http.post(`${this.BASE_URL}/register`, data);
    }

    // LOGOUT
    logout(): void {
        localStorage.clear();
        this.router.navigate(['/login']);
    }

    // TOKEN
    getToken(): string | null {
        return localStorage.getItem('token');
    }

    // ROLE
    getRole(): string | null {
        return localStorage.getItem('role');
    }

    // LOGIN STATUS
    isLoggedIn(): boolean {
        return !!this.getToken();
    }

    // ROLE REDIRECT
    private redirectByRole(role: string): void {

        const routes: Record<string, string> = {

            'ROLE_ADMIN': '/admin',
            'ROLE_CUSTOMER': '/customer',
            'ROLE_UNDERWRITER': '/underwriter',
            'ROLE_CLAIMS_OFFICER': '/claims'

        };

        this.router.navigate([routes[role] ?? '/']);
    }

}