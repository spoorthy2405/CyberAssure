import { TestBed } from '@angular/core/testing';
import { HttpClient, provideHttpClient, withInterceptors, HttpErrorResponse } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { errorInterceptor } from './error.interceptor';
import { Router } from '@angular/router';
import { provideRouter } from '@angular/router';
import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest';

describe('errorInterceptor', () => {
  let httpMock: HttpTestingController;
  let httpClient: HttpClient;
  let router: Router;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([errorInterceptor])),
        provideHttpClientTesting(),
        provideRouter([]),
      ],
    });

    httpClient = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
    localStorage.clear();
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should clear localStorage and navigate to /login on 401 error', () => {
    const navigateSpy = vi.spyOn(router, 'navigate');
    localStorage.setItem('token', 'some-token');

    httpClient.get('/api/test').subscribe({
      error: (err) => expect(err.status).toBe(401)
    });

    const req = httpMock.expectOne('/api/test');
    req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });

    expect(localStorage.getItem('token')).toBeNull();
    expect(navigateSpy).toHaveBeenCalledWith(['/login']);
  });

  it('should clear localStorage and navigate to /login on 403 error', () => {
    const navigateSpy = vi.spyOn(router, 'navigate');
    localStorage.setItem('token', 'some-token');

    httpClient.get('/api/test').subscribe({
      error: (err) => expect(err.status).toBe(403)
    });

    const req = httpMock.expectOne('/api/test');
    req.flush('Forbidden', { status: 403, statusText: 'Forbidden' });

    expect(localStorage.getItem('token')).toBeNull();
    expect(navigateSpy).toHaveBeenCalledWith(['/login']);
  });

  it('should not navigate and just throw error for other statuses', () => {
    const navigateSpy = vi.spyOn(router, 'navigate');
    
    httpClient.get('/api/test').subscribe({
      error: (err) => expect(err.status).toBe(400)
    });

    const req = httpMock.expectOne('/api/test');
    req.flush('Bad Request', { status: 400, statusText: 'Bad Request' });

    expect(navigateSpy).not.toHaveBeenCalled();
  });
});
