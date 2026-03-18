import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { LoginRequestDto, RegisterRequestDto, AuthResponseDto } from '../../shared/models/auth.dto';

@Injectable({
    providedIn: 'root'
})
export class AuthService {

    private readonly BASE_URL = 'http://localhost:8080/api/v1/auth';

    private http = inject(HttpClient);
    private router = inject(Router);

    // LOGIN
    login(credentials: LoginRequestDto): Observable<AuthResponseDto> {
        return this.http.post<AuthResponseDto>(
            `${this.BASE_URL}/login`,
            credentials
        );
    }

    // REGISTER
    register(data: RegisterRequestDto): Observable<any> {
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
    redirectByRole(role: string): void {

        const routes: Record<string, string> = {
            'ROLE_ADMIN': '/admin/dashboard',
            'ROLE_CUSTOMER': '/customer',
            'ROLE_UNDERWRITER': '/underwriter',
            'ROLE_CLAIMS_OFFICER': '/claims'
        };

        this.router.navigate([routes[role] ?? '/']);
    }

}