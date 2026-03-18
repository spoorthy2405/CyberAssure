import 'zone.js';
import 'zone.js/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Header } from './header';
import { provideRouter } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { vi, describe, it, expect, beforeEach, afterEach } from 'vitest';

describe('Header', () => {
  let component: Header;
  let fixture: ComponentFixture<Header>;

  beforeEach(async () => {
    localStorage.clear();

    await TestBed.configureTestingModule({
      imports: [Header],
      providers: [
        { provide: AuthService, useValue: { logout: vi.fn() } },
        provideRouter([])
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Header);
    component = fixture.componentInstance;
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should toggle dropdown', () => {
    fixture.detectChanges();
    expect(component.showDropdown).toBe(false);
    component.toggleDropdown();
    expect(component.showDropdown).toBe(true);
  });
});
