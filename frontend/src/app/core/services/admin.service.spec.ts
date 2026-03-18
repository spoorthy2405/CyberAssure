import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { AdminService } from './admin.service';
import { describe, it, expect, beforeEach, afterEach } from 'vitest';

describe('AdminService', () => {
  let service: AdminService;
  let httpMock: HttpTestingController;
  const API_URL = 'http://localhost:8080/api/v1';

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AdminService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(AdminService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch dashboard stats', () => {
    const mockStats = { totalUsers: 10, activePolicies: 5 };
    service.getDashboardStats().subscribe(stats => {
      expect(stats).toEqual(mockStats);
    });

    const req = httpMock.expectOne(`${API_URL}/admin/dashboard`);
    expect(req.request.method).toBe('GET');
    req.flush(mockStats);
  });

  it('should fetch users', () => {
    const mockUsers = [{ id: 1, email: 'admin@test.com' }];
    service.getUsers().subscribe(users => {
      expect(users).toEqual(mockUsers);
    });

    const req = httpMock.expectOne(`${API_URL}/admin/users`);
    expect(req.request.method).toBe('GET');
    req.flush(mockUsers);
  });

  it('should assign a claims officer', () => {
    service.assignClaimsOfficer(1, 2).subscribe(response => {
      expect(response).toBeDefined();
    });

    const req = httpMock.expectOne(`${API_URL}/admin/claims/1/assign`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual({ officerId: 2 });
    req.flush({ success: true });
  });

  it('should assign an underwriter', () => {
    service.assignUnderwriter(1, 2).subscribe(response => {
      expect(response).toBeDefined();
    });

    const req = httpMock.expectOne(`${API_URL}/admin/subscriptions/1/assign`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual({ underwriterId: 2 });
    req.flush({ success: true });
  });
});
