import { Component, inject, signal, computed } from '@angular/core';
import { RouterModule } from '@angular/router';

import { Router } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { catchError, of } from 'rxjs';
import { UnderwriterService } from '../services/underwriter.service';

@Component({
  selector: 'app-underwriter-dashboard',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './underwriter-dashboard.html'
})
export class UnderwriterDashboard {

  private service = inject(UnderwriterService);
  private router = inject(Router);

  // Load subscriptions via toSignal — auto-triggers re-render when HTTP responds
  private allSubs = toSignal(
    this.service.getMyAssignedSubscriptions().pipe(catchError(() => of([]))),
    { initialValue: [] as any[] }
  );

  total    = computed(() => this.allSubs().length);
  pending  = computed(() => this.allSubs().filter((s: any) => s.status === 'PENDING').length);
  approved = computed(() => this.allSubs().filter((s: any) => s.status === 'APPROVED').length);
  rejected = computed(() => this.allSubs().filter((s: any) => s.status === 'REJECTED').length);

  userName = signal('Underwriter');
  userInitial = signal('U');

  constructor() {
    // Read user info from localStorage synchronously — no async needed
    const stored = localStorage.getItem('user') || sessionStorage.getItem('user');
    if (stored) {
      try {
        const user = JSON.parse(stored);
        const name = user.fullName || user.name || 'Underwriter';
        this.userName.set(name);
        this.userInitial.set(name.charAt(0).toUpperCase());
      } catch (e) { }
    }
  }

  signOut() {
    localStorage.clear();
    sessionStorage.clear();
    this.router.navigate(['/login']);
  }
}