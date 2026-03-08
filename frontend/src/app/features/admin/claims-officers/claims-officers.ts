import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminApiService, User } from '../services/admin.service';

@Component({
    selector: 'app-claims-officers',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './claims-officers.html'
})
export class ClaimsOfficers implements OnInit {

    officersList: any[] = [];

    constructor(private adminApi: AdminApiService) { }

    ngOnInit() {
        this.adminApi.getClaimsOfficers().subscribe({
            next: (data) => this.officersList = data,
            error: (err) => console.error("Error fetching claims officers", err)
        });
    }

}
