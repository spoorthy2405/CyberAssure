import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

@Component({
    selector: 'app-register',
    imports: [CommonModule, RouterModule, FormsModule],
    templateUrl: './register.html',
    styleUrl: './register.css'
})
export class Register {

    // Form fields — match your RegisterRequest exactly
    fullName = '';
    email = '';
    phoneNumber = '';
    companyName = '';
    industry = '';
    companySize = '';

    // New Corporate Identity Fields
    companyAddress = '';
    companyWebsite = '';
    registrationNumber = '';
    annualRevenue = '';

    password = '';
    confirmPassword = '';
    agreeTerms = false;
    agreeData = false;

    loading = false;
    errorMsg = '';
    successMsg = '';
    showPassword = false;
    showConfirm = false;

    industries = [
        'Banking',
        'Healthcare',
        'E-commerce',
        'IT / SaaS',
        'Manufacturing',
        'Education',
        'Government',
        'Media',
        'Other'
    ];

    companySizes = [
        '1–10 employees',
        '11–50 employees',
        '51–200 employees',
        '201–1000 employees',
        '1000+ employees'
    ];

    annualRevenues = [
        'Pre-revenue',
        'Under ₹1 Crore',
        '₹1 Crore - ₹10 Crore',
        '₹10 Crore - ₹50 Crore',
        '₹50 Crore - ₹250 Crore',
        'Over ₹250 Crore'
    ];

    constructor(private http: HttpClient, private router: Router) { }

    get passwordStrength(): number {
        const p = this.password;
        if (!p) return 0;
        let score = 0;
        if (p.length >= 8) score++;
        if (/[A-Z]/.test(p)) score++;
        if (/[0-9]/.test(p)) score++;
        if (/[^A-Za-z0-9]/.test(p)) score++;
        return score;
    }

    get strengthLabel(): string {
        const labels = ['', 'Weak', 'Fair', 'Good', 'Strong'];
        return labels[this.passwordStrength] || '';
    }

    get strengthColor(): string {
        const colors = ['', '#ef4444', '#f59e0b', '#3b82f6', '#10b981'];
        return colors[this.passwordStrength] || '';
    }

    togglePassword() { this.showPassword = !this.showPassword; }
    toggleConfirm() { this.showConfirm = !this.showConfirm; }

    onRegister() {
        this.errorMsg = '';
        this.successMsg = '';

        // Validate all fields
        if (!this.fullName || !this.email || !this.phoneNumber ||
            !this.companyName || !this.password || !this.confirmPassword) {
            this.errorMsg = 'Please fill in all required fields.';
            return;
        }

        if (this.password !== this.confirmPassword) {
            this.errorMsg = 'Passwords do not match.';
            return;
        }

        if (this.password.length < 8) {
            this.errorMsg = 'Password must be at least 8 characters.';
            return;
        }

        if (!this.agreeTerms) {
            this.errorMsg = 'Please accept the Terms & Privacy Policy.';
            return;
        }

        this.loading = true;

        // Payload matches your RegisterRequest exactly
        const payload = {
            fullName: this.fullName,
            email: this.email,
            password: this.password,
            companyName: this.companyName,
            phoneNumber: this.phoneNumber,
            industry: this.industry,
            companySize: this.companySize,
            companyAddress: this.companyAddress,
            companyWebsite: this.companyWebsite,
            registrationNumber: this.registrationNumber,
            annualRevenue: this.annualRevenue
        };

        this.http.post<any>(
            'http://localhost:8080/api/v1/auth/register', payload
        ).subscribe({
            next: (res) => {
                this.loading = false;
                this.successMsg = res.message || 'Registration successful!';
                setTimeout(() => this.router.navigate(['/login']), 1500);
            },
            error: (err) => {
                this.loading = false;
                this.errorMsg =
                    err.error?.message || 'Registration failed. Please try again.';
            }
        });
    }
}