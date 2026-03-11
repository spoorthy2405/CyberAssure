import { Component, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { toSignal, toObservable } from '@angular/core/rxjs-interop';
import { catchError, of, switchMap, tap } from 'rxjs';
import { UnderwriterService } from '../services/underwriter.service';

@Component({
  selector: 'app-review-queue',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './review-queue.html',
  styleUrl: './review-queue.css'
})
export class ReviewQueue {

  private service = inject(UnderwriterService);

  // -- Refresh counter: incrementing re-triggers the HTTP fetch via toObservable --
  private refreshCounter = signal(0);

  private isLoaded = signal(false);

  // Convert the signal to an Observable → switchMap → fetch → back to signal
  // This is the correct Angular 21 pattern for refreshable HTTP data
  applications = toSignal(
    toObservable(this.refreshCounter).pipe(
      switchMap(() =>
        this.service.getMyAssignedSubscriptions().pipe(
          tap(() => this.isLoaded.set(true)),
          catchError(() => { this.isLoaded.set(true); return of([]); })
        )
      )
    ),
    { initialValue: [] as any[] }
  );

  loading = computed(() => !this.isLoaded());
  selected    = signal<any>(null);
  submitting  = signal<'APPROVED' | 'REJECTED' | null>(null);
  successMessage = signal('');

  // -- Form fields as signals --
  riskScore        = signal<number | null>(null);
  tenureMonths     = signal<number>(12);
  rejectionReason  = signal('');
  underwriterNotes = signal('');
  fixedPremium     = signal<number | null>(null);
  coverageAmount   = signal<number | null>(null);
  policyLimit      = signal<number | null>(null);
  deductible       = signal<number | null>(null);
  exclusions       = signal('');

  // -- Risk calculation computed signals --
  riskLevel = computed<string>(() => {
    const s = this.riskScore();
    if (s === null) return '—';
    if (s <= 40) return 'LOW';
    if (s <= 70) return 'MEDIUM';
    return 'HIGH';
  });

  riskLevelClass = computed<string>(() => {
    switch (this.riskLevel()) {
      case 'LOW':    return 'text-emerald-400 bg-emerald-500/20 border-emerald-500/40';
      case 'MEDIUM': return 'text-yellow-400 bg-yellow-500/20 border-yellow-500/40';
      case 'HIGH':   return 'text-red-400 bg-red-500/20 border-red-500/40';
      default:       return 'text-slate-400 bg-slate-500/20 border-slate-500/40';
    }
  });

  private riskMultiplierValue = computed<number>(() => {
    switch (this.riskLevel()) {
      case 'MEDIUM': return 1.3;
      case 'HIGH':   return 1.7;
      default:       return 1.0;
    }
  });

  private coverageRatioValue = computed<number>(() => {
    switch (this.riskLevel()) {
      case 'MEDIUM': return 0.80;
      case 'HIGH':   return 0.60;
      default:       return 1.0;
    }
  });

  calculatedPremium = computed<number>(() => {
    const sel = this.selected();
    if (!sel || this.riskScore() === null) return 0;
    return Number(sel.policy?.basePremium || 0) * this.riskMultiplierValue() * (this.tenureMonths() / 12);
  });

  calculatedCoverage = computed<number>(() => {
    const sel = this.selected();
    if (!sel || this.riskScore() === null) return 0;
    return Number(sel.policy?.coverageLimit || 0) * this.coverageRatioValue();
  });

  // -- Actions --
  openReview(app: any) {
    this.selected.set(app);
    this.riskScore.set(null);
    this.tenureMonths.set(app.policy?.durationMonths || 12);
    this.rejectionReason.set('');
    this.underwriterNotes.set('');
    this.fixedPremium.set(null);
    this.coverageAmount.set(null);
    this.policyLimit.set(null);
    this.deductible.set(null);
    this.exclusions.set('');
  }

  closeReview() { this.selected.set(null); }

  onRiskScoreChange() {
    const sel = this.selected();
    if (!sel || this.riskScore() === null) return;

    const base   = Number(sel.policy?.basePremium || 0);
    const maxCov = Number(sel.policy?.coverageLimit || 0);
    const level  = this.riskLevel();
    const tenure = this.tenureMonths();

    const mult    = level === 'MEDIUM' ? 1.3 : level === 'HIGH' ? 1.7 : 1.0;
    const premium = Math.round(base * mult * (tenure / 12));
    this.fixedPremium.set(premium);

    const ratio = level === 'MEDIUM' ? 0.8 : level === 'HIGH' ? 0.6 : 1.0;
    const cov   = Math.round(maxCov * ratio);
    this.coverageAmount.set(cov);
    this.policyLimit.set(Math.round(cov * 0.8));

    const dedRatio = level === 'MEDIUM' ? 0.10 : level === 'HIGH' ? 0.15 : 0.05;
    this.deductible.set(Math.round(premium * dedRatio));

    let exc = 'Standard policy exclusions apply.';
    if (level === 'HIGH') exc = 'Standard exclusions apply. PLUS: Social Engineering Fraud, Ransomware Payments (unless strictly authorized), and Cryptojacking are EXCLUDED.';
    else if (level === 'MEDIUM') exc = 'Standard exclusions apply. Note: Coverage for Social Engineering is limited.';
    this.exclusions.set(exc);
  }

  submitDecision(decision: 'APPROVED' | 'REJECTED') {
    const score = this.riskScore();
    if (score === null || score < 0 || score > 100) { alert('Please enter a valid Risk Score (0–100).'); return; }
    if (decision === 'REJECTED' && !this.rejectionReason().trim()) { alert('Please enter a rejection reason.'); return; }

    this.submitting.set(decision);
    const selId = this.selected()?.id;

    const body = {
      decision, riskScore: score, tenureMonths: this.tenureMonths(),
      fixedPremium: this.fixedPremium(), coverageAmount: this.coverageAmount(),
      policyLimit: this.policyLimit(), deductible: this.deductible(),
      exclusions: this.exclusions(), underwriterNotes: this.underwriterNotes(),
      rejectionReason: this.rejectionReason() || null
    };

    this.service.reviewSubscription(selId, body).subscribe({
      next: () => {
        this.submitting.set(null);
        this.successMessage.set(decision === 'APPROVED'
          ? `✅ Application CA-${selId} approved successfully!`
          : `❌ Application CA-${selId} rejected.`);
        this.closeReview();
        // Increment counter → toObservable emits → switchMap re-fetches → signal updates
        this.refreshCounter.update(v => v + 1);
        setTimeout(() => this.successMessage.set(''), 4000);
      },
      error: (err: any) => {
        this.submitting.set(null);
        alert('Error: ' + (err?.error?.message || err?.message || 'Submission failed'));
      }
    });
  }

  getProofDocuments(app: any): Array<{ name: string; url: string; icon: string }> {
    const paths: string = app?.riskAssessment?.proofDocumentPaths;
    if (!paths) return [];
    return paths.split(',').filter(p => p.trim()).map(p => {
      const trimmed = p.trim().replace(/\\/g, '/');
      const parts = trimmed.split('/');
      const filename = parts[parts.length - 1];
      const userId = parts[parts.length - 2];
      return { name: filename.replace(/^\d+_/, ''), url: `http://localhost:8080/api/v1/files/proof/${userId}/${filename}`, icon: this.getFileIcon(filename) };
    });
  }

  getFileIcon(filename: string): string {
    const lower = filename.toLowerCase();
    if (lower.endsWith('.pdf')) return '📄';
    if (lower.endsWith('.jpg') || lower.endsWith('.jpeg') || lower.endsWith('.png')) return '🖼️';
    if (lower.endsWith('.doc') || lower.endsWith('.docx')) return '📝';
    return '📎';
  }

  viewDocument(url: string) {
    this.service.downloadProof(url).subscribe({
      next: (blob) => {
        let type = 'application/octet-stream';
        if (url.toLowerCase().endsWith('.pdf')) type = 'application/pdf';
        else if (url.toLowerCase().endsWith('.jpg') || url.toLowerCase().endsWith('.jpeg')) type = 'image/jpeg';
        else if (url.toLowerCase().endsWith('.png')) type = 'image/png';
        window.open(window.URL.createObjectURL(new Blob([blob], { type })), '_blank');
      },
      error: () => alert('Could not open document. Please check network or file permissions.')
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