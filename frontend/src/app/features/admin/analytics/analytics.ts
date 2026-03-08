import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminApiService } from '../services/admin.service';

@Component({
  selector: 'app-analytics',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './analytics.html'
})
export class Analytics implements OnInit {

  analyticsData: any = {};

  constructor(private adminApi: AdminApiService) { }

  ngOnInit() {
    this.adminApi.getAnalytics().subscribe({
      next: (data) => this.analyticsData = data,
      error: (err) => console.error("Error fetching analytics data", err)
    });
  }

}