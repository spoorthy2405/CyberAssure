import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CustomerService } from '../services/customer.service';

@Component({
    standalone: true,
    imports: [CommonModule, RouterLink],
    templateUrl: './subscriptions.html'
})
export class Subscriptions implements OnInit {

    subscriptions: any[] = [];
    loading = true;
    selectedPlan: any = null;
    paying = false;

    constructor(private service: CustomerService) { }

    ngOnInit(): void {
        this.loadSubs();
    }

    loadSubs() {
        this.loading = true;
        this.service.getSubscriptions().subscribe({
            next: (data) => { this.subscriptions = data; this.loading = false; },
            error: () => { this.loading = false; }
        });
    }

    viewPlan(s: any) {
        this.selectedPlan = s;
    }

    closePlan() {
        this.selectedPlan = null;
    }

    payPlan() {
        if (!this.selectedPlan) return;
        this.paying = true;
        this.service.paySubscription(this.selectedPlan.id).subscribe({
            next: () => {
                this.paying = false;
                this.selectedPlan = null;
                alert('Payment successful! Your policy is now ACTIVE.');
                window.location.reload();
            },
            error: (err) => {
                this.paying = false;
                alert('Payment failed.');
            }
        });
    }

    getStatusClass(status: string) {
        const map: any = {
            ACTIVE: 'badge-approved', PENDING_PAYMENT: 'badge-info',
            APPROVED: 'badge-approved', PENDING: 'badge-pending', REJECTED: 'badge-rejected'
        };
        return map[status] || 'badge-pending';
    }

    coverageFmt(val: number) {
        if (!val) return '₹0';
        if (val >= 10000000) return `₹${(val / 10000000).toFixed(1)} Cr`;
        if (val >= 100000) return `₹${(val / 100000).toFixed(0)} L`;
        return `₹${val}`;
    }
}