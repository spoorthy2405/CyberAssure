import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminApiService } from '../../../core/services/admin-api-service';

@Component({
  selector: 'admin-policy-plans',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './policy-plans.html',
  styleUrl: './policy-plans.css'
})
export class PolicyPlans implements OnInit {

  policies: any = [];

  constructor(private api: AdminApiService) { }

  ngOnInit() {
    this.api.getPolicies().subscribe(res => {
      this.policies = res;
      console.log(res);
    })
  }

}
