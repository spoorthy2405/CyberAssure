import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CustomerService } from '../services/customer.service';

@Component({
    standalone: true,
    imports: [CommonModule, FormsModule, RouterLink],
    templateUrl: './incidents.html'
})
export class Incidents implements OnInit {

    activeTab: 'list' | 'report' = 'list';

    // Past incidents
    incidents: any[] = [];
    incidentsLoading = true;

    // Report form
    formData: any = { incidentType: '', description: '', estimatedLossAmount: null, subscriptionId: null };
    selectedFiles: File[] = [];
    subscriptions: any[] = [];
    formLoading = false;
    formError = '';
    formSuccess = '';

    constructor(private service: CustomerService) { }

    ngOnInit() {
        this.loadIncidents();
        this.service.getSubscriptions().subscribe({
            next: (subs) => {
                this.subscriptions = subs.filter((s: any) => s.status === 'APPROVED');
                if (this.subscriptions.length > 0 && !this.formData.subscriptionId) {
                    this.formData.subscriptionId = this.subscriptions[0].id;
                }
                // If user has no incidents yet, show the report tab
                if (this.incidents.length === 0 && this.subscriptions.length > 0) {
                    this.activeTab = 'report';
                }
            }
        });
    }

    loadIncidents() {
        this.incidentsLoading = true;
        this.service.getMyIncidents().subscribe({
            next: (data) => { this.incidents = data; this.incidentsLoading = false; },
            error: () => { this.incidentsLoading = false; }
        });
    }

    onFileSelected(event: any) {
        if (event.target.files.length > 0) {
            this.selectedFiles = Array.from(event.target.files);
        }
    }

    removeFile(index: number) { this.selectedFiles.splice(index, 1); }

    submitReport() {
        this.formError = '';
        this.formSuccess = '';
        if (!this.formData.subscriptionId) { this.formError = 'Please select an active policy.'; return; }
        if (!this.formData.incidentType) { this.formError = 'Please select an incident type.'; return; }
        if (!this.formData.description) { this.formError = 'Please describe the incident.'; return; }
        if (!this.formData.estimatedLossAmount) { this.formError = 'Please enter an estimated loss amount.'; return; }

        this.formLoading = true;
        const fd = new FormData();
        fd.append('incidentType', this.formData.incidentType);
        fd.append('description', this.formData.description);
        fd.append('estimatedLossAmount', this.formData.estimatedLossAmount);
        fd.append('subscriptionId', this.formData.subscriptionId);
        this.selectedFiles.forEach(f => fd.append('files', f));

        this.service.reportIncident(fd).subscribe({
            next: () => {
                this.formLoading = false;
                this.formSuccess = 'Incident reported successfully! Our claims team will review it shortly.';
                this.formData = { incidentType: '', description: '', estimatedLossAmount: null, subscriptionId: this.subscriptions[0]?.id };
                this.selectedFiles = [];
                this.loadIncidents();
                setTimeout(() => { this.activeTab = 'list'; this.formSuccess = ''; }, 2500);
            },
            error: (err) => {
                this.formLoading = false;
                this.formError = err.error?.message || 'Failed to report incident. Ensure you have an active policy.';
            }
        });
    }

    getStatusColor(status: string) {
        const map: any = { 'REPORTED': 'badge-pending', 'UNDER_INVESTIGATION': 'badge-review', 'CLOSED': 'badge-approved' };
        return map[status] || 'badge-pending';
    }
}