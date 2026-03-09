import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminApiService, User } from '../services/admin.service';

@Component({
    selector: 'app-customers',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './customers.html'
})
export class Customers implements OnInit {

    customersList: User[] = [];
    filteredCustomers: User[] = [];

    searchText: string = '';
    statusFilter: string = '';
    industryFilter: string = '';

    isLoading = true;
    selectedCustomer: User | null = null;

    page = 1;
    pageSize = 10;

    /** Unique industries derived from loaded data */
    industries: string[] = [];

    /** Avatar background gradient palette */
    private avatarColors = [
        'linear-gradient(135deg, #3b82f6, #6366f1)',
        'linear-gradient(135deg, #10b981, #059669)',
        'linear-gradient(135deg, #f59e0b, #ef4444)',
        'linear-gradient(135deg, #8b5cf6, #ec4899)',
        'linear-gradient(135deg, #06b6d4, #3b82f6)',
        'linear-gradient(135deg, #f97316, #fb923c)',
    ];

    constructor(private adminApi: AdminApiService) { }

    ngOnInit() {
        this.adminApi.getCustomers().subscribe({
            next: (data) => {
                this.customersList = data;
                this.industries = [...new Set(
                    data.map(c => c.industry).filter((i): i is string => !!i)
                )].sort();
                this.applyFilters();
                this.isLoading = false;
            },
            error: (err) => {
                console.error('Error loading customers', err);
                this.isLoading = false;
            }
        });
    }

    applyFilters() {
        const text = this.searchText.toLowerCase();

        this.filteredCustomers = this.customersList.filter(c => {
            const matchesText =
                !text ||
                c.fullName.toLowerCase().includes(text) ||
                c.email.toLowerCase().includes(text) ||
                (c.companyName || '').toLowerCase().includes(text);

            const matchesStatus =
                !this.statusFilter || c.accountStatus === this.statusFilter;

            const matchesIndustry =
                !this.industryFilter || c.industry === this.industryFilter;

            return matchesText && matchesStatus && matchesIndustry;
        });

        this.page = 1;
    }

    get paginatedCustomers(): User[] {
        const start = (this.page - 1) * this.pageSize;
        return this.filteredCustomers.slice(start, start + this.pageSize);
    }

    get totalPages(): number {
        return Math.ceil(this.filteredCustomers.length / this.pageSize);
    }

    nextPage() {
        if (this.page < this.totalPages) this.page++;
    }

    prevPage() {
        if (this.page > 1) this.page--;
    }

    min(a: number, b: number): number {
        return Math.min(a, b);
    }

    openDetail(cust: User) {
        this.selectedCustomer = cust;
    }

    closeDetail() {
        this.selectedCustomer = null;
    }

    toggleSuspend(cust: User) {
        const action = cust.accountStatus === 'SUSPENDED' ? 'reactivate' : 'suspend';
        if (!confirm(`Are you sure you want to ${action} this customer's account?`)) return;

        // Optimistically update UI
        const newStatus = cust.accountStatus === 'SUSPENDED' ? 'ACTIVE' : 'SUSPENDED';
        cust.accountStatus = newStatus;

        if (this.selectedCustomer?.userId === cust.userId) {
            this.selectedCustomer = { ...cust };
        }

        // TODO: wire up to a real API call when the endpoint is available
        // this.adminApi.updateCustomerStatus(cust.userId, newStatus).subscribe({...})
    }

    getAvatarColor(name: string): string {
        if (!name) return this.avatarColors[0];
        const idx = name.charCodeAt(0) % this.avatarColors.length;
        return this.avatarColors[idx];
    }
}