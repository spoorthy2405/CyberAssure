import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CustomerService } from '../services/customer.service';

@Component({
    standalone: true,
    imports: [CommonModule],
    templateUrl: './policies.html'
})
export class Policies implements OnInit {

    policies: any[] = [];

    constructor(private service: CustomerService) { }

    ngOnInit() {

        this.service.getPolicies().subscribe(data => {

            this.policies = data;

        });

    }

    subscribe(policy: any) {

        this.service.subscribePolicy({

            policyId: policy.id

        }).subscribe(() => {

            alert("Subscription Request Sent");

        });

    }

}