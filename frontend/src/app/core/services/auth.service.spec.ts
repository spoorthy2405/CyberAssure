import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { provideRouter } from '@angular/router';
import { AuthService } from './auth.service';
import { LoginRequestDto, RegisterRequestDto, AuthResponseDto } from '../../shared/models/auth.dto';
import { vi, describe, it, expect, beforeEach, afterEach } from 'vitest';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;
  let router: Router;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([])
      ]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
    
    // Clear localStorage before each test
    localStorage.clear();
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('login', () => {
    it('should send a POST request to login endpoint', () => {
      const mockCredentials: LoginRequestDto = { email: 'test@example.com', password: 'password' };
      const mockResponse: AuthResponseDto = { token: 'mock-token', role: 'ROLE_ADMIN' };

      service.login(mockCredentials).subscribe(response => {
        expect(response).toEqual(mockResponse);
      });

      const req = httpMock.expectOne('http://localhost:8080/api/v1/auth/login');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(mockCredentials);
      req.flush(mockResponse);
    });
  });

  describe('register', () => {
    it('should send a POST request to register endpoint', () => {
      const mockData: RegisterRequestDto = { 
        email: 'test@example.com', 
        password: 'password', 
        fullName: 'Test User'
      };

      service.register(mockData).subscribe(response => {
        expect(response).toBeDefined();
      });

      const req = httpMock.expectOne('http://localhost:8080/api/v1/auth/register');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(mockData);
      req.flush({ success: true });
    });
  });

  describe('logout', () => {
    it('should clear localStorage and navigate to /login', () => {
      const navigateSpy = vi.spyOn(router, 'navigate');
      localStorage.setItem('token', 'mock-token');
      localStorage.setItem('role', 'ROLE_ADMIN');

      service.logout();

      expect(localStorage.getItem('token')).toBeNull();
      expect(localStorage.getItem('role')).toBeNull();
      expect(navigateSpy).toHaveBeenCalledWith(['/login']);
    });
  });

  describe('getToken', () => {
    it('should return token from localStorage', () => {
      localStorage.setItem('token', 'mock-token');
      expect(service.getToken()).toBe('mock-token');
    });

    it('should return null if token is not set', () => {
      expect(service.getToken()).toBeNull();
    });
  });

  describe('getRole', () => {
    it('should return role from localStorage', () => {
      localStorage.setItem('role', 'ROLE_ADMIN');
      expect(service.getRole()).toBe('ROLE_ADMIN');
    });
  });

  describe('isLoggedIn', () => {
    it('should return true if token exists', () => {
      localStorage.setItem('token', 'mock-token');
      expect(service.isLoggedIn()).toBe(true);
    });

    it('should return false if token does not exist', () => {
      expect(service.isLoggedIn()).toBe(false);
    });
  });

  describe('redirectByRole', () => {
    it('should navigate to /admin/dashboard for ROLE_ADMIN', () => {
      const navigateSpy = vi.spyOn(router, 'navigate');
      service.redirectByRole('ROLE_ADMIN');
      expect(navigateSpy).toHaveBeenCalledWith(['/admin/dashboard']);
    });

    it('should navigate to /customer for ROLE_CUSTOMER', () => {
      const navigateSpy = vi.spyOn(router, 'navigate');
      service.redirectByRole('ROLE_CUSTOMER');
      expect(navigateSpy).toHaveBeenCalledWith(['/customer']);
    });

    it('should navigate to / for unknown role', () => {
      const navigateSpy = vi.spyOn(router, 'navigate');
      service.redirectByRole('UNKNOWN');
      expect(navigateSpy).toHaveBeenCalledWith(['/']);
    });
  });
});
