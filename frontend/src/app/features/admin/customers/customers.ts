import { Component, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { toSignal } from '@angular/core/rxjs-interop';
import { map, catchError, of } from 'rxjs';
import { AdminApiService, User } from '../services/admin.service';

@Component({
  selector: 'app-customers',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './customers.html'
})
export class Customers {

  private adminApi = inject(AdminApiService);

  searchText = signal('');
  statusFilter = signal('');
  industryFilter = signal('');
  page = signal(1);
  pageSize = 10;
  selectedCustomer = signal<User | null>(null);

  private avatarColors = [
    'linear-gradient(135deg, #3b82f6, #6366f1)',
    'linear-gradient(135deg, #10b981, #059669)',
    'linear-gradient(135deg, #f59e0b, #ef4444)',
    'linear-gradient(135deg, #8b5cf6, #ec4899)',
    'linear-gradient(135deg, #06b6d4, #3b82f6)',
    'linear-gradient(135deg, #f97316, #fb923c)',
  ];

  // All customers from HTTP — auto-rerenders via signal
  private allCustomersRaw = toSignal(
    this.adminApi.getCustomers().pipe(catchError(() => of([]))),
    { initialValue: [] as User[] }
  );

  isLoading = computed(() => this.allCustomersRaw().length === 0);

  industries = computed(() => [...new Set(
    this.allCustomersRaw().map(c => c.industry).filter((i): i is string => !!i)
  )].sort());

  filteredCustomers = computed(() => {
    const text = this.searchText().toLowerCase();
    return this.allCustomersRaw().filter(c => {
      const matchesText =
        !text ||
        c.fullName.toLowerCase().includes(text) ||
        c.email.toLowerCase().includes(text) ||
        (c.companyName || '').toLowerCase().includes(text);
      const matchesStatus = !this.statusFilter() || c.accountStatus === this.statusFilter();
      const matchesIndustry = !this.industryFilter() || c.industry === this.industryFilter();
      return matchesText && matchesStatus && matchesIndustry;
    });
  });

  paginatedCustomers = computed(() => {
    const start = (this.page() - 1) * this.pageSize;
    return this.filteredCustomers().slice(start, start + this.pageSize);
  });

  totalPages = computed(() => Math.ceil(this.filteredCustomers().length / this.pageSize));

  nextPage() {
    if (this.page() < this.totalPages()) this.page.update(p => p + 1);
  }

  prevPage() {
    if (this.page() > 1) this.page.update(p => p - 1);
  }

  applyFilters() {
    this.page.set(1);
  }

  min(a: number, b: number): number {
    return Math.min(a, b);
  }

  openDetail(cust: User) {
    this.selectedCustomer.set(cust);
  }

  closeDetail() {
    this.selectedCustomer.set(null);
  }

  toggleSuspend(cust: User) {
    const action = cust.accountStatus === 'SUSPENDED' ? 'reactivate' : 'suspend';
    if (!confirm(`Are you sure you want to ${action} this customer's account?`)) return;
    const newStatus = cust.accountStatus === 'SUSPENDED' ? 'ACTIVE' : 'SUSPENDED';
    cust.accountStatus = newStatus;
    if (this.selectedCustomer()?.userId === cust.userId) {
      this.selectedCustomer.set({ ...cust });
    }
  }

  getAvatarColor(name: string): string {
    if (!name) return this.avatarColors[0];
    const idx = name.charCodeAt(0) % this.avatarColors.length;
    return this.avatarColors[idx];
  }
}