import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CustomerService } from '../services/customer.service';
import { timeout, catchError } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-customer-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './customer-dashboard.html'
})
export class CustomerDashboard implements OnInit {

  stats: any = null;
  claims: any[] = [];
  loading = false;      // Start as FALSE — show page immediately
  claimsLoading = false;
  dataFetching = true;  // Small indicator, not full-page block

  // File a Claim modal
  showClaimModal = false;
  myIncidents: any[] = [];
  claimData = { incidentId: null as number | null, claimAmount: null as number | null };
  claimSubmitting = false;
  claimError = '';

  today = new Date().toLocaleDateString('en-IN', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' });

  constructor(private service: CustomerService) { }

  ngOnInit() {
    this.dataFetching = true;

    // DASHBOARD STATS — 5 second max wait
    this.service.getDashboardStats().pipe(
      timeout(5000),
      catchError(err => {
        console.error('Dashboard API error (or timeout):', err);
        return of(null);
      })
    ).subscribe(data => {
      if (data) this.stats = data;
      this.dataFetching = false;
      console.log('Dashboard data:', data);
    });

    // CLAIMS — 5 second max wait
    this.service.getClaims().pipe(
      timeout(5000),
      catchError(err => {
        console.error('Claims API error:', err);
        return of([]);
      })
    ).subscribe(data => {
      this.claims = data || [];
    });
  }

  get riskScore() { return this.stats?.latestRiskScore ?? null; }
  get riskLevel() { return this.stats?.latestRiskLevel ?? 'UNKNOWN'; }

  get riskStrokeDashoffset() {
    const circumference = 2 * Math.PI * 40;
    const score = this.riskScore ?? 0;
    return circumference - (score / 100) * circumference;
  }

  get riskColor() {
    const level = this.riskLevel?.toUpperCase();
    if (level === 'LOW') return '#10b981';
    if (level === 'HIGH') return '#ef4444';
    return '#f59e0b';
  }

  get coverageDisplay() {
    const val = this.stats?.totalCoverage ?? 0;
    if (val >= 10000000) return `₹${(val / 10000000).toFixed(1)} Cr`;
    if (val >= 100000) return `₹${(val / 100000).toFixed(0)} L`;
    if (val > 0) return `₹${val}`;
    return '—';
  }

  get premiumDisplay(): string {
    const val = Number(this.stats?.calculatedPremium ?? 0);
    if (val >= 100000) return `₹${(val / 100000).toFixed(2)} L`;
    if (val > 0) return `₹${val.toLocaleString('en-IN', { maximumFractionDigits: 2 })}`;
    return '—';
  }

  get coverageGrantedDisplay(): string {
    const val = Number(this.stats?.coverageAmount ?? 0);
    if (val >= 10000000) return `₹${(val / 10000000).toFixed(1)} Cr`;
    if (val >= 100000) return `₹${(val / 100000).toFixed(0)} L`;
    if (val > 0) return `₹${val.toLocaleString('en-IN')}`;
    return '—';
  }

  get claimedDisplay() {
    const val = this.stats?.claimedAmountYearly ?? 0;
    if (val >= 100000) return `₹${(val / 100000).toFixed(0)}L`;
    return `₹${val}`;
  }

  openClaimModal() {
    this.claimError = '';
    this.claimData = { incidentId: null, claimAmount: null };
    this.showClaimModal = true;
    this.service.getMyIncidents().pipe(
      catchError(() => of([]))
    ).subscribe(incidents => { this.myIncidents = incidents; });
  }

  closeClaimModal() { this.showClaimModal = false; }

  submitClaim() {
    if (!this.claimData.incidentId || !this.claimData.claimAmount) {
      this.claimError = 'Please select an incident and enter a claim amount.';
      return;
    }
    this.claimSubmitting = true;
    this.service.fileClaim(this.claimData).subscribe({
      next: () => {
        this.claimSubmitting = false;
        this.showClaimModal = false;
        this.service.getClaims().pipe(catchError(() => of([]))).subscribe(data => this.claims = data);
        this.service.getDashboardStats().pipe(catchError(() => of(null))).subscribe(data => { if (data) this.stats = data; });
      },
      error: (err) => {
        this.claimSubmitting = false;
        this.claimError = err.error?.message || 'Failed to file claim.';
      }
    });
  }
}