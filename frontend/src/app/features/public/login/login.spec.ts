import 'zone.js';
import 'zone.js/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { Login } from './login';
import { AuthService } from '../../../core/services/auth.service';
import { describe, it, expect, beforeEach, vi } from 'vitest';

describe('Login Component', () => {
  let component: Login;
  let fixture: ComponentFixture<Login>;
  let authServiceMock: any;

  beforeEach(async () => {
    authServiceMock = {
      login: vi.fn(),
      redirectByRole: vi.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [Login, ReactiveFormsModule, RouterModule],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        provideRouter([])
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Login);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should call authService.login on valid form submission', async () => {
    fixture.detectChanges();
    const mockResponse = { token: 'test-token', role: 'ROLE_ADMIN', email: 'test@test.com' };
    authServiceMock.login.mockReturnValue(of(mockResponse));

    component.loginForm.controls['email'].setValue('test@test.com');
    component.loginForm.controls['password'].setValue('password123');
    
    component.onLogin();
    
    await fixture.whenStable();
    fixture.detectChanges();
    
    expect(authServiceMock.login).toHaveBeenCalled();
    expect(localStorage.getItem('token')).toBe('test-token');
    expect(authServiceMock.redirectByRole).toHaveBeenCalledWith('ROLE_ADMIN');
  });

  it('should show error message on login failure', async () => {
    fixture.detectChanges();
    authServiceMock.login.mockReturnValue(throwError(() => ({ error: { message: 'Invalid credentials' } })));

    component.loginForm.controls['email'].setValue('test@test.com');
    component.loginForm.controls['password'].setValue('wrong-password');
    
    component.onLogin();
    
    await fixture.whenStable();
    fixture.detectChanges();
    
    expect(component.errorMsg).toBe('Invalid credentials');
    expect(component.loading).toBe(false);
  });
});
