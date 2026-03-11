import { Component, inject, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router, NavigationEnd } from '@angular/router';
import { ClaimsService } from '../services/claims.service';
import { toSignal } from '@angular/core/rxjs-interop';
import { filter, startWith, switchMap, catchError, of } from 'rxjs';

@Component({
  selector: 'app-claims-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './claims-dashboard.html'
})
export class ClaimsDashboard {

  private service = inject(ClaimsService);
  private router = inject(Router);

  userName = '';
  userInitial = 'C';

  constructor() {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    this.userName = user.fullName || 'Claims Officer';
    this.userInitial = this.userName.charAt(0).toUpperCase();
  }

  // Auto-refresh stats whenever the router finishes navigation (e.g. from queue to decisions)
  private claimsList = toSignal(
    this.router.events.pipe(
      filter(e => e instanceof NavigationEnd),
      startWith(null), // fetch immediately on load
      switchMap(() => this.service.getAllClaims().pipe(catchError(() => of([]))))
    ),
    { initialValue: [] as any[] }
  );

  total = computed(() => this.claimsList().length);
  pending = computed(() => this.claimsList().filter(c => c.status === 'PENDING').length);
  investigating = computed(() => this.claimsList().filter(c => c.status === 'UNDER_INVESTIGATION').length);
  approved = computed(() => this.claimsList().filter(c => c.status === 'APPROVED').length);
  rejected = computed(() => this.claimsList().filter(c => c.status === 'REJECTED').length);
  settled = computed(() => this.claimsList().filter(c => c.status === 'SETTLED').length);

  signOut() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    this.router.navigate(['/login']);
  }
}