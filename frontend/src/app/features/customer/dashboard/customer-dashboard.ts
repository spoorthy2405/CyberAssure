import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterOutlet } from '@angular/router';
import { CustomerService } from '../services/customer.service';

@Component({
  selector: 'app-customer-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterOutlet],
  templateUrl: './customer-dashboard.html'
})
export class CustomerDashboard implements OnInit {

  risk: any;
  subscriptions: any[] = [];
  claims: any[] = [];

  constructor(private service: CustomerService) { }

  ngOnInit() {

    this.loadData();

  }

  loadData() {

    this.service.getRisk().subscribe(r => this.risk = r);

    this.service.getSubscriptions().subscribe(s => this.subscriptions = s);

    this.service.getClaims().subscribe(c => this.claims = c);

  }

}