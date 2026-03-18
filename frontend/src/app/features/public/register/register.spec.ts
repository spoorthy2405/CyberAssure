import 'zone.js';
import 'zone.js/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { Router, provideRouter } from '@angular/router';
import { Register } from './register';
import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest';

describe('Register Component', () => {
  let component: Register;
  let fixture: ComponentFixture<Register>;
  let httpMock: HttpTestingController;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Register, ReactiveFormsModule],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([])
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Register);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should call register API on valid submission', async () => {
    fixture.detectChanges();
    const navigateSpy = vi.spyOn(router, 'navigate');
    
    // Fill required fields
    const controls = component.registerForm.controls;
    controls['fullName'].setValue('Test User');
    controls['email'].setValue('test@test.com');
    controls['phoneNumber'].setValue('+919876543210');
    controls['companyName'].setValue('Test Corp');
    controls['registrationNumber'].setValue('REG123');
    controls['companyAddress'].setValue('123 Test St');
    controls['annualRevenue'].setValue('Under ₹1 Crore');
    controls['industry'].setValue('IT / SaaS');
    controls['companySize'].setValue('1–10 employees');
    controls['password'].setValue('Password123!');
    controls['confirmPassword'].setValue('Password123!');
    controls['agreeTerms'].setValue(true);
    controls['agreeData'].setValue(true);

    fixture.detectChanges();
    expect(component.registerForm.valid).toBe(true);

    component.onRegister();

    const req = httpMock.expectOne('http://localhost:8080/api/v1/auth/register');
    req.flush({ message: 'Success' });

    fixture.detectChanges();
    await fixture.whenStable();
    expect(component.successMsg).toContain('Success');
  });

  it('should show error on registration failure', async () => {
    fixture.detectChanges();
    component.registerForm.patchValue({
        fullName: 'Test', email: 'test@test.com', phoneNumber: '1234567890',
        companyName: 'Test', registrationNumber: '123', companyAddress: 'Test',
        annualRevenue: 'Under ₹1 Crore', industry: 'IT / SaaS', companySize: '1–10 employees',
        password: 'Password123!', confirmPassword: 'Password123!', 
        agreeTerms: true, agreeData: true
    });

    component.onRegister();

    const req = httpMock.expectOne('http://localhost:8080/api/v1/auth/register');
    req.flush({ message: 'Email already exists' }, { status: 400, statusText: 'Bad Request' });

    fixture.detectChanges();
    await fixture.whenStable();
    expect(component.errorMsg).toBe('Email already exists');
    expect(component.loading).toBe(false);
  });
});
