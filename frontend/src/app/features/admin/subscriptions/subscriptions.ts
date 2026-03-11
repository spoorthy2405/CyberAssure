import { Component, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { toSignal, toObservable } from '@angular/core/rxjs-interop';
import { catchError, of, switchMap, tap } from 'rxjs';
import { AdminApiService } from '../services/admin.service';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './subscriptions.html'
})
export class AdminSubscriptions {

  private api = inject(AdminApiService);

  filterStatus = signal('ALL');
  showAssignModal = signal(false);
  selectedSub = signal<any>(null);
  selectedUnderwriterId = signal<number | null>(null);
  assigning = signal(false);
  successMessage = signal('');

  // Refresh counter — increment to re-fetch subscriptions
  private refreshCounter = signal(0);

  private isLoaded = signal(false);

  // Proper Angular 21 refreshable pattern: toObservable(signal) → switchMap → toSignal
  subscriptions = toSignal(
    toObservable(this.refreshCounter).pipe(
      switchMap(() => this.api.getSubscriptions().pipe(
        tap(() => this.isLoaded.set(true)),
        catchError(() => { this.isLoaded.set(true); return of([]); })
      ))
    ),
    { initialValue: [] as any[] }
  );

  underwriters = toSignal(
    this.api.getUnderwriters().pipe(catchError(() => of([]))),
    { initialValue: [] as any[] }
  );

  loading = computed(() => !this.isLoaded());

  filtered = computed(() => {
    const status = this.filterStatus();
    if (status === 'ALL') return this.subscriptions();
    return this.subscriptions().filter((s: any) => s.status === status);
  });

  openAssign(sub: any) {
    this.selectedSub.set(sub);
    this.selectedUnderwriterId.set(sub.assignedUnderwriter?.userId ?? null);
    this.assigning.set(false);
    this.successMessage.set('');
    this.showAssignModal.set(true);
  }

  closeModal() {
    this.showAssignModal.set(false);
    this.selectedSub.set(null);
    this.selectedUnderwriterId.set(null);
    this.assigning.set(false);
  }

  confirmAssign() {
    const subId = this.selectedSub()?.id;
    const uwId  = this.selectedUnderwriterId();
    if (!subId || !uwId) return;

    this.assigning.set(true);
    this.api.assignUnderwriter(subId, uwId).subscribe({
      next: () => {
        // Increment → toObservable emits → switchMap re-fetches → toSignal updates → UI refreshes
        this.refreshCounter.update(v => v + 1);
        this.assigning.set(false);
        this.closeModal();
        this.successMessage.set('Underwriter successfully assigned!');
        setTimeout(() => this.successMessage.set(''), 3000);
      },
      error: (err: any) => {
        this.assigning.set(false);
        alert('Error: ' + (err?.error?.message || err?.message || 'Assignment failed.'));
      }
    });
  }

  statusClass(status: string) {
    return {
      'bg-yellow-500/20 text-yellow-300 border-yellow-500/40': status === 'PENDING',
      'bg-emerald-500/20 text-emerald-300 border-emerald-500/40': status === 'APPROVED',
      'bg-red-500/20 text-red-300 border-red-500/40': status === 'REJECTED',
    };
  }

  getCount(status: string): number {
    return this.subscriptions().filter((s: any) => s.status === status).length;
  }

  getProofFiles(sub: any): string[] {
    const paths = sub?.riskAssessment?.proofDocumentPaths;
    if (!paths) return [];
    return paths.split(',').filter((p: string) => p.trim());
  }

  getFileName(path: string): string {
    if (!path) return '';
    return path.split(/[\\\/]/).pop() || '';
  }
}
