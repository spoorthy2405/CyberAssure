import { Component, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { toSignal } from '@angular/core/rxjs-interop';
import { catchError, of } from 'rxjs';
import { AdminApiService } from '../services/admin.service';

@Component({
  selector: 'app-policies',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './policies.html'
})
export class Policies {

  private adminApi = inject(AdminApiService);

  sectors = ['Banking', 'Healthcare', 'E-commerce', 'IT / SaaS', 'Manufacturing', 'Education', 'Government', 'Media'];
  currentSector = signal('Banking');

  showForm = signal(false);
  isEditing = signal(false);
  currentEditId = signal<number | null>(null);

  policyData = signal({
    policyName: '',
    sector: 'Banking',
    description: '',
    coverageLimit: 0,
    basePremium: 0,
    durationMonths: 12,
    benefitsText: ''
  });

  private _refreshTrigger = signal(0);

  // Note: For a simple list that needs manual refresh after CRUD, we use a signal-backed reload pattern
  policiesList = signal<any[]>([]);

  filteredPolicies = computed(() =>
    this.policiesList().filter(p => p.sector === this.currentSector())
  );

  constructor() {
    this.fetchCyberPolicies();
  }

  fetchCyberPolicies() {
    this.adminApi.getCyberPolicies().pipe(catchError(() => of([]))).subscribe(data => {
      this.policiesList.set(data);
    });
  }

  filterBySector(sector: string) {
    this.currentSector.set(sector);
  }

  openCreateForm() {
    this.isEditing.set(false);
    this.currentEditId.set(null);
    this.policyData.set({
      policyName: '',
      sector: this.currentSector(),
      description: '',
      coverageLimit: 0,
      basePremium: 0,
      durationMonths: 12,
      benefitsText: ''
    });
    this.showForm.set(true);
  }

  openEditForm(policy: any) {
    this.isEditing.set(true);
    this.currentEditId.set(policy.id);
    this.policyData.set({
      policyName: policy.policyName,
      sector: policy.sector,
      description: policy.description,
      coverageLimit: policy.coverageLimit,
      basePremium: policy.basePremium,
      durationMonths: policy.durationMonths,
      benefitsText: policy.benefits ? policy.benefits.join('\n') : ''
    });
    this.showForm.set(true);
  }

  closeForm() {
    this.showForm.set(false);
  }

  getPolicyData() {
    return this.policyData();
  }

  updatePolicyField(field: string, value: any) {
    this.policyData.update(d => ({ ...d, [field]: value }));
  }

  deletePolicy(id: number) {
    if (confirm('Are you sure you want to delete this policy template?')) {
      this.adminApi.deleteCyberPolicy(id).subscribe({
        next: () => this.fetchCyberPolicies(),
        error: (err) => console.error('Error deleting policy', err)
      });
    }
  }

  savePolicy() {
    const data = this.policyData();
    const payload = {
      ...data,
      benefits: data.benefitsText.split('\n').map((b: string) => b.trim()).filter((b: string) => b.length > 0)
    };

    const editId = this.currentEditId();
    if (this.isEditing() && editId) {
      this.adminApi.updateCyberPolicy(editId, payload).subscribe({
        next: () => { this.fetchCyberPolicies(); this.closeForm(); },
        error: (err) => console.error('Error updating', err)
      });
    } else {
      this.adminApi.createCyberPolicy(payload).subscribe({
        next: () => { this.fetchCyberPolicies(); this.closeForm(); },
        error: (err) => console.error('Error creating', err)
      });
    }
  }
}