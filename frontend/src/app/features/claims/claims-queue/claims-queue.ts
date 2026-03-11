import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ClaimsService } from '../services/claims.service';
import { toSignal } from '@angular/core/rxjs-interop';
import { Subject, switchMap, tap, catchError, of, startWith } from 'rxjs';

@Component({
    selector: 'app-claims-queue',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './claims.queue.html'
})
export class ClaimsQueue {
    private service = inject(ClaimsService);
    private router = inject(Router);

    selected = signal<any>(null);
    claimAssessment = signal<any>(null); // New assessment variable
    loading = signal(false);
    isLoaded = signal(false);
    submitting = signal(false);
    successMessage = signal('');

    // Review fields
    rejectionReason = signal('');
    investigationNotes = signal('');
    settlementAmount = signal<number | null>(null);

    // Trigger for manual refreshes
    private refreshTrigger$ = new Subject<void>();

    claims = toSignal(
        this.refreshTrigger$.pipe(
            startWith(null), // Initial load
            tap(() => {
                this.loading.set(true);
                this.isLoaded.set(false);
            }),
            switchMap(() => this.service.getActiveClaims().pipe(
                tap(() => {
                    this.loading.set(false);
                    this.isLoaded.set(true);
                }),
                catchError((err) => {
                    console.error('Error loading active claims', err);
                    this.loading.set(false);
                    this.isLoaded.set(true); // Stop spinner on error
                    return of([]);
                })
            ))
        ),
        { initialValue: [] as any[] }
    );

    openReview(claim: any) {
        this.selected.set(claim);
        this.rejectionReason.set('');
        this.investigationNotes.set('');

        // Provide immediate fallback calculation while system assessment loads
        const deductible = claim.incident?.subscription?.deductible || 0;
        const requested = claim.claimAmount || 0;
        this.settlementAmount.set(Math.max(0, requested - deductible));

        // Load formal System Assessment
        this.claimAssessment.set(null); // Clear previous
        this.service.getClaimAssessment(claim.id).subscribe({
            next: (assessment) => {
                this.claimAssessment.set(assessment);
                // Pre-fill editable settlement box with recommended AI payout
                this.settlementAmount.set(assessment.payableAmount);
            },
            error: (err) => console.error("Could not load formal claim assessment", err)
        });
    }

    closeReview() {
        this.selected.set(null);
    }

    submitDecision(decision: 'SETTLE' | 'REJECTED' | 'UNDER_INVESTIGATION') {
        const currentSelected = this.selected();
        if (!currentSelected) return;

        if (decision === 'REJECTED' && !this.rejectionReason().trim()) {
            alert('Please enter a rejection reason.');
            return;
        }

        if (decision === 'SETTLE' && !this.settlementAmount()) {
            alert('Please enter a settlement amount before approving.');
            return;
        }

        this.submitting.set(true);

        // SETTLE directly calls the /settle endpoint
        if (decision === 'SETTLE') {
            const body = {
                settlementAmount: this.settlementAmount(),
                notes: this.investigationNotes()
            };
            this.service.settleClaim(currentSelected.id, body).subscribe({
                next: () => {
                    this.submitting.set(false);
                    this.successMessage.set(`Claim #${currentSelected.id} settled successfully!`);
                    this.refreshTrigger$.next(); // Instant UI refresh
                    this.closeReview();
                    setTimeout(() => this.successMessage.set(''), 5000);
                    this.router.navigate(['/claims/decisions']);
                },
                error: (err) => {
                    this.submitting.set(false);
                    alert('Error: ' + (err?.error?.message || err?.message || 'Settlement failed'));
                }
            });
            return;
        }

        // REJECTED / UNDER_INVESTIGATION use /review
        const body: any = {
            decision,
            rejectionReason: decision === 'REJECTED' ? this.rejectionReason() : this.investigationNotes()
        };

        this.service.reviewClaim(currentSelected.id, body).subscribe({
            next: () => {
                this.submitting.set(false);
                this.successMessage.set(decision === 'REJECTED'
                    ? `Claim #${currentSelected.id} rejected.`
                    : `Claim #${currentSelected.id} marked for investigation.`);
                this.refreshTrigger$.next(); // Instant UI refresh
                this.closeReview();
                setTimeout(() => this.successMessage.set(''), 4000);
                this.router.navigate(['/claims/decisions']);
            },
            error: (err) => {
                this.submitting.set(false);
                alert('Error: ' + (err?.error?.message || err?.message || 'Submission failed'));
            }
        });
    }

    statusClass(status: string) {
        return {
            'bg-yellow-500/20 text-yellow-300 border border-yellow-500/40': status === 'PENDING',
            'bg-blue-500/20 text-blue-300 border border-blue-500/40': status === 'UNDER_INVESTIGATION',
            'bg-emerald-500/20 text-emerald-300 border border-emerald-500/40': status === 'APPROVED',
            'bg-red-500/20 text-red-300 border border-red-500/40': status === 'REJECTED',
            'bg-cyan-500/20 text-cyan-300 border border-cyan-500/40': status === 'SETTLED',
        };
    }

    formatCurrency(val: number): string {
        if (!val) return '₹0';
        return '₹' + val.toLocaleString('en-IN', { maximumFractionDigits: 2 });
    }

    statusLabel(status: string): string {
        return status?.replace(/_/g, ' ') || '—';
    }
}