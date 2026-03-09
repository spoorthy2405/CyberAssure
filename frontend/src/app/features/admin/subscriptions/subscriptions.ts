import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminApiService } from '../services/admin.service';

@Component({
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './subscriptions.html'
})
export class AdminSubscriptions implements OnInit {

    subscriptions: any[] = [];
    underwriters: any[] = [];
    loading = false;
    filterStatus = 'ALL';

    // Assignment modal
    showAssignModal = false;
    selectedSub: any = null;
    selectedUnderwriterId: number | null = null;
    assigning = false;
    successMessage = '';

    constructor(private api: AdminApiService) { }

    ngOnInit() {
        this.loadData();
    }

    loadData() {
        this.loading = true;
        this.api.getSubscriptions().subscribe({
            next: (data) => {
                this.subscriptions = data || [];
                this.loading = false;
            },
            error: () => { this.loading = false; }
        });

        this.api.getUnderwriters().subscribe({
            next: (data) => { this.underwriters = data || []; },
            error: () => { }
        });
    }

    get filtered() {
        if (this.filterStatus === 'ALL') return this.subscriptions;
        return this.subscriptions.filter(s => s.status === this.filterStatus);
    }

    openAssign(sub: any) {
        this.selectedSub = sub;
        this.selectedUnderwriterId = sub.assignedUnderwriter?.userId ?? null;
        this.assigning = false; // always reset
        this.successMessage = '';
        this.showAssignModal = true;
    }

    closeModal() {
        this.showAssignModal = false;
        this.selectedSub = null;
        this.selectedUnderwriterId = null;
        this.assigning = false;
        this.successMessage = '';
    }

    confirmAssign() {
        const subId = this.selectedSub?.id;
        const uwId = this.selectedUnderwriterId;

        if (!subId || !uwId) return;

        this.assigning = true;

        this.api.assignUnderwriter(subId, uwId).subscribe({
            next: () => {
                // Update the row in-place (no full reload)
                const assignedUw = this.underwriters.find(u => u.userId === uwId);
                if (assignedUw && this.selectedSub) {
                    this.selectedSub.assignedUnderwriter = assignedUw;
                }
                this.assigning = false;
                this.closeModal();
                this.successMessage = 'Underwriter successfully assigned!';
                setTimeout(() => { this.successMessage = ''; }, 3000);
            },
            error: (err: any) => {
                this.assigning = false;
                const msg = err?.error?.message || err?.message || 'Assignment failed. Please try again.';
                alert('Error: ' + msg);
            }
        });
    }

    statusClass(status: string) {
        return {
            'bg-yellow-500/20 text-yellow-300 border-yellow-500/40': status === 'PENDING',
            'bg-emerald-500/20 text-emerald-300 border-emerald-500/40': status === 'APPROVED',
            'bg-red-500/20 text-red-300 border-red-500/40': status === 'REJECTED',
        };
    }

    getCount(status: string): number {
        return this.subscriptions.filter(s => s.status === status).length;
    }

    getPendingCount(): number {
        return this.subscriptions.filter(s => s.status === 'PENDING').length;
    }

    getProofFiles(sub: any): string[] {
        const paths = sub?.riskAssessment?.proofDocumentPaths;
        if (!paths) return [];
        return paths.split(',').filter((p: string) => p.trim());
    }

    getFileName(path: string): string {
        if (!path) return '';
        const parts = path.split(/[\\/]/);
        return parts[parts.length - 1];
    }
}
