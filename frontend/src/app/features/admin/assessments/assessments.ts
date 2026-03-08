import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminApiService } from '../../../core/services/admin-api-service';

@Component({
  selector: 'admin-assessments',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './assessments.html'
})
export class Assessments implements OnInit {

  risks: any[] = [];

  constructor(private api: AdminApiService) { }

  ngOnInit(): void {
    this.api.getRisks().subscribe(res => {
      console.log("RISKS:", res);
      this.risks = res;
    });
  }

}