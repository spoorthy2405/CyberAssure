import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminApiService } from '../services/admin.service';

@Component({
    selector: 'app-policies',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './policies.html'
})
export class Policies implements OnInit {

    policiesList: any[] = [];
    sectors: string[] = ['Banking', 'Healthcare', 'E-commerce', 'IT / SaaS', 'Manufacturing', 'Education', 'Government', 'Media'];
    currentSector: string = 'Banking';
    filteredPolicies: any[] = [];

    showForm = false;
    isEditing = false;
    currentEditId: number | null = null;

    // Form model
    policyData = {
        policyName: '',
        sector: 'Banking',
        description: '',
        coverageLimit: 0,
        basePremium: 0,
        durationMonths: 12,
        benefitsText: ''
    };

    constructor(private adminApi: AdminApiService) { }

    ngOnInit() {
        this.fetchCyberPolicies();
    }

    fetchCyberPolicies() {
        this.adminApi.getCyberPolicies().subscribe({
            next: (data) => {
                this.policiesList = data;
                this.filterBySector(this.currentSector);
            },
            error: (err) => console.error('Error loading cyber policies', err)
        });
    }

    filterBySector(sector: string) {
        this.currentSector = sector;
        this.filteredPolicies = this.policiesList.filter(p => p.sector === sector);
    }

    openCreateForm() {
        this.isEditing = false;
        this.currentEditId = null;
        this.policyData = {
            policyName: '',
            sector: this.currentSector,
            description: '',
            coverageLimit: 0,
            basePremium: 0,
            durationMonths: 12,
            benefitsText: ''
        };
        this.showForm = true;
    }

    openEditForm(policy: any) {
        this.isEditing = true;
        this.currentEditId = policy.id;
        this.policyData = {
            policyName: policy.policyName,
            sector: policy.sector,
            description: policy.description,
            coverageLimit: policy.coverageLimit,
            basePremium: policy.basePremium,
            durationMonths: policy.durationMonths,
            benefitsText: policy.benefits ? policy.benefits.join('\n') : ''
        };
        this.showForm = true;
    }

    closeForm() {
        this.showForm = false;
    }

    deletePolicy(id: number) {
        if (confirm("Are you sure you want to delete this policy template?")) {
            this.adminApi.deleteCyberPolicy(id).subscribe({
                next: () => this.fetchCyberPolicies(),
                error: (err) => console.error("Error deleting policy", err)
            });
        }
    }

    savePolicy() {
        const payload = {
            ...this.policyData,
            benefits: this.policyData.benefitsText.split('\n').map((b: string) => b.trim()).filter((b: string) => b.length > 0)
        };

        if (this.isEditing && this.currentEditId) {
            this.adminApi.updateCyberPolicy(this.currentEditId, payload).subscribe({
                next: () => {
                    this.fetchCyberPolicies();
                    this.closeForm();
                },
                error: (err) => console.error("Error updating", err)
            });
        } else {
            this.adminApi.createCyberPolicy(payload).subscribe({
                next: () => {
                    this.fetchCyberPolicies();
                    this.closeForm();
                },
                error: (err) => console.error("Error creating", err)
            });
        }
    }
}