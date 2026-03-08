import { Component, OnInit, AfterViewInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { UnderwriterService } from '../services/underwriter.service';

import Chart from 'chart.js/auto';

@Component({
  selector: 'app-underwriter-dashboard',
  standalone: true,
  imports: [RouterModule, CommonModule],
  templateUrl: './underwriter-dashboard.html'
})
export class UnderwriterDashboard implements OnInit, AfterViewInit {

  total = 0;
  pending = 0;
  approved = 0;
  rejected = 0;
  premiumTotal = 0;

  subscriptions: any[] = [];

  constructor(private service: UnderwriterService) { }

  ngOnInit(): void {
    this.loadStats();
  }

  ngAfterViewInit(): void { }

  loadStats() {

    this.service.getSubscriptions().subscribe(data => {

      this.subscriptions = data;

      this.total = data.length;

      this.pending = data.filter((s: any) => s.status === "PENDING").length;

      this.approved = data.filter((s: any) => s.status === "APPROVED").length;

      this.rejected = data.filter((s: any) => s.status === "REJECTED").length;

      this.premiumTotal = data
        .filter((s: any) => s.status === "APPROVED")
        .reduce((sum: number, s: any) => sum + s.calculatedPremium, 0);

      this.renderCharts();

    });

  }

  renderCharts() {

    const riskCounts: Record<'LOW' | 'MEDIUM' | 'HIGH', number> = {
      LOW: 0,
      MEDIUM: 0,
      HIGH: 0
    };

    this.subscriptions.forEach((s: any) => {

      const risk = s.riskAssessment?.riskLevel as 'LOW' | 'MEDIUM' | 'HIGH';

      if (risk && riskCounts[risk] !== undefined) {
        riskCounts[risk]++;
      }

    });

    new Chart("decisionChart", {
      type: 'pie',
      data: {
        labels: ['Approved', 'Rejected', 'Pending'],
        datasets: [{
          data: [this.approved, this.rejected, this.pending],
        }]
      }
    });

    new Chart("riskChart", {
      type: 'bar',
      data: {
        labels: ['LOW', 'MEDIUM', 'HIGH'],
        datasets: [{
          label: 'Risk Levels',
          data: [
            riskCounts.LOW,
            riskCounts.MEDIUM,
            riskCounts.HIGH
          ]
        }]
      }
    });

  }

}