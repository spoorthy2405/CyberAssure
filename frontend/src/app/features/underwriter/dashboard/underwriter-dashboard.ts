import { Component, OnInit, AfterViewInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { UnderwriterService } from '../services/underwriter.service';

@Component({
  selector: 'app-underwriter-dashboard',
  standalone: true,
  imports: [RouterModule, CommonModule],
  templateUrl: './underwriter-dashboard.html'
})
export class UnderwriterDashboard implements OnInit {

  total = 0;
  pending = 0;
  approved = 0;
  rejected = 0;
  userName = 'Underwriter';
  userInitial = 'U';

  constructor(private service: UnderwriterService, private router: Router) { }

  ngOnInit(): void {
    this.loadStats();
    // Get name from stored token/session
    const stored = localStorage.getItem('user') || sessionStorage.getItem('user');
    if (stored) {
      try {
        const user = JSON.parse(stored);
        this.userName = user.fullName || user.name || 'Underwriter';
        this.userInitial = this.userName.charAt(0).toUpperCase();
      } catch (e) { }
    }
  }

  loadStats() {
    this.service.getMyAssignedSubscriptions().subscribe((data: any[]) => {
      this.total = data.length;
      this.pending = data.filter((s: any) => s.status === 'PENDING').length;
      this.approved = data.filter((s: any) => s.status === 'APPROVED').length;
      this.rejected = data.filter((s: any) => s.status === 'REJECTED').length;
    });
  }

  signOut() {
    localStorage.clear();
    sessionStorage.clear();
    this.router.navigate(['/login']);
  }
}