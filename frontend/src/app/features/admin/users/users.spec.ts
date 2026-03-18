import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Users } from './users';
import { AdminApiService } from '../../../core/services/admin-api-service';
import { of } from 'rxjs';
import { vi } from 'vitest';

describe('Users', () => {
  let component: Users;
  let fixture: ComponentFixture<Users>;
  let adminApiMock: any;

  beforeEach(async () => {
    adminApiMock = {
      getCustomers: vi.fn().mockReturnValue(of([]))
    };

    await TestBed.configureTestingModule({
      imports: [Users],
      providers: [
        { provide: AdminApiService, useValue: adminApiMock }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Users);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
