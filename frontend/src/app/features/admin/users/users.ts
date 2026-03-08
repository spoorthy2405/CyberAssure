import { Component, OnInit } from '@angular/core';
import { AdminApiService } from '../../../core/services/admin-api-service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'admin-users',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './users.html'
})
export class Users implements OnInit {

  users: any[] = [];

  constructor(private api: AdminApiService) { }

  ngOnInit() {
    this.api.getCustomers().subscribe(res => {
      this.users = res;
    })
  }

}