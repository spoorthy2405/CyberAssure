import { Component, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AdminApiService } from '../services/admin.service';

@Component({
  selector: 'app-staff',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './staff.html'
})
export class Staff {

  private fb = inject(FormBuilder);
  private adminApi = inject(AdminApiService);

  showForm = signal(false);
  staffMembers = signal<any[]>([]);
  searchTerm = signal('');
  roleFilter = signal('ALL');
  currentPage = signal(1);
  pageSize = 10;
  editingStaffId = signal<number | null>(null);

  staffForm: FormGroup = this.fb.group({
    fullName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
    roleName: ['', Validators.required],
    department: ['CyberAssure Internal', Validators.required]
  });

  constructor() {
    this.fetchStaff();
  }

  fetchStaff() {
    this.adminApi.getStaff().subscribe({
      next: (data) => { this.staffMembers.set(data); this.currentPage.set(1); },
      error: (err) => console.error('Error fetching staff', err)
    });
  }

  displayedStaff = computed(() => {
    const filtered = this.staffMembers().filter(staff => {
      const matchesSearch =
        staff.fullName.toLowerCase().includes(this.searchTerm().toLowerCase()) ||
        staff.email.toLowerCase().includes(this.searchTerm().toLowerCase());
      const roleName = staff.role?.roleName || staff.role || '';
      const matchesRole = this.roleFilter() === 'ALL' || roleName.includes(this.roleFilter());
      return matchesSearch && matchesRole;
    });
    const startIndex = (this.currentPage() - 1) * this.pageSize;
    return filtered.slice(startIndex, startIndex + this.pageSize);
  });

  totalFilteredPages = computed(() => {
    const count = this.staffMembers().filter(staff => {
      const matchesSearch =
        staff.fullName.toLowerCase().includes(this.searchTerm().toLowerCase()) ||
        staff.email.toLowerCase().includes(this.searchTerm().toLowerCase());
      const roleName = staff.role?.roleName || staff.role || '';
      return matchesSearch && (this.roleFilter() === 'ALL' || roleName.includes(this.roleFilter()));
    }).length;
    return Math.max(1, Math.ceil(count / this.pageSize));
  });

  totalFilteredMebersLength = computed(() =>
    this.staffMembers().filter(staff => {
      const matchesSearch =
        staff.fullName.toLowerCase().includes(this.searchTerm().toLowerCase()) ||
        staff.email.toLowerCase().includes(this.searchTerm().toLowerCase());
      const roleName = staff.role?.roleName || staff.role || '';
      return matchesSearch && (this.roleFilter() === 'ALL' || roleName.includes(this.roleFilter()));
    }).length
  );

  nextPage() {
    if (this.currentPage() < this.totalFilteredPages()) this.currentPage.update(p => p + 1);
  }

  prevPage() {
    if (this.currentPage() > 1) this.currentPage.update(p => p - 1);
  }

  onSearchChange(event: any) {
    this.searchTerm.set(event.target.value);
    this.currentPage.set(1);
  }

  onRoleFilterChange(event: any) {
    this.roleFilter.set(event.target.value);
    this.currentPage.set(1);
  }

  toggleForm() {
    this.showForm.update(v => !v);
    if (!this.showForm()) {
      this.staffForm.reset({ department: 'CyberAssure Internal', roleName: '' });
      this.editingStaffId.set(null);
      this.staffForm.get('password')?.setValidators([Validators.required, Validators.minLength(8)]);
      this.staffForm.get('password')?.updateValueAndValidity();
    }
  }

  editStaff(staff: any) {
    this.editingStaffId.set(staff.userId);
    const internalRoleName = staff.role?.roleName || staff.role;
    this.staffForm.patchValue({
      fullName: staff.fullName,
      email: staff.email,
      roleName: internalRoleName,
      department: 'CyberAssure Internal',
      password: ''
    });
    this.staffForm.get('password')?.clearValidators();
    this.staffForm.get('password')?.setValidators([Validators.minLength(8)]);
    this.staffForm.get('password')?.updateValueAndValidity();
    this.showForm.set(true);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  deactivateStaff(staff: any) {
    if (confirm(`Are you sure you want to toggle the status for ${staff.fullName}?`)) {
      this.adminApi.deactivateStaff(staff.userId).subscribe({
        next: () => this.fetchStaff(),
        error: (err) => console.error('Error deactivating staff', err)
      });
    }
  }

  viewProfile(staff: any) {
    alert('Navigating to full profile view for ' + staff.fullName + '... (To be implemented)');
  }

  submitNewStaff() {
    if (this.staffForm.invalid) {
      this.staffForm.markAllAsTouched();
      return;
    }
    const payload = { ...this.staffForm.value };
    if (typeof payload.roleName === 'object' && payload.roleName !== null) {
      payload.roleName = payload.roleName.roleName;
    }
    if (this.editingStaffId() && !payload.password) delete payload.password;

    if (this.editingStaffId()) {
      this.adminApi.updateStaff(this.editingStaffId()!, payload).subscribe({
        next: () => { this.fetchStaff(); this.toggleForm(); },
        error: (err) => console.error('Error updating staff', err)
      });
    } else {
      this.adminApi.createStaff(payload).subscribe({
        next: () => { this.fetchStaff(); this.toggleForm(); },
        error: (err) => console.error('Error creating staff', err)
      });
    }
  }
}
