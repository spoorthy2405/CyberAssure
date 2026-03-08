import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UnderwriterService } from '../services/underwriter.service';

@Component({
    selector: 'app-review-queue',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './review-queue.html',
    styleUrl: './review-queue.css'
})
export class ReviewQueue implements OnInit {

    assessments: any[] = [];
    selected: any = null;
    loading = false;

    constructor(private service: UnderwriterService) { }

    ngOnInit(): void {
        this.loadAssessments();
    }

    loadAssessments() {

        this.loading = true;

        this.service.getSubscriptions().subscribe({

            next: (data) => {

                console.log("Subscriptions:", data);

                // show only pending subscriptions
                this.assessments = data.filter(
                    (s: any) => s.status === 'PENDING'
                );

                this.loading = false;

            },

            error: (err) => {

                console.error("Error loading subscriptions", err);

                this.loading = false;

            }

        });

    }

    review(subscription: any) {
        this.selected = subscription;
    }

    approve() {

        const body = {
            decision: "APPROVED"
        };

        this.service.reviewSubscription(this.selected.id, body)
            .subscribe(() => {

                alert("Policy Approved");

                this.selected = null;

                this.loadAssessments();

            });

    }

    decline() {

        const body = {
            decision: "REJECTED",
            rejectionReason: "Risk too high"
        };

        this.service.reviewSubscription(this.selected.id, body)
            .subscribe(() => {

                alert("Policy Rejected");

                this.selected = null;

                this.loadAssessments();

            });

    }

}