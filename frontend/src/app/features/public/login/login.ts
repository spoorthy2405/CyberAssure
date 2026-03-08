import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';

@Component({
    selector: 'app-login',
    standalone: true,
    imports: [CommonModule, RouterModule, FormsModule],
    templateUrl: './login.html',
    styleUrl: './login.css'
})
export class Login {

    email = '';
    password = '';
    rememberMe = false;
    loading = false;
    errorMsg = '';
    showPassword = false;

    constructor(private authService: AuthService) { }

    togglePassword() {
        this.showPassword = !this.showPassword;
    }

    onLogin() {

        this.errorMsg = '';

        if (!this.email || !this.password) {
            this.errorMsg = 'Please enter your email and password.';
            return;
        }

        this.loading = true;

        this.authService.login({
            email: this.email,
            password: this.password
        }).subscribe({

            next: () => {
                this.loading = false;
                // redirect handled in AuthService
            },

            error: (err) => {
                this.loading = false;
                this.errorMsg =
                    err.error?.message || 'Invalid email or password.';
            }

        });
    }
}