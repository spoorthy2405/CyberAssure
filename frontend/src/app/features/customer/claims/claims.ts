import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CustomerService } from '../services/customer.service';

@Component({
    standalone: true,
    imports: [CommonModule, FormsModule, RouterLink],
    templateUrl: './claims.html'
})
export class Claims implements OnInit {

    claims: any[] = [];
    loading = true;

    // Modal
    showModal = false;
    myIncidents: any[] = [];
    incidentsLoading = false;
    claimData = { incidentId: null as number | null, claimAmount: null as number | null };
    submitting = false;
    errorMsg = '';
    successMsg = '';

    constructor(private service: CustomerService) { }

    ngOnInit() {
        this.loadClaims();
    }

    loadClaims() {
        this.loading = true;
        this.service.getClaims().subscribe({
            next: (data) => { this.claims = data; this.loading = false; },
            error: () => { this.loading = false; }
        });
    }

    openModal() {
        this.errorMsg = '';
        this.successMsg = '';
        this.claimData = { incidentId: null, claimAmount: null };
        this.showModal = true;
        this.incidentsLoading = true;
        this.service.getMyIncidents().subscribe({
            next: (data) => { this.myIncidents = data; this.incidentsLoading = false; },
            error: () => { this.incidentsLoading = false; }
        });
    }

    closeModal() { this.showModal = false; }

    submitClaim() {
        this.errorMsg = '';
        if (!this.claimData.incidentId || !this.claimData.claimAmount) {
            this.errorMsg = 'Please select an incident and enter a claim amount.';
            return;
        }
        this.submitting = true;
        this.service.fileClaim(this.claimData).subscribe({
            next: () => {
                this.submitting = false;
                this.showModal = false;
                this.loadClaims();
            },
            error: (err) => {
                this.submitting = false;
                this.errorMsg = err.error?.message || 'Failed to submit claim.';
            }
        });
    }

    getStatusClass(s: string) {
        return { 'badge-pending': s === 'PENDING', 'badge-approved': s === 'APPROVED', 'badge-rejected': s === 'REJECTED' };
    }
}