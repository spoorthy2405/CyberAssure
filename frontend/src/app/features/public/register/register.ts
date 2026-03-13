import { Component, OnInit } from '@angular/core';

import { RouterModule, Router } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

@Component({
    selector: 'app-register',
    imports: [RouterModule, ReactiveFormsModule],
    templateUrl: './register.html',
    styleUrl: './register.css'
})
export class Register implements OnInit {
    registerForm!: FormGroup;

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

    constructor(
        private fb: FormBuilder,
        private http: HttpClient, 
        private router: Router
    ) { }

    ngOnInit(): void {
        this.registerForm = this.fb.group({
            fullName: ['', [Validators.required]],
            email: ['', [Validators.required, Validators.email]],
            phoneNumber: ['', [Validators.required, Validators.pattern('^[+0-9\\s-]{10,15}$')]],
            companyName: ['', [Validators.required]],
            registrationNumber: ['', [Validators.required]],
            companyAddress: ['', [Validators.required]],
            companyWebsite: [''],
            annualRevenue: ['', [Validators.required]],
            industry: ['', [Validators.required]],
            companySize: ['', [Validators.required]],
            password: ['', [Validators.required, Validators.minLength(8)]],
            confirmPassword: ['', [Validators.required]],
            agreeTerms: [false, Validators.requiredTrue],
            agreeData: [false, Validators.requiredTrue]
        }, { validators: this.passwordMatchValidator });
    }

    passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
        const password = control.get('password')?.value;
        const confirm = control.get('confirmPassword')?.value;
        return password === confirm ? null : { passwordMismatch: true };
    }

    get passwordStrength(): number {
        const p = this.registerForm.get('password')?.value || '';
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

        if (this.registerForm.invalid) {
            this.registerForm.markAllAsTouched();
            // Show a specific common error if passwords mismatch
            if (this.registerForm.hasError('passwordMismatch')) {
                 this.errorMsg = 'Passwords do not match.';
            } else {
                 this.errorMsg = 'Please ensure all required fields are filled out correctly.';
            }
            return;
        }

        this.loading = true;

        const payload = this.registerForm.value;

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