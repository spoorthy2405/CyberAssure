import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';

@Component({
    selector: 'app-login',
    standalone: true,
    imports: [CommonModule, RouterModule, ReactiveFormsModule],
    templateUrl: './login.html',
    styleUrl: './login.css'
})
export class Login implements OnInit {
    loginForm!: FormGroup;

    loading = false;
    errorMsg = '';
    showPassword = false;

    constructor(
        private authService: AuthService,
        private fb: FormBuilder
    ) { }

    ngOnInit(): void {
        this.loginForm = this.fb.group({
            email: ['', [Validators.required, Validators.email]],
            password: ['', Validators.required],
            rememberMe: [false]
        });
    }

    togglePassword() {
        this.showPassword = !this.showPassword;
    }

    onLogin() {
        this.errorMsg = '';

        if (this.loginForm.invalid) {
            this.loginForm.markAllAsTouched();
            return;
        }

        this.loading = true;

        this.authService.login({
            email: this.loginForm.value.email,
            password: this.loginForm.value.password
        }).subscribe({
            next: (res) => {
                this.loading = false;
                
                // store JWT synchronously first
                if (res.token) localStorage.setItem('token', res.token);
                if (res.role) localStorage.setItem('role', res.role);
                if (res.email) localStorage.setItem('email', res.email);

                // Redirect based on role
                Promise.resolve().then(() => {
                    if (res.role) {
                        this.authService.redirectByRole(res.role);
                    }
                });
            },
            error: (err) => {
                this.loading = false;
                this.errorMsg = err.error?.message || 'Invalid email or password.';
            }
        });
    }
}