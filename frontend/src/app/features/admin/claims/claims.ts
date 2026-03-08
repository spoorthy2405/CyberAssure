import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminApiService } from '../../../core/services/admin-api-service';

@Component({
  selector: 'admin-claims',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './claims.html',
  styleUrl: './claims.css'
})
export class Claims implements OnInit {

  claims: any[] = [];

  constructor(private api: AdminApiService) { }

  ngOnInit() {
    this.loadClaims();
  }

  loadClaims() {
    this.api.getClaims().subscribe({
      next: (res) => {
        console.log("CLAIMS:", res);
        this.claims = res;
      },
      error: (err) => {
        console.error("Claims API Error:", err);
      }
    })
  }

}