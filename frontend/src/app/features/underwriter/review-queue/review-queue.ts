import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UnderwriterService } from '../services/underwriter.service';

@Component({
    selector: 'app-review-queue',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './review-queue.html',
    styleUrl: './review-queue.css'
})
export class ReviewQueue implements OnInit {

    applications: any[] = [];
    selected: any = null;
    loading = false;
    submitting = false;
    successMessage = '';

    // Form fields
    riskScore: number | null = null;
    tenureMonths: number = 12;
    rejectionReason: string = '';
    underwriterNotes: string = '';

    // Auto-calculated (shown as suggestions)
    suggestedPremium: number | null = null;
    suggestedCoverage: number | null = null;

    constructor(private service: UnderwriterService) { }

    ngOnInit(): void {
        this.loadApplications();
    }

    loadApplications() {
        this.loading = true;
        this.service.getMyAssignedSubscriptions().subscribe({
            next: (data) => {
                this.applications = data || [];
                this.loading = false;
            },
            error: (err) => {
                console.error('Error loading assigned applications', err);
                this.loading = false;
            }
        });
    }

    openReview(app: any) {
        this.selected = app;
        this.riskScore = null;
        this.tenureMonths = app.policy?.durationMonths || 12;
        this.rejectionReason = '';
        this.underwriterNotes = '';
        this.suggestedPremium = null;
        this.suggestedCoverage = null;
    }

    closeReview() {
        this.selected = null;
    }

    get riskLevel(): string {
        if (this.riskScore === null) return '—';
        if (this.riskScore <= 40) return 'LOW';
        if (this.riskScore <= 70) return 'MEDIUM';
        return 'HIGH';
    }

    get riskLevelClass(): string {
        if (this.riskLevel === 'LOW') return 'text-emerald-400 bg-emerald-500/20 border-emerald-500/40';
        if (this.riskLevel === 'MEDIUM') return 'text-yellow-400 bg-yellow-500/20 border-yellow-500/40';
        if (this.riskLevel === 'HIGH') return 'text-red-400 bg-red-500/20 border-red-500/40';
        return 'text-slate-400 bg-slate-500/20 border-slate-500/40';
    }

    get riskMultiplier(): number {
        if (this.riskLevel === 'LOW') return 1.0;
        if (this.riskLevel === 'MEDIUM') return 1.3;
        if (this.riskLevel === 'HIGH') return 1.7;
        return 1.0;
    }

    get coverageRatio(): number {
        if (this.riskLevel === 'LOW') return 1.0;
        if (this.riskLevel === 'MEDIUM') return 0.80;
        if (this.riskLevel === 'HIGH') return 0.60;
        return 1.0;
    }

    /**
     * Formula: Premium = basePremium × riskMultiplier × (tenure / 12)
     */
    get calculatedPremium(): number {
        if (!this.selected || this.riskScore === null) return 0;
        const base = Number(this.selected.policy?.basePremium || 0);
        return base * this.riskMultiplier * (this.tenureMonths / 12);
    }

    /**
     * Formula: Coverage = maxCoverage × coverageRatio
     *   LOW → 100%, MEDIUM → 80%, HIGH → 60%
     */
    get calculatedCoverage(): number {
        if (!this.selected || this.riskScore === null) return 0;
        const max = Number(this.selected.policy?.coverageLimit || 0);
        return max * this.coverageRatio;
    }

    onRiskScoreChange() {
        // Triggers re-computation via getters
    }

    submitDecision(decision: 'APPROVED' | 'REJECTED') {
        if (this.riskScore === null || this.riskScore < 0 || this.riskScore > 100) {
            alert('Please enter a valid Risk Score (0–100).');
            return;
        }
        if (decision === 'REJECTED' && !this.rejectionReason.trim()) {
            alert('Please enter a rejection reason.');
            return;
        }

        this.submitting = true;

        const body = {
            decision,
            riskScore: this.riskScore,
            tenureMonths: this.tenureMonths,
            coverageAmount: this.calculatedCoverage,
            underwriterNotes: this.underwriterNotes,
            rejectionReason: this.rejectionReason || null
        };

        this.service.reviewSubscription(this.selected.id, body).subscribe({
            next: (updated) => {
                // Update the row in-place
                const idx = this.applications.findIndex(a => a.id === this.selected.id);
                if (idx !== -1) {
                    this.applications[idx] = updated;
                }
                this.submitting = false;
                this.successMessage = decision === 'APPROVED'
                    ? `✅ Application CA-${this.selected.id} approved successfully!`
                    : `❌ Application CA-${this.selected.id} rejected.`;
                this.closeReview();
                setTimeout(() => this.successMessage = '', 4000);
            },
            error: (err) => {
                this.submitting = false;
                alert('Error: ' + (err?.error?.message || err?.message || 'Submission failed'));
            }
        });
    }

    statusClass(status: string) {
        return {
            'bg-yellow-500/20 text-yellow-300 border border-yellow-500/40': status === 'PENDING',
            'bg-emerald-500/20 text-emerald-300 border border-emerald-500/40': status === 'APPROVED',
            'bg-red-500/20 text-red-300 border border-red-500/40': status === 'REJECTED',
        };
    }

    formatCurrency(val: number): string {
        if (!val) return '₹0';
        return '₹' + val.toLocaleString('en-IN', { maximumFractionDigits: 2 });
    }
}