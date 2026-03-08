import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ClaimsService } from '../services/claims.service';

@Component({
  selector: 'app-claims-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './claims-dashboard.html'
})
export class ClaimsDashboard implements OnInit {

  total = 0;
  pending = 0;
  approved = 0;
  rejected = 0;

  constructor(private service: ClaimsService) { }

  ngOnInit(): void {

    this.service.getAllClaims().subscribe((claims: any[]) => {

      console.log("CLAIMS RESPONSE:", claims);

      this.total = claims.length;

      this.pending = claims.filter(c => c.status === 'PENDING').length;
      this.approved = claims.filter(c => c.status === 'APPROVED').length;
      this.rejected = claims.filter(c => c.status === 'REJECTED').length;

    });

  }

}