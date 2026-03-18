import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { authGuard } from './auth.guard';
import { AuthService } from '../services/auth.service';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { describe, it, expect, beforeEach, vi } from 'vitest';

describe('authGuard', () => {
  let authServiceMock: any;
  let routerMock: any;

  beforeEach(() => {
    authServiceMock = {
      isLoggedIn: vi.fn(),
      getRole: vi.fn(),
    };
    routerMock = {
      navigate: vi.fn(),
    };

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock },
      ],
    });
  });

  const runGuard = (route: Partial<ActivatedRouteSnapshot> = {}, state: Partial<RouterStateSnapshot> = {}) => {
    return TestBed.runInInjectionContext(() => authGuard(route as ActivatedRouteSnapshot, state as RouterStateSnapshot));
  };

  it('should return true if user is logged in and no roles required', () => {
    authServiceMock.isLoggedIn.mockReturnValue(true);
    const result = runGuard({ data: {} });
    expect(result).toBe(true);
  });

  it('should navigate to /login and return false if user is not logged in', () => {
    authServiceMock.isLoggedIn.mockReturnValue(false);
    const result = runGuard();
    expect(result).toBe(false);
    expect(routerMock.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should return true if user has required role', () => {
    authServiceMock.isLoggedIn.mockReturnValue(true);
    authServiceMock.getRole.mockReturnValue('ROLE_ADMIN');
    const result = runGuard({ data: { roles: ['ROLE_ADMIN'] } });
    expect(result).toBe(true);
  });

  it('should navigate to / and return false if user does not have required role', () => {
    authServiceMock.isLoggedIn.mockReturnValue(true);
    authServiceMock.getRole.mockReturnValue('ROLE_CUSTOMER');
    const result = runGuard({ data: { roles: ['ROLE_ADMIN'] } });
    expect(result).toBe(false);
    expect(routerMock.navigate).toHaveBeenCalledWith(['/']);
  });
});
