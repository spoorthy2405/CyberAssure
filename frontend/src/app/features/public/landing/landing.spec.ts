import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { Landing } from './landing';
import { provideRouter } from '@angular/router';
import { describe, it, expect, beforeEach, vi, afterEach } from 'vitest';

describe('Landing Component', () => {
  let component: Landing;
  let fixture: ComponentFixture<Landing>;

  beforeEach(async () => {
    // Mock IntersectionObserver
    const mockIntersectionObserver = vi.fn();
    mockIntersectionObserver.prototype.observe = vi.fn();
    mockIntersectionObserver.prototype.unobserve = vi.fn();
    mockIntersectionObserver.prototype.disconnect = vi.fn();
    vi.stubGlobal('IntersectionObserver', mockIntersectionObserver);

    await TestBed.configureTestingModule({
      imports: [Landing],
      providers: [
        provideRouter([])
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Landing);
    component = fixture.componentInstance;
  });

  afterEach(() => {
    vi.unstubAllGlobals();
    vi.useRealTimers();
    if (component && component.ngOnDestroy) {
      component.ngOnDestroy();
    }
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should increment attackCount over time', () => {
    vi.useFakeTimers();
    fixture.detectChanges(); // Starts interval
    const initialCount = component.attackCount;
    
    vi.advanceTimersByTime(3100);
    fixture.detectChanges();
    
    expect(component.attackCount).toBeGreaterThan(initialCount);
  });

  it('should set active industry', () => {
    fixture.detectChanges();
    component.setIndustry('finance');
    expect(component.activeIndustry).toBe('finance');
    expect(component.currentIndustry.title).toBe('Finance & BFSI');
  });

  it('should toggle FAQ', () => {
    fixture.detectChanges();
    const initialStatus = component.faqs[0].open;
    component.toggleFaq(0);
    expect(component.faqs[0].open).toBe(!initialStatus);
  });
});
