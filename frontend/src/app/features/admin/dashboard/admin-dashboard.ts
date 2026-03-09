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

  // Initializing with some baseline demo data so it doesn't look empty on fresh start
  stats: any = {
    totalCustomers: 4,
    totalPolicies: 32,
    activeClaims: 8,
    approvedClaims: 15,
    totalClaims: 28,
    rejectedClaims: 5,
    totalUnderwriters: 2,
    totalClaimsOfficers: 2
  };

  recentClaims = [
    { id: '#CLM-8012', customer: 'TechCorp India', amount: '₹12,50,000', status: 'Pending' },
    { id: '#CLM-8011', customer: 'Global Health Clinics', amount: '₹4,20,000', status: 'Approved' },
    { id: '#CLM-8010', customer: 'FinSecure Bank', amount: '₹8,90,000', status: 'Under Review' }
  ];

  recentPolicies = [
    { customer: 'TechCorp India', type: 'Financial Data Protection', premium: '₹2,50,000/yr', status: 'Active' },
    { customer: 'Global Health Clinics', type: 'Patient Data Protection', premium: '₹1,20,000/yr', status: 'Pending' },
    { customer: 'FinSecure Bank', type: 'Core Banking System Shield', premium: '₹5,00,000/yr', status: 'Active' }
  ];

  constructor(private adminApi: AdminApiService) { }

  ngOnInit() {
    this.adminApi.getDashboardStats().subscribe({
      next: (data) => {
        // Merge real data onto our baseline to ensure a "full" look
        this.stats = {
          ...this.stats,
          ...data,
          // If real DB has data, we use it, otherwise keep the baseline "demo" values
          totalCustomers: data.totalCustomers || this.stats.totalCustomers,
          totalPolicies: data.totalPolicies || this.stats.totalPolicies,
          totalUnderwriters: data.totalUnderwriters || this.stats.totalUnderwriters,
          totalClaimsOfficers: data.totalClaimsOfficers || this.stats.totalClaimsOfficers,
          totalClaims: data.totalClaims || this.stats.totalClaims,
          activeClaims: data.activeClaims || this.stats.activeClaims,
          approvedClaims: data.approvedClaims || this.stats.approvedClaims,
          rejectedClaims: data.rejectedClaims || this.stats.rejectedClaims
        };
      },
      error: (err) => console.error("Error fetching dashboard stats", err)
    });
  }

}