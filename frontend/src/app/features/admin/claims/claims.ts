import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminApiService } from '../services/admin.service';

@Component({
  selector: 'app-claims',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './claims.html'
})
export class Claims implements OnInit {

  claimsList: any[] = [];

  constructor(private adminApi: AdminApiService) { }

  ngOnInit() {
    this.adminApi.getClaims().subscribe({
      next: (data) => {
        this.claimsList = data;
      },
      error: (err) => console.error('Error loading claims', err)
    });
  }

}