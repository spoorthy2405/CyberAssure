import 'zone.js';
import 'zone.js/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AdminDashboard } from './admin-dashboard';
import { AdminApiService } from '../services/admin.service';
import { of } from 'rxjs';
import { describe, it, expect, beforeEach, vi } from 'vitest';

describe('AdminDashboard', () => {
  let component: AdminDashboard;
  let fixture: ComponentFixture<AdminDashboard>;
  let adminApiMock: any;

  beforeEach(async () => {
    adminApiMock = {
      getDashboardStats: vi.fn().mockReturnValue(of({
        totalCustomers: 10,
        totalPolicies: 5
      })),
      getClaims: vi.fn().mockReturnValue(of([])),
      getPolicies: vi.fn().mockReturnValue(of([]))
    };

    await TestBed.configureTestingModule({
      imports: [AdminDashboard],
      providers: [
        { provide: AdminApiService, useValue: adminApiMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AdminDashboard);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should calculate stats from raw data', () => {
    fixture.detectChanges();
    expect(component.stats().totalCustomers).toBe(10);
    expect(component.stats().totalPolicies).toBe(5);
  });
});
