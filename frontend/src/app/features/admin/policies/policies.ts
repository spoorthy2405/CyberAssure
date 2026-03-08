import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminApiService } from '../services/admin.service';

@Component({
    selector: 'app-policies',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './policies.html'
})
export class Policies implements OnInit {

    policiesList: any[] = [];

    constructor(private adminApi: AdminApiService) { }

    ngOnInit() {
        this.adminApi.getPolicies().subscribe({
            next: (data) => {
                this.policiesList = data;
            },
            error: (err) => console.error('Error loading policies', err)
        });
    }

}