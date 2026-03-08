
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminApiService } from '../services/admin.service';

@Component({
    selector: 'app-risk-monitoring',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './risk-monitoring.html'
})
export class RiskMonitoring implements OnInit {

    riskData: any = {};

    alerts: any[] = [];

    constructor(private adminApi: AdminApiService) { }

    ngOnInit() {

        this.adminApi.getRiskMonitoring().subscribe({
            next: (data) => this.riskData = data,
            error: (err) => console.error("Risk Monitoring Error", err)
        });

        // Temporary alerts (can later come from backend)
        this.alerts = [
            {
                type: 'Critical',
                source: 'Policy System',
                description: 'Multiple suspicious claims detected',
                time: '10 mins ago'
            },
            {
                type: 'Warning',
                source: 'Risk Engine',
                description: 'Risk score increased significantly',
                time: '2 hours ago'
            },
            {
                type: 'Info',
                source: 'System',
                description: 'Automated risk evaluation completed',
                time: '5 hours ago'
            }
        ];

    }

}

