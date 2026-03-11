import { Component, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { toSignal } from '@angular/core/rxjs-interop';
import { map, catchError, of } from 'rxjs';
import { AdminApiService } from '../services/admin.service';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.html'
})
export class AdminDashboard {

  private adminApi = inject(AdminApiService);

  // Modal State
  showClaimsModal = signal(false);
  showPoliciesModal = signal(false);

  // Stats — converted from Observable to Signal (auto-triggers re-render)
  private statsRaw = toSignal(
    this.adminApi.getDashboardStats().pipe(
      catchError(() => of(null))
    ),
    { initialValue: null }
  );

  stats = computed(() => {
    const data = this.statsRaw();
    return {
      totalCustomers: data?.totalCustomers ?? 0,
      totalPolicies: data?.totalPolicies ?? 0,
      totalUnderwriters: data?.totalUnderwriters ?? 0,
      totalClaimsOfficers: data?.totalClaimsOfficers ?? 0,
      totalClaims: data?.totalClaims ?? 0,
      activeClaims: data?.activeClaims ?? 0,
      approvedClaims: data?.approvedClaims ?? 0,
      rejectedClaims: data?.rejectedClaims ?? 0
    };
  });

  // Claims — Signal from Observable
  private claimsRaw = toSignal(
    this.adminApi.getClaims().pipe(
      map(claims => claims.map(c => ({
        id: '#CLM-' + c.id,
        customer: c.customer?.companyName || c.customer?.fullName || 'Unknown',
        amount: '₹' + (c.claimAmount || 0).toLocaleString('en-IN'),
        status: c.status,
        date: c.filedAt ? new Date(c.filedAt).toLocaleDateString() : 'N/A'
      }))),
      catchError(() => of([]))
    ),
    { initialValue: [] }
  );

  allClaims = computed(() => this.claimsRaw());
  recentClaims = computed(() => this.claimsRaw().slice(0, 5));

  // Policies — Signal from Observable
  private policiesRaw = toSignal(
    this.adminApi.getPolicies().pipe(
      map(policies => policies.map(p => ({
        customer: p.customer?.companyName || p.customer?.fullName || 'Unknown',
        type: p.policy?.policyName || 'Standard Policy',
        premium: '₹' + (p.calculatedPremium || 0).toLocaleString('en-IN') + '/yr',
        status: p.status,
        date: p.createdAt ? new Date(p.createdAt).toLocaleDateString() : 'N/A'
      }))),
      catchError(() => of([]))
    ),
    { initialValue: [] }
  );

  allPolicies = computed(() => this.policiesRaw());
  recentPolicies = computed(() => this.policiesRaw().slice(0, 5));

  // --- Modal Triggers ---

  openClaimsDirectory() {
    this.showClaimsModal.set(true);
  }

  closeClaimsDirectory() {
    this.showClaimsModal.set(false);
  }

  openPolicyCatalog() {
    this.showPoliciesModal.set(true);
  }

  closePolicyCatalog() {
    this.showPoliciesModal.set(false);
  }
}