import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminApiService } from '../../../core/services/admin-api-service';

@Component({
  selector: 'admin-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.html'
})
export class AdminDashboard implements OnInit {

  stats = {
    totalCompanies: 0,
    activePolicies: 0,
    pendingAssessments: 0,
    openClaims: 0
  };

  constructor(private api: AdminApiService) { }

  ngOnInit(): void {
    this.loadStats();
  }

  loadStats() {

    // Policies
    this.api.getPolicies().subscribe(res => {
      this.stats.activePolicies = res.length;
    });

    // Claims
    this.api.getPendingClaims().subscribe(res => {
      this.stats.openClaims = res.length;
    });

    // Risk assessments
    this.api.getRiskAssessments().subscribe(res => {
      this.stats.pendingAssessments = res.length;
    });

    // For now we simulate companies count
    this.stats.totalCompanies = 10;

  }

}