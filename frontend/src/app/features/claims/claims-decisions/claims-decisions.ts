import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ClaimsService } from '../services/claims.service';

@Component({
    selector: 'app-claims-decisions',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './claims-decisions.html'
})
export class ClaimsDecisions implements OnInit {

    claims: any[] = [];

    constructor(private service: ClaimsService) { }

    ngOnInit(): void {

        this.service.getAllClaims().subscribe((data: any[]) => {

            console.log("CLAIMS RESPONSE:", data);

            this.claims = data.filter(
                c => c.status !== 'PENDING'
            );

        });

    }

}