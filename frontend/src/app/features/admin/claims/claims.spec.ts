import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Claims } from './claims';
import { AdminApiService } from '../services/admin.service';
import { of } from 'rxjs';
import { vi } from 'vitest';

describe('Claims', () => {
  let component: Claims;
  let fixture: ComponentFixture<Claims>;
  let adminApiMock: any;

  beforeEach(async () => {
    adminApiMock = {
      getClaims: vi.fn().mockReturnValue(of([])),
      getClaimsOfficers: vi.fn().mockReturnValue(of([])),
      assignClaimsOfficer: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [Claims],
      providers: [
        { provide: AdminApiService, useValue: adminApiMock }
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(Claims);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
