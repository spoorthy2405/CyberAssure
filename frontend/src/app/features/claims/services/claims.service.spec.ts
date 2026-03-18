import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { ClaimsService } from './claims.service';
import { describe, it, expect, beforeEach, afterEach } from 'vitest';

describe('ClaimsService', () => {
  let service: ClaimsService;
  let httpMock: HttpTestingController;
  const API_URL = 'http://localhost:8080/api/v1/claims';

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ClaimsService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(ClaimsService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get all claims', () => {
    const mockClaims = [{ id: 1, title: 'Claim 1' }];
    service.getAllClaims().subscribe(claims => {
      expect(claims).toEqual(mockClaims);
    });

    const req = httpMock.expectOne(`${API_URL}`);
    expect(req.request.method).toBe('GET');
    req.flush(mockClaims);
  });

  it('should get active claims', () => {
    const mockClaims = [{ id: 1, status: 'PENDING' }];
    service.getActiveClaims().subscribe(claims => {
      expect(claims).toEqual(mockClaims);
    });

    const req = httpMock.expectOne(`${API_URL}/active`);
    expect(req.request.method).toBe('GET');
    req.flush(mockClaims);
  });

  it('should review a claim', () => {
    const body = { status: 'APPROVED' };
    service.reviewClaim(1, body).subscribe(response => {
      expect(response).toBeDefined();
    });

    const req = httpMock.expectOne(`${API_URL}/1/review`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(body);
    req.flush({ id: 1, status: 'APPROVED' });
  });

  it('should get claim assessment', () => {
    service.getClaimAssessment(1).subscribe(response => {
      expect(response).toEqual({ risk: 'LOW' });
    });

    const req = httpMock.expectOne(`${API_URL}/1/assessment`);
    expect(req.request.method).toBe('GET');
    req.flush({ risk: 'LOW' });
  });
});
