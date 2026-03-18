import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { CustomerService } from './customer.service';
import { describe, it, expect, beforeEach, afterEach } from 'vitest';

describe('CustomerService', () => {
  let service: CustomerService;
  let httpMock: HttpTestingController;
  const BASE_URL = 'http://localhost:8080/api/v1';

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        CustomerService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(CustomerService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get dashboard stats', () => {
    const mockStats = { totalActivePolicies: 2 };
    service.getDashboardStats().subscribe(stats => {
      expect(stats).toEqual(mockStats);
    });

    const req = httpMock.expectOne(`${BASE_URL}/customer/dashboard`);
    expect(req.request.method).toBe('GET');
    req.flush(mockStats);
  });

  it('should get recommended policies', () => {
    const mockPolicies = [{ id: 1, name: 'Cyber Basic' }];
    service.getRecommendedPolicies().subscribe(policies => {
      expect(policies).toEqual(mockPolicies);
    });

    const req = httpMock.expectOne(`${BASE_URL}/customer/recommended-policies`);
    expect(req.request.method).toBe('GET');
    req.flush(mockPolicies);
  });

  it('should apply for a policy', () => {
    const formData = new FormData();
    service.applyForPolicy(formData).subscribe(response => {
      expect(response).toBe('Success');
    });

    const req = httpMock.expectOne(`${BASE_URL}/customer/apply`);
    expect(req.request.method).toBe('POST');
    req.flush('Success');
  });

  it('should pay for a subscription', () => {
    service.paySubscription(1).subscribe(response => {
      expect(response).toBeDefined();
    });

    const req = httpMock.expectOne(`${BASE_URL}/subscriptions/1/pay`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({});
    req.flush({ success: true });
  });

  it('should report an incident', () => {
    const formData = new FormData();
    const mockIncident = { id: 1, description: 'Test Incident' };
    service.reportIncident(formData).subscribe(incident => {
      expect(incident).toEqual(mockIncident);
    });

    const req = httpMock.expectOne(`${BASE_URL}/incidents`);
    expect(req.request.method).toBe('POST');
    req.flush(mockIncident);
  });

  it('should file a claim', () => {
    const body = { incidentId: 1, reason: 'Loss' };
    const mockClaim = { id: 1, status: 'PENDING' };
    service.fileClaim(body).subscribe(claim => {
      expect(claim).toEqual(mockClaim);
    });

    const req = httpMock.expectOne(`${BASE_URL}/claims`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(body);
    req.flush(mockClaim);
  });
});
