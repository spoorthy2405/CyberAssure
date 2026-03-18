import { Component, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router, NavigationEnd } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CustomerService } from '../services/customer.service';
import { catchError, filter, startWith, switchMap, tap } from 'rxjs/operators';
import { of } from 'rxjs';
import { toSignal } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-customer-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './customer-dashboard.html'
})
export class CustomerDashboard {
  private service = inject(CustomerService);
  private router = inject(Router);

  // Trigger data fetch on navigation to ensure freshness
  private refresh$ = this.router.events.pipe(
    filter(event => event instanceof NavigationEnd),
    startWith(null)
  );

  //Triggers the stream immediately when component loads.
  loading = signal(true);

  // Dashboard Stats Signal
  //Whenever refresh happens → fetch stats.
  stats = toSignal(
    this.refresh$.pipe(
      tap(() => this.loading.set(true)),
      switchMap(() => this.service.getDashboardStats().pipe(
        catchError((err) => {
          console.error('Dashboard Stats Error:', err);
          return of(null);
        })
      )),
      tap(() => this.loading.set(false))
    ),
    { initialValue: null }
  );

  // Claims Signal
  claimsList = toSignal(
    this.refresh$.pipe(
      switchMap(() => this.service.getClaims().pipe(
        catchError(err => {
          console.error('Claims API error:', err);
          return of([]);
        })
      ))
    ),
    { initialValue: [] as any[] }
  );

  // Modal State Signals
  showClaimModal = signal(false);
  myIncidents = signal<any[]>([]);
  claimSubmitting = signal(false);
  claimError = signal('');
  claimData = signal({ incidentId: null as number | null, claimAmount: null as number | null });

  today = new Date().toLocaleDateString('en-IN', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' });

  // Computed Properties for Display Logic
  riskScore = computed(() => this.stats()?.latestRiskScore ?? null);
  riskLevel = computed(() => this.stats()?.latestRiskLevel ?? 'UNKNOWN');

  riskStrokeDashoffset = computed(() => {
    const circumference = 2 * Math.PI * 40;
    const score = this.riskScore() ?? 0;
    return circumference - (score / 100) * circumference;
  });

  riskColor = computed(() => {
    const level = this.riskLevel()?.toUpperCase();
    if (level === 'LOW') return '#10b981';
    if (level === 'HIGH') return '#ef4444';
    return '#f59e0b';
  });

  coverageDisplay = computed(() => {
    const val = this.stats()?.totalCoverage ?? 0;
    if (val >= 10000000) return `₹${(val / 10000000).toFixed(1)} Cr`;
    if (val >= 100000) return `₹${(val / 100000).toFixed(0)} L`;
    if (val > 0) return `₹${val}`;
    return '—';
  });

  premiumDisplay = computed(() => {
    const val = Number(this.stats()?.calculatedPremium ?? 0);
    if (val >= 100000) return `₹${(val / 100000).toFixed(2)} L`;
    if (val > 0) return `₹${val.toLocaleString('en-IN', { maximumFractionDigits: 2 })}`;
    return '—';
  });

  coverageGrantedDisplay = computed(() => {
    const val = Number(this.stats()?.coverageAmount ?? 0);
    if (val >= 10000000) return `₹${(val / 10000000).toFixed(1)} Cr`;
    if (val >= 100000) return `₹${(val / 100000).toFixed(0)} L`;
    if (val > 0) return `₹${val.toLocaleString('en-IN')}`;
    return '—';
  });

  policyLimitDisplay = computed(() => {
    const val = Number(this.stats()?.policyLimit ?? 0);
    if (val >= 10000000) return `₹${(val / 10000000).toFixed(1)} Cr`;
    if (val >= 100000) return `₹${(val / 100000).toFixed(0)} L`;
    if (val > 0) return `₹${val.toLocaleString('en-IN')}`;
    return '—';
  });

  deductibleDisplay = computed(() => {
    const val = Number(this.stats()?.deductible ?? 0);
    if (val >= 100000) return `₹${(val / 100000).toFixed(2)} L`;
    if (val > 0) return `₹${val.toLocaleString('en-IN')}`;
    return '—';
  });

  exclusionsList = computed(() => {
    const exc = this.stats()?.exclusions;
    if (!exc) return [];
    if (exc.includes('. ')) {
      return exc.split('. ').map((s: string) => s.trim()).filter((s: string) => s.length > 0)
        .map((s: string) => s.endsWith('.') ? s : s + '.');
    }
    return [exc];
  });

  claimedDisplay = computed(() => {
    const val = this.stats()?.claimedAmountYearly ?? 0;
    if (val >= 100000) return `₹${(val / 100000).toFixed(0)}L`;
    return `₹${val}`;
  });

  openClaimModal() {
    this.claimError.set('');
    this.claimData.set({ incidentId: null, claimAmount: null });
    this.showClaimModal.set(true);
    this.service.getMyIncidents().subscribe(incidents => {
      this.myIncidents.set(incidents);
    });
  }

  closeClaimModal() {
    this.showClaimModal.set(false);
  }

  submitClaim() {
    const data = this.claimData();
    if (!data.incidentId || !data.claimAmount) {
      this.claimError.set('Please select an incident and enter a claim amount.');
      return;
    }
    this.claimSubmitting.set(true);
    this.service.fileClaim(data).subscribe({
      next: () => {
        this.claimSubmitting.set(false);
        this.showClaimModal.set(false);
        // Force refresh by navigating to same route or handling internally (handled by service normally, but here we can just reload the router)
        this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
          this.router.navigate(['/customer/dashboard']);
        });
      },
      error: (err) => {
        this.claimSubmitting.set(false);
        this.claimError.set(err.error?.message || 'Failed to file claim.');
      }
    });
  }

  updateClaimData(field: 'incidentId' | 'claimAmount', value: any) {
    this.claimData.update(curr => ({ ...curr, [field]: value }));
  }
}