import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ClaimsService } from '../services/claims.service';

@Component({
    selector: 'app-claims-queue',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './claims.queue.html'
})
export class ClaimsQueue implements OnInit {

    claims: any[] = [];
    selected: any = null;

    constructor(private service: ClaimsService) { }

    ngOnInit(): void {
        this.loadClaims();
    }

    loadClaims() {

        this.service.getPendingClaims().subscribe({

            next: (data: any[]) => {

                console.log("Pending Claims:", data);

                this.claims = data;

            },

            error: (err) => {

                console.error("Error loading claims", err);

            }

        });

    }

    review(claim: any) {
        this.selected = claim;
    }

    approve() {

        this.service.reviewClaim(this.selected.id, {
            decision: "APPROVED"
        }).subscribe(() => {
            alert("Claim Approved");
            this.selected = null;
            this.loadClaims();
        });

    }

    reject() {

        const reason = prompt("Enter rejection reason");

        this.service.reviewClaim(this.selected.id, {
            decision: "REJECTED",
            rejectionReason: reason || "Invalid claim"
        }).subscribe(() => {
            alert("Claim Rejected");
            this.selected = null;
            this.loadClaims();
        });

    }

}