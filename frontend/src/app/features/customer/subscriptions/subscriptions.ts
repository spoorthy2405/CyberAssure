import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CustomerService } from '../services/customer.service';

@Component({
    standalone: true,
    imports: [CommonModule],
    templateUrl: './subscriptions.html'
})
export class Subscriptions implements OnInit {

    subscriptions: any[] = [];

    constructor(private service: CustomerService) { }

    ngOnInit(): void {

        this.service.getSubscriptions().subscribe(data => {

            this.subscriptions = data;

        });

    }

}