import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UnderwriterService } from '../services/underwriter.service';

@Component({
    selector: 'app-decisions',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './decisions.html'
})
export class Decisions implements OnInit {

    decisions: any[] = [];
    loading = false;

    constructor(private service: UnderwriterService) { }

    ngOnInit(): void {
        this.loadDecisions();
    }

    loadDecisions() {

        this.loading = true;

        this.service.getMyAssignedSubscriptions().subscribe({

            next: (data: any[]) => {

                console.log("Decisions:", data);

                // show only approved/rejected
                this.decisions = data.filter(
                    (s: any) => s.status !== 'PENDING'
                );

                this.loading = false;

            },

            error: (err: any) => {

                console.error("Error loading decisions", err);
                this.loading = false;

            }

        });

    }

}