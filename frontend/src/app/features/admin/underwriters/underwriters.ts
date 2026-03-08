import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminApiService, User } from '../services/admin.service';

@Component({
    selector: 'app-underwriters',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './underwriters.html'
})
export class Underwriters implements OnInit {

    underwritersList: any[] = [];

    constructor(private adminApi: AdminApiService) { }

    ngOnInit() {
        this.adminApi.getUnderwriters().subscribe({
            next: (data) => this.underwritersList = data,
            error: (err) => console.error("Error fetching underwriters", err)
        });
    }

}
