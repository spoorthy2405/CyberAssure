import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CustomerService } from '../services/customer.service';

@Component({
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './policies.html'
})
export class Policies implements OnInit {

    policies: any[] = [];
    selectedPolicy: any = null;
    showModal = false;
    loading = false;

    // Risk Assessment Form
    riskForm = {
        firewallEnabled: false,
        encryptionEnabled: false,
        backupAvailable: false,
        mfaEnabled: false,
        iso27001Certified: false,
        hasDataPrivacyOfficer: false,
        previousIncidentCount: 0,
        employeeCount: 50,
        annualRevenue: 5000000
    };

    // Individual proof documents for each control
    controlProofs: { [key: string]: File } = {};

    // Additional proof document files
    proofFiles: File[] = [];

    constructor(private service: CustomerService) { }

    ngOnInit() {
        this.fetchRecommendedPolicies();
    }

    fetchRecommendedPolicies() {
        this.loading = true;
        this.service.getRecommendedPolicies().subscribe({
            next: (data) => {
                console.log('Fetched policies:', data);
                this.policies = data || [];
                this.loading = false;
            },
            error: (err) => {
                console.error('Error fetching policies:', err);
                this.loading = false;
            }
        });
    }

    openApplication(policy: any) {
        this.selectedPolicy = policy;
        this.showModal = true;
        this.proofFiles = [];
        this.controlProofs = {};
        this.riskForm = {
            firewallEnabled: false,
            encryptionEnabled: false,
            backupAvailable: false,
            mfaEnabled: false,
            iso27001Certified: false,
            hasDataPrivacyOfficer: false,
            previousIncidentCount: 0,
            employeeCount: 50,
            annualRevenue: 5000000
        };
    }

    closeModal() {
        this.showModal = false;
        this.selectedPolicy = null;
        this.proofFiles = [];
        this.controlProofs = {};
    }

    onControlFileSelect(event: any, controlKey: string) {
        const file = event.target.files[0];
        if (file) {
            this.controlProofs[controlKey] = file;
        }
    }

    onFileSelect(event: any) {
        const files = event.target.files;
        if (files) {
            for (let i = 0; i < files.length; i++) {
                this.proofFiles.push(files[i]);
            }
        }
        // reset input so same file can be re-selected
        event.target.value = '';
    }

    removeFile(index: number) {
        this.proofFiles.splice(index, 1);
    }

    submitApplication() {
        if (!this.selectedPolicy) return;

        // Check if every enabled security control has a proof uploaded
        const missingProofs = [];
        if (this.riskForm.firewallEnabled && !this.controlProofs['firewall']) missingProofs.push('Active Firewall');
        if (this.riskForm.encryptionEnabled && !this.controlProofs['encryption']) missingProofs.push('Data Encryption');
        if (this.riskForm.backupAvailable && !this.controlProofs['backup']) missingProofs.push('Regular Backups');
        if (this.riskForm.mfaEnabled && !this.controlProofs['mfa']) missingProofs.push('Multi-Factor Auth');
        if (this.riskForm.iso27001Certified && !this.controlProofs['iso27001']) missingProofs.push('ISO 27001 Certified');
        if (this.riskForm.hasDataPrivacyOfficer && !this.controlProofs['dpo']) missingProofs.push('Dedicated DPO');

        if (missingProofs.length > 0) {
            alert(`Please upload proof documents for the following enabled controls:\n- ${missingProofs.join('\n- ')}`);
            return;
        }

        if (!this.riskForm.employeeCount || !this.riskForm.annualRevenue) {
            alert("Please fill in Employee Count and Annual Revenue.");
            return;
        }

        this.loading = true;

        const payload = {
            policyId: this.selectedPolicy.id,
            ...this.riskForm
        };

        const formData = new FormData();
        formData.append('data', new Blob([JSON.stringify(payload)], { type: 'application/json' }));

        // Append individual control proofs with prefix
        for (const [key, file] of Object.entries(this.controlProofs)) {
            if (file) {
                formData.append('proofFiles', file, `${key.toUpperCase()}_PROOF_${file.name}`);
            }
        }

        // Append additional optional proofs
        for (const file of this.proofFiles) {
            formData.append('proofFiles', file, `ADDITIONAL_${file.name}`);
        }

        this.service.applyForPolicy(formData).subscribe({
            next: (res: any) => {
                this.loading = false;
                alert("✅ Application submitted successfully! Your request has been sent to the Admin for underwriter assignment.");
                this.closeModal();
            },
            error: (err: any) => {
                this.loading = false;
                alert("Failed to apply: " + (err.error?.message || err.error || err.message));
            }
        });
    }
}
