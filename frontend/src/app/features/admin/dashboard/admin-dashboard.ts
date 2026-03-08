import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminApiService } from '../services/admin.service';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.html'
})
export class AdminDashboard implements OnInit {

  stats: any = {
    totalUsers: 0,
    totalPolicies: 0,
    riskAssessments: 0,
    claims: 0
  };

  recentClaims = [
    { id: '#CLM-8012', customer: 'Global Finance LLC', policy: 'POL-452', amount: '₹12,50,000', status: 'Pending Review' },
    { id: '#CLM-8011', customer: 'NextGen Retail Inc', policy: 'POL-398', amount: '₹4,20,000', status: 'Approved' },
    { id: '#CLM-8010', customer: 'TechCorp Solutions', policy: 'POL-511', amount: '₹8,90,000', status: 'In Investigation' }
  ];

  recentPolicies = [
    { customer: 'Global Finance LLC', type: 'Data Breach Premium', premium: '₹4,50,000/yr', date: 'Today', status: 'Active' },
    { customer: 'NextGen Retail Inc', type: 'Ransomware Shield', premium: '₹2,20,000/yr', date: 'Yesterday', status: 'Pending' },
    { customer: 'TechCorp Solutions', type: 'DDoS Protection', premium: '₹1,50,000/yr', date: 'Oct 12', status: 'Active' }
  ];

  constructor(private adminApi: AdminApiService) { }

  ngOnInit() {
    this.adminApi.getDashboardStats().subscribe({
      next: (data) => this.stats = data,
      error: (err) => console.error("Error fetching dashboard stats", err)
    });
  }

}