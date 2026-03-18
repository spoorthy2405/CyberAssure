import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, ActivatedRoute, Router } from '@angular/router';
import { CustomerService } from '../services/customer.service';
import { firstValueFrom } from 'rxjs';

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
    
    // Professional Claim Form Data
    claimData = { 
        incidentId: null as number | null, 
        claimAmount: null as number | null,
        bankAccountNumber: '',
        bankIfscCode: '',
        policeReportFiled: false,
        policeReportNumber: '',
        claimDescription: ''
    };
    
    submitting = false;
    errorMsg = '';
    successMsg = '';

    selectedSettlement: any = null;

    constructor(
        private service: CustomerService, 
        private route: ActivatedRoute, 
        private router: Router,
        private cdr: ChangeDetectorRef
    ) { }

    ngOnInit() {
        this.loadClaims();
        this.loadMyIncidents();
        this.route.queryParams.subscribe(params => {
            if (params['openModal'] === 'true') {
                const incidentId = params['incidentId'] ? Number(params['incidentId']) : null;
                // Use setTimeout to ensure change detection picks up modal opening state
                setTimeout(() => {
                    this.openModal(incidentId);
                }, 0);
            }
        });
    }

    loadClaims() {
        this.loading = true;
        this.cdr.detectChanges();
        this.service.getClaims().subscribe({
            next: (data) => { this.claims = data; this.loading = false; this.cdr.detectChanges(); },
            error: () => { this.loading = false; this.cdr.detectChanges(); }
        });
    }

    loadMyIncidents() {
        this.incidentsLoading = true;
        this.cdr.detectChanges();
        this.service.getMyIncidents().subscribe({
            next: (data) => {
                this.myIncidents = data;
                this.incidentsLoading = false;
                this.cdr.detectChanges();
            },
            error: () => {
                this.incidentsLoading = false;
                this.errorMsg = 'Could not load your incidents. Please try refreshing.';
                this.cdr.detectChanges();
            }
        });
    }

    openModal(preselectIncidentId?: number | null) {
        this.errorMsg = '';
        this.successMsg = '';
        
        // Reset form
        this.claimData = { 
            incidentId: preselectIncidentId || null, 
            claimAmount: null,
            bankAccountNumber: '',
            bankIfscCode: '',
            policeReportFiled: false,
            policeReportNumber: '',
            claimDescription: '' 
        };
        
        this.showModal = true;
        this.cdr.detectChanges();
    }

    closeModal() { this.showModal = false; this.cdr.detectChanges(); }

    submitClaim() {
        this.errorMsg = '';
        if (!this.claimData.incidentId || !this.claimData.claimAmount) {
            this.errorMsg = 'Please select an incident and enter a claim amount.';
            this.cdr.detectChanges();
            return;
        }
        this.submitting = true;
        this.cdr.detectChanges();
        this.service.fileClaim(this.claimData).subscribe({
            next: () => {
                this.submitting = false;
                this.showModal = false;
                this.successMsg = 'Claim successfully submitted! Our Claims Officer will review it shortly.';
                this.loadClaims();
                // Clear query params so it doesn't try to open modal again on refresh
                this.router.navigate([], { queryParams: {} });
                window.scrollTo({ top: 0, behavior: 'smooth' });
                this.cdr.detectChanges();
            },
            error: (err) => {
                this.submitting = false;
                this.errorMsg = err.error?.message || 'Failed to submit claim.';
                this.cdr.detectChanges();
            }
        });
    }

    getStatusClass(s: string) {
        return { 
            'badge-pending': s === 'PENDING' || s === 'UNDER_INVESTIGATION', 
            'badge-approved': s === 'APPROVED' || s === 'SETTLED', 
            'badge-rejected': s === 'REJECTED' 
        };
    }

    viewSettlement(c: any) {
        this.selectedSettlement = c;
        this.cdr.detectChanges();
    }

    closeSettlement() {
        this.selectedSettlement = null;
        this.cdr.detectChanges();
    }

    printSettlement() {
        window.print();
    }
}