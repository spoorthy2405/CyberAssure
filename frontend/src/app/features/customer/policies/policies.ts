import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, NavigationEnd } from '@angular/router';
import { CustomerService } from '../services/customer.service';
import { toSignal } from '@angular/core/rxjs-interop';
import { catchError, filter, startWith, switchMap, tap } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-policies',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './policies.html'
})
export class Policies {
  private service = inject(CustomerService);
  private router = inject(Router);

  // Trigger data fetch on navigation for fresh data without caching bugs
  private refresh$ = this.router.events.pipe(
    filter(event => event instanceof NavigationEnd),
    startWith(null)
  );

  loadingPage = signal(true);

  // Signal for policies list
  policies = toSignal(
    this.refresh$.pipe(
      tap(() => this.loadingPage.set(true)),
      switchMap(() => this.service.getRecommendedPolicies().pipe(
        catchError((err) => {
          console.error('Error fetching policies:', err);
          return of([]);
        })
      )),
      tap(() => this.loadingPage.set(false))
    ),
    { initialValue: [] as any[] }
  );

  // Modal State Signals
  selectedPolicy = signal<any>(null);
  showModal = signal(false);
  loadingSubmit = signal(false);

  // Risk Assessment Form Signal
  riskForm = signal({
    firewallEnabled: false,
    encryptionEnabled: false,
    backupAvailable: false,
    mfaEnabled: false,
    iso27001Certified: false,
    hasDataPrivacyOfficer: false,
    previousIncidentCount: 0,
    employeeCount: 50,
    annualRevenue: 5000000
  });

  // Individual proof documents for each control
  controlProofs = signal<{ [key: string]: File }>({});

  // Additional proof document files
  proofFiles = signal<File[]>([]);

  openApplication(policy: any) {
    this.selectedPolicy.set(policy);
    this.showModal.set(true);
    this.proofFiles.set([]);
    this.controlProofs.set({});
    this.riskForm.set({
      firewallEnabled: false,
      encryptionEnabled: false,
      backupAvailable: false,
      mfaEnabled: false,
      iso27001Certified: false,
      hasDataPrivacyOfficer: false,
      previousIncidentCount: 0,
      employeeCount: 50,
      annualRevenue: 5000000
    });
  }

  closeModal() {
    this.showModal.set(false);
    this.selectedPolicy.set(null);
    this.proofFiles.set([]);
    this.controlProofs.set({});
  }

  onControlFileSelect(event: any, controlKey: string) {
    const file = event.target.files[0];
    if (file) {
      this.controlProofs.update(curr => ({ ...curr, [controlKey]: file }));
    }
  }

  onFileSelect(event: any) {
    const files = event.target.files;
    if (files) {
      const newFiles = [...this.proofFiles()];
      for (let i = 0; i < files.length; i++) {
        newFiles.push(files[i]);
      }
      this.proofFiles.set(newFiles);
    }
    event.target.value = '';
  }

  removeFile(index: number) {
    this.proofFiles.update(curr => {
      const copy = [...curr];
      copy.splice(index, 1);
      return copy;
    });
  }

  updateRiskForm(field: string, value: any) {
      this.riskForm.update(curr => ({ ...curr, [field]: value }));
  }

  submitApplication() {
    const policy = this.selectedPolicy();
    const form = this.riskForm();
    const controls = this.controlProofs();
    const additionalFiles = this.proofFiles();

    if (!policy) return;

    const missingProofs = [];
    if (form.firewallEnabled && !controls['firewall']) missingProofs.push('Active Firewall');
    if (form.encryptionEnabled && !controls['encryption']) missingProofs.push('Data Encryption');
    if (form.backupAvailable && !controls['backup']) missingProofs.push('Regular Backups');
    if (form.mfaEnabled && !controls['mfa']) missingProofs.push('Multi-Factor Auth');
    if (form.iso27001Certified && !controls['iso27001']) missingProofs.push('ISO 27001 Certified');
    if (form.hasDataPrivacyOfficer && !controls['dpo']) missingProofs.push('Dedicated DPO');

    if (missingProofs.length > 0) {
      alert(`Please upload proof documents for the following enabled controls:\n- ${missingProofs.join('\n- ')}`);
      return;
    }

    if (!form.employeeCount || !form.annualRevenue) {
      alert("Please fill in Employee Count and Annual Revenue.");
      return;
    }

    this.loadingSubmit.set(true);

    const payload = {
      policyId: policy.id,
      ...form
    };

    const formData = new FormData();
    formData.append('data', new Blob([JSON.stringify(payload)], { type: 'application/json' }));

    for (const [key, file] of Object.entries(controls)) {
      if (file) {
        formData.append('proofFiles', file, `${key.toUpperCase()}_PROOF_${file.name}`);
      }
    }

    for (const file of additionalFiles) {
      formData.append('proofFiles', file, `ADDITIONAL_${file.name}`);
    }

    this.service.applyForPolicy(formData).subscribe({
      next: (res: any) => {
        this.loadingSubmit.set(false);
        this.closeModal();
        this.router.navigate(['/customer/subscriptions']);
      },
      error: (err: any) => {
        this.loadingSubmit.set(false);
        alert("Failed to apply: " + (err.error?.message || err.error || err.message));
      }
    });
  }
}
